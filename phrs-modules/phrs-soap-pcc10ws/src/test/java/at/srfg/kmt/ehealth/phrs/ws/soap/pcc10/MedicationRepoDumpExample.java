package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;

/**
 * Runnable class able pull all the medication entries entry for a given owner 
 * and transform then on in to a HL7 message and store this message.<br/>
 * The exact location for this file is listed in the log file (located in 
 * target/log.out)<br/> 
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.MedicationRepoDumpExample -Dexec.classpathScope=test -Dexec.args="191"<br/>
 * </pre>
 * Take care this command does not compile the classes. <br/>
 * <b>Nota Bene : </b> this class will remove/clean the triplestore related 
 * files. The location for this files is configurated via the configuration 
 * file named generic_triplestore.xml. <br/>
  * <b>Note : </b> this class requires one argument : the owner id for the medication.
 * 
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public class MedicationRepoDumpExample {
    
    public static void main(String... args) throws GenericRepositoryException, 
            TripleException, IllegalAccessException, 
            InstantiationException, JAXBException {
        
        if (args == null) {
            System.out.println("One argument expected - the owner id."); 
            return;
        }
        
//        final String owner = Constants.PROTOCOL_ID_UNIT_TEST;
        final String owner = args[0];
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();
        
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE,
                Constants.PHRS_MEDICATION_CLASS);
        queryMap.put(Constants.OWNER, owner);

        // here I search for all resources with 
        // rdf type == medication
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);
        
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
            beans.add(dynaBean);
        }
        
        final QUPCIN043200UV01 pCC10Message = MedicationSignPCC10.getPCC10Message(owner,beans);
        final String fileName = "repo-dump-medication-" + owner;
        QUPCAR004030UVUtil.toWriteInTemp(pCC10Message, fileName);


        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
    }
}
