/*
 * Project :iCardea
 * File : SesameTriplestore.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.PathElement;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.Constants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class SesameTriplestore
        implements GenericTriplestore, GenericTriplestoreLifecycle {

    private final RepositoryConnection connection;

    private final ValueFactory valueFactory;

    private final List<Runnable> postconst;

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SesameTriplestore.class);

    public SesameTriplestore() throws GenericRepositoryException {
        connection = getConnection(null);
        valueFactory = connection.getValueFactory();
        postconstruct();
        postconst = new ArrayList<Runnable>();
    }

    public SesameTriplestore(Configuration configuration) throws GenericRepositoryException {
        connection = getConnection(configuration);
        valueFactory = connection.getValueFactory();
        postconst = new ArrayList<Runnable>();
    }

    public SesameTriplestore(File configuration) throws GenericRepositoryException {
        this((Configuration) null);
    }

    public SesameTriplestore(String filePath) throws GenericRepositoryException {
        this((Configuration) null);
    }

    public SesameTriplestore(InputStream inputStream) throws GenericRepositoryException {
        this((Configuration) null);
    }

    @Override
    public void init() {
        injectConnection();
        for (Runnable r : postconst) {
            r.run();
        }
    }

    /**
     * 
     */
    private void injectConnection() {
        for (Runnable r : postconst) {
            final Class<? extends Runnable> clazz = r.getClass();
            try {
                final Field declaredField = clazz.getDeclaredField("connection");
                declaredField.set(r, connection);
            } catch (Exception ex) {
                LOGGER.warn("No connection filed found for the class" + clazz.getName());
            }
        }
    }

    private void postconstruct() {
    }

    @Override
    public void shutdown() throws GenericRepositoryException {
        try {
            connection.close();
            connection.getRepository().shutDown();
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            throw new GenericRepositoryException(repositoryException);
        }
    }

    @Override
    public void addToPostconstruct(Runnable runnable) {
        postconst.add(runnable);
    }

    @Override
    public void addToPredestroy(Runnable runnable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cleanEnvironment() throws GenericRepositoryException {
        final File dataDir = connection.getRepository().getDataDir();

        if (dataDir == null) {
            return;
        }

        if (!dataDir.exists() && !dataDir.isDirectory()) {
            return;
        }
        try {
            FileUtils.deleteDirectory(dataDir);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new GenericRepositoryException(ex);
        }

        LOGGER.debug("Clean the Data Direcotry " + dataDir.getAbsolutePath());
    }

    private RepositoryConnection getConnection(Configuration configuration)
            throws GenericRepositoryException {
        // FIXME : do this via the configuration 
        final File dataDir = new File("/tmp/iCardea/triplestore/sasame");
        final Repository repository = new SailRepository(new MemoryStore(dataDir));
        try {
            repository.initialize();
            final RepositoryConnection result = repository.getConnection();
            return result;
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            throw new GenericRepositoryException(repositoryException);
        }
    }

    @Override
    public void persist(String subject, String predicate, String value, ValueType valueType)
            throws TripleException {

        final Statement statement = getStatement(subject, predicate, value, valueType);

        try {
            connection.add(statement);
            //connection.add(subjectURI,predicateURI,val);
        } catch (Exception repException) {
            LOGGER.error(repException.getMessage(), repException);
            final TripleException tripleException =
                    new TripleException(repException);
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;
        }
    }

    private Statement getStatement(String subject, String predicate, String value,
            ValueType valueType) {
        final URI subjectURI = valueFactory.createURI(subject);
        final URI predicateURI = valueFactory.createURI(predicate);

        final Value val = getValue(value, valueType);

        final Statement statement =
                valueFactory.createStatement(subjectURI, predicateURI, val);
        return statement;
    }

    private Value getValue(String value, ValueType valueType) {
        final Value result;

        switch (valueType) {
            case LITERAL:
                result = valueFactory.createLiteral(value);
                break;
            case RESOURCE:
                result = valueFactory.createLiteral(value);
                break;
            case B_NODE:
                result = valueFactory.createBNode();
                break;
            default:
                String msg = String.format("The type %s is not supported", valueType);
                final IllegalArgumentException exception =
                        new IllegalArgumentException(msg);
                LOGGER.error(msg, exception);
                throw exception;
        }

        return result;
    }

    @Override
    public String persist(String predicate, String value, ValueType valueType) throws TripleException {
        final URI subjectURI = getUUIDURI(Constants.ICARDEA_NS);
        final URI predicateURI = valueFactory.createURI(predicate);
        final Value val = getValue(value, valueType);
        final Statement statement =
                valueFactory.createStatement(subjectURI, predicateURI, val);

        try {
            connection.add(statement);
            //connection.add(subjectURI,predicateURI,val);
        } catch (Exception repException) {
            LOGGER.error(repException.getMessage(), repException);
            final TripleException tripleException =
                    new TripleException(repException);
            tripleException.setSubject(subjectURI.stringValue());
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;
        }

        return subjectURI.stringValue();
    }

    private URI getUUIDURI(String base) {
        final UUID id = UUID.randomUUID();

        final StringBuilder result = new StringBuilder(base);
        result.append('/');
        result.append(id);

        return valueFactory.createURI(result.toString());
    }

    @Override
    public boolean exists(String subject, String predicate, String value, ValueType type) {
        try {
            final Statement statement =
                    getStatement(subject, predicate, value, type);
            final boolean hasStatement = connection.hasStatement(statement, true);
            return hasStatement;
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            return false;
        }
    }

    @Override
    public final boolean exists(String subject, String predicate) {
        final URI subjectURI = valueFactory.createURI(subject);

        final URI predicateURI = predicate == null
                ? null
                : valueFactory.createURI(predicate);

        try {
            final boolean hasStatement =
                    connection.hasStatement(subjectURI, predicateURI, null, false);
            return hasStatement;
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            return false;
        }
    }

    @Override
    public boolean exists(String subject) {
        return exists(subject, null);
    }

    @Override
    public boolean exists(List<PathElement> path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<String> getForPredicateAndValue(String predicate, String value, ValueType valueType)
            throws TripleException {

        final URI predicateURI = valueFactory.createURI(predicate);
        final Value val = getValue(value, valueType);
        try {
            // wild card on subject
            final RepositoryResult<Statement> statements =
                    connection.getStatements(null, predicateURI, val, false);
            final StatementSubjectIterableResult result =
                    new StatementSubjectIterableResult(statements);
            return result;
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            final TripleException tripleException =
                    new TripleException(repositoryException);
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            throw tripleException;
        }
    }
//    @Override
//    public Iterable<String> getForPredicateAndValue(String predicate, String value)
//            throws TripleException {
//        final String query = buildPredicateAndValueQuery(predicate, value);
//        LOGGER.debug("Excute quirey : " + query);
//
//        try {
//            final TupleQuery tupleQuery =
//                    connection.prepareTupleQuery(QueryLanguage.SERQL, query);
//            final TupleQueryResult evaluate = tupleQuery.evaluate();
//            final List<String> bindingNames = evaluate.getBindingNames();
//            if (bindingNames.size() != 1) {
//                final String msg = String.format("Only one binding name expected, the actaul bind name list is %s",
//                        bindingNames.toString());
//                final IllegalStateException illegalStateException =
//                        new IllegalStateException(msg);
//                LOGGER.error(msg, illegalStateException);
//                throw illegalStateException;
//            }
//
//            final String subject = bindingNames.get(0);
//
//            while (evaluate.hasNext()) {
//                final BindingSet next = evaluate.next();
//                final Binding subjBinding = next.getBinding(subject);
//                if (subjBinding == null) {
//                    final String msg = 
//                            String.format("The binding % must exist.", subject);
//                    final IllegalStateException illegalStateException =
//                            new IllegalStateException(msg);
//                    LOGGER.error(msg, illegalStateException);
//                    throw illegalStateException;
//                }
//            }
//        } catch (Exception exception) {
//            LOGGER.debug(exception.getMessage(), exception);
//            final TripleException tripleException = new TripleException(exception);
//            tripleException.setPredicate(predicate);
//            tripleException.setValue(value);
//            throw tripleException;
//        }
//
//        return null;
//    }

    @Override
    public Iterable<Triple> getForSubject(String subject) throws TripleException {
        final URI subjectURI = valueFactory.createURI(subject);
        try {
            final RepositoryResult<Statement> statements =
                    connection.getStatements(subjectURI, null, null, false);
            final TripleIterableResult result =
                    new TripleIterableResult(statements);
            return result;
        } catch (RepositoryException repException) {
            LOGGER.error(repException.getMessage(), repException);
            final TripleException tripleException =
                    new TripleException(repException);
            tripleException.setSubject(subject);
            throw tripleException;
        }
    }

    private String buildPredicateAndValueQuery(String predicate, String value) {
        final StringBuilder result = new StringBuilder();
        result.append("select subject from {subject} ");
        result.append(uriForSERQL(predicate));
        result.append(" {value} where value LIKE ");
        result.append("\"");
        result.append(value);
        result.append("\"");

        return result.toString();
    }

    private String buildMultivalueNodeQuery(Map<String, String> perdicatesAndValues) {


        final StringBuilder result = new StringBuilder();
        final StringBuilder where = new StringBuilder();
        result.append("select subject from ");

        int count = 0;
        for (Map.Entry<String, String> entry : perdicatesAndValues.entrySet()) {
            final String predicate = entry.getKey();
            final String value = entry.getValue();
            // "{subject} pred1 {obj1}, ");
            result.append("{subject} ");
            result.append(uriForSERQL(predicate));
            result.append(" {value_");
            result.append(count);
            result.append("}, ");

            where.append(" value_");
            where.append(count);
            where.append(" like ");
            where.append("\"");
            where.append(value);
            where.append("\"");
            where.append(" AND ");

            count++;

        }
        result.deleteCharAt(result.lastIndexOf(","));
        where.delete(where.lastIndexOf("AND"), where.length());

        result.append(" where ");
        result.append(where);

        return result.toString();
    }

    private String uriForSERQL(String uri) {
        final StringBuilder result = new StringBuilder();
        result.append('<');
        result.append(uri);
        result.append('>');

        return result.toString();
    }

    @Override
    public Iterable<String> getForPredicatesAndValues(Map<String, String> predicatesValues)
            throws TripleException {
        final String query = buildMultivalueNodeQuery(predicatesValues);
        LOGGER.debug("Excute quirey : " + query);

        try {
            final TupleQuery tupleQuery =
                    connection.prepareTupleQuery(QueryLanguage.SERQL, query);
            final TupleQueryResult queryResult = tupleQuery.evaluate();
            final List<String> bindingNames = queryResult.getBindingNames();

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
                    new TupleQueryAndBindingNameIterableResult(queryResult, bindName);

            return result;
        } catch (Exception exception) {
            LOGGER.debug(exception.getMessage(), exception);
            final TripleException tripleException = new TripleException(exception);
            throw tripleException;
        }
    }

    @Override
    public Iterable<String> getForSubjectAndPredicate(String subject, String predicate) throws TripleException {
        final URI subjectURI = valueFactory.createURI(subject);
        final URI predicateURI = valueFactory.createURI(predicate);
        try {
            final RepositoryResult<Statement> statements =
                    connection.getStatements(subjectURI, predicateURI, null, true);
            
            return null;

        } catch (Exception exception) {
            LOGGER.debug(exception.getMessage(), exception);
            final TripleException tripleException = new TripleException(exception);
            throw tripleException;
        }
    }

    @Override
    public TupleQueryResult getForQuery(String query) throws GenericRepositoryException, GenericQueryException {

        try {
            final TupleQuery tupleQuery =
                    connection.prepareTupleQuery(QueryLanguage.SERQL, query);
            final TupleQueryResult result = tupleQuery.evaluate();
            return result;
        } catch (RepositoryException repositoryException) {
            final GenericRepositoryException repEx =
                    new GenericRepositoryException(repositoryException);
            LOGGER.error(repositoryException.getMessage(), repositoryException);

            throw repEx;
        } catch (MalformedQueryException malformedQueryException) {
            final GenericQueryException genericQException =
                    new GenericQueryException(malformedQueryException);
            LOGGER.error(malformedQueryException.getMessage(),
                    malformedQueryException);

            throw genericQException;
        } catch (QueryEvaluationException queryEvaluationException) {
            final GenericQueryException genericQException =
                    new GenericQueryException(queryEvaluationException);
            LOGGER.error(queryEvaluationException.getMessage(),
                    queryEvaluationException);

            throw genericQException;
        }
    }

    @Override
    public Iterable<Triple> getForPath(List<PathElement> path) throws TripleException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//

    @Override
    public void delete(List<PathElement> path) throws TripleException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String subject, String predicate, String value, ValueType valueType) throws TripleException {
        final Statement statement = getStatement(subject, predicate, value, valueType);
        try {
            connection.remove(statement);
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            final TripleException tripleException =
                    new TripleException();
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;
        }
    }

    @Override
    public void delete(String subject, String predicate) throws TripleException {
        final URI subjectURI = valueFactory.createURI(subject);
        final URI predicateURI = predicate == null ? null : valueFactory.createURI(predicate);
        try {
            final RepositoryResult<Statement> statements =
                    connection.getStatements(subjectURI, predicateURI, null, false);
            connection.remove(statements);
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getLocalizedMessage(), repositoryException);
            final TripleException tripleException = new TripleException();
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            throw tripleException;
        }
    }

    @Override
    public void deleteForSubject(String subject) throws TripleException {
        delete(subject, null);
    }
}
