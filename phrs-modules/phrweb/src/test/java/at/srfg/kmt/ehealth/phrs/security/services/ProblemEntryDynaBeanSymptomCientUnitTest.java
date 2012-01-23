/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import java.util.List;
import org.junit.Ignore;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.apache.commons.beanutils.DynaBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test adds a symptom named fever to the underlying persistence layer, 
 * retrieve it like DynaBean it proves if this bean has the right content. 
 * 
 * @author Mihai
 */
//@Ignore
public class ProblemEntryDynaBeanSymptomCientUnitTest {

    /**
     * The time for the registered problem.
     */
    public static final String DATE_START = "201006010000";
    public static final String DATE_END = "201106010000";
    /**
     * The note for the vial sign.
     */
    public static final String NOTE = "123456789-777-111";
    //public static final String NOTE = "Free text note for symptom fever.";
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>ProblemEntryDynaBeanCientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryDynaBeanSymptomCientUnitTest.class);
    private static final String USER = ProblemEntryDynaBeanSymptomCientUnitTest.class.getName();
    private GenericTriplestore triplestore;
    private DynaBeanClient dynaBeanClient;
    private ProblemEntryClient problemClient;

    public ProblemEntryDynaBeanSymptomCientUnitTest() {
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
        problemClient = new ProblemEntryClient(triplestore);
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
    public void testAddProblemEntryForOwner() throws Exception{
        System.out.println("testAddProblemEntryForOwner...");
        problemClient.addProblemEntry(
                USER,
                Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                DATE_START,
                DATE_END,
                NOTE,
                Constants.HL7V3_FEVER);

        final Iterable<String> resources =
                problemClient.getProblemEntriesURIForUser(USER);

        for (String resoure : resources) {
           
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
             System.out.println("probem dynabean= "+dynaBean.toString());
            final Object rdfType = dynaBean.get(Constants.RDFS_TYPE);
            assertEquals(Constants.PHRS_OBSERVATION_ENTRY_CLASS, rdfType);

            final Object createDate = dynaBean.get(Constants.CREATE_DATE);
            assertNotNull(createDate);

            final Object creator = dynaBean.get(Constants.CREATOR);
            assertEquals(ProblemEntryClient.class.getName(), (String)creator);
            System.out.println("probem creator= "+dynaBean.get(Constants.CREATOR));

            final List rootTemplates =
                    (List) dynaBean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
            assertTrue(rootTemplates.size() == 2);

            //final DynaBean code = (DynaBean) dynaBean.get(Constants.HL7V3_CODE);
            
            //No, these are UMLS proveCode(code);

            final Object note = dynaBean.get(Constants.SKOS_NOTE);
            assertEquals(NOTE, note);
            System.out.println("probem NOTE prop= "+Constants.SKOS_NOTE+" value= "+(String)dynaBean.get(Constants.SKOS_NOTE));


            final Object startDateStr = dynaBean.get(Constants.HL7V3_START_DATE);
            assertEquals(DATE_START, startDateStr);

            final Object endDateStr = dynaBean.get(Constants.HL7V3_END_DATE);
            assertEquals(DATE_END, endDateStr);

            //final DynaBean statusBean =
            //        (DynaBean) dynaBean.get(Constants.HL7V3_STATUS);
            //proveStatusBean(statusBean);

            //final DynaBean codeValueBean = (DynaBean) dynaBean.get(Constants.HL7V3_VALUE_CODE);
            //proveCodeValueBean(codeValueBean);

        }
    }
//
//    private void proveCode(DynaBean bean) {
//        final Object codePrefLabel = bean.get(Constants.SKOS_PREFLABEL);
//        // this value is according with the loaded rdf file
//        assertEquals("Symptom", codePrefLabel);
//        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
//
//        final Object codeValue = codeBean.get(Constants.CODE_VALUE);
//        // this is according with the loaded rdf file
//        assertEquals("C1457887", codeValue);
//
//        final DynaBean codeSystemBean =
//                (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
//        proveCodeSystem(codeSystemBean);
//    }
//
//    private void proveCodeSystem(DynaBean bean) {
//        final Object codeSystemCode = bean.get(Constants.CODE_SYSTEM_CODE);
//        assertEquals("2.16.840.1.113883.6.86", codeSystemCode);
//        final Object codeSystemName = bean.get(Constants.CODE_SYSTEM_NAME);
//        assertEquals("UMLS", codeSystemName);
//    }
//
//    private void proveStatusBean(DynaBean bean) {
//        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);
//        assertEquals("Complete", prefLabel);
//
//        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
//        final Object codeValue = codeBean.get(Constants.CODE_VALUE);
//        assertEquals("C0205197", codeValue);
//
//        final DynaBean codeSystemBean = (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
//        proveCodeSystem(codeSystemBean);
//    }
//
//    private void proveCodeValueBean(DynaBean bean) {
//        final String toString = DynaBeanUtil.toString(bean);
//        final Object prefLabel = bean.get(Constants.SKOS_PREFLABEL);
//        assertEquals("Fever", prefLabel);
//
//        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
//        final DynaBean codeSystemBean = (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
//        proveCodeSystem(codeSystemBean);
//    }
}
