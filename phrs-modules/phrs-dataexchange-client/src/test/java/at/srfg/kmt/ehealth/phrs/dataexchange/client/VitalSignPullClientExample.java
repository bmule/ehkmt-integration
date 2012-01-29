/*
 * Project :iCardea
 * File : VitalSignPushClientExample.java
 * Encoding : UTF-8
 * Date : Jan 27, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;



/**
 * Runnable class able to pull more vital signs to to the PHRS repository for a
 * given user.<br/>
 * The entries are added(push) with an other (previous) process.<br/>
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignPullClientExample -Dexec.classpathScope=test<br/>
 * </pre> <br/>
 * Take care this manven command does not compile the classes. <br/> 
 * This class was not designed to be extended.
 * <b>Note : <b/> The connection with the underlying persistnce context is
 * configurated with a configuration file named "generic_triplestore.xml" placed
 * in the classpath.
 * 
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class VitalSignPullClientExample {
    
    /**
     * Don't let anyone to instantiate this class.
     */
    private VitalSignPullClientExample() {
        // UNIMPLEMENTD
    }
        
    /**
     * Runs this class from the command line.
     * 
     * @param args the command line arguments, no arguments are required.
     * @throws TripleException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws GenericRepositoryException 
     */
    public static void main(String... args) throws TripleException, IllegalAccessException, InstantiationException, GenericRepositoryException {

        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE,
                Constants.PHRS_VITAL_SIGN_CLASS);
        queryMap.put(Constants.OWNER, owner);

        // here I search for all resources with 
        // rdf type == vital sign 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
            System.out.println(dynaBean);
        }

        // close the connection with the triplestore.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
    }


}
