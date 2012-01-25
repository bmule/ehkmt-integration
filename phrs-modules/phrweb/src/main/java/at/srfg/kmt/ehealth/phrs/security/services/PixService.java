package at.srfg.kmt.ehealth.phrs.security.services;

/*
 import static gr.forth.ics.icardea.pid.iCARDEA_Patient_CONSTANTS.ACCNUM_SEG_FLD;
 import static gr.forth.ics.icardea.pid.iCARDEA_Patient_CONSTANTS.DOB_SEG_FLD;
 import static gr.forth.ics.icardea.pid.iCARDEA_Patient_CONSTANTS.FNAME_SEG_FLD;
 import static gr.forth.ics.icardea.pid.iCARDEA_Patient_CONSTANTS.GNAME_SEG_FLD;
 import static gr.forth.ics.icardea.pid.iCARDEA_Patient_CONSTANTS.SEX_SEG_FLD;
 */

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.PixIdentifier;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.message.QBP_Q21;
import ca.uhn.hl7v2.model.v25.message.RSP_K23;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.QPD;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import gr.forth.ics.icardea.pid.HL7Utils;
import static gr.forth.ics.icardea.pid.PatientIndexConstants.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 
 import ca.uhn.hl7v2.model.v25.datatype.CX;
 import ca.uhn.hl7v2.model.v25.message.QBP_Q21;
 import ca.uhn.hl7v2.model.v25.message.RSP_K23; //reponse
 import ca.uhn.hl7v2.model.v25.segment.PID; // For iCARDEA_PATIENT construtor
 import ca.uhn.hl7v2.model.v25.segment.QPD;
 */

/**
 * 
 * From ConfigurationService get the endpoint
 * 
 * Includes code snippets from FORTH and SRDC
 */
@SuppressWarnings("serial")
public class PixService implements Serializable {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(PixService.class);

	public final static String IDENTIFIER_TYPE_PROTOCOL_ID = "PROTOCOL";
	public final static String IDENTIFIER_TYPE_CIED = "CIED";
	public final static String IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA = "icardea.pix";
	public final static String IDENTIFIER_NAMESPACE_PHR_ID = "PHR";
        
        public final static String ICARDEA_PIX_OID = "1.2.826.0.1.3680043.2.44.248240.1";
	public final static String IDENTIFIER_NAMESPACE_UNIVERSAL_ICARDEA = ICARDEA_PIX_OID;
 
	// String endPoint = "localhost:8080";
	String host = "localhost";

	int port = 2575;
	public final static String APPLICATION_NAME = "PHR"; // config.ini
															// namespace=PHR

	// testclient^icardea
	// private static PatientIndex pid = null;
	// private static ConnectionHub connectionHub = null;
	private ConfigurationService config;
	private AuditAtnaService aas;

	private String domainDefault = IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA;

	public PixService() {
		initSSl();
		init();

	}

	public AuditAtnaService getAuditService() {

		return aas;
	}

	protected void initSSl() {
            
            SSLLocalClient.sslSetup();
            //must setup secure sockets also
	}

	private void init() {
		// parse endpoint into host and port
		try {
			config = ConfigurationService.getInstance();
			String endpoint = config
					.getEndPoint(ConfigurationService.ENDPOINT_TYPE_PIX);
			String[] parts = endpoint.split(":");

			if (parts != null && parts.length > 0) {
				host = parts[0];
				if (parts.length > 1) {
					String tmp = parts[1];
					port = Integer.parseInt(tmp);
				}

			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		LOGGER.debug("Configured PIX host=" + host + " port" + port);

		try {
			aas = new AuditAtnaService();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
        /**
         * Sending a patient registration audit message... if performing a general query
         * @param protocolId
         * @param attrs
         * @return 
         */
	public boolean sendAuditMessage(String protocolId, Map<String, String> attrs) {
		boolean result = false;

		if (protocolId != null && protocolId.length() > 1) {
			try {
				if (aas != null)
					aas.sendAuditMessageForPatientRegistration(protocolId);
			} catch (Exception e) {
				LOGGER.error("error protocolId=" + protocolId, e);
				e.printStackTrace();
			}
		}

		return result;
	}

	/*
	 * public ICARDEA_Patient createPatient(PID pidSegment){ iCARDEA_Patient
	 * patient = ICARDEA_Patient.create_from_PID( pidSegment); return patient;
	 * 
	 * }
	 */
	/*
	 * public String pixQuerySample(String pid, String fromDomain, String
	 * toDomain,boolean query_23) throws Exception {
	 * 
	 * DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); Date date
	 * = new Date(); String dateString = dateFormat.format(date); String
	 * protocolId=null;
	 * 
	 * if(query_23){ String qpd_23 =
	 * "MSH|^~\\&|PHR|SRFG|PAT_IDENTITY_X_REF_MGR|FORTH|" + dateString +
	 * "||QBP^Q23|8686183982575368499|P|2.5||||||||\r"; qpd_23 +=
	 * "QPD|QRY_1001^Query for Corresponding Identifiers^IHEDEMO|QRY10501108|" +
	 * pid + "^^^" + fromDomain + "|^^^" + toDomain + "||||\r"; qpd_23 +=
	 * "RCP|I||||||\r";
	 * 
	 * } else {
	 * 
	 * //2.5 String qpd_25 = "MSH|^~\\&|PHR|SRFG|PAT_IDENTITY_X_REF_MGR|FORTH|"
	 * + dateString + "||QBP^Q23|8686183982575368499|P|2.5||||||||\r"; qpd_25 +=
	 * "QPD|QRY_1001^Query for Corresponding Identifiers^IHEDEMO|QRY10501108|" +
	 * pid + "^^^" + fromDomain + "|^^^" + toDomain + "||||\r"; qpd_25 +=
	 * "RCP|I||||||\r";
	 * 
	 * Parser p = new GenericParser(); Message message_25 = p.parse(qpd_25);
	 * Message response_25 = sendAndRecvPixMessage(message_25); RSP_K25 rspk23 =
	 * (RSP_K25) response_25;
	 * 
	 * ca.uhn.hl7v2.model.v25.segment.PID pidSegment =
	 * rspk25.getQUERY_RESPONSE().getPID();
	 * 
	 * CX patientID = pidSegment.getPid3_PatientIdentifierList(0); String value
	 * = patientID.getCx1_IDNumber().getValue(); RSP_K23 rspk25 = (RSP_K23)
	 * response_25;
	 * 
	 * ca.uhn.hl7v2.model.v25.segment.PID pidSegment =
	 * rspk25.getQUERY_RESPONSE().getPID(); //2.5
	 * pidSegment.getPid3_PatientIdentifierList(0); might not be protocol
	 * id,must iterate V2.3 message? CX patientID =
	 * pidSegment.getPid3_PatientIdentifierList(0); protocolId =
	 * patientID.getCx1_IDNumber().getValue(); }
	 * 
	 * //QBP_Q21
	 * 
	 * //sent 2.5
	 * 
	 * //ORU_R01 oruR01 //PID pidSegment =
	 * oruR01.getPATIENT_RESULT().getPATIENT().getPID();
	 * 
	 * 
	 * 
	 * return protocolId; }(
	 * 
	 * /* public String pixIdentifiersQueryProtocolId(String
	 * identifierValue,String fromDomain){ //Map<String, String> map = new
	 * HashMap<String,String>();; String protocol_id = null; try { Date date =
	 * new Date(); DateFormat dateFormat = new
	 * SimpleDateFormat("yyyyMMddHHmmss"); String dateString =
	 * dateFormat.format(date);
	 * 
	 * String qpd =
	 * "MSH|^~\\&|testclient^icardea|SALK^icardea|PID^icardea|iCARDEAPlatform|"
	 * + dateString + "||QBP^Q23|1235421946|P|2.5|||||||\r"; //+ dateString +
	 * "||QBP^Q23^QBP_Q21|1235421946|P|2.5|||||||\r"; qpd +=
	 * "QPD|IHEPIXQuery|Q231235421946|"
	 * +identifierValue+"^^^PHR"+"|^^^icardea.pix"+"||||\r";
	 * //PHR-ID,PHR,icardea.pix or 1.2.826.0.1.3680043.2.44.248240.1
	 * //"|^^^icardea.pix" |^^^1.2.826.0.1.3680043.2.44.248240.1 //qpd +=
	 * "RCP|I| ";
	 * 
	 * qpd += "RCP|I||||||\r"; Parser p = new GenericParser(); Message message =
	 * p.parse(qpd); Message response = sendAndRecvPixMessage(message);
	 * 
	 * 
	 * RSP_K23 rspk23 = (RSP_K23) response; ca.uhn.hl7v2.model.v25.segment.PID
	 * pidSegment = rspk23.getQUERY_RESPONSE().getPID(); //RSP_K23 rspk23 =
	 * (RSP_K23) response; //ca.uhn.hl7v2.model.v231.segment.PID pidSegment =
	 * rspk23.getQUERY_RESPONSE().getPID();
	 * 
	 * 
	 * ca.uhn.hl7v2.model.v25.datatype.CX patientID=
	 * pidSegment.getPid3_PatientIdentifierList(0); String value =
	 * patientID.getCx1_IDNumber().getValue();
	 * 
	 * ca.uhn.hl7v2.model.v25.datatype.CX[] ids =
	 * pidSegment.getPatientIdentifierList() ;
	 * 
	 * 
	 * //and then iterate to find the icardea.pix domain (in the code below I am
	 * using the universal id to make it more robust):
	 * 
	 * for (ca.uhn.hl7v2.model.v25.datatype.CX cx: ids) { String dom_id =
	 * cx.getAssigningAuthority().getUniversalID().getValue(); if
	 * (dom_id.equals("1.2.826.0.1.3680043.2.44.248240.1")) { protocol_id =
	 * cx.getCx1_IDNumber().getValue(); //v2.3 cx.getID().getValue(); break; } }
	 * 
	 * } catch (EncodingNotSupportedException e) { e.printStackTrace(); } catch
	 * (HL7Exception e) { e.printStackTrace(); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return protocol_id; }
	 */
	public Message sendAndRecvPixMessage(Message message) throws Exception {
		return sendAndRecvPixMessage(message, false, host, port);
	}

	public Message sendAndRecvPixMessage(Message message, boolean reponseInXML)
			throws Exception {
		return sendAndRecvPixMessage(message, reponseInXML, host, port);
	}
        

	public Message sendAndRecvPixMessage(Message message, boolean inXML,
			String host, int port) throws Exception {

		if (message == null)
			throw new Exception("Message message ?  null");

		ConnectionHub connectionHub = null;
		Connection connection = null;

		Message response = null;

		try {
			connectionHub = ConnectionHub.getInstance();
			if (inXML) {
				connection = connectionHub.attach(host, port,
						new DefaultXMLParser(), MinLowerLayerProtocol.class);
			} else {
				connection = connectionHub.attach(host, port, new PipeParser(),
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
					if (connectionHub != null)
						connectionHub.discard(connection);
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

	/*
	 * public Message sendAndRecv(Message msg) throws LLPException,
	 * HL7Exception, IOException { // The connection hub connects to listening
	 * servers // A connection object represents a socket attached to an HL7
	 * server ConnectionHub connectionHub = ConnectionHub.getInstance();
	 * 
	 * Connection connection = connectionHub.attach(host, port, new
	 * PipeParser(), MinLowerLayerProtocol.class);
	 * 
	 * // The initiator is used to transmit unsolicited messages Initiator
	 * initiator = connection.getInitiator(); Message response =
	 * initiator.sendAndReceive(msg);
	 * 
	 * return response; }
	 * 
	 * 
	 * public String validPatientByProtocolId(PixIdentifier pixIdentifier) {
	 * Map<String, String> queryResultMap = null;
	 * 
	 * return this.validPatientByProtocolId(pixIdentifier, queryResultMap); }
	 */



	/**
	 * Use the phrId to get the patient identity info and update the pixIdentifier object
	 * @param phrId
	 * @param pixIdentifier
	 * @return status code
	 */
	public String validatePatientProtocolId(String phrId,
			PixIdentifier pixIdentifier,
			Map<String,String> resultsMap) {
		
		return this.validatePatientProtocolId(phrId, pixIdentifier,resultsMap, false);
		
	}
	/**
	 * Use the phrId or known protocolId to get the patient identity info and update the pixIdentifier object
	 * @param phrId
	 * @param pixIdentifier
	 * @param useProtocolId in pixIdentifier = true, otherwise use phrId to find protocol ID and patient identity
	 * @return status code
	 */
	public String validatePatientProtocolId(String phrId,
			PixIdentifier pixIdentifier, 
			Map<String,String> resultsMap,
			boolean useProtocolId) {

		String knownStatus = null;
		
		//correct a problem
		if(pixIdentifier==null){
			pixIdentifier = new PixIdentifier();
			useProtocolId = false;
		}
		
		if( !useProtocolId && (phrId==null || (phrId!=null && phrId.length()<1))){
			//fail


			knownStatus = pixIdentifier.getStatus();

			String protocolId = null;

			try {
				//phrId
				
				String pixProtocolId = null;
				if(useProtocolId && pixIdentifier.getIdentifier()!=null && pixIdentifier.getIdentifier().length()>1){
					//ok
				} else {
					useProtocolId = false;
				}
				
				if(useProtocolId){				
					boolean exists = validProtocolId(pixIdentifier.getIdentifier(),resultsMap);// use default namespace of  domain
				
				} else {
					pixProtocolId = getPatientProtocolIdByPhrId(phrId,resultsMap);// use default namespace of  domain
				
				}
				
				if(pixProtocolId!=null) pixProtocolId = pixProtocolId.trim();
				
				if (pixProtocolId != null && pixProtocolId.length() > 1) { // fail for many reasons
					knownStatus = PhrsConstants.IDENTIFIER_STATUS_VALID;
					pixIdentifier.setStatus(knownStatus);
					pixIdentifier.setIdentifier(pixProtocolId);
					pixIdentifier.setDomain(IDENTIFIER_NAMESPACE_PHR_ID);
				} else {
					knownStatus = PhrsConstants.IDENTIFIER_STATUS_INVALID;
					pixIdentifier.setStatus(knownStatus);
					//TODO SHOULD we remove any unconfirmed id? Issue during testing
					//pixIdentifier.setIdentifier(null);
					//pixIdentifier.setDomain(null);
					LOGGER.error(" Pix query returned protocol_id  =null for phrId="+phrId);
				}
			} catch (Exception e) {
				LOGGER.error(
						" protocolId protocolId to validate=" + protocolId, e);
				e.printStackTrace();
			}
		} else {
			LOGGER.error(
					" validate phrId ==null ");
		}
		if (knownStatus == null) {
			knownStatus = PhrsConstants.IDENTIFIER_STATUS_UNKNOWN;
			pixIdentifier.setStatus(knownStatus);
		}

		return knownStatus;
	}

	/**
	 * Is this known to the system?
	 * 
	 * @param protocolId
	 * @param pidResults
	 * @return
	 */
	public boolean validProtocolId(String protocolId,
			Map<String, String> pidResults) {
		boolean flag = false;

		String pixProtocolId = getPatientProtocolIdByPhrId(protocolId,
				pidResults);// use default
		if (pixProtocolId != null) {
			flag = true;
		}
		return flag;
	}

	/*
	 * PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID
	 * PhrsConstants.IDENTIFIER_TYPE_PIX_PATIENT_NAME
	 * PhrsConstants.IDENTIFIER_TYPE_PIX_DATE_OF_BIRTH
	 */
	public String getPatientProtocolIdByPhrId(String phrId,
			Map<String, String> pidResult) {

		String protocolId = null;
		try {
			if (phrId != null && phrId.length() > 1) {

				pidResult = getPatientByProtocolId(phrId);

				if (pidResult == null) {
					pidResult = new HashMap<String, String>();
				}
				if (pidResult != null
						&& pidResult
								.containsKey(PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID)) {
					protocolId = pidResult
							.get(PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID);
				}

			} else {
				throw new Exception("phrId null or blank");
			}
		} catch (Exception e) {
			LOGGER.error("phrId=" + phrId, e);
			e.printStackTrace();
		}
		return protocolId;
	}

	public Map<String, String> getPatientByPhrId(String phrId) {

		Map<String, String> resultMap = null;
		try {
			if (phrId != null && phrId.length() > 1) {

				resultMap = getPatientPIDById(phrId,
						IDENTIFIER_NAMESPACE_PHR_ID);

			} else {
				throw new Exception("phrId null or blank");
			}
		} catch (Exception e) {
			LOGGER.error("phrId=" + phrId, e);
			e.printStackTrace();
		}
		return resultMap;
	}

	public Map<String, String> getPatientByProtocolId(String protocolId)
			throws Exception {

		Map<String, String> resultMap = null;
		try {
			if (protocolId != null && protocolId.length() > 1) {

				resultMap = getPatientPIDById(protocolId,
					IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA);
				}
			} catch (Exception e) {
				LOGGER.error("protocolId=" + protocolId, e);
				e.printStackTrace();
			}
		return resultMap;
	}
        
        /*
         NEW TO INTEGRATE
        	public static void setup() throws Exception {
		//pid = new PatientIndexServerTest2();
		//pid.run(new String[]{"../../icardea-config/config.ini"});
		
		PipeParser p = new PipeParser();
		Socket socket = null;

		if (pid.usesTLS()) {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket("localhost", pid.port());
			sslsocket.startHandshake();
			socket = sslsocket;
		}
		else
			socket = new Socket("localhost", 2575);//pid.port());
                
		connection = new Connection(p, new MinLowerLayerProtocol(), socket);
	}
	public void testQuery() throws HL7Exception, LLPException, IOException {

		// PIX Query
		//MSH|^~\&|PACS_FUJIFILM|FUJIFILM|PAT_IDENTITY_X_REF_MGR_MISYS|ALLSCRIPTS|20090223144546||QBP^Q23^QBP_Q21|1235421946|P|2.5|||||||
		//QPD|IHEPIXQuery|Q231235421946|103^^^icardea|^^^ORBIS
		//RCP|I|

		QBP_Q21 a = new QBP_Q21();		
		// Construct MSH according to C2.2 of ITI TF-2x
		HL7Utils.createHeader(a.getMSH(), "2.5");
		a.getMSH().getMsh9_MessageType().parse("QBP^Q23^QBP_Q21");
		// Set UTF-8 character set? See:
		// http://wiki.hl7.org/index.php?title=Character_Set_used_in_v2_messages
		a.getMSH().getCharacterSet(0).setValue("UNICODE UTF-8");

		// Set Sending app identification
		a.getMSH().getSendingApplication().parse("testclient^icardea");
		a.getMSH().getSendingFacility().parse("SALK^icardea");
		// Set Receiving app identification
		a.getMSH().getReceivingApplication().parse("PID^icardea");
		a.getMSH().getReceivingFacility().parse("iCARDEAPlatform");
		QPD qpd = a.getQPD();
		qpd.getQpd1_MessageQueryName().parse("IHE PIX Query");
		qpd.getQpd2_QueryTag().setValue("pix-query-1"); // A query identifier
		CX from = new CX(a);
		from.getCx1_IDNumber().setValue("o103"); // we search for this id in the iCARDEA domain

		from.getCx4_AssigningAuthority().getHd2_UniversalID().setValue(ICARDEA_PIX_OID);
		from.getCx4_AssigningAuthority().getHd3_UniversalIDType().setValue("ISO");

		qpd.getField(3,0).parse(from.encode());
		
		String toDomain = "ORBIS";
		
		a.getRCP().getRcp3_ResponseModality().getCe1_Identifier().setValue("I");
                
		Message m = this.sendAndRecv(a);
                
		RSP_K23 ret = (RSP_K23) m;
		System.out.println("***PIX*****\n"+ret.encode()+"\n+++++++");
		String status =ret.getQAK().getQak2_QueryResponseStatus().getValue();
		if ("AE".equals(status)) {
			// Application Error!!
			// ...
		}
		else if ("NF".equals(status)){
			// Not Found!!!
		}
		else {
			PID pid = ret.getQUERY_RESPONSE().getPID();
			for (CX d: pid.getPatientIdentifierList()) {
				if (toDomain.equals(d.getAssigningAuthority().getHd1_NamespaceID().getValue())) {
					String id = d.getCx1_IDNumber().getValue();
					System.out.println("Found ID="+id+" in domain "+toDomain);
				}
			}
		}
	}
        	private Message sendAndRecv(Message msg) throws LLPException, HL7Exception, IOException {
		// The initiator is used to transmit unsolicited messages
		Initiator initiator = connection.getInitiator();
		Message response = initiator.sendAndReceive(msg);
		return response;
	}
*/
	/*
	 * public PID getPatientPIDById(String identifierValue,String namespace)
	 * throws HL7Exception, LLPException, IOException, Exception {
	 */

	public Map<String, String> getPatientPIDById(String identifierValue,
			String namespace) throws HL7Exception, LLPException, IOException,
			Exception {
		Map<String, String> resultMap = new HashMap<String, String>();

		if (identifierValue != null) {
			//
		} else {
			throw new Exception("field or field value is null");
		}

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

		int k = 0;

		/*
		 * the 3rd field of QPD should have the identifier you know and the 4th
		 * field should have the domain that you want to know its corresponding
		 * identifier.
		 * 
		 * qpd.getField(3).parse(the_phr_id_associated_by_nurse_ellen +
		 * "^^^PHR"); qpd.getField(4).parse("^^^icardea.pix");
		 */
		if (namespace.equalsIgnoreCase(IDENTIFIER_NAMESPACE_PHR_ID)) {
			qpd.getField(3, k++).parse(
					identifierValue + "^^^" + IDENTIFIER_NAMESPACE_PHR_ID); // the_phr_id_associated_by_nurse_ellen
																			// +
																			// "^^^PHR");
			qpd.getField(4, k++).parse(
					"^^^" + IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // domain
																		// ^^^icardea.pix

		} else if (namespace
				.equalsIgnoreCase(IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA)) {
			// from domain is same as to domain ??
			qpd.getField(3, k++).parse(
					identifierValue + "^^^"
							+ IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // the_phr_id_associated_by_nurse_ellen
																			// +
																			// "^^^PHR");

			qpd.getField(4, k++).parse(
					"^^^" + IDENTIFIER_NAMESPACE_PROTOCOL_ID_ICARDEA); // domain
																		// ^^^icardea.pix
		} else {
			throw new Exception("namespace not supported " + namespace);
		}

		int num = qpd.getField(3).length;
		for (int i = 0; i < num; ++i) {
			String nv = qpd.getField(3, i).encode();
			int ind = nv.indexOf('^');
			String trait = nv.substring(0, ind);
			String val = nv.substring(ind + 1);
			System.out.println(trait + "=" + val);
		}

		a.getRCP().getRcp1_QueryPriority().setValue("I"); // immediate mode
															// response

		Message response = this.sendAndRecvPixMessage(a);
		if (response != null) {

			// RSP_K23 resp = new RSP_K23();
//			
//			  RSP_K23 rspk23 = (RSP_K23) response;
//			  ca.uhn.hl7v2.model.v25.segment.PID pidSegment = rspk23
//			  .getQUERY_RESPONSE().getPID();
//			  
//			  if (pidSegment != null) { //v25 <-- RSP_K23 ??
//			  ca.uhn.hl7v2.model.v25.datatype.CX[] ids =
//			  pidSegment.getPatientIdentifierList();
//			  
//			  if (ids != null && ids.length > 0) {
//			  
//			  
//			  } } // ca.uhn.hl7v2.model.v25.message.QBP_Q21; //
//			  ca.uhn.hl7v2.model.v25.message.RSP_K23;
//			
			// no v231 K23
			ca.uhn.hl7v2.model.v25.message.RSP_K23 k23Response = (ca.uhn.hl7v2.model.v25.message.RSP_K23) response;

			// ca.uhn.hl7v2.model.v231.segment.PID pid231 =k23Response.get
			ca.uhn.hl7v2.model.v25.segment.PID pidSegment = k23Response
					.getQUERY_RESPONSE().getPID();
			ca.uhn.hl7v2.model.v25.datatype.CX[] ids = pidSegment
					.getPatientIdentifierList();

			String protocol_id = null;
			for (ca.uhn.hl7v2.model.v25.datatype.CX cx : ids) {
				String dom_id = cx.getAssigningAuthority().getUniversalID()
						.getValue();
				if (dom_id.equals(IDENTIFIER_NAMESPACE_UNIVERSAL_ICARDEA)) {// 1.2.826.0.1.3680043.2.44.248240.1

					// CX 231 protocol_id = cx.getID().getValue();
					protocol_id = cx.getIDNumber().getValue();

					break;
				}

				// ca.uhn.hl7v2.model.v25.segment.PID pid =
				// resp.getQUERY_RESPONSE().getPID();

			}
			
			 //PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID
			 // PhrsConstants.IDENTIFIER_TYPE_PIX_PATIENT_NAME
			 //PhrsConstants.IDENTIFIER_TYPE_PIX_DATE_OF_BIRTH
			 
			if (protocol_id != null) {
				resultMap.put(PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID,
						protocol_id);

				String name = pidSegment.getName();
				String dob = pidSegment.getDateTimeOfBirth().encode();
				if (name != null && name.length() > 0)
					resultMap.put(
							PhrsConstants.IDENTIFIER_TYPE_PIX_PATIENT_NAME,
							name);
				if (dob != null && dob.length() > 0)
					resultMap.put(
							PhrsConstants.IDENTIFIER_TYPE_PIX_DATE_OF_BIRTH,
							dob);
			}
		}
		// return this.sendAndRecv(a);

		return resultMap;
	}

	// MSH|^~\&|testclient^icardea|SALK^icardea|PID^icardea|iCARDEAPlatform|||QBP
	// ^Q23^QBP_Q21|1235421946|P|2.5|||||||
	// QPD|IHEPIXQuery|Q231235421946|PHR-ID^^^PHR|^^^icardea.pix RCP|I|
	//
	// the 3rd field of QPD should have the identifier you know and the 4th
	// field should have the domain that you want to know its corresponding
	// identifier. So I assume something similar to the code below should work
	// (please note that I haven't actually tested it):
	//
	// qpd.getField(3).parse(the_phr_id_associated_by_nurse_ellen + "^^^PHR");
	// qpd.getField(4).parse("^^^icardea.pix");
	//
	//
	// This code above is for parsing the request at the server and verifying
	// that it contains domains I know, either with their simple namespace id
	// (e.g. "icardea.pix") or with their universal id (e.g.
	// "1.2.826.0.1.3680043.2.44.248240.1").
	//
	// After getting the PID segment you get the "patient identifier list"
	// (field 3), like this:
	//
	// ca.uhn.hl7v2.model.v231.segment.PID pid = ... CX[] ids =
	// pid.getPatientIdentifierList() ;
	//
	// and then iterate to find the icardea.pix domain (in the code below I am
	// using the universal id to make it more robust):
	//
	// String protocol_id = null;
	// for (CX cx: ids) {
	// String dom_id =cx.getAssigningAuthority().getUniversalID().getValue();
	// if (dom_id.equals("1.2.826.0.1.3680043.2.44.248240.1")) {
	// protocol_id = cx.getID().getValue();
	// break;
	// }
	// }

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

	/**
	 * From FORTH test case
	 * 
	 * @param fam_name
	 * @param giv_name
	 * @param sex
	 * @param dob
	 * @return
	 * @throws HL7Exception
	 * @throws LLPException
	 * @throws IOException
	 */
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
		if (fam_name != null)
			qpd.getField(QIP_FLD_NUM, k++)
					.parse(FNAME_SEG_FLD + "^" + fam_name);
		if (giv_name != null)
			qpd.getField(QIP_FLD_NUM, k++)
					.parse(GNAME_SEG_FLD + "^" + giv_name);
		if (sex != null)
			qpd.getField(QIP_FLD_NUM, k++).parse(SEX_SEG_FLD + "^" + sex);
		if (dob != null)
			qpd.getField(QIP_FLD_NUM, k++).parse(DOB_SEG_FLD + "^" + dob);

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
	/*
	 * public String validPatientByProtocolId(String phrId, PixIdentifier
	 * pixIdentifier) {
	 * 
	 * String knownStatus = null;
	 * 
	 * if (pixIdentifier != null && pixIdentifier.getIdentifier()!=null &&
	 * pixIdentifier.getIdentifier().length()>1) { knownStatus =
	 * pixIdentifier.getStatus();
	 * 
	 * String protocolId = pixIdentifier.getIdentifier();
	 * 
	 * try { String pixProtocolId=
	 * getPatientProtocolIdById(protocolId,IDENTIFIER_NAMESPACE_PHR_ID);//use
	 * default namespace of domain
	 * 
	 * if (pixProtocolId != null) { // fail for many reasons pixProtocolId =
	 * pixProtocolId.trim();
	 * 
	 * if(pixProtocolId !=null && pixProtocolId.length()>1 && (pixProtocolId ==
	 * protocolId)){ knownStatus = PhrsConstants.IDENTIFIER_STATUS_VALID;
	 * pixIdentifier.setStatus(knownStatus); } else { knownStatus =
	 * PhrsConstants.IDENTIFIER_STATUS_INVALID;
	 * pixIdentifier.setStatus(knownStatus); } } else {
	 * LOGGER.error(" Pix query returned protocol_id  =null"); }
	 * 
	 * } catch (Exception e) {
	 * LOGGER.error(" protocolId protocolId to validate=" + protocolId, e);
	 * e.printStackTrace(); }
	 * 
	 * } else { LOGGER.error(" pixIdentifier object or identifier =NULL"); } if
	 * (knownStatus == null){ knownStatus =
	 * PhrsConstants.IDENTIFIER_STATUS_UNKNOWN;
	 * pixIdentifier.setStatus(knownStatus); }
	 * 
	 * return knownStatus; }
	 */
}
