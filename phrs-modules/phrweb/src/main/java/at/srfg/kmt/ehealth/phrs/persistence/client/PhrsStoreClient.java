package at.srfg.kmt.ehealth.phrs.persistence.client;

import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.TermClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import java.util.*;

import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;

/**
 *
 * Provide wrapper over store API .getLogger(PhrsStoreClient.class);
 */
@SuppressWarnings("serial")
public class PhrsStoreClient implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhrsStoreClient.class);
    private static PhrsStoreClient m_instance = null;
    private GenericTriplestore triplestore;
    private TermClient termClient;
    private Mongo mongo;
    private int mongoPort = 27017;
    private String mongoHost = "127.0.0.1";
    private Datastore datastore;
    private Datastore datastoreVersions;
    private Datastore datastoreAuditing;
    private Morphia morphia;
    private PhrsRepositoryClient phrsRepositoryClient;
    private InteropClients interopClients;
    private InteropAccessService interopService;
    private InteropProcessor interopProcessor;
    static boolean TEST = true;

    private PhrsStoreClient() {
        configure(false);
    }

    /**
     * Used primarily for testing
     *
     * @param triplestore - can be null
     * @param useTestStores - the document repository, not the triplestore
     */
    private PhrsStoreClient(GenericTriplestore triplestore, boolean useTestStores) {
        //option to set triplestore
        this.triplestore = triplestore;
        configure(useTestStores);
    }

    public static PhrsStoreClient getInstance() {
        if (m_instance == null) {
            m_instance = new PhrsStoreClient();
        }
        return m_instance;
    }

    /**
     * Support especially testing. Creates a new instance
     *
     * @param createNewInstance - use once to create or initialize a new
     * instance in cases where one needs to switch to testStores. In the
     * following code, one can use
     * <code>getInstance()</code> to refer to this new instance.
     * @param useTestStores - the document repository, not the triplestore
     * @return
     */
    public static PhrsStoreClient getInstance(boolean createNewInstance, boolean useTestStores) {
        if (m_instance == null || createNewInstance) {
            m_instance = new PhrsStoreClient(null, useTestStores);
        }
        return m_instance;
    }

    /**
     * Especially for unit testing, if needed, otherwise get the triplestore for
     * initialization or cleanup
     *
     * Creates a new instance but using this triplestore.
     *
     * @param triplestore
     * @param useTestStores - the document repository, not the triplestore
     * @return
     */
    public static PhrsStoreClient getInstance(GenericTriplestore triplestore, boolean useTestStores) {
        if (m_instance == null) {
            m_instance = new PhrsStoreClient(triplestore, useTestStores);
        }
        if (triplestore != null) {
            m_instance.setTripleStore(triplestore);
        }
        return m_instance;
    }

    private void configure(boolean useTestStores) {
        if (useTestStores) {
            assignTestStoresNames();
        }

        initPhrsDb();
        initClients();
    }

    /**
     * For Testing
     *
     * @param newInstance - true create new instance
     * @return
     */
    public static PhrsStoreClient getInstance(boolean newInstance) {
        if (newInstance) {
            m_instance = new PhrsStoreClient();
        } else if (m_instance == null) {
            m_instance = new PhrsStoreClient();
        }

        return m_instance;
    }

    public void setTripleStore(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    /**
     * The RDF based tripleStore
     *
     * @return
     */
    public GenericTriplestore getTripleStore() {
        return triplestore;
    }
    /*
     * Initialization-on-demand holder idiom if instance fails will get
     * ambiguous java.lang.NoClassDefFoundError Must remove m_instance private
     * static class LazyHolder { public static final PhrsStoreClient INSTANCE =
     * new PhrsStoreClient(); }
     *
     * public static PhrsStoreClient getInstance() { return LazyHolder.INSTANCE;
     * }
     */
    private String phrs_database_name = PhrsConstants.PHRS_DATABASE_NAME;
    private String phrs_database_versioning_name = PhrsConstants.PHRS_VERSIONING_DATABASE_NAME;
    private String phrs_database_auditing_name = PhrsConstants.PHRS_AUDIT_DATABASE_NAME;

    protected void assignTestStoresNames() {
        StringBuffer buf1 = new StringBuffer();
        buf1.append("unittest_");
        buf1.append(phrs_database_name);
        phrs_database_name = buf1.toString();

        buf1 = new StringBuffer();
        buf1.append("unittest_");
        buf1.append(phrs_database_versioning_name);
        phrs_database_versioning_name = buf1.toString();

        buf1 = new StringBuffer();
        buf1.append("unittest_");
        buf1.append(phrs_database_auditing_name);
        phrs_database_auditing_name = buf1.toString();
    }

    public void dropTestStores() {
    }

    /**
     * TODO Add a simple query to demonstrate that the Mongo store is up. The
     * initialization alone doesn't check even though we create data stores
     *
     */
    private void initPhrsDb() {

        try {
            mongo = new Mongo(mongoHost, mongoPort);

            morphia = new Morphia();

            // TODO user password mongodb
            datastore = morphia.createDatastore(mongo,
                    phrs_database_name, null, null);

            datastoreVersions = morphia.createDatastore(mongo,
                    phrs_database_versioning_name, null, null);
            datastoreAuditing = morphia.createDatastore(mongo,
                    phrs_database_auditing_name, null, null);

            morphia.mapPackage("at.srfg.kmt.ehealth.phrs.model.baseform");
            morphia.mapPackage("at.srfg.kmt.ehealth.phrs.model.basesupport");
            morphia.mapPackage("at.srfg.kmt.ehealth.phrs.usermgt");

            if (mongo == null) {
                throw new Exception();
            }
            if (datastore == null) {
                throw new Exception();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("initPhrsStore MongoDB failed . is MongoDB started?",
                    e);
            System.err.println("Exiting initPhrsStore MongoDB failed. Is MongoDB started? host="
                    + mongoHost + " mongoPort=" + mongoPort);
            System.err.println("Please Start MongoDB and restart Tomcat if needed");

            // System.exit(0);
        }
    }

    public PhrsRepositoryClient getPhrsRepositoryClient() {
        return phrsRepositoryClient;
    }

    public CommonDao getCommonDao() {
        return this.getPhrsRepositoryClient().getCommonDao();
    }

    public InteropClients getInteropClients() {
        return interopClients;
    }

    public InteropAccessService getInteropService() {
        return interopService;
    }

    public Mongo getMongoDB() {

        return mongo;
    }

    public Morphia getMongoDBMorphia() {
        return morphia;
    }

    public Datastore getPhrsDatastore() {
        return datastore;
    }

    public Datastore getPhrsVersioningDatastore() {
        return datastoreVersions;
    }

    public Datastore getPhrsAuditDatastore() {
        return datastoreAuditing;
    }

    public InteropProcessor getInteropProcessor() {
        return interopProcessor;
    }

    /**
     * Initialize interop store and clients for interoperability
     */
    private void initClients() {

        try {
            if (triplestore == null) {
                final TriplestoreConnectionFactory connectionFactory = TriplestoreConnectionFactory.getInstance();

                triplestore = connectionFactory.getTriplestore();
            }

            phrsRepositoryClient = new PhrsRepositoryClient(this);
            interopClients = new InteropClients(triplestore);
            interopService = new InteropAccessService(this);
            interopProcessor = new InteropProcessor(this);
            termClient = interopClients.getTermClient();
            if (termClient == null) {
                System.out.println("getTermClient NULL");
                LOGGER.error("getTermClient NULL");
            }
        } catch (GenericRepositoryException e) {
            e.printStackTrace();
            System.out.println("initInteropStore  GenericRepositoryException ");
            LOGGER.error("initInteropStore GenericRepositoryException NULL", e);
        } catch (Exception e) {
            System.out.println("initInteropStore  Exception ");
            e.printStackTrace();
            LOGGER.error("error initInteropStore", e);
        }

        if (triplestore == null) {
            System.out.println("initInteropStore  triple store null ");
            LOGGER.error("initInteropStore  triple store null");
        }
        if (interopClients == null) {
            System.out.println("initInteropStore  interopClients  null ");
            LOGGER.error("initInteropStore interopClients null");
        }
        if (interopService == null) {
            System.out.println("initInteropStore  interopService  null ");
            LOGGER.error("initInteropStore  interopService null");
        }
    }

    public TermClient getTermClient() {
        if (termClient == null) {
            System.out.println("getTermClient NULL");
            LOGGER.error("getTermClient NULL");
        }
        return termClient;
    }

    public GenericTriplestore getGenericTriplestore() {
        return triplestore;
    }

    /**
     *
     * @param subject
     * @return resource map key=propertyName value= one or more property values
     */
    public Map<String, Collection<String>> getResource(String subject) {

        return this.getResource(subject, null);

    }

    /**
     *
     * @param subject
     * @param language
     * @return
     */
    public Map<String, Collection<String>> getResource(String subject,
            String language) {

        Map<String, Collection<String>> map = null;
        if (subject != null && subject.length() > 1 && subject.contains("://")) {

            try {
                Iterable<Triple> triples = this.getGenericTriplestore().getForSubject(subject);
                map = new HashMap<String, Collection<String>>();

                // create key and collection value
                for (Triple triple : triples) {
                    // add empty collection once per property
                    if (!map.containsKey(triple.getPredicate())) {
                        Set<String> value = new HashSet<String>();
                        map.put(triple.getPredicate(), value);
                        value.add(triple.getValue());
                    } else {
                        Collection<String> value = map.get(triple.getPredicate());
                        value.add(triple.getValue());
                    }

                }

                // include the subject
                if (!map.isEmpty()) {
                    map.put(PhrsConstants.RDF_MAP_KEY_SUBJECT,
                            Collections.singleton(subject));
                }
            } catch (TripleException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     *
     * @param subject
     * @return
     */
    public Collection<Map<String, Collection<String>>> getResourcesAsMaps(
            Iterable<String> subjects) {

        Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
        Collection<Map<String, Collection<String>>> collection = null;
        // Map<String,Collection<String>> map=null;
        if (subjects != null) {// && subjects.iterator().hasNext()){

            collection = new ArrayList<Map<String, Collection<String>>>();

            // if(subjects!=null){
            for (String subject : subjects) {
                map = getResource(subject);

                if (map != null && !map.isEmpty()) {
                    collection.add(map);
                }
            }
            // }
        }
        return collection;
    }

    /**
     * TODO Refactor to use TermClient
     *
     * @param resourceURI
     * @param language
     * @return
     */
    public Collection<Map<String, Collection<String>>> getTermResourcesBySkosRelated(
            String resourceURI, String language) {
        Collection<Map<String, Collection<String>>> collection = null;
        try {
            final Iterable<String> subjects = triplestore.getForPredicateAndValue(Constants.SKOS_RELATED,
                    resourceURI, ValueType.RESOURCE);

            collection = this.getResourcesAsMaps(subjects);
        } catch (TripleException e) {
            e.printStackTrace();
            System.out.print("getTermResourcesBySkosRelated  exception  using:"
                    + resourceURI);
        }
        /*
         * if (TEST && collection == null) System.out
         * .print("getTermResourcesBySkosRelated results NULL using:" +
         * resourceURI);
         *
         * if (TEST && collection != null && collection.isEmpty()) System.out
         * .print("getTermResourcesBySkosRelated results EMPTY using:" +
         * resourceURI);
         *
         * System.out.print("getTermResourcesBySkosRelated for " + resourceURI);
         * if (collection != null) System.out.println(" size=" +
         * collection.size()); else System.out.println(" =NULL results");
         */
        return collection;
    }

    /**
     * TODO Use Dynabean solution!
     *
     * @param resourceURI
     * @param language
     * @return
     */
    public Collection<DynaBean> getTermResourceDynabeans(String resourceURI,
            String language) {

        return interopClients.getTermResourceDynabeans(resourceURI, language);
    }

    public DynaBeanClient getDynaBeanClient() {
        return interopClients.getDynaBeanClient();
    }

    public DynaBean getResourceDynabeans(String interopResourceUri) {
        DynaBean dyna = null;
        try {
            dyna = getDynaBeanClient().getDynaBean(interopResourceUri);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (TripleException e) {
            e.printStackTrace();
        }
        return dyna;
    }

    /**
     * Work around
     *
     * @param resourceURI
     * @param newValue
     * @throws Exception
     */
    public void updateSkosNote(String subjectUri, String newValue)
            throws Exception {

        updateTriple(subjectUri, Constants.SKOS_NOTE, newValue, false);

    }

    /**
     * Work around for client Property check issue. Allows Constants.SKOS_NOTE
     *
     *
     * @param subjectUri
     * @param predicate
     * @param newValue
     * @param updateDate - normally date is updated unless a change to a
     * reference note
     * @throws Exception
     */
    public void updateTriple(String subjectUri, String predicate, String newValue, boolean updateDate)
            throws Exception {
        if (subjectUri != null && predicate != null && newValue != null) {
            
            try {
                final boolean resourceExists = triplestore.exists(subjectUri);
                if (!resourceExists) {
                    final String msg =
                            String.format("There is no resource for this URI %s ", subjectUri);
                    throw new IllegalArgumentException(msg);
                }

                //No check for SKOS_NOTE...., dynabeans will pick it up but it will break schemaClient
                if (!predicate.equals(Constants.SKOS_NOTE)) {

                    final boolean propertyExists = getInteropClients().getSchemeClient().propertyExists(predicate);
                    if (!propertyExists) {
                        final String msg =
                                String.format("The predicate %s does not exists like property", predicate);
                        throw new IllegalArgumentException(msg);
                    }

                    // TODO : this mode to detect the value type is not the best one,
                    // it has its limitations. E.G. no Bnode is supported, no expicit
                    // classes are supported.
                    // I chose to se this simple (and limited) solution becuase I it fits 
                    // the needs for the medication
                    final boolean isPropertyLiteral = getInteropClients().getSchemeClient().isPropertyLiteral(predicate);
                    final ValueType type = isPropertyLiteral
                            ? ValueType.LITERAL
                            : ValueType.RESOURCE;
                    triplestore.delete(subjectUri, predicate);
                    triplestore.persist(subjectUri, predicate, newValue, type);
                } else {
                    //known as Literal
                    triplestore.delete(subjectUri, predicate);
                    triplestore.persist(subjectUri, predicate, newValue, ValueType.LITERAL);
                }

                // NOTA BENE : the update operatin is responsible for the UPDATE_DATE property.
                // More preciselly the update must set the updated date to the date when
                // the resource was modified.
                if (updateDate) {
                    setUpdateDate(subjectUri);
                }
            } catch (Exception e) {
                    LOGGER.error("Error on triple update for subjectUri=" + subjectUri + " predicate=" + predicate + " newValue=" + newValue);
                    throw new Exception("Error on triple update forsubjectUri=" + subjectUri + " predicate=" + predicate + " newValue=" + newValue);
  
            }

        } else {
            LOGGER.error("Null input parameter subjectUri=" + subjectUri + " predicate=" + predicate + " newValue=" + newValue);
            throw new Exception("Null input parameter subjectUri=" + subjectUri + " predicate=" + predicate + " newValue=" + newValue);
        }
    }

    private void setUpdateDate(String subjectUri) {
        try {
            final boolean updateDateExists =
                    triplestore.exists(subjectUri, Constants.UPDATE_DATE);
            if (updateDateExists) {
                triplestore.delete(subjectUri, Constants.UPDATE_DATE);
            }

            final String newDate = DateUtil.getFormatedDate(new Date());
            triplestore.persist(subjectUri, Constants.UPDATE_DATE, newDate, LITERAL);
        } catch (Exception e) {
            LOGGER.error(" setUpdateDate subjectUri=" + subjectUri, e);
        }
    }
}
