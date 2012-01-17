package tr.com.srdc.icardea.atnalog.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
public class Audit {

    private static DateFormat ISO8601Local = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
    private InetAddress host_;
    private int port_;

    public Audit(String host, int port) throws UnknownHostException {
        this.host_ = InetAddress.getByName(host);
        this.port_ = port;
    }
    
	public String syslog_header(String appname) {
		String procID = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		String hostname = "";
		try {
		    InetAddress addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		} catch (UnknownHostException e) {
		}
		String dateStr = ISO8601Local.format (new Date());
		return "<80>1 " + dateStr + " " + hostname + " " + appname + " " + procID + " " + "IHE+RFC-3881"+ " - ";
	}
	public byte[] create_syslog_xml(String appname, String xml) {
		String header = syslog_header(appname);
		byte[] bheader = header.getBytes();
		byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
		byte[] bxml;
		try {
			bxml = xml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		
		int len = bheader.length + bom.length + bxml.length;
		ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
		buf.write(bheader, 0, bheader.length);
		buf.write(bom, 0, bom.length);
		buf.write(bxml, 0, bxml.length);
		
		return buf.toByteArray();
	}
	public void send_udp(byte[] msg) {
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(msg, msg.length, this.host_, this.port_);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    public static void main(String args[]) {
//		String dateStr = ISO8601Local.format (new Date());
//        String xml =
//            "<AuditMessage>"+
//            "    <EventIdentification EventOutcomeIndicator=\"0\" EventDateTime=\"" + dateStr + "\" EventActionCode=\"R\">"+
//            "        <EventID codeSystemName=\"DCM\" displayName=\"Export\" code=\"110106\"/>"+
//            "        <EventTypeCode codeSystemName=\"IHE Transactions\" displayName=\"Retrieve Document Set\" code=\"ITI-43\"/>"+
//            "    </EventIdentification>"+
//            "    <ActiveParticipant NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"129.162.101.164\" UserIsRequestor=\"false\" UserID=\"http://www.w3.org/2005/08/addressing/anonymous\">"+
//            "        <RoleIDCode codeSystemName=\"DCM\" displayName=\"Source\" code=\"110153\"/>"+
//            "    </ActiveParticipant>"+
//            "    <ActiveParticipant NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"129.162.101.116\" UserIsRequestor=\"true\" UserID=\"http://localhost:8020/axis2/services/xdsrepositoryb\">"+
//            "        <RoleIDCode codeSystemName=\"DCM\" displayName=\"Destination\" code=\"110152\"/>"+
//            "    </ActiveParticipant>"+
//            "    <AuditSourceIdentification AuditSourceID=\"MISYS\" AuditEnterpriseSiteID=\"MISYS\"/>"+
//            "    <ParticipantObjectIdentification ParticipantObjectTypeCodeRole=\"3\" ParticipantObjectTypeCode=\"2\" ParticipantObjectID=\"2.16.840.1.113883.3.65.2.1266421824214\">"+
//            "        <ParticipantObjectIDTypeCode code=\"9\"/>"+
//            "        <ParticipantObjectDetail value=\"MS4zLjYuMS40LjEuMjEzNjcuMjAxMC4xLjIuMTEyNQ==\" type=\"RepositoryUniqueId\"/>"+
//            "    </ParticipantObjectIdentification>"+
//            "</AuditMessage>";
//
//        try {
//            Audit a = new Audit("127.0.0.1", 2861);
//            a.send_udp( a.create_syslog_xml("testapp", xml) );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

	public static String createMessage(String messageType, String patientID, String code, String requesterRole){
		String xml = "";
		String date = ISO8601Local.format (new Date());
		if(messageType == "PCC-9"){		
			xml = 
				"<AuditMessage>"+
					"<EventIdentification EventActionCode="+"\"R\""+" EventDateTime="+"\""+date+"\""+" EventOutcomeIndicator="+"\"0\""+">"+
					"<EventID code="+"\""+code+"\""+"/>"+
						"<EventTypeCode code="+"\"PCC-9\""+" codeSystemName="+"\"IHE Transactions\""+"/>"+
					"</EventIdentification>"+
					"<ActiveParticipant UserID="+"\"Adaptive Care Planner\""+">"+
					"</ActiveParticipant>"+
					"<ActiveParticipant UserID="+"\"EHR\""+">"+
					"</ActiveParticipant>"+
					"<AuditSourceIdentification AuditSourceID="+"\"Adaptive Care Planner\""+">"+
					"</AuditSourceIdentification>"+
					"<ParticipantObjectIdentification ParticipantObjectID="+"\""+patientID+"\""+">"+
					"<ParticipantObjectIDTypeCode code="+"\"iCardea\""+"/>"+
					"</ParticipantObjectIdentification>"+
				"</AuditMessage>";
			
		}
		else if(messageType == "PCC-10"){
			xml = 
				"<AuditMessage>"+
					"<EventIdentification EventActionCode="+"\"C\""+" EventDateTime="+"\""+date+"\""+" EventOutcomeIndicator="+"\"0\""+">"+
					"<EventID code="+"\""+code+"\""+"/>"+
						"<EventTypeCode code="+"\"PCC-10\""+" codeSystemName="+"\"IHE Transactions\""+"/>"+
					"</EventIdentification>"+
					"<ActiveParticipant UserID="+"\"PHR\""+">"+
					"</ActiveParticipant>"+
					"<ActiveParticipant UserID="+"\"Adaptive Care Planner\""+">"+
					"</ActiveParticipant>"+
					"<AuditSourceIdentification AuditSourceID="+"\"PHR\""+">"+
					"</AuditSourceIdentification>"+
					"<ParticipantObjectIdentification ParticipantObjectID="+"\""+patientID+"\""+">"+
					"<ParticipantObjectIDTypeCode code="+"\"iCardea\""+"/>"+
					"</ParticipantObjectIdentification>"+
				"</AuditMessage>";
			
		}
		else if(messageType == "PCD-9"){
			xml = 
				"<AuditMessage>"+
					"<EventIdentification EventActionCode="+"\"C\""+" EventDateTime="+"\""+date+"\""+" EventOutcomeIndicator="+"\"0\""+">"+
						"<EventID code="+"\"CIED\""+" displayName="+"\"Cardiac Implantable Electronic Device\""+"/>"+
						"<EventTypeCode code="+"\"PCD-9\""+" codeSystemName="+"\"IHE Transactions\""+"/>"+
					"</EventIdentification>"+
					"<ActiveParticipant UserID="+"\"CIED Data Exposure System\""+">"+
					"</ActiveParticipant>"+
					"<ActiveParticipant UserID="+"\"Adaptive Care Planner\""+">"+
					"</ActiveParticipant>"+
					"<AuditSourceIdentification AuditSourceID="+"\"CIED Data Exposure System\""+">"+
					"</AuditSourceIdentification>"+
					"<ParticipantObjectIdentification ParticipantObjectID="+"\""+patientID+"\""+">"+
					"<ParticipantObjectIDTypeCode code="+"\"iCardea\""+"/>"+
					"</ParticipantObjectIdentification>"+
				"</AuditMessage>";
				
		}
		else if(messageType == "GRM"){
			xml =
				"<AuditMessage>"+
				    "<EventIdentification EventActionCode="+"\"R\""+" EventDateTime="+"\""+date+"\""+" EventOutcomeIndicator="+"\"0\""+">"+
				        "<EventID code="+"\""+code+"\""+" displayName="+"\"Grant Request Message\""+"/>"+
				        "<EventTypeCode code="+"\"GRM\""+" codeSystemName="+"\"IHE Transactions\""+"/>"+
				    "</EventIdentification>"+
				    "<ActiveParticipant UserID="+"\"Consent Manager\""+">"+
					"</ActiveParticipant>"+
					"<ActiveParticipant UserID="+"\""+  requesterRole +"\""+">"+
					"</ActiveParticipant>"+
					"<AuditSourceIdentification AuditSourceID="+"\"Consent Manager\""+">"+
					"</AuditSourceIdentification>"+
					"<ParticipantObjectIdentification ParticipantObjectID="+"\""+patientID+"\""+">"+
					"<ParticipantObjectIDTypeCode code="+"\"iCardea\""+"/>"+
					"</ParticipantObjectIdentification>"+
				"</AuditMessage>";
			//TODO
			// Send ATNA Message: Grant Request Message
			// +"resource"+ is requested from "+requesterRole+" for
			// "+patientID+" with result "+result.
		}
		else if(messageType == "register"){
			xml = 
				"<AuditMessage>"+
					"<EventIdentification EventActionCode="+"\"C\""+" EventDateTime="+"\""+date+"\""+" EventOutcomeIndicator="+"\"0\""+">"+
						"<EventID code="+"\"Register\""+" displayName="+"\"Registration of Patient\""+"/>"+
						"<EventTypeCode code="+"\"register\""+" codeSystemName="+"\"IHE Transactions\""+"/>"+
					"</EventIdentification>"+
					"<ActiveParticipant UserID="+"\"HIS\""+">"+
					"</ActiveParticipant>"+
					"<ActiveParticipant UserID="+"\"Adaptive Care Planner\""+">"+
					"</ActiveParticipant>"+
					"<AuditSourceIdentification AuditSourceID="+"\"HIS\""+">"+
					"</AuditSourceIdentification>"+
					"<ParticipantObjectIdentification ParticipantObjectID="+"\""+patientID+"\""+">"+
					"<ParticipantObjectIDTypeCode code="+"\"iCardea\""+"/>"+
					"</ParticipantObjectIdentification>"+
				"</AuditMessage>";
			//TODO
			// Send ATNA Message: Patient registration message is received for
			// "+citizenshipID+"
			// "+givenName+" "+familyName from Hospital Information System
		}
		
		return xml;
	}
	
}
