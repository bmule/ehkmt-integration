package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
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
 * Runnable class able to add a daily living entry.<br/>
 * More precisely this class adds a <i>Fatique</i> with the estabilish code
 * <i>Finding</i> to the underlying persistence layer; after this it generates 
 * PCC10 conform message and serialize it in to a file named 
 * <i>fatigue-finding.xml</i> stored in to the temporary directory. 
 * The exact location for this file is listed in the log file (located in 
 * target/log.out)<br/> 
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.DailyLivingStep8ClientExample -Dexec.classpathScope=test<br/>
 * </pre>
 * Take care this command does not compile the classes. <br/>
 * <b>Nota Bene : </b> this class will remove/clean the triplestore related 
 * files. The location for this files is configurated via the configuration 
 * file named generic_triplestore.xml. <br/>
 * 
 * @author fstrohmeier
 * @version 0.1
 * @since 0.1
 */
public class DailyLivingStep8ClientExample {

    public static void main(String... args) 
            throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {
        
        final String owner = Constants.PROTOCOL_ID_UNIT_TEST;
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final ProblemEntryClient client = new ProblemEntryClient(triplestore);

        client.addProblemEntry(
                owner,
                Constants.HL7V3_FINDING,
                Constants.STATUS_ACTIVE,
                "201006010000",
                "201007120000",
                "Free text note.",
                Constants.HL7V3_WALKING);

        client.addProblemEntry(
                owner,
                Constants.HL7V3_FINDING,
                Constants.STATUS_ACTIVE,
                "201006010000",
                "201007120000",
                "Free text note.",
                Constants.HL7V3_BICYCLE_RIDING);

        final Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pCC10Message = ProblemEntryPCC10.getPCC10Message(owner,beans);
        QUPCAR004030UVUtil.toWriteInTemp(pCC10Message, "daily-living-step8");



        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();

    }
}
