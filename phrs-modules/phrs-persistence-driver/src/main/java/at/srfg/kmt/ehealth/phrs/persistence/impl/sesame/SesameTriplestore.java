/*
 * Project :iCardea
 * File : SesameTriplestore.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
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
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
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

    public static final String TEMP_DIR = "${TEMP_DIR}";

    public static final String HOME_DIR = "${HOME_DIR}";

    private static final String DEFAULT_FILE_DUMP = getDefaultDumpDirectory();

    private final RepositoryConnection connection;

    private final ValueFactory valueFactory;

    private final List<Runnable> postconst;

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SesameTriplestore.class);

    /**
     * Builds a
     * <code>SesameTriplestore</code> instance using the default configuration.
     * The default configuration implies : <ul> <li> The triplestore is a memory
     * seal <li> The dump file for this memory sail is defined with the
     * <code>DEFAULT_FILE_DUMP</code> constant. </ul>
     *
     * @throws GenericRepositoryException by any (triplestore) repository
     * related exception.
     * @see #DEFAULT_FILE_DUMP
     * @see #getDefaultDumpDirectory()
     */
    public SesameTriplestore() throws GenericRepositoryException {
        connection = getMemoryStoreConnection(null);
        valueFactory = connection.getValueFactory();
        postconstruct();
        postconst = new ArrayList<Runnable>();
    }

    public SesameTriplestore(Configuration configuration) throws GenericRepositoryException {
        connection = getRepositoryConnection(configuration);
        valueFactory = connection.getValueFactory();
        postconst = new ArrayList<Runnable>();
    }

    private RepositoryConnection getRepositoryConnection(Configuration configuration)
            throws GenericRepositoryException {

        if (configuration == null) {
            return getMemoryStoreConnection(null);
        }

        final String fileDump = configuration.getString("memorysail.filedump");
        if (fileDump != null) {
            return getMemoryStoreConnection(configuration);
        }

        final String remoteURI = configuration.getString("remote.uri");
        if (remoteURI != null) {
            return getRemoteConnection(configuration);
        }

        final String msg = String.format("This configuration {} is illegal.", configuration);
        final IllegalArgumentException exception =
                new IllegalArgumentException(msg);
        LOGGER.debug(exception.getMessage(), exception);
        throw exception;

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
            LOGGER.debug("On env to be clean - up");
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

    private RepositoryConnection getMemoryStoreConnection(Configuration configuration)
            throws GenericRepositoryException {

        LOGGER.debug("Builds MemoryStore for this configuration {}", configuration);

        final String confDataDirStr = configuration == null
                ? DEFAULT_FILE_DUMP
                : configuration.getString("memorysail.filedump") == null
                ? DEFAULT_FILE_DUMP
                : configuration.getString("memorysail.filedump");

        final String dumpDirStr = solveVars(confDataDirStr);
        LOGGER.debug("The actual data dump directory is {}", dumpDirStr);

        final File dataDir = new File(dumpDirStr);
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

    private RepositoryConnection getRemoteConnection(Configuration configuration)
            throws GenericRepositoryException {
        if (configuration == null) {
            final NullPointerException exception =
                    new NullPointerException("The connection can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        LOGGER.debug("Builds HTTPRepository for this configuration {}", configuration);

        final String uri = configuration.getString("remote.uri");
        if (uri == null) {
            final NullPointerException exception =
                    new NullPointerException("The configuration must contains a remote.uri property");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final String repositoryID = configuration.getString("remote.repository-id");
        if (repositoryID == null) {
            final NullPointerException exception =
                    new NullPointerException("The configuration must contains a remote.repository-id");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }



        LOGGER.debug("Tries to init the reposiotry with ID = {} localted on URI = {}", repositoryID, uri);
        final Repository repository = new HTTPRepository(uri, repositoryID);
        try {
            repository.initialize();
            final RepositoryConnection result = repository.getConnection();
            LOGGER.debug("The Reposiotry with ID = {} localted on URI = {} is initialized.", repositoryID, uri);
            return result;
        } catch (RepositoryException repositoryException) {
            LOGGER.error(repositoryException.getMessage(), repositoryException);
            throw new GenericRepositoryException(repositoryException);
        }


    }

    /**
     *
     * @param input
     * @return
     */
    private String solveVars(String input) {
        if (!input.startsWith(TEMP_DIR) && !input.startsWith(HOME_DIR)) {
            return input;
        }

        final StringBuilder in = new StringBuilder(input);
        in.delete(0, 12);

        final StringBuilder result = new StringBuilder();
        final String dir = input.startsWith(TEMP_DIR)
                ? System.getProperty("java.io.tmpdir")
                : System.getProperty("user.home");

        result.append(dir);
        if (!dir.endsWith(File.separator)) {
            result.append(File.separator);
        }
        result.append(in);


        return result.toString();
    }

    /**
     * Persists the relation between the a subject, its predicate and the
     * predicate value.
     *
     * @param subject the resources (the subject), it must be an URI and it can
     * not be null.
     * @param predicate the predicate for the given resource, it can not be
     * null.
     * @param value the value for the predicate for the given resource, it can
     * not be null.
     * @param valueType the value type, it can not be null.
     * @throws TripleException by any triplestore related exception.
     * @throws NullPointerException if and of the algorithms are null.
     */
    @Override
    public void persist(String subject, String predicate, String value, ValueType valueType)
            throws TripleException {

        final Object[] toLog = {subject, predicate, value, valueType};
        LOGGER.debug("Tries to persists Subject : {}, predicate : {}, value {} and value type {}", toLog);

        if (subject == null) {
            final NullPointerException exception =
                    new NullPointerException("The subject argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (predicate == null) {
            final NullPointerException exception =
                    new NullPointerException("The predicate argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (value == null) {
            final NullPointerException exception =
                    new NullPointerException("The value argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (valueType == null) {
            final NullPointerException exception =
                    new NullPointerException("The valueType argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        try {
            final Statement statement = getStatement(subject, predicate, value, valueType);
            connection.add(statement);
            //connection.add(subjectURI,predicateURI,val);
        } catch (AssertionError repException) {
            LOGGER.error(repException.getMessage(), repException);
            final TripleException tripleException =
                    new TripleException(repException);
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;
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

        final Object[] toLogFinal = {subject, predicate, value, valueType};
        LOGGER.debug("The subject : {}, predicate : {}, value {} and value type {} was persisted", toLog);

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

    /**
     * Persists the relation between the a given subject, its predicate and the
     * predicate value.
     *
     * @param predicate the predicate for the given resource, it can not be
     * null.
     * @param value the value for the predicate for the given resource, it can
     * not be null.
     * @param valueType the value type, it can not be null.
     * @throws TripleException by any triplestore related exception.
     * @throws NullPointerException if and of the algorithms are null.
     */
    @Override
    public String persist(String predicate, String value, ValueType valueType) throws TripleException {

        final Object[] toLog = {predicate, value, valueType};
        LOGGER.debug("Tries to persists Subject : {}, predicate : {}, value {} and value type {}", toLog);

        if (predicate == null) {
            final NullPointerException exception =
                    new NullPointerException("The predicate argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (value == null) {
            final NullPointerException exception =
                    new NullPointerException("The value argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (valueType == null) {
            final NullPointerException exception =
                    new NullPointerException("The valueType argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final URI subjectURI = getUUIDURI(Constants.ICARDEA_NS);
        final URI predicateURI = valueFactory.createURI(predicate);
        final Value val = getValue(value, valueType);

        try {
            final Statement statement =
                    valueFactory.createStatement(subjectURI, predicateURI, val);
            connection.add(statement);
            //connection.add(subjectURI,predicateURI,val);
        } catch (AssertionError repException) {
            LOGGER.error(repException.getMessage(), repException);
            final TripleException tripleException =
                    new TripleException(repException);
            tripleException.setSubject(subjectURI.toString());
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;

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

        final String result = subjectURI.stringValue();

        final Object[] toLogFinal = {predicate, value, valueType, result};
        LOGGER.debug("The subject : {}, predicate : {}, value {} and value type {} was persisted unde the subject : {}", toLogFinal);
        return result;
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
        LOGGER.debug("Excute query : " + query);

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
            final StatementValueIterableResult result =
                    new StatementValueIterableResult(statements);
            return result;

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

        final Object[] toLog = {subject, predicate, value, valueType};
        LOGGER.debug("Tries to delete Subject : {}, predicate : {}, value {} and value type {}", toLog);

        if (subject == null) {
            final NullPointerException exception =
                    new NullPointerException("The subject argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (predicate == null) {
            final NullPointerException exception =
                    new NullPointerException("The predicate argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (value == null) {
            final NullPointerException exception =
                    new NullPointerException("The value argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (valueType == null) {
            final NullPointerException exception =
                    new NullPointerException("The valueType argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        try {
            final Statement statement =
                    getStatement(subject, predicate, value, valueType);
            connection.remove(statement);
        } catch (IllegalArgumentException exception) {
            LOGGER.error(exception.getLocalizedMessage(), exception);
            final TripleException tripleException =
                    new TripleException();
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            tripleException.setValue(value);
            tripleException.setValueType(valueType);
            throw tripleException;
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

        if (subject == null) {
            final NullPointerException exception =
                    new NullPointerException("The subject argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final URI subjectURI;
        final URI predicateURI;
        try {
            subjectURI = valueFactory.createURI(subject);
            predicateURI = predicate == null
                    ? null
                    : valueFactory.createURI(predicate);

        } catch (IllegalArgumentException exception) {
            LOGGER.error(exception.getLocalizedMessage(), exception);
            final TripleException tripleException = new TripleException();
            tripleException.setSubject(subject);
            tripleException.setPredicate(predicate);
            throw tripleException;
        }

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

    @Override
    public boolean isClosed() throws GenericRepositoryException {
        final boolean isOpen;
        try {
            isOpen = connection.isOpen();
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new GenericRepositoryException(ex);
        }
        return !isOpen;
    }

    /**
     * Returns the default file dump directory path. For this implementation
     * this path point to : User Home Directory (operating system
     * dependent)/.generictriplestore/sesame
     *
     * @return the default file dump directory path.
     */
    private static String getDefaultDumpDirectory() {
        final String dir = System.getProperty("user.home");

        final StringBuilder resut = new StringBuilder(dir);
        if (!dir.endsWith(File.separator)) {
            resut.append(File.separator);
        }
        resut.append(".generictriplestore");
        resut.append(File.separator);
        resut.append("sesame");

        return resut.toString();
    }

    @Override
    public Iterable<String> getForPredicatesAndValues(Map<String, String> predicatesValuesTrue,
            Map<String, String> predicatesValuesFalse) throws TripleException {
        
        final String query = buildMultivalueNodeQuery(predicatesValuesTrue, predicatesValuesFalse);
        LOGGER.debug("Excute query : " + query);

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

    private String buildMultivalueNodeQuery(Map<String, String> perdicatesAndValues, Map<String, String> perdicatesAndValues1) {

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
        
        for (Map.Entry<String, String> entry : perdicatesAndValues1.entrySet()) {
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
            where.append(" != ");
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
}
