/*
 * Project :iCardea
 * File : QueryUtil.java
 * Encoding : UTF-8
 * Date : Mar 5, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import java.util.List;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Collection of methods related with the Sesame Query. <br/>
 * This class is not designed to be extend.
 * 
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class QueryUtil {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.QueryUtil</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QueryUtil.class);

    /**
     * Don't let anyone to instantiate this class.
     */
    private QueryUtil() {
        // UNIMPLEMENTED
    }

    /**
     * Transforms a given TupleQueryResult in to a Iterable.
     * 
     * @param queryResult the TupleQueryResult to be tranformed.
     * @return 
     */
    public static Iterable<String> getItrableForQuery(Object queryResult) {
        
        if (queryResult == null) {
            final NullPointerException exception = 
                    new NullPointerException("The queryResult can not be null.");
                        LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        
        final boolean isTupleQueryResult = queryResult instanceof TupleQueryResult;
        
        if (!isTupleQueryResult) {
            final ClassCastException exception = 
                    new ClassCastException("The queryResult must be from type org.openrdf.query.TupleQueryResult.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        
        final TupleQueryResult qResult = (TupleQueryResult) queryResult;
        final List<String> bindingNames = qResult.getBindingNames();
        if (bindingNames.size() != 1) {
            final String msg =
                    String.format("Only one binding name expected, the actaul bind name list is %s",
                    bindingNames.toString());
            final IllegalStateException illegalStateException =
                    new IllegalStateException(msg);
            LOGGER.error(msg, illegalStateException);
            throw illegalStateException;
        }

        final String bindName = bindingNames.get(0);
        final TupleQueryAndBindingNameIterableResult result =
                new TupleQueryAndBindingNameIterableResult(qResult, bindName);

        return result;

    }
}
