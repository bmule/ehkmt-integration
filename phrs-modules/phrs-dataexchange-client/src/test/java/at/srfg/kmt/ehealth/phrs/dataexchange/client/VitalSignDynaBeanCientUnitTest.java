/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.List;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.apache.commons.beanutils.DynaBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test it access the vial sign information stored in the underlying 
 * triplestore (as triples) and manipulate like DynaBean instance. <br/>
 * More precisely this test unit   
 *
 * 
 * @author Mihai
 */
public class VitalSignDynaBeanCientUnitTest {

    /**
     * The time for the registered vital sign.
     */
    public static final String TIME = "201006010000";
    /**
     * The note for the vial sign.
     */
    public static final String NOTE = "Free text note for systolic.";
    /**
     * 
     */
    public static final String VALUE = "100";
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignDynaBeanCientUnitTest.class);
    private static final String USER = VitalSignDynaBeanCientUnitTest.class.getName();
    private GenericTriplestore triplestore;
    private DynaBeanClient dynaBeanClient;
    private VitalSignClient vitalSignClient;

    public VitalSignDynaBeanCientUnitTest() {
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
                Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                NOTE,
                TIME,
                Constants.STATUS_COMPELETE,
                VALUE,
                Constants.MM_HG);

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE,
                Constants.PHRS_VITAL_SIGN_CLASS);
        queryMap.put(Constants.OWNER, USER);

        // here I search for all resources with 
        // rdf type == vital sign 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
            final Object rdfType = dynaBean.get(Constants.RDFS_TYPE);
            assertEquals(Constants.PHRS_VITAL_SIGN_CLASS, rdfType);

            final Object createDate = dynaBean.get(Constants.CREATE_DATE);
            assertNotNull(createDate);

            final Object creator = dynaBean.get(Constants.CREATOR);
            assertEquals(VitalSignClient.class.getName(), creator);

            final List rootTemplates =
                    (List) dynaBean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
            assertTrue(rootTemplates.size() == 3);

            final DynaBean code = (DynaBean) dynaBean.get(Constants.HL7V3_CODE);
            proveCode(code);

            final Object note = dynaBean.get(Constants.SKOS_NOTE);
            assertEquals(NOTE, note);

            final Object effectiveTime = dynaBean.get(Constants.EFFECTIVE_TIME);
            assertEquals(TIME, effectiveTime);

            final DynaBean statusBean =
                    (DynaBean) dynaBean.get(Constants.HL7V3_STATUS);
            proveStatusBean(statusBean);

            final Object value = dynaBean.get(Constants.HL7V3_VALUE);
            assertEquals(VALUE, value);

            final DynaBean unit = (DynaBean) dynaBean.get(Constants.HL7V3_UNIT);
            proveUnit(unit);
        }
    }

    private void proveCode(DynaBean bean) {
        final Object codePrefLabel = bean.get(Constants.SKOS_PREFLABEL);
        // this value is according with the loaded rdf file
        assertEquals("Systolic blood pressure", codePrefLabel);
        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);

        final Object codeValue = codeBean.get(Constants.CODE_VALUE);
        // this is according with the loaded rdf file
        assertEquals("C0871470", codeValue);

        final DynaBean codeSystemBean =
                (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
        proveCodeSystem(codeSystemBean);
    }

    private void proveCodeSystem(DynaBean bean) {
        final Object codeSystemCode = bean.get(Constants.CODE_SYSTEM_CODE);
        assertEquals("2.16.840.1.113883.6.86", codeSystemCode);
        final Object codeSystemName = bean.get(Constants.CODE_SYSTEM_NAME);
        assertEquals("UMLS", codeSystemName);
    }

    private void proveStatusBean(DynaBean bean) {
        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);
        assertEquals("Complete", prefLabel);
        
        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
        final Object codeValue = codeBean.get(Constants.CODE_VALUE);
        assertEquals("C0205197", codeValue);
        
        final DynaBean codeSystemBean =  (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
        final Object codeSystemName = codeSystemBean.get(Constants.CODE_SYSTEM_NAME);
        assertEquals("UMLS", codeSystemName);
        
        final Object codeSystemCode = codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        assertEquals("2.16.840.1.113883.6.86", codeSystemCode);
    }

    private void proveUnit(DynaBean bean) {
        final Object preflabel = bean.get(Constants.SKOS_PREFLABEL);
        assertEquals("Milimeter Hg", preflabel);
        final Object notation = bean.get(Constants.SKOS_NOTATION);
        assertEquals("mm[Hg]", notation);
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
