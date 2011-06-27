/*
 * Project :iCardea
 * File : StringResult.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import java.util.Iterator;
import org.openrdf.model.Statement;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class StatementSubjectIterableResult implements Iterable<String> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.RepositoryIterableResult</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(StatementSubjectIterableResult.class);

    private final Iterator<String> iterableStrings;

    public StatementSubjectIterableResult(RepositoryResult<Statement> result) {
        iterableStrings = new SubjectIterator(result);
    }

    @Override
    public Iterator<String> iterator() {
        return iterableStrings;
    }

    private final class SubjectIterator implements Iterator<String> {

        private final RepositoryResult<Statement> result;

        private SubjectIterator(RepositoryResult<Statement> result) {
            this.result = result;
        }

        @Override
        public boolean hasNext() {
            try {
                return result.hasNext();
            } catch (RepositoryException exception) {
                LOGGER.error(exception.getMessage(), exception);
                final IllegalStateException illegalException =
                        new IllegalStateException(exception);
                throw illegalException;
            }
        }

        @Override
        public String next() {
            final Statement next;
            try {
                next = result.next();
            } catch (RepositoryException exception) {
                LOGGER.error(exception.getMessage(), exception);
                final IllegalStateException illegalException =
                        new IllegalStateException(exception);
                throw illegalException;
            }

            final String stringValue = next.getSubject().stringValue();
            return stringValue;
        }

        @Override
        public void remove() {
            try {
                result.remove();
            } catch (RepositoryException exception) {
                LOGGER.error(exception.getMessage(), exception);
                final IllegalStateException illegalException =
                        new IllegalStateException(exception);
                throw illegalException;
            }
        }
    }
}
