/*
 * Project :iCardea
 * File : TermClient.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.Constants.SKOS_RELATED;

import at.srfg.kmt.ehealth.phrs.dataexchange.util.StoreValidator;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.RESOURCE;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.QueryUtil;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class TermClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.TermClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TermClient.class);

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    public TermClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    public Iterable<Triple> getTermsRelatedWith(String resourceURI) throws TripleException {
        final boolean isValidURI = StoreValidator.isValidReourceValue(resourceURI);
        if (!StoreValidator.isValidReourceValue(resourceURI)) {
            final IllegalArgumentException exception =
                    StoreValidator.buildWrongReourceValueException("resourceURI", resourceURI);
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(SKOS_RELATED, resourceURI, RESOURCE);

        final MultiIterable result = new MultiIterable();
        for (String resource : resources) {
            final Iterable<Triple> triples = triplestore.getForSubject(resource);
            result.addIterable(triples);
        }

        return result;
    }

    /**
     * Gets a certain term for a given code and code system code. 
     * The underlying persistence model can contain only one term for a given 
     * code system - code pair.
     *
     * @param code the code in the given code system, it can not be null. 
     * It can not be null.
     * @param codeSystemCode the code system code, it can not be null. 
     * It can not be null.
     * @return a certain term for a given code and code system code or null if
     * the underlying code system does not contains a term for the given 
     * code/code-system.
     * @throws NullPointerException if the code or codeSystem arguments are null
     * or empty string.
     */
    public String getTermURI(String code, String codeSystemCode)
            throws ClientException {

        if (code == null) {
            final NullPointerException exception =
                    new NullPointerException("The code argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (codeSystemCode == null) {
            final NullPointerException exception =
                    new NullPointerException("The codeSystemCode argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final String queryStr = buildQuery(code, codeSystemCode);

        try {
            final Object query = triplestore.getForQuery(queryStr);
            final Iterable<String> itrableForQuery = QueryUtil.getItrableForQuery(query);
            if (!itrableForQuery.iterator().hasNext()) {
                return null;
            }

            // TODO : prove for more values

            final String result = itrableForQuery.iterator().next();
            return result;

        } catch (GenericQueryException genericQueryException) {
            LOGGER.error("The Query {} can not be executed.", queryStr);
            LOGGER.error(genericQueryException.getMessage(), genericQueryException);
            final ClientException exception =
                    new ClientException(genericQueryException);
            throw exception;
        } catch (GenericRepositoryException genericRepositoryException) {
            LOGGER.error("The Query {} can not be executed.", queryStr);
            LOGGER.error(genericRepositoryException.getMessage(), genericRepositoryException);
            final ClientException exception =
                    new ClientException(genericRepositoryException);
            throw exception;
        }
    }
    
    private String buildQuery(String code, String codeSystemCode) {
        return " select subject "
                + "from {subject} <http://www.icardea.at/phrs/code#code> {code} "
                + " <http://www.icardea.at/phrs/code#codeSystem> {codeSystem}  "
                + " <http://www.icardea.at/phrs/codeSystem#codeSystemCode> {codeSystemCode}, "
                + " {subject} <http://www.icardea.at/phrs/code#code> {code} "
                + " <http://www.icardea.at/phrs/code#codeValue> {codeValue} "
                + " WHERE codeSystemCode = \"" + codeSystemCode + "\" AND"
                + " codeValue = \"" + code + "\"";
    }
//    private String buildQuery() {
//        return " select subject "
//                + "from {subject} <http://www.icardea.at/phrs/code#code> {code} "
//                + " <http://www.icardea.at/phrs/code#codeSystem> {codeSystem}  "
//                + " <http://www.icardea.at/phrs/codeSystem#codeSystemCode> {codeSystemCode}, "
//                + " {subject} <http://www.icardea.at/phrs/code#code> {code} "
//                + " <http://www.icardea.at/phrs/code#codeValue> {codeValue} "
//                + " WHERE codeSystemCode = \"2.16.840.1.113883.6.86\" AND"
//                + " codeValue = \"C0423797\"";
//    }
}
