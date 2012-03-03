package at.srfg.kmt.ehealth.phrs.persistence.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.TermClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.LITERAL;

/**
 *
 * Provide wrapper over store API .getLogger(PhrsStoreClient.class);
 */
@SuppressWarnings("serial")
public  class PhrsStoreClient implements Serializable {

    static boolean TEST = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhrsStoreClient.class);
    //private static PhrsStoreClient m_instance = null;
    private static PhrsStoreClient m_instance;


//    static {
//        try {
//            m_instance = new PhrsStoreClient();
//        } catch (Exception ex) {
//            LOGGER.warn(ex.getMessage(), ex);
//            throw new IllegalStateException(ex);
//        }
//    }
    static {
        staticInit();
    }

    protected static void staticInit() {
        try {
            m_instance = new PhrsStoreClient();
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }
    //private GenericTriplestore triplestore;
    
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
    private GenericTriplestore tripleStore;

    private PhrsStoreClient() {
        configure(false);
    }

    /**
     *
     * @return
     */
    public static PhrsStoreClient getInstance() {
        return m_instance;
    }

    /**
     * Use test document repositories, primarily for unit testing The test
     * repository for the interop store is handled by the a configuration file
     *
     * @param useTestDocumentRepositoryStores
     */
    private void configure(boolean useTestDocumentRepositoryStores) {
        if (useTestDocumentRepositoryStores) {
            assignTestStoresNames();
        }
        //assignTripleStore();
        
        initPhrsDb();
        initClients();
        
    }

    /**
     * For unit tests, do not use in production, not thread safe
     *
     * @param newInstance - true create new instance
     * @return
     */
    public static PhrsStoreClient getInstance(boolean newInstance) {
        // if (newInstance) {
        //   staticInit();
        // }

        return m_instance;
    }

    /**
     * The RDF based tripleStore
     *
     * @return
     */
    public GenericTriplestore getTripleStore() {
        //will open connection if closed, but how does one close a connection?
        //return tripleStore;
        return  TriplestoreConnectionFactory.getInstance().getTriplestore();
    }
    /**
     * shutdown when closing application, see DefaultLoader a servlet context listener 
     */
    public  void shutdown(){     
       
        try {
            GenericTriplestore triplestore=getTripleStore();
            if(triplestore!=null){
                ((GenericTriplestoreLifecycle) triplestore).shutdown();
            }
        } catch (GenericRepositoryException e) {
            LOGGER.error("closeConnection()",e);
        }
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

            LOGGER.error("initPhrsStore MongoDB failed . is MongoDB started? mongohost=" + mongoHost + " mongoPort=" + mongoPort,
                    e);

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
            GenericTriplestore tripleStore=getTripleStore();
            phrsRepositoryClient = new PhrsRepositoryClient();
            interopClients = new InteropClients();
            interopService = new InteropAccessService();
            interopProcessor = new InteropProcessor();


        } catch (GenericRepositoryException e) {

            LOGGER.error("initInteropStore GenericRepositoryException NULL", e);
        } catch (Exception e) {

            LOGGER.error("error initInteropStore", e);
        }

        if (getTripleStore() == null) {

            LOGGER.error("initInteropStore  triple store null");
        }
        if (interopClients == null) {

            LOGGER.error("initInteropStore interopClients null");
        }
        if (interopService == null) {

            LOGGER.error("initInteropStore  interopService null");
        }
    }

    public TermClient getTermClient() {
        return this.getInteropClients().getTermClient();
    }

    public GenericTriplestore getGenericTriplestore() {
        return getTripleStore();
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
                LOGGER.error(e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
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
            final Iterable<String> subjects = getTripleStore().getForPredicateAndValue(Constants.SKOS_RELATED,
                    resourceURI, ValueType.RESOURCE);

            collection = this.getResourcesAsMaps(subjects);
        } catch (Exception e) {
            LOGGER.error("getTermResourcesBySkosRelated  exception  using:"
                    + resourceURI, e);
        }

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
            LOGGER.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (TripleException e) {
            LOGGER.error(e.getMessage(), e);
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
                final boolean resourceExists = getTripleStore().exists(subjectUri);
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
                    getTripleStore().delete(subjectUri, predicate);
                    getTripleStore().persist(subjectUri, predicate, newValue, type);
                } else {
                    //known as Literal
                    getTripleStore().delete(subjectUri, predicate);
                    getTripleStore().persist(subjectUri, predicate, newValue, ValueType.LITERAL);
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
                    getTripleStore().exists(subjectUri, Constants.UPDATE_DATE);
            if (updateDateExists) {
                getTripleStore().delete(subjectUri, Constants.UPDATE_DATE);
            }

            final String newDate = DateUtil.getFormatedDate(new Date());
            getTripleStore().persist(subjectUri, Constants.UPDATE_DATE, newDate, LITERAL);
        } catch (Exception e) {
            LOGGER.error(" setUpdateDate subjectUri=" + subjectUri, e);
        }
    }

    public  boolean getSystemStatus() {
        boolean systemStatusInterop=false;
        boolean systemStatus=false;

        try{
            if(mongo != null){
                systemStatusInterop=true;
            }
            //will this cause exception if unavailable or do we try something
            if(this.getGenericTriplestore()!=null) {
                systemStatusInterop=true;
            }
        } catch (Exception e) {
            LOGGER.error(" getSystemStatus or mongoDB not available ", e);
        }
        return systemStatus && systemStatusInterop;
    }
}
