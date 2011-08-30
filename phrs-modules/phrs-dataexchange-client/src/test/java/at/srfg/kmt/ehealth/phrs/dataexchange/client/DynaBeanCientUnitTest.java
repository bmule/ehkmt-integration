/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.apache.commons.beanutils.DynaBean;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mradules
 */
public class DynaBeanCientUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynaBeanCientUnitTest.class);
    private static final String USER = DynaBeanCientUnitTest.class.getName();
    private GenericTriplestore triplestore;
    private DynaBeanClient dynaBeanClient;
    private VitalSignClient vitalSignClient;

    public DynaBeanCientUnitTest() {
    }

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     * 
     * @throws GenericRepositoryException if this occurs then the test 
     * environment may be wrong set.
     */
    @Before
    public void initSuite() throws GenericRepositoryException, TripleException {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        dynaBeanClient = new DynaBeanClient(triplestore);
        vitalSignClient = new VitalSignClient(triplestore);
    }

    /**
     * Runs all after any test from this suite and cleans the environment in 
     * order that the next test will get a 'clean' environment.
     * 
     * @throws GenericRepositoryException 
     */
    @After
    public void shutdownSuite() throws GenericRepositoryException {
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
        triplestore = null;
    }

    @Test
    public void testAddVitalSignForOwner() throws TripleException, IllegalAccessException, InstantiationException {
        
        vitalSignClient.addVitalSign(USER,
                ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                "Free text note for systolic.",
                "201006010000",
                "100",
                MM_HG);

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(RDFS_TYPE, PHRS_VITAL_SIGN_CLASS);
        queryMap.put(OWNER, USER);

        // here I search for all resources with 
        // rdf type == vital sign 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
            final Object rdfType = dynaBean.get(RDFS_TYPE);
            System.out.println("->" + rdfType);
            final Object createDate = dynaBean.get(CREATE_DATE);
            System.out.println("->" + createDate);
            final Object creator = dynaBean.get(CREATOR);
            System.out.println("->" + creator);
            final Object rootTemplates = dynaBean.get(HL7V3_TEMPLATE_ID_ROOT);
            System.out.println("->" + rootTemplates);
            final Object code = dynaBean.get(HL7V3_CODE);
            System.out.println("->" + toString((DynaBean) code));
            final Object note = dynaBean.get(SKOS_NOTE);
            System.out.println("->" + note);
            final Object effectiveTime = dynaBean.get(EFFECTIVE_TIME);
            System.out.println("->" + effectiveTime);
            final Object value = dynaBean.get(HL7V3_VALUE);
            System.out.println("->" + value);
            final Object unit = dynaBean.get(HL7V3_UNIT);
            System.out.println("->" + unit);
        }
    }

    /**                                                                                                                                                                         
     * Returns a String representation for a given <code>DynaBean</code>.                                                                                                       
     *                                                                                                                                                                          
     * @param bean the involved bean.                                                                                                                                           
     * @return a String representation for a given <code>DynaBean</code>.                                                                                                       
     */
    public String toString(DynaBean bean) {

        if (bean == null) {
            throw new NullPointerException("The bean argument can not be null.");
        }

        final StringBuffer msg = new StringBuffer();
        final DynaClass dynaClass = bean.getDynaClass();
        msg.append(dynaClass.getName());
        msg.append(":{");
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        for (DynaProperty property : properties) {
            final String name = property.getName();
            msg.append(name);
            msg.append(":");
            msg.append(bean.get(name));
            msg.append(" ,");
        }
        // remove the last " ,"                                                                                                                                                 
        msg.delete(msg.length() - 2, msg.length());

        return msg.toString();
    }
}
