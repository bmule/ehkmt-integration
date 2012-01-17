package at.srfg.kmt.ehealth.phrs.security.services;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.icardea.atnalog.client.Audit;

@SuppressWarnings("serial")
public class AuditAtnaService implements Serializable {
	public final static String AUDIT_SYSTEM_SOURCE_PHRS = "phrs";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditAtnaService.class.getName());
	private boolean atnalog = false;
	private Audit audit = null;

	public AuditAtnaService() {
		init();
	}

	/*
	 	try {
	  		AuditAtnaService aas = new AuditAtnaService();
	  		aas.sendAuditMessageForPatientRegistration(protocolId)
		} catch (Exception e) {
			e.printStackTrace();
		}  
	 */

	private void init() {
		try {
			atnalog = new Boolean(ResourceBundle.getBundle("icardea")
					.getString("atna.log")).booleanValue();

			if (atnalog) {

				ResourceBundle properties = ResourceBundle.getBundle("icardea");
				String atnalogServer = properties.getString("atna.log.server");
				int atnalogServerPort = 2861;
				// String xml = Audit.createMessage("GRM", patientId, resource,
				// requesterRole);//TODO: Grant Request Message

				audit = new Audit(atnalogServer, atnalogServerPort);

				// a.send_udp( a.create_syslog_xml("caremanager", xml) );
			}
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/*
	 * public void sendAuditMessageForGrantRequest(String patientId,String
	 * resource,String requestorRole){ String xml = Audit.createMessage("GRM",
	 * patientId, resource, requestorRole); a.send_udp(
	 * a.create_syslog_xml("caremanager", xml) );
	 * 
	 * }
	 */
	public void sendAuditMessageForPatientRegistration(String patientId)
			throws Exception {
		//TODO new thread on Audit message
		String xml = Audit.createMessage("register", patientId, null, null);
		if (audit != null) {
			audit.send_udp(audit.create_syslog_xml(AUDIT_SYSTEM_SOURCE_PHRS,
					xml));
		} else {
			LOGGER.error("Audit is null, invalid host or configuration");
		}

	}

}
