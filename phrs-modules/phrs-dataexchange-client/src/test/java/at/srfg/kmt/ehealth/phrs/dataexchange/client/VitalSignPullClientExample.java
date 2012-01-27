/*
 * Project :iCardea
 * File : VitalSignPushClientExample.java
 * Encoding : UTF-8
 * Date : Jan 27, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
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



/**
 * Runnable class able to pull more  vital signs to to the PHRS repository.<br/>
 * More precisely this class pull <i>Vital Sign</i> instaces from the underlying
 * persistence layer. The entries are added(push) with other application.<br/>
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignPullClientExample -Dexec.classpathScope=test<br/>
 * </pre> Take care this command does not compile the classes. <br/> 
 * <b>Nota Bene : </b> the repository URI and reposiotry ID are configured with
 * the file named "generic_triplestore.xml" placed in classpath.
 * 
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class VitalSignPullClientExample {
        public static void main(String... args) throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {

        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final VitalSignClient client = new VitalSignClient(triplestore);

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
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
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
