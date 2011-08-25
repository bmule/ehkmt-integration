/*
 * Project :iCardea
 * File : SesameTriplestoreUnitTest.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashMap;
import java.util.Map;


/**
 * This test suite is used to test the <code>SesameTriplestore</code> 
 * functionality.
 * 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 * @see SesameTriplestore
 */
public class SesameTriplestoreUnitTest {

    private static final String SUBJECT = "http://my.base.uri#subj";

    private static final String PREDICATE = "http://my.base.uri#pred";

    private static final String VALUE = "Value";

    private static final String WRONG_VALUE = "ValueXXXX";

    private GenericTriplestore triplestore;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     * 
     * @throws GenericRepositoryException if this occurs then the test 
     * environment may be wrong set.
     */
    @Before
    public void initSiute() throws GenericRepositoryException {
        // The connection factory allows me to configure the connection.
        final TriplestoreConnectionFactory connectionFactory = 
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
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

    /**
     * Adds a triple to the triple store, retrieve it and proves if the stored
     * triple was correct. The new added triple subject URI is specified by 
     * user (manually).
     * 
     * @throws TripleException if this exception occurs then this test it fails.
     * @see SesameTriplestore#persist(java.lang.String, java.lang.String, at.srfg.kmt.ehealth.phrs.persistence.api.ValueType) 
     * @see SesameTriplestore#exists(java.lang.String, java.lang.String, java.lang.String, at.srfg.kmt.ehealth.phrs.persistence.api.ValueType) 
     * @see SesameTriplestore#getForPredicateAndValue(java.lang.String, java.lang.String) 
     */
    @Test
    public void persitExistTest() throws TripleException {

        triplestore.persist(SUBJECT, PREDICATE, VALUE, LITERAL);

        // test the existence for subject, predicat value
        final boolean existsSPV = triplestore.exists(SUBJECT, PREDICATE, VALUE, LITERAL);
        assertTrue(existsSPV);

        // test with wrong value
        final boolean doesNotExistsSPV =
                triplestore.exists(SUBJECT, PREDICATE, WRONG_VALUE, LITERAL);
        assertFalse(doesNotExistsSPV);

        // test the existence for subject and  predicat.
        final boolean existsSP = triplestore.exists(SUBJECT, PREDICATE);
        assertTrue(existsSP);

        // test the existence for only subject.
        final boolean existsS = triplestore.exists(SUBJECT);
        assertTrue(existsS);


        final Iterable<String> forPredicateAndValue =
                triplestore.getForPredicateAndValue(PREDICATE, VALUE, LITERAL);
        final boolean hasNext = forPredicateAndValue.iterator().hasNext();
        assertTrue(hasNext);

        final int count = count(forPredicateAndValue);
        // there is only one tripe with the subject predicat value.
        assertEquals(1, count);

        // I do this because the resoult is already depleat in the count 
        // operation. You need to get a new result. 
        // The result for this is the memory foot print for the result, 
        // the application tries to keep it as small as possible.
        final Iterable<String> forPredicateAndValue1 =
                triplestore.getForPredicateAndValue(PREDICATE, VALUE, LITERAL);
        // the subject for the given predicat and value
        final String getSubject = forPredicateAndValue1.iterator().next();
        assertEquals(SUBJECT, getSubject);
    }

    private int count(Iterable<?> iterable) {
        int count = 0;
        for (Object s : iterable) {
            count++;
        }

        return count;
    }

    @Test
    public void persitExistWithGenURITest() throws TripleException {
        final String subjectURI = triplestore.persist(PREDICATE, VALUE, LITERAL);
        assertNotNull(subjectURI);

        final boolean existsSPV = triplestore.exists(subjectURI, PREDICATE, VALUE, LITERAL);
        assertTrue(existsSPV);

        final boolean doesNotExistsSPV =
                triplestore.exists(subjectURI, PREDICATE, WRONG_VALUE, LITERAL);
        assertFalse(doesNotExistsSPV);

        final boolean existsSP = triplestore.exists(subjectURI, PREDICATE);
        assertTrue(existsSP);

        final boolean existsS = triplestore.exists(subjectURI, PREDICATE);
        assertTrue(existsS);


        final Iterable<String> forPredicateAndValue =
                triplestore.getForPredicateAndValue(PREDICATE, VALUE, LITERAL);
        final boolean hasNext = forPredicateAndValue.iterator().hasNext();
        assertTrue(hasNext);

        // I do this because the resoult is already depleat in the count 
        // operation. You need to get a new result. 
        // The result for this is the memory foot print for the result, 
        // the application tries to keep it as small as possible.
        final Iterable<String> forPredicateAndValue1 =
                triplestore.getForPredicateAndValue(PREDICATE, VALUE, LITERAL);
        // the subject for the given predicat and value
        final String getSubject = forPredicateAndValue1.iterator().next();
        assertEquals(subjectURI, getSubject);

    }

    /**
     * Adds more values to the same resource (multi value node) and retrieve it
     * using the getForPredicatesAndValues(Map) method.
     * 
     * @throws TripleException 
     */
    @Test
    public void multivalueNodeTest() throws TripleException {
        final Map<String, String> toSearch = buildPredicatesValuesMap(3);
        int count = 3;
        // I create a subject, on this I add n palue object 
        final String subject = SUBJECT + "MultivalueNode";
        addNTriples(subject, count);
        final String otherSubject = SUBJECT + "Other";

        // add other triple, the triplestore contains now 2 resources
        // one is multi value and the other has a sigular value.
        triplestore.persist(otherSubject, PREDICATE + "X", VALUE + "X", LITERAL);

        final Iterable<String> subjects =
                triplestore.getForPredicatesAndValues(toSearch);

        final int subjectsCount = count(subjects);
        // I expect only one subject (with the given multivalue)
        assertEquals(1, subjectsCount);

        // I do this because the resoult is already depleat in the count 
        // operation. You need to get a new result. 
        // The result for this is the memory foot print for the result, 
        // the application tries to keep it as small as possible.
        final Iterable<String> subjects1 =
                triplestore.getForPredicatesAndValues(toSearch);

        final String getSubject = subjects1.iterator().next();
        assertEquals(subject, getSubject);

    }

    private Map<String, String> buildPredicatesValuesMap(int count) {
        final Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < count; i++) {
            result.put(PREDICATE + i, VALUE + i);
        }

        return result;
    }

    private void addNTriples(String subject, int count) throws TripleException {
        for (int i = 0; i < count; i++) {
            triplestore.persist(subject, PREDICATE + i, VALUE + i, LITERAL);
        }
    }

    @Test
    public void getSinglePredicatValue() throws TripleException {
        triplestore.persist(SUBJECT, PREDICATE, VALUE, LITERAL);

        final Iterable<Triple> values = triplestore.getForSubject(SUBJECT);
        final int subjectsCount = count(values);

        // I expect only one subject
        assertEquals(1, subjectsCount);

        // I do this because the resoult is already depleat in the count 
        // operation. You need to get a new result. 
        // The result for this is the memory foot print for the result, 
        // the application tries to keep it as small as possible.
        final Iterable<Triple> values1 = triplestore.getForSubject(SUBJECT);

        final Triple next = values1.iterator().next();
        final String predicate = next.getPredicate();
        assertEquals(PREDICATE, predicate);

        final String value = next.getValue();
        assertEquals(VALUE, value);
    }

    @Test
    public void getMultiPredicatValue() throws TripleException {
        int count = 3;
        // I create a subject, on this I add n palue object 
        final String subject = SUBJECT;
        addNTriples(subject, count);

        final Iterable<Triple> values = triplestore.getForSubject(SUBJECT);
        final int valuesCount = count(values);
        assertEquals(3, valuesCount);
    }

    @Test
    public void removeSimpleNodeBySPV() throws TripleException {
        triplestore.persist(SUBJECT, PREDICATE, VALUE, LITERAL);

        final Iterable<Triple> values = triplestore.getForSubject(SUBJECT);
        final int subjectsCount = count(values);

        // I expect only one subject
        assertEquals(1, subjectsCount);

        triplestore.delete(SUBJECT, SUBJECT, VALUE, LITERAL);

        final boolean exists =
                triplestore.exists(SUBJECT, SUBJECT, VALUE, LITERAL);
        assertFalse(exists);
    }

    @Test
    public void removeSimpleNodeBySP() throws TripleException {
        triplestore.persist(SUBJECT, PREDICATE, VALUE, LITERAL);

        final Iterable<Triple> values = triplestore.getForSubject(SUBJECT);
        final int subjectsCount = count(values);

        // I expect only one subject
        assertEquals(1, subjectsCount);

        triplestore.delete(SUBJECT, PREDICATE);
        final boolean exists =
                triplestore.exists(SUBJECT, SUBJECT, VALUE, LITERAL);
        assertFalse(exists);

    }

    @Test
    public void removeSimpleNodeByS() throws TripleException {
        triplestore.persist(SUBJECT, PREDICATE, VALUE, LITERAL);
        final Iterable<Triple> values = triplestore.getForSubject(SUBJECT);
        final int subjectsCount = count(values);

        // I expect only one subject
        assertEquals(1, subjectsCount);

        triplestore.deleteForSubject(SUBJECT);

        final boolean exists =
                triplestore.exists(SUBJECT, SUBJECT, VALUE, LITERAL);
        assertFalse(exists);
    }
}
