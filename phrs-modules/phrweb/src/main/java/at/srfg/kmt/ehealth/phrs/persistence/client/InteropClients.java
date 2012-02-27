package at.srfg.kmt.ehealth.phrs.persistence.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
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

    }

    public GenericTriplestore getTriplestore() {

        return getPhrsStoreClient().getTripleStore();
    }

    public VitalSignClient getVitalSignClient() {

        try {
            VitalSignClient vitalSignClient = new VitalSignClient(getTriplestore());
            vitalSignClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);
            return  vitalSignClient;
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
            MedicationClient medicationClient = new MedicationClient(getTriplestore());
            medicationClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);
            return medicationClient;

        } catch (Exception e) {
            LOGGER.error("getVitalSignClient ", e);
        }
        return null;
    }

    public ProblemEntryClient getProblemEntryClient() {
        try {
            ProblemEntryClient problemEntryClient = new ProblemEntryClient(getTriplestore());
            problemEntryClient.setCreator(PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR);
            return problemEntryClient;

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
     * Notify subscribers about ALL USERS!
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
 * 
 * @param protocolId
 * @param resourceType 
 */
   public void notifyInteropMessageSubscribersByProtocolId(String protocolId, String resourceType) {
        try {
            //future type will inform specific single or multiple requests
            notifyInteropMessageSubscribers(null, protocolId);
        } catch (Exception e) {
            LOGGER.error("Failed to Notify subscriptions" + e.getMessage(), e);
        } 
    }


    /**
     * 
     * Notify subscribers but only if we find a protocolId 
     * from a PhrId lookup from local store, not Interop Component Actor registry
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
       int countRequests=0;
       int countNotified=0;
        for (String resource : resources) {
            final DynaBean request = beanClient.getDynaBean(resource);
            countRequests++;
            boolean sendNotification = false;
            String careProvisionCode = (String) request.get(Constants.HL7V3_CARE_PROVISION_CODE);
            //filter on careProvisionCode  ?
            if (selectedCareProvisionCode == null) {
                sendNotification = true;
            } else {
                if (careProvisionCode == null) {
                    sendNotification = false;
                } else if (careProvisionCode.equalsIgnoreCase(selectedCareProvisionCode)) {
                    sendNotification = true;
                }

            }
            String id = (String) request.get("http://www.icardea.at/phrs/actor#protocolId");
            //filter on protocolId ?
            if (id == null) {
                sendNotification = false;
            } else if (protocolId != null) {
                if (id.equals(protocolId)) {
                    sendNotification = true;
                } else {
                    sendNotification = false;
                }
            }

            if (sendNotification) {
                countNotified++;
                final String wsAdress =
                        (String) request.get("http://www.icardea.at/phrs/hl7V3#wsReplyAddress");


                final Map<String, String> properties = new HashMap<String, String>();
                properties.put("patientId", id);
                properties.put("patientNames", "patientNames");

                //Care Provision Code
                properties.put("careProvisionCode", careProvisionCode);
                properties.put("responseEndpointURI", wsAdress);
                int port = ConfigurationService.getInstance().getSubscriberSocketListnerPort();

                notifyInteropMessageSubscribers("localhost", port, properties);


            }
            LOGGER.debug("END notifyInteropMessageSubscribers notify="+sendNotification+ " protocolId" + protocolId+ " selectedCareProvisionCode "+selectedCareProvisionCode);

        }
        LOGGER.debug("Finished - Notified Core after Loading test data countPHRSRequests found="+countRequests+" countNotified=" +countNotified);

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

    public void notifyInteropMessageSubscribers(String host, int port, Map<String, String> params) {
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
     * @deprecated
     * @param owneruri
     * @param protocolId
     * @param namespace      if null defaults to Constants.ICARDEA_DOMAIN_PIX_OID
     */
    public void registerProtocolId(String owneruri, String protocolId, String namespace) {
        registerUser(owneruri,protocolId,namespace);
    }

    
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

    /**
     *  @deprecated
     * @param ownerUri
     * @param protocolId
     * @param protocolNamespace
     */
        public void registerUser(String ownerUri, String protocolId, String protocolNamespace){
            //
        }
    
//
//    public void registerUser(String ownerUri, String protocolId, String protocolNamespace){
//        if(ownerUri!=null && protocolId!=null){
//
//
//            if (protocolNamespace == null) {
//                protocolNamespace = Constants.ICARDEA_DOMAIN_PIX_OID;
//
//            }
//            ActorClient actorClient= getActorClient();
//            boolean register=true;
//            try {
//                String temp= getProtocolId(ownerUri,protocolNamespace) ;
//                //Manage these, this is not done by the Actor client
//                if(temp!=null){
//                    if( temp.equals(protocolId))
//                        register=false;
//                    else {
//                        actorClient.removeProtocolIds(protocolNamespace,ownerUri);
//                        register=true;
//                        String temp2= getProtocolId(ownerUri,protocolNamespace) ;
//                        System.out.print("temp2"+temp2);
//                    }
//                }
//
//            } catch (Exception e) {
//                LOGGER.error(" ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace,e);
//            }
//            if(register){
//                try {
//                    getActorClient().register(protocolNamespace,ownerUri,protocolId);
//                } catch (Exception e) {
//                    LOGGER.error(" ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace,e);
//                }
//            }
//        } else {
//            LOGGER.error(" Null input: ownerUri "+ownerUri+" protocolId "+protocolId+" protocolNamespace "+protocolNamespace);
//        }
//    }
    /**
     *
     * @param ownerUri
     * @param protocolNamespace
     * @return
     */
    public String getProtocolId(String ownerUri, String protocolNamespace){
        CommonDao commonDao= PhrsStoreClient.getInstance().getCommonDao();
        return commonDao.getProtocolId(ownerUri); 
     }

//        if (protocolNamespace == null) {
//            protocolNamespace = Constants.ICARDEA_DOMAIN_PIX_OID;
//
//        }

//        String value= null;
//        try {
//            value=getActorClient().getProtocolId(protocolNamespace, ownerUri);
//        } catch (Exception e) {
//            LOGGER.error(" ownerUri "+ownerUri+" protocolNamespace "+protocolNamespace,e);
//        }
//        
//        return value;

    /**
     *
     * @param phrId
     * @return
     */
    public String getProtocolId(String ownerUri) {

        return getProtocolId(ownerUri,null);
    }

}
