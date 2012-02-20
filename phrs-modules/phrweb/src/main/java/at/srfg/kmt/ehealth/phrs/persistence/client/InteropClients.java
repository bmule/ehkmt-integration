package at.srfg.kmt.ehealth.phrs.persistence.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.*;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;

/**
 * The available interoperability clients
 * <p/>
 * interop.subscribers.notify
 */
public class InteropClients {

    @SuppressWarnings("unused")
    private final static Logger LOGGER = LoggerFactory.getLogger(InteropClients.class);
    /*
    VitalSignClient vitalSignClient;
    MedicationClient medicationClient;
    ProblemEntryClient problemEntryClient;
    SchemeClient schemeClient;
    TermClient termClient;
    ActorClient actorClient;
    DynaBeanClient dynaBeanClient;
    GenericTriplestore triplestore;
    PHRSRequestClient phrsRequestClient;
    */


    public InteropClients() throws Exception {

        init();

    }

    public PhrsStoreClient getPhrsStoreClient() {
        return PhrsStoreClient.getInstance();
    }

    private void init() {

        /*
        vitalSignClient = new VitalSignClient(triplestore);
        vitalSignClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);

        medicationClient = new MedicationClient(triplestore);
        medicationClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);

        problemEntryClient = new ProblemEntryClient(triplestore);
        problemEntryClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);

        schemeClient = new SchemeClient(triplestore);

        actorClient = new ActorClient(triplestore);

        termClient = new TermClient(triplestore);

        dynaBeanClient = new DynaBeanClient(triplestore);
        //dynaBeanClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);

        phrsRequestClient = new PHRSRequestClient(triplestore);
        */
    }

    public GenericTriplestore getTriplestore() {

        return getPhrsStoreClient().getTripleStore();
    }

    public VitalSignClient getVitalSignClient() {

        try {
            return new VitalSignClient(getTriplestore());
        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
        //return vitalSignClient;
    }

    public PHRSRequestClient getPHRSRequestClient() {
        try {
            return new PHRSRequestClient(getTriplestore() );

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public MedicationClient getMedicationClient() {
        try {
            return new MedicationClient(getTriplestore());

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public ProblemEntryClient getProblemEntryClient() {
        try {
            return new ProblemEntryClient(getTriplestore());

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public SchemeClient getSchemeClient() {
        try {
            return new SchemeClient(getTriplestore() );

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public TermClient getTermClient() {
        try {
            return new TermClient(getTriplestore());

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public ActorClient getActorClient() {
        try {
            return new ActorClient(getTriplestore());

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public DynaBeanClient getDynaBeanClient() {
        try {
            return new DynaBeanClient(getTriplestore());
        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public DynaBean getResourceDynabean(String referenceId) {
        DynaBean dyna = null;
        try {
            dyna = getDynaBeanClient().getDynaBean(referenceId);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (TripleException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
        if (dyna != null) {
            System.out.println("DynaBeanUtil.toString(dyna)"
                    + DynaBeanUtil.toString(dyna));
        } else {
        }
        return dyna;
    }

    // TODO complete the refactoring of Triple based code to dynabean. Remove
    // PhrsStoreClient methods

    /**
     * @param resourceURI
     * @param language
     * @return
     */
    public Collection<DynaBean> getTermResourceDynabeans(String resourceURI,
                                                         String language) {

        Iterable<Triple> subjects = null;
        Collection<DynaBean> collection = new ArrayList<DynaBean>();

        if (getTermClient() != null) {
            try {
                subjects = getTermClient().getTermsRelatedWith(resourceURI);
                for (Triple subject : subjects) {
                    String res = subject.getSubject();
                    DynaBean dyna = getResourceDynabean(res);
                    if (dyna != null) {
                        collection.add(dyna);

                    }
                }

            } catch (TripleException e) {
                e.printStackTrace();
                LOGGER.error("", e);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("", e);
            }

        }

        return collection;
    }
    //String careProvisionCode) {
    //Check interop.subscribers.notify

    /**
     *
     */
    public void notifyInteropMessageSubscribers() {
        try {
            notifyInteropMessageSubscribers(null, null);
        } catch (Exception e) {
            LOGGER.error("Failed to Notify subscriptions" + e.getMessage(), e);
        }
    }

    /**
     * For all subscriptions
     * Note this can be run after the user saves a change, but also after the protocolId is changed or added.
     *
     * @param protocolId
     */
    public void notifyInteropMessageSubscribersByProtocolId(String protocolId) {
        try {
            notifyInteropMessageSubscribers(null, protocolId);
        } catch (Exception e) {
            LOGGER.error("Failed to Notify subscriptions" + e.getMessage(), e);
        }
    }

    /**
     * Look up PhrId from registered ProtocolId. If it is not found, either we did not register it or the user did not
     * orovide yet a Pix identifier for us to lookup in PIX
     * @param phrId
     * @return
     */
    public boolean notifyInteropMessageSubscribersByPhrId(String phrId) {

        boolean flag = false;
        String protocolId = getProtocolId(phrId);
        if (phrId != null && !phrId.isEmpty()) {


            if (protocolId != null) {
                try {
                    LOGGER.debug("start notifying phrId" + phrId + " protocolId " + protocolId);
                    notifyInteropMessageSubscribers(null, protocolId);
                } catch (Exception e) {
                    LOGGER.error("Failed to Notify subscriptions " + e.getMessage(), e);
                }
                flag = true;
            } else {
                LOGGER.error("error to notify subscriptions, no protocolId found for phrId=" + phrId);
            }
        } else {
            LOGGER.error("error to notify subscriptions, phrId is null or empty, cannot get protocol Id");
        }
        return flag;
    }



    /**
     * @param selectedCareProvisionCode
     */
    public void notifyInteropMessageSubscribers(String selectedCareProvisionCode, String protocolId) throws Exception {
        LOGGER.debug("START notifyInteropMessageSubscribers protocolId " + protocolId+ " selectedCareProvisionCode "+selectedCareProvisionCode);
        PHRSRequestClient requestClient = getPHRSRequestClient();
        DynaBeanClient beanClient = getDynaBeanClient();
        final Iterable<String> resources = requestClient.getAllPHRSRequests();
        for (String resource : resources) {
            final DynaBean request = beanClient.getDynaBean(resource);

            boolean notify = false;
            String careProvisionCode = (String) request.get(Constants.HL7V3_CARE_PROVISION_CODE);
            //filter on careProvisionCode  ?
            if (selectedCareProvisionCode == null) {
                notify = true;
            } else {
                if (careProvisionCode == null) {
                    notify = false;
                } else if (careProvisionCode.equalsIgnoreCase(selectedCareProvisionCode)) {
                    notify = true;
                }

            }
            String id = (String) request.get("http://www.icardea.at/phrs/actor#protocolId");
            //filter on protocolId ?
            if (id == null) {
                notify = false;
            } else if (protocolId != null) {
                if (id.equals(protocolId)) {
                    notify = true;
                } else {
                    notify = false;
                }
            }

            if (notify) {
                final String wsAdress =
                        (String) request.get("http://www.icardea.at/phrs/hl7V3#wsReplyAddress");


                final Map<String, String> properties = new HashMap<String, String>();
                properties.put("patientId", id);
                properties.put("patientNames", "patientNames");

                //Care Provision Code
                properties.put("careProvisionCode", careProvisionCode);
                properties.put("responseEndpointURI", wsAdress);
                int port = ConfigurationService.getInstance().getSubscriberSocketListnerPort();

                notify("localhost", port, properties);


            }
            LOGGER.debug("END notifyInteropMessageSubscribers notify="+notify+ " protocolId" + protocolId+ " selectedCareProvisionCode "+selectedCareProvisionCode);

        }
        LOGGER.debug("Finished - Notified Core after Loading test data ");

    }

    public Map<String, String> createNotifyQueryProperties(String wsAdress, String protocolId, String careProvisionCode) {

        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("patientId", protocolId);
        properties.put("patientNames", "patientNames");
        //Care Provision Code
        properties.put("careProvisionCode", careProvisionCode);
        properties.put("responseEndpointURI", wsAdress);


        return properties;
    }

    public void notify(String host, int port, Map<String, String> params) {
        LOGGER.debug("Tries to dispach this properties {}.", params);
        try {
            final Socket socket = new Socket(host, port);
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(params);
        } catch (Exception e) {
            LOGGER.error("Prameters : {} can not be dispathed.", params);
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("The {} was distpatched", params);
    }

    /**
     *
     * @param owneruri
     * @param protocolId
     * @param namespace      if null defaults to Constants.ICARDEA_DOMAIN_PIX_OID
     */
    public void registerProtocolId(String owneruri, String protocolId, String namespace) {
        registerUser(owneruri,protocolId,namespace);
//        if (namespace == null) {
//            namespace = Constants.ICARDEA_DOMAIN_PIX_OID;
//
//        }
//        //AUTHORIZE_USER_PREFIX_TEST
//        try {
//            getActorClient().register(namespace, owneruri, protocolId);
//            LOGGER.debug("registered protocolId in core for owneruri= " + owneruri + " protocolId= " + protocolId + " namespace=" + namespace);
//
//        } catch (TripleException e) {
//            LOGGER.error("owneruri= " + owneruri + " protocolId= " + protocolId, e);
//        }

    }
    /**
     *
     * @param ownerUri
     * @param protocolId
     * @param protocolNamespace
     */
    public void registerUser(String ownerUri, String protocolId, String protocolNamespace){
        if(ownerUri!=null && protocolId!=null){


            if (protocolNamespace == null) {
                protocolNamespace = Constants.ICARDEA_DOMAIN_PIX_OID;

            }
            ActorClient actorClient= getActorClient();
            boolean register=true;
            try {
                String temp= getProtocolId(ownerUri,protocolNamespace) ;
                //Manage these, this is not done by the Actor client
                if(temp!=null){
                    if( temp.equals(protocolId))
                        register=false;
                    else {
                        actorClient.removeProtocolIds(protocolNamespace,ownerUri);
                        register=true;
                        String temp2= getProtocolId(ownerUri,protocolNamespace) ;
                        System.out.print("temp2"+temp2);
                    }
                }

            } catch (Exception e) {
                LOGGER.error(" ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace,e);
            }
            if(register){
                try {
                    getActorClient().register(protocolNamespace,ownerUri,protocolId);
                } catch (Exception e) {
                    LOGGER.error(" ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace,e);
                }
            }
        } else {
            LOGGER.error(" Null input: ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace);
        }
    }
    /**
     *
     * @param ownerUri
     * @param protocolNamespace
     * @return
     */
    public String getProtocolId(String ownerUri, String protocolNamespace){
        if (protocolNamespace == null) {
            protocolNamespace = Constants.ICARDEA_DOMAIN_PIX_OID;

        }
        String value= null;
        try {
            value=getActorClient().getProtocolId(protocolNamespace, ownerUri);
        } catch (Exception e) {
            LOGGER.error(" ownerUri "+ownerUri+" protocolNamespace "+protocolNamespace,e);
        }
        
        return value;
    }

    /**
     *
     * @param phrId
     * @return
     */
    public String getProtocolId(String phrId) {

        return getProtocolId(phrId,null);
    }


    /*
    private void setupTest() {
        try{
            ActorClient actorClient = new ActorClient();
            boolean hasTestProtocolId = false; //register the 191 protocol ID
            try {
                //Constants.PHRS_NAMESPACE
                String p1 = actorClient.getProtocolId(Constants.ICARDEA_DOMAIN_PIX_OID, Constants.OWNER_URI_CORE_PORTAL_TEST_USER);
                if (p1 != null) {
                    hasTestProtocolId = true;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to find protocolId for user, must create - user=" + Constants.OWNER_URI_CORE_PORTAL_TEST_USER + " for namespace=" + Constants.OWNER_URI_CORE_PORTAL_TEST_USER, e);
            }
            try {
                //Constants.PHRS_NAMESPACE
                String p1 = actorClient.getProtocolId(Constants.OWNER_URI_CORE_PORTAL_TEST_USER);
                if (p1 != null) {
                    hasTestProtocolId = true;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to find protocolId for user, must create - user=" + Constants.OWNER_URI_CORE_PORTAL_TEST_USER + " for default namespace=" + Constants.PHRS_NAMESPACE, e);
            }



        } catch (Exception e) {
            LOGGER.error("Failed to Register Test protocol ID=" + USER_PROTOCOL_ID + " for namespace=" + PROTOCOL_ID_NAMESPACE, e);
        }
    } */
}
