/*
 * Project :iCardea
 * File : ItterableResult.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import java.util.Iterator;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
final class TripleIterableResult implements Iterable<Triple> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.TripleIterableResult</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TripleIterableResult.class);

    private final Iterator<Triple> iterableTriples;

    public TripleIterableResult(RepositoryResult<Statement> iterableStatements) {
        iterableTriples = new StringIterator(iterableStatements);
    }

    @Override
    public Iterator<Triple> iterator() {
        return iterableTriples;
    }

    private final class StringIterator implements Iterator<Triple> {

        private final RepositoryResult<Statement> iterableStatements;

        private StringIterator() {
            this(null);
        }

        private StringIterator(RepositoryResult<Statement> iterableStatements) {
            this.iterableStatements = iterableStatements;
        }

        @Override
        public boolean hasNext() {

            if (iterableStatements == null) {
                return false;
            }

            try {
                return iterableStatements.hasNext();
            } catch (RepositoryException repositoryException) {
                LOGGER.error(repositoryException.getMessage(), repositoryException);
                final IllegalStateException illegalException =
                        new IllegalStateException(repositoryException);
                throw illegalException;
            }
        }

        @Override
        public Triple next() {

            if (iterableStatements == null) {
                return null;
            }

            final Statement statement;
            try {
                statement = iterableStatements.next();
                final Resource subject = statement.getSubject();
                final URI predicate = statement.getPredicate();
                final Value object = statement.getObject();
                final Triple result = 
                        new Triple(subject.stringValue(), 
                        predicate.stringValue(), 
                        object == null ? null : object.stringValue());
                return result;
            } catch (RepositoryException repositoryException) {
                LOGGER.error(repositoryException.getMessage(), repositoryException);
                final IllegalStateException illegalException =
                        new IllegalStateException(repositoryException);
                throw illegalException;
            }
        }

        @Override
        public void remove() {

            if (iterableStatements == null) {
                return;
            }

            try {
                iterableStatements.remove();
            } catch (RepositoryException repositoryException) {
                LOGGER.error(repositoryException.getMessage(), repositoryException);
                final IllegalStateException illegalException =
                        new IllegalStateException(repositoryException);
                throw illegalException;
            }
        }
    }
}
