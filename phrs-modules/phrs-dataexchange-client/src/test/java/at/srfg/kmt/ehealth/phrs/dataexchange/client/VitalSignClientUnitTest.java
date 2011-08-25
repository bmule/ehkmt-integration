/*
 * Project :iCardea
 * File : VitalSignClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 24, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.Constants.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
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
public class VitalSignClientUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignClientUnitTest.class);
    private static final String owner = VitalSignClientUnitTest.class.getName();
    private GenericTriplestore triplestore;
    private VitalSignClient vitalSignClient;

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
    public void testAddVitalSignForOwner() throws TripleException {
        final String resourceURI = 
                vitalSignClient.addVitalSign(owner,
                                            ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                                            "Free text note for systolic.",
                                            "201006010000",
                                            "100", MM_HG);
        assertNotNull(resourceURI);

        final Iterable<Triple> vitalSigns = vitalSignClient.getVitalSignsForUser(owner);
        int count = 0;
        Set<String> rootIds = new HashSet<String>();
        for (Triple vitalSign : vitalSigns) {
            final String predicate = vitalSign.getPredicate();
            final String value = vitalSign.getValue();
            if (predicate.equals(OWNER)) {
                assertEquals(owner, value);
            }
            
            if (predicate.equals(RDFS_TYPE)) {
                assertEquals(PHRS_VITAL_SIGN_CLASS, value);
            }
            
            if (predicate.equals(CREATE_DATE)) {
                assertNotNull(value);
            }
            
            if (predicate.equals(HL7V3_TEMPLATE_ID_ROOT)) {
                rootIds.add(value);
            }
            
            if (predicate.equals(HL7V3_CODE)) {
                assertEquals(ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE, value);
            }
            
            if (predicate.equals(SKOS_NOTE)) {
                assertEquals("Free text note for systolic.", value);
            }
            
            if (predicate.equals(EFFECTIVE_TIME)) {
                assertEquals("201006010000", value);
            }
            
            if (predicate.equals(HL7V3_VALUE)) {
                assertEquals("100",value);
            }
            
            if (predicate.equals(HL7V3_UNIT)) {
                assertEquals(MM_HG,value);
            }
            
            count++;
        }
        
        final Set<String> expectedRootId = new HashSet<String>();
        // all this three describes a vital sign
        expectedRootId.add(SIMPLE_OBSERVATIONS);
        expectedRootId.add(VITAL_SIGNS_OBSERVATIONS);
        expectedRootId.add(ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT);
        assertEquals(expectedRootId, rootIds);
        
        // the vial sign has 12 tripels, see the documentaion for VitalSignClient
        assertEquals(12,count);
        
        final boolean exists = triplestore.exists(resourceURI);
        assertTrue(exists);
        final Iterable<String> forOwner = 
                triplestore.getForPredicateAndValue(OWNER, owner, ValueType.LITERAL);
        
        count = 0;
        for (String resource : forOwner) {
            count++;
        }
        
        // I expect only one vital sign for this owner
        assertEquals(1,count);
    }
}
