package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;


/**
 * Runnable class able to add  one (or more) problem entry.<br/>
 * More precisely this class adds a <i>Medication</i> to the underlying 
 * persistence layer; after this it list on the standard output the results
 * (dynabeans). <br/>
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClientExample -Dexec.classpathScope=test<br/>
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
public final class MedicationClientExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {
        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final MedicationClient client = new MedicationClient(triplestore);

        // this adds a problem-symptom named fever
        client.addMedicationSign(
                owner,
                "Free text note for the medication.",
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "MyFreqency",
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

        for (DynaBean dynaBean : beans) {
            final String toString = DynaBeanUtil.toString(dynaBean);
            System.out.println(toString);
        }

        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();

    }
}
