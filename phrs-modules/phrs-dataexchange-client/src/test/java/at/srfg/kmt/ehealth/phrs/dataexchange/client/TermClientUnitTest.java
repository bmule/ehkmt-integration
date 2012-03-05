/*
 * Project :iCardea
 * File : TermClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import org.apache.commons.beanutils.DynaBean;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class TermClientUnitTest {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TermClientUnitTest.class);
    
    private static final String UMLS_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.86";


    private GenericTriplestore triplestore;

    private TermClient termClient;
    
    private DynaBeanClient dynaBeanClient;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     *
     * @throws GenericRepositoryException if this occurs then the test
     * environment may be wrong set.
     */
    @Before
    public void initSuite() throws GenericRepositoryException {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        termClient = new TermClient(triplestore);
        dynaBeanClient = new DynaBeanClient(triplestore);
        
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
    public void searchSymptomTerms() throws TripleException {
        final Iterable<Triple> termsRelatedWith =
                termClient.getTermsRelatedWith(Constants.HL7V3_SYMPTOM);
        assertTrue(termsRelatedWith.iterator().hasNext());
    }

    @Test
    public void searchConditionTerms() throws TripleException {
        final Iterable<Triple> termsRelatedWith =
                termClient.getTermsRelatedWith(Constants.HL7V3_CONDITION);
        assertFalse(termsRelatedWith.iterator().hasNext());
    }

    /**
     * Prove the existence in to the underlying persistence layer for a given
     * term, the term is located after its code and code system information.
     * This test is based on the file "startup.test.rdf".
     * 
     * @throws ClientException if this exception occurs then this test will fails.
     * @throws IllegalAccessException if this exception occurs then this test will
     * fails.
     * @throws InstantiationException if this exception occurs then this test will
     * fails.
     * @throws TripleException if this exception occurs then this test will fails.
     */
    @Test
    public void testGetTermURI() throws ClientException, IllegalAccessException, InstantiationException, TripleException {
        final String codeValueStr = "C0423797";
        // the BruisingSymptom has 
        // code = C0423797 in the UMLS Code Ssytem
        // the UMLS Code Ssytem has code = 2.16.840.1.113883.6.86
        
        final String termURI = 
                termClient.getTermURI(codeValueStr, UMLS_CODE_SYSTEM_CODE);
        assertNotNull(termURI);
        
        final DynaBean term = dynaBeanClient.getDynaBean(termURI);
        assertNotNull(term);
        
        // according with the init rdf file (startup.test.rdf) the pref label for 
        // this term is "Bruising symptom"
        final String prefLabel = (String) term.get(Constants.SKOS_PREFLABEL);
        assertEquals("Bruising symptom", prefLabel);
        
        // here I extract the code information
        final DynaBean code = (DynaBean) term.get(Constants.CODE);
        final String codeValue = (String) code.get(Constants.CODE_VALUE);
        assertEquals(codeValueStr, codeValue);
        
        // here I extract the code system
        final DynaBean codeSystem = (DynaBean) code.get(Constants.CODE_SYSTEM);
        final String codeSystemCode = (String) codeSystem.get(Constants.CODE_SYSTEM_CODE);
        assertEquals(UMLS_CODE_SYSTEM_CODE, codeSystemCode);
    }
}
