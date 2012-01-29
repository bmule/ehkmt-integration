/*
 * Project :iCardea
 * File : PHRSRequestActor.java
 * Encoding : UTF-8
 * Date : Jan 29, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;



import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.LITERAL;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.RESOURCE;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class PHRSRequestClient {

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    public PHRSRequestClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    public String addPHRSRequest(String replyAddress, String id,
            String careProcisionCode) throws TripleException {
        final String subject =
                triplestore.persist(Constants.RDFS_TYPE,
                Constants.PHRS_REQUEST_CLASS,
                RESOURCE);

        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_REPLY_ADRESS,
                replyAddress,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_CARE_PROVISION_CODE,
                careProcisionCode,
                LITERAL);

        triplestore.persist(subject,
                Constants.PHRS_ACTOR_PROTOCOL_ID,
                id,
                LITERAL);
        
        return subject;
    }

    public Iterable<String> getAllPHRSRequests() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }

    public Iterable<Triple> getAllPHRSRequestsTriples() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final MultiIterable result = new MultiIterable();
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }
        
        return result;
    }

    public Iterable<String> getAllPHRSRequests(String replyAddress, String id,
            String careProcisionCode) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);
        queryMap.put(Constants.HL7V3_CARE_PROVISION_CODE, careProcisionCode);
        queryMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, id);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }

    public Iterable<String> getAllPHRSRequests(String replyAddress) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }
    
    public Iterable<String> removeAllPHRSRequests() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : result) {
            triplestore.deleteForSubject(resource);
        }
                
        return result;
    }

    public Iterable<String> removeAllPHRSRequests(String replyAddress, String id,
            String careProcisionCode) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);
        queryMap.put(Constants.HL7V3_CARE_PROVISION_CODE, careProcisionCode);
        queryMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, id);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : result) {
            triplestore.deleteForSubject(resource);
        }
                
        return result;
    }
}
