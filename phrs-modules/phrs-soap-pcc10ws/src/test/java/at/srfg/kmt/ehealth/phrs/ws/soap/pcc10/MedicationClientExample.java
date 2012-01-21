package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;


/**
 * Runnable class able to add  one (or more) problem entry.<br/>
 * More precisely this class adds a <i>Symptom</i> with the estabilish code
 * <i>Fever</i> to the underlying persistence layer; after this it generates 
 * PCC10 conform message and serialize it in to a file named 
 * <i>medication.xml</i> stored in to the temporary directory. 
 * The exact location for this file is listed in the log file (located in 
 * target/log.out)<br/> 
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.MedicationClientExample -Dexec.classpathScope=test
 * </pre>
 * Take care this command does not compile the classes. <br/>
 * <b>Nota Bene : </b> this class will remove/clean the triplestore related 
 * files. The location for this files is configurated via the configuration 
 * file named generic_triplestore.xml. <br/>
 * 
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public class MedicationClientExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {
        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final MedicationClient client = new MedicationClient(triplestore);
        client.addMedicationSign(
                owner,
                "Free text note for the medication.",
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                client.buildFrequency("morgen", -1, 8, Constants.HOUR),
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "1",
                Constants.PILL,
                "MyDrug");

        final Iterable<String> uris = client.getMedicationURIsForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pCC10Message = MedicationSignPCC10.getPCC10Message(beans);
        QUPCAR004030UVUtil.toWriteInTemp(pCC10Message, "medication");

        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
    }
}
