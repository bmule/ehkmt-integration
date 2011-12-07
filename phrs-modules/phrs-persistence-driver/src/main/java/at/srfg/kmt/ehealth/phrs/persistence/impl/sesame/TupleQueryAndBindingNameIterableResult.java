/*
 * Project :iCardea
 * File : ItterableResult.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import java.util.Iterator;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
final class TupleQueryAndBindingNameIterableResult implements Iterable<String> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.TupleQueryIterableResult</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TupleQueryAndBindingNameIterableResult.class);

    private final Iterator<String> iterableStrings;

    public TupleQueryAndBindingNameIterableResult(TupleQueryResult result, String bindName) {
        iterableStrings = new StringIterator(result, bindName);
    }

    @Override
    public Iterator<String> iterator() {
        return iterableStrings;
    }

    private final class StringIterator implements Iterator<String> {

        private final String bindName;

        private final TupleQueryResult result;

        private StringIterator(TupleQueryResult result, String bindName) {
            this.bindName = bindName;
            this.result = result;
        }

        @Override
        public boolean hasNext() {
            try {
                return result.hasNext();
            } catch (QueryEvaluationException exception) {
                LOGGER.error(exception.getMessage(), exception);
                final IllegalStateException illegalException =
                        new IllegalStateException(exception);
                throw illegalException;
            }
        }

        @Override
        public String next() {
            final BindingSet next;
            try {
                next = result.next();
            } catch (QueryEvaluationException exception) {
                LOGGER.error(exception.getMessage(), exception);
                final IllegalStateException illegalException =
                        new IllegalStateException(exception);
                throw illegalException;
            }

            if (!next.hasBinding(bindName)) {
                return null;
            }

            final Binding subjBinding = next.getBinding(bindName);
            final String stringValue = subjBinding.getValue().stringValue();
            return stringValue;
        }

        @Override
        public void remove() {
            final UnsupportedOperationException exception =
                    new UnsupportedOperationException("Remove is nut supprted.");
            throw exception;
        }
    }
}
