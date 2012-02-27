package at.srfg.kmt.ehealth.phrs.security.services;


import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.QBP_Q21;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.QPD;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import gr.forth.ics.icardea.pid.HL7Utils;

import static gr.forth.ics.icardea.pid.PatientIndexConstants.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * From ConfigurationService get the endpoint
 * <p/>
 * Includes code snippets from FORTH TODO refactor based on latest example code
 */
@SuppressWarnings("serial")
public class PixService implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(PixService.class);
    // public final static String IDENTIFIER_TYPE_PROTOCOL_ID = "PROTOCOL";
    // public final static String IDENTIFIER_TYPE_CIED = "CIED";
    public static final String ICARDEA_PIX_CIED_FULL_NAMESPACE = "CIED&bbe3a050-079a-11e0-81e0-0800200c9a66&UUID";
    public static final String ICARDEA_PIX_PID_FULL_NAMESPACE = "icardea.pix&1.2.826.0.1.3680043.2.44.248240.1&ISO";
    //not needed?
    public final static String IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA = "icardea.pix";
    public final static String IDENTIFIER_NAMESPACE_PHR_ID = "PHR";
    public final static String ICARDEA_PIX_OID = "1.2.826.0.1.3680043.2.44.248240.1";
    public final static String IDENTIFIER_NAMESPACE_UNIVERSAL_ICARDEA = ICARDEA_PIX_OID;
    // String endPoint = "localhost:8080";
    // Striport = 2575;ng host = "localhost";
    // int
    public final static String APPLICATION_NAME = "PHR"; // config.ini
    // namespace=PHR
    // testclient^icardea
    // private static PatientIndex pid = null;
    // private static ConnectionHub connectionHub = null;
    private ConfigurationService config;
    private AuditAtnaService aas;
    private String domainDefault = IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA;
    private int sslConfigSetting = 2;
    private int pix_port = 2575;
    private String pix_host = "localhost";
    private boolean pix_tls = true;


    public final static String PIX_QUERY_TYPE_PREFIX_CIED = "cied";
    public final static String PIX_QUERY_TYPE_PREFIX_PID = "pid";
    public final static String PIX_QUERY_TYPE_DEFAULT = "cied:model:Maximo";

    public PixService() {
        //get from ConfigurationService
        sslConfigSetting = 2;
        initSSl();
        init();

    }

    public PixService(int sslConfigSetting) {
        this.sslConfigSetting = sslConfigSetting;
        initSSl();
        init();

    }

    public String getPatientProtocolIdByCIED(String cied) {
        String pid = null;

        try {
            pid = queryProtocolIdById(cied, ICARDEA_PIX_CIED_FULL_NAMESPACE);
        } catch (Exception e) {
            LOGGER.error("Error invoking queryProtocolIdById for cied=" + cied, e);  //To change body of catch statement use File | Settings | File Templates.
        }


        return pid;

    }

    private static Message sendAndRecv(Connection connection, Message msg) throws LLPException, HL7Exception, IOException {
        // The initiator is used to transmit unsolicited messages
        Initiator initiator = connection.getInitiator();
        //Message response = initiator.sendAndReceive(msg);
        return initiator.sendAndReceive(msg);

    }
    // msg=(char)11+msg+(char)28+(char)13;
//    public static String sendMessage(String msg){
//
//        try {
//            System.out.println("PIX Query>" + msg);
//            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(Config.GetSetting("PIX_ip"), Integer.parseInt(Config.GetSetting("PIX_port")));
//            sslsocket.startHandshake();
//            Connection connection = new Connection(new PipeParser(), new MinLowerLayerProtocol(), sslsocket);
//
//            QBP_Q21 hl7Msg = new QBP_Q21();
//            hl7Msg.parse(msg);
//            Message resp = sendAndRecv(connection, hl7Msg);
//            System.out.println("PIX server response>" + resp.encode());
//            return resp.encode();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            return "";
//        }
//    }

    /**
     * YYMMDD
     *
     * @return
     */
    public static String getCurrentDate() {
        return HealthyUtils.formatDate(new Date(), "20120202", "yyyyMMdd");

    }

    public String queryProtocolIdById(String id, String namespace) throws Exception {
        String dateStr = PixService.getCurrentDate();
        // search using CIED number:  theId^^^thedomain e.g. 12345^^^CIED&bbe3a050-079a-11e0-81e0-0800200c9a66&UUID
        String socket_msg = "MSH|^~\\&|EHRListener|EHR|PIX|EHR|" + dateStr + "||QBP^Q23^QBP_Q21|ListenerMsg|P|2.5\r"
                + "QPD|IHE PIX Query|ListenerQry|" + id + "^^^" + namespace + "|\r"
                + "RCP|I\r";

        String response = sendMessage(socket_msg);
        String pid = PixService.parsePid(response, id, namespace);
        LOGGER.debug("PixService.parsePid =  " + pid + " from id=" + id + " namespace=" + namespace);
        return pid;
    }

    public static String parseTest() {
        String response = "MSH|^~ABC|OTHER_IBM_BRIDGE_TLS|IBM|PAT_IDENTITY_X_REF_MGR_MISYS|ALLSCRIPTS|20090224104204-0600||ADT^A08^ADT_A01|9241351356666182528|P|2.3.1||20090224104204-0600"
                + "PID|||102^^^IBOT&1.3.6.1.4.1.21367.2009.1.2.370&ISO||OTHER_IBM_BRIDGE^MARION||19661109|FPV1||O";
        String pid = PixService.parsePid(response, "123", ICARDEA_PIX_CIED_FULL_NAMESPACE);
        return pid;
    }

    /**
     * @param response
     * @param id
     * @param namespace
     * @return
     */
    public static String parsePid(String response, String id, String namespace) {
        String pid = null;

        if (response != null) {
            LOGGER.debug("PIX query response of id=" + id + " namespace0" + namespace);
        }

        if (response != null && response.contains("PID")) {

            String[] pidPart = response.split("PID");

            if (pidPart.length > 1) {
                pid = pidPart[1];
                //pidSegment PID
                String[] pidSegment = pid.split("\\|", -1);//keep empty strings
                // |||102^^^IBOT&1.3.6.1.4.1.21367.2009.1.2.370&ISO||
                //in the 3 section
                pid = pidSegment[3];
                LOGGER.debug("PIX query response parsing, after splitting | = " + pid);
            }

            if (pid != null && pid.contains("^")) {
                LOGGER.debug("PIX query response parsing, remove domain stuff= " + pid);
                pid = pid.trim();
                //includes PID and domain e.g. "191^^^icardea.pix&1.2.826.0.1.3680043.2.44.248240.1&ISO" or co
                String[] pidSegment = pid.split("\\^");//-1 not needed
                pid = pidSegment[0];
            }

            //final split on "^"
            if (pid != null) {
                LOGGER.debug("PIX query - protocol ID found for =" + id + " " + " PID=" + pid);
            } else {
                LOGGER.debug("PIX query - protocol ID not found for =" + id + " in response:" + response);
            }
        } else if (response != null && !response.contains("PID")) {
            LOGGER.debug("PIX query - query reponse not found, reponse does not contain PID. Protocol ID not found for id=" + id);
        } else {
            LOGGER.debug("PIX query - query reponse null. Protocol ID not found for id=" + id);
        }

        return pid;

    }

//parse msg PID
//http://openpixpdq.sourceforge.net/messagesamples.html#_pixquery
//icardea.pix&1.2.826.0.1.3680043.2.44.248240.1&ISO
//CIED&bbe3a050-079a-11e0-81e0-0800200c9a66&UUID
//ORBIS&www.salk.at&DNS
//Use as namespace of ID that we pass: e.g. the CIED implant serial number CIED&bbe3a050-079a-11e0-81e0-0800200c9a66&UUID
//No connection hub needed  see gr.forth.ics.icardea.listener.CDAConverter EHR listener

    /**
     * Send message PIX querx
     *
     * @param msg
     * @return
     */
    public String sendMessage(String msg) {
        // msg=(char)11+msg+(char)28+(char)13;
        Connection connection = null;
        try {
            LOGGER.debug("sendMessage PIX Query>" + msg);
            //ConfigurationService

            //pix_tls

            if (pix_tls) {
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                //SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(Config.GetSetting("PIX_ip"), Integer.parseInt(Config.GetSetting("PIX_port")));

                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(pix_host, pix_port);
                sslsocket.startHandshake();
                connection = new Connection(new PipeParser(), new MinLowerLayerProtocol(), sslsocket);
            } else {
                SocketFactory socketfactory = (SocketFactory) SocketFactory.getDefault();

                Socket socket = (Socket) socketfactory.createSocket(pix_host, pix_port);
                //socket.startHandshake();
                connection = new Connection(new PipeParser(), new MinLowerLayerProtocol(), socket);
            }
            if (connection != null) {
                QBP_Q21 hl7Msg = new QBP_Q21();
                hl7Msg.parse(msg);
                Message resp = sendAndRecv(connection, hl7Msg);
                //Message resp = sendAndRecv(connection, hl7Msg);
                LOGGER.debug("sendMessage PIX server response>" + resp.encode());
                return resp.encode();
            } else {
                LOGGER.error("FAIL message Connection object is null" + msg);
                throw new Exception("sendMessage - Connection object is null!");
            }
        } catch (Exception e) {
            LOGGER.error("FAIL PIX QUERY message " + msg, e);

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return "";
    }

    public AuditAtnaService getAuditService() {

        return aas;
    }

    protected void initSSl() {

        SSLLocalClient.sslSetup(sslConfigSetting);
        //must setup secure sockets also
    }

    private void init() {
        // parse endpoint into host and port
        try {
//            config = ConfigurationService.getInstance();
//            String endpoint = config.getEndPoint(ConfigurationService.ENDPOINT_TYPE_PIX);
//            String[] parts = endpoint.split(":");
//
//            if (parts != null && parts.length > 0) {
//                host = parts[0];
//                if (parts.length > 1) {
//                    String tmp = parts[1];
//                    port = Integer.parseInt(tmp);
//                }
//
//            }


            pix_host = ConfigurationService.getInstance().getProperty("pix.host").trim();
            String port = ConfigurationService.getInstance().getProperty("pix.port").trim();
            String tls = ConfigurationService.getInstance().getProperty("pix.tls").trim();

            pix_tls = Boolean.parseBoolean(tls);
            pix_port = Integer.parseInt(port);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }

        LOGGER.debug("Configured pix_host=" + pix_host + " pix_port=" + pix_port + " pix_tls=" + pix_tls);

        try {
            aas = new AuditAtnaService();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * Sending a patient registration audit message... if performing a general
     * query
     *
     * @param protocolId
     * @param attrs
     * @return
     */
    public boolean sendAuditMessage(String protocolId, Map<String, String> attrs) {
        boolean result = false;

        if (protocolId != null && protocolId.length() > 1) {
            try {
                if (aas != null) {
                    aas.sendAuditMessageForPatientRegistration(protocolId);
                }
            } catch (Exception e) {
                LOGGER.error("error protocolId=" + protocolId, e);
                e.printStackTrace();
            }
        }

        return result;
    }

    public Message sendAndRecvPixMessage(Message message) throws Exception {
        return sendAndRecvPixMessage(message, false);
    }

    public Message sendAndRecvPixMessage(Message message, boolean inXML) throws Exception {

        if (message == null) {
            throw new Exception("Message message ?  null");
        }

        ConnectionHub connectionHub = null;
        Connection connection = null;

        Message response = null;

        try {
            connectionHub = ConnectionHub.getInstance();
            if (inXML) {
                connection = connectionHub.attach(pix_host, pix_port,
                        new DefaultXMLParser(), MinLowerLayerProtocol.class);
            } else {
                connection = connectionHub.attach(pix_host, pix_port, new PipeParser(),
                        MinLowerLayerProtocol.class);
            }

            // The initiator is used to transmit unsolicited messages
            Initiator initiator = connection.getInitiator();
            initiator.setTimeoutMillis(1000000);
            response = initiator.sendAndReceive(message);
            String responseString = "";
            if (inXML) {
                responseString = (new DefaultXMLParser()).encode(response);
            } else {
                responseString = (new PipeParser()).encode(response);
            }
        } catch (HL7Exception e) {
            LOGGER.error(" message =" + message.toString(), e);
            e.printStackTrace();
        } catch (LLPException e) {
            LOGGER.error(" message =" + message.toString(), e);
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(" message=" + message.toString(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error(" message " + message.toString(), e);
            e.printStackTrace();

        } finally {
            try {
                if (connection != null) {
                    if (connectionHub != null) {
                        connectionHub.discard(connection);
                    }
                    connection.close();
                }

                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (Exception e) {
                LOGGER.error(
                        " connection close or connectionhub disgard error ", e);
                e.printStackTrace();

            }
        }

        return response;
    }

//
//    public Map<String, String> getPatientByPhrId(String phrId) {
//
//        Map<String, String> resultMap = null;
//        try {
//            if (phrId != null && phrId.length() > 1) {
//
//                resultMap = getPatientPIDById(phrId,
//                        IDENTIFIER_NAMESPACE_PHR_ID);
//
//            } else {
//                throw new Exception("phrId null or blank");
//            }
//        } catch (Exception e) {
//            LOGGER.error("phrId=" + phrId, e);
//            e.printStackTrace();
//        }
//        return resultMap;
//    }

    /*
     * @deprecated public PID getPatientPIDById(String identifierValue,String
     * namespace) throws HL7Exception, LLPException, IOException, Exception {
     */
    /*
     * the 3rd field of QPD should have the identifier you know and the 4th
     * field should have the domain that you want to know its corresponding
     * identifier.
     *
     * qpd.getField(3).parse(the_phr_id_associated_by_nurse_ellen + "^^^PHR");
     * qpd.getField(4).parse("^^^icardea.pix");
     */
//    public Map<String, String> getPatientPIDById(String identifierValue,
//            String namespace) throws HL7Exception, LLPException, IOException,
//            Exception {
//        Map<String, String> resultMap = new HashMap<String, String>();
//
//        if (identifierValue != null) {
//            //
//        } else {
//            throw new Exception("field or field value is null");
//        }
//
//        QBP_Q21 a = new QBP_Q21();
//        // Construct MSH according to C2.2 of ITI TF-2x
//        HL7Utils.createHeader(a.getMSH(), "2.5");
//        a.getMSH().getMsh9_MessageType().parse("QBP^Q22^QBP_Q21");
//        // Set UTF-8 character set? See:
//        // http://wiki.hl7.org/index.php?title=Character_Set_used_in_v2_messages
//        a.getMSH().getCharacterSet(0).setValue("UNICODE UTF-8");
//
//        // Set Sending app identification
//        // a.getMSH().getSendingApplication().parse("testclient^icardea");
//
//        a.getMSH().getSendingApplication().parse(APPLICATION_NAME);
//        a.getMSH().getSendingFacility().parse("SALK^icardea");
//        // Set Receiving app identification
//        a.getMSH().getReceivingApplication().parse("PID^icardea");
//
//        a.getMSH().getReceivingFacility().parse("iCARDEAPlatform");
//
//        QPD qpd = a.getQPD();
//        qpd.getQpd1_MessageQueryName().parse("IHE PDQ Query");
//        qpd.getQpd2_QueryTag().setValue("query-1"); // A query identifier
//
//        int k = 0;
//
//
//        if (namespace.equalsIgnoreCase(IDENTIFIER_NAMESPACE_PHR_ID)) {
//            qpd.getField(3, k++).parse(
//                    identifierValue + "^^^" + IDENTIFIER_NAMESPACE_PHR_ID); // the_phr_id_associated_by_nurse_ellen
//            // +
//            // "^^^PHR");
//            qpd.getField(4, k++).parse(
//                    "^^^" + IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // domain
//            // ^^^icardea.pix
//
//        } else if (namespace.equalsIgnoreCase(IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA)) {
//            // from domain is same as to domain ??
//            qpd.getField(3, k++).parse(
//                    identifierValue + "^^^"
//                    + IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // the_phr_id_associated_by_nurse_ellen
//            // +
//            // "^^^PHR");
//
//            qpd.getField(4, k++).parse(
//                    "^^^" + IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // domain
//            // ^^^icardea.pix
//        } else {
//            throw new Exception("namespace not supported " + namespace);
//        }
//
//        int num = qpd.getField(3).length;
//        for (int i = 0; i < num; ++i) {
//            String nv = qpd.getField(3, i).encode();
//            int ind = nv.indexOf('^');
//            String trait = nv.substring(0, ind);
//            String val = nv.substring(ind + 1);
//            System.out.println(trait + "=" + val);
//        }
//
//        a.getRCP().getRcp1_QueryPriority().setValue("I"); // immediate mode
//        // response
//
//        Message response = this.sendAndRecvPixMessage(a);
//        if (response != null) {
//
//
//            // no v231 K23
//            ca.uhn.hl7v2.model.v25.message.RSP_K23 k23Response = (ca.uhn.hl7v2.model.v25.message.RSP_K23) response;
//
//
//            ca.uhn.hl7v2.model.v25.segment.PID pidSegment = k23Response.getQUERY_RESPONSE().getPID();
//            ca.uhn.hl7v2.model.v25.datatype.CX[] ids = pidSegment.getPatientIdentifierList();
//
//            String protocol_id = null;
//            for (ca.uhn.hl7v2.model.v25.datatype.CX cx : ids) {
//                String dom_id = cx.getAssigningAuthority().getUniversalID().getValue();
//                if (dom_id.equals(IDENTIFIER_NAMESPACE_UNIVERSAL_ICARDEA)) {// 1.2.826.0.1.3680043.2.44.248240.1
//
//                    protocol_id = cx.getIDNumber().getValue();
//
//                    break;
//                }
//
//
//            }
//
//            if (protocol_id != null) {
//                resultMap.put(PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID,
//                        protocol_id);
//
//                String name = pidSegment.getName();
//                String dob = pidSegment.getDateTimeOfBirth().encode();
//                if (name != null && name.length() > 0) {
//                    resultMap.put(
//                            PhrsConstants.IDENTIFIER_TYPE_PIX_PATIENT_NAME,
//                            name);
//                }
//                if (dob != null && dob.length() > 0) {
//                    resultMap.put(
//                            PhrsConstants.IDENTIFIER_TYPE_PIX_DATE_OF_BIRTH,
//                            dob);
//                }
//            }
//        }
//        // return this.sendAndRecv(a);
//
//        return resultMap;
//    }
    public PID getPatientMessage(String fam_name, String giv_name, String sex,
                                 String dob) {

        PID pid = null;
        try {
            Message message = this.pdq(fam_name, giv_name, sex, dob);
        } catch (HL7Exception e) {
            e.printStackTrace();
        } catch (LLPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pid;
    }

    // a.getMSH().getSendingApplication().parse("testclient^icardea");
    protected Message pdq(String fam_name, String giv_name, String sex,
                          String dob) throws HL7Exception, LLPException, IOException,
            Exception {

        QBP_Q21 a = new QBP_Q21();
        // Construct MSH according to C2.2 of ITI TF-2x
        HL7Utils.createHeader(a.getMSH(), "2.5");
        a.getMSH().getMsh9_MessageType().parse("QBP^Q22^QBP_Q21");
        // Set UTF-8 character set? See:
        // http://wiki.hl7.org/index.php?title=Character_Set_used_in_v2_messages
        a.getMSH().getCharacterSet(0).setValue("UNICODE UTF-8");

        // Set Sending app identification
        // a.getMSH().getSendingApplication().parse("testclient^icardea");
        a.getMSH().getSendingApplication().parse(APPLICATION_NAME);

        a.getMSH().getSendingFacility().parse("SALK^icardea");
        // Set Receiving app identification
        a.getMSH().getReceivingApplication().parse("PID^icardea");
        a.getMSH().getReceivingFacility().parse("iCARDEAPlatform");
        QPD qpd = a.getQPD();
        qpd.getQpd1_MessageQueryName().parse("IHE PDQ Query");
        qpd.getQpd2_QueryTag().setValue("query-1"); // A query identifier

        final int QIP_FLD_NUM = 3;
        int k = 0;
        if (fam_name != null) {
            qpd.getField(QIP_FLD_NUM, k++).parse(FNAME_SEG_FLD + "^" + fam_name);
        }
        if (giv_name != null) {
            qpd.getField(QIP_FLD_NUM, k++).parse(GNAME_SEG_FLD + "^" + giv_name);
        }
        if (sex != null) {
            qpd.getField(QIP_FLD_NUM, k++).parse(SEX_SEG_FLD + "^" + sex);
        }
        if (dob != null) {
            qpd.getField(QIP_FLD_NUM, k++).parse(DOB_SEG_FLD + "^" + dob);
        }

        int num = qpd.getField(QIP_FLD_NUM).length;
        for (int i = 0; i < num; ++i) {
            String nv = qpd.getField(QIP_FLD_NUM, i).encode();
            int ind = nv.indexOf('^');
            String trait = nv.substring(0, ind);
            String val = nv.substring(ind + 1);
            System.out.println(trait + "=" + val);
        }
        a.getRCP().getRcp1_QueryPriority().setValue("I"); // immediate mode
        // response
        return this.sendAndRecvPixMessage(a);
    }

    private boolean useMessageDispatcher = true;

    public boolean isUseMessageDispatcher() {
        return useMessageDispatcher;
    }

    public void setUseMessageDispatcher(boolean useMessageDispatcher) {
        this.useMessageDispatcher = useMessageDispatcher;
    }

    public String updateProtocolIdFromUserProvidedCiedId(final String ownerUri, final String pixQueryIdUser, String pixQueryIdType) {
        return updateProtocolIdFromUserProvidedId(ownerUri, pixQueryIdUser, pixQueryIdType);
    }

    /**
     * @param ownerUri
     * @param pixQueryIdUser
     * @param idType
     */
    public String updateProtocolIdFromUserProvidedId(final String ownerUri, final String pixQueryIdUser, final String pixQueryIdType) {

        //if (!useMessageDispatcher) {
        return updateIdentifierFromUser(ownerUri, pixQueryIdUser, pixQueryIdType);

//        } else {
//            final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();
//
//            final Runnable task = new Runnable() {
//
//                @Override
//                public void run() {
//                    String pid = updateIdentifierFromUser(ownerUri, pixQueryIdUser, pixQueryIdType);
//                }
//            };
//
//            dispatcher.dispatch(task);
//        }
    }


    /**
     * @param ownerUri
     * @param pixQueryIdUser
     * @return pixProtocolId
     */
    public String updateIdentifierFromUser(final String ownerUri, final String pixQueryIdUser, final String pixQueryIdType) {
        return this.updateIdentifierFromUser(ownerUri, pixQueryIdUser, pixQueryIdType, false);
    }

    /**
     * @param ownerUri
     * @param pixQueryIdUser "cied" pixQueryIdType -->Serial number or "pid"
     *                       pixQueryIdType --> protocolId
     * @param pixQueryIdType cied or pid
     * @param requeryPix     requery even if there is a current PIX protocol ID
     * @return The protocolId if successful. Either from PIX or a test
     *         protocolId enter via the UI
     */
    public String updateIdentifierFromUser(final String ownerUri, final String pixQueryIdUser, final String pixQueryIdType, boolean requeryPix) {

        String theProtocolId = null;
        LOGGER.debug("updateIdentifierFromUser Start  ");


        if (ownerUri != null && pixQueryIdUser != null && !pixQueryIdUser.isEmpty() && pixQueryIdType != null && !pixQueryIdType.isEmpty()) {

            LOGGER.debug("updateIdentifierFromUser from pixQueryIdUser to protocolId for  ownerUri=" + ownerUri
                    + " pixQueryIdType" + pixQueryIdType + " pixQueryIdUser=" + pixQueryIdUser);

            CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();

            PhrFederatedUser pfu = commonDao.getPhrUser(ownerUri);
            if (pfu == null) {
                LOGGER.debug("updateIdentifiers PhrFederatedUser not found for ownerUri=" + ownerUri);
            }

            //null, unless the UI pixQueryIdType  is "pid", not "cied"

            if (pfu != null) {
                boolean changedInput = false;//should requery
                //compare just serial number
                if (pfu.getPixQueryIdUser() != null && !pixQueryIdUser.equals(pfu.getPixQueryIdUser())) {
                    //saveResource=true;
                    pfu.setPixQueryIdUser(pixQueryIdUser);
                    changedInput = true;
                }
                if (pfu.getPixQueryIdType() != null && !pixQueryIdType.equals(pfu.getPixQueryIdType())) {
                    //saveResource=true;
                    pfu.setPixQueryIdType(pixQueryIdType);
                    changedInput = true;
                }

                //What kind of id?
                String type = pixQueryIdType;
                type = type.trim();
                //provided a PID prototocol ID?
                if (type.startsWith(PIX_QUERY_TYPE_PREFIX_PID)) {
                    //null, unless the UI pixQueryIdType  is "pid", not "cied"

                    pfu.setProtocolIdUser(pixQueryIdUser);

                    String protocolIdUser = pixQueryIdUser;
                    LOGGER.debug("updateIdentifiers LOCAL PID for  protocolIdUser ownerUri=" + ownerUri
                            + " info.getProtocolIdUser()= " + protocolIdUser + " info.getPixQueryIdUser()=" + pixQueryIdUser);
                    //deprecated commonDao.registerProtocolId(ownerUri, protocolIdUser, null);

                    theProtocolId = pfu.getProtocolId(); //protocolIdUser;

                } else {
                    //CIED type contains model info, but remove cied part
                    StringBuffer pixQueryString = new StringBuffer();
                    boolean okPixQuery = false;

                    //Query? if exists, requery only if indicated (requeryPix) or if critical inputs changed type or serial number
                    if (pfu.getProtocolIdPix() != null && !pfu.getProtocolIdPix().isEmpty()) {
                        //exists                     
                        if (requeryPix || changedInput) {
                            okPixQuery = true;
                        }
                    } else {
                        okPixQuery = true;
                    }

                    if (!okPixQuery) {
                        LOGGER.debug("updateIdentifiers No PIX query performed - no change in input type or number AND PID protocolPIX exists "
                                + " protocolIdPIX= " + pfu.getProtocolIdPix()
                                + " ownerUri= " + ownerUri
                                + " pixQueryIdType= " + pixQueryIdType
                                + " type= " + type);
                    } else {
                        if (type.startsWith("cied:")) {
                            type = type.replaceFirst("cied:", type);
                        }
                        //  else if(type.startsWith(PIX_QUERY_TYPE_PREFIX_CIED)) {

                        pixQueryString.append("model:");

                        pixQueryString.append(type);

                        // pixQueryString.append(PIX_QUERY_TYPE_DEFAULT);

                        pixQueryString.append("/serial:");
                        pixQueryString.append(pixQueryIdUser);

                        LOGGER.debug("updateIdentifiers CIED for  protocolIdUser ownerUri=" + ownerUri
                                + " pixQueryIdType= " + pixQueryIdType
                                + " type= " + type + " pixQueryString= " + pixQueryString);

                        //Is there a Pix prototcol ID already saved?

                        try {

                            LOGGER.debug("updateIdentifiers Pix pixQueryString " + pixQueryString);

                            //do query
                            String pixProtocolId = getPatientProtocolIdByCIED(pixQueryString.toString());

                            if (pixProtocolId != null && !pixProtocolId.isEmpty()) {
                                pfu.setProtocolIdPix(pixProtocolId);

                                LOGGER.debug("updateIdentifiers: write user based protocol ID, a pix Protocol Id was found for =" + ownerUri
                                        + " info.pixProtocolId()= " + pixProtocolId + " info.getPixQueryIdUser()=" + pixQueryIdUser);

                                //deprecated commonDao.registerProtocolId(ownerUri, pixProtocolId, null);//do first, if error we do not write pfu
                                //commonDao.crudSaveResource(pfu, pfu.getOwnerUri(), pfu.getCreatorUri());
                                theProtocolId = pixProtocolId;
                            }


                        } catch (Exception e) {
                            LOGGER.error("Pix exception ", e);
                        }
                    }

                }
                //saving at least the input form data, even if okPixQuery=false or PID type
                commonDao.crudSaveResource(pfu, pfu.getOwnerUri(), pfu.getCreatorUri());

            }


        }

        return theProtocolId;
    }
}
