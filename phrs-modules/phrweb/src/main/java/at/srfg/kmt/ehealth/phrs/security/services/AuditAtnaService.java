package at.srfg.kmt.ehealth.phrs.security.services;

import java.io.Serializable;

import java.net.UnknownHostException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.icardea.atnalog.client.Audit;
/*
 * TODO create new thread for each send*
 */

@SuppressWarnings("serial")
public class AuditAtnaService implements Serializable {

    public final static String AUDIT_SYSTEM_SOURCE_PHRS = "phrs";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAtnaService.class.getName());
    private boolean atnalog = false;
    private boolean secure = false;
    private Audit audit = null;
    private String host = "127.0.0.1";
    private int port = 2861;
    private int portSecure = 8443;

    public void setupSSL() {
        SSLLocalClient.sslSetup();
    }

    public AuditAtnaService() {
        init();
    }

    /*
     * try { AuditAtnaService aas = new AuditAtnaService();
     * aas.sendAuditMessageForPatientRegistration(protocolId) } catch (Exception
     * e) { e.printStackTrace(); }
     */
    private void init() {
        try {
            atnalog = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.log")).booleanValue();

            secure = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.tls")).booleanValue();

            if (atnalog) {
                port = 2861;

                if (secure) {
                    port = 8443;
                    setupSSL();
                }

                ResourceBundle properties = ResourceBundle.getBundle("icardea");
                host = properties.getString("atna.log.server");


                // String xml = Audit.createMessage("GRM", patientId, resource,
                // requesterRole);//TODO: Grant Request Message

                audit = new Audit(host, port);

                // a.send_udp( a.create_syslog_xml("caremanager", xml) );
            }

        } catch (UnknownHostException e) {
            LOGGER.error("", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("", e);
            e.printStackTrace();
        }
        System.out.println("do atna?=" + atnalog + " secure? " + secure + " host=" + host + " port=" + port);
        LOGGER.debug("do atna?=" + atnalog + " secure? " + secure + " host=" + host + " port=" + port);

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isAtnalogRequired() {
        return atnalog;
    }

    public void sendAuditMessageGrantForRole(String patientId, String resource, String requestorRole) {
        try {
            if (atnalog && audit != null) {
                String xml = Audit.createMessage("GRM",
                        patientId, resource, requestorRole);
                audit.send_udp(audit.create_syslog_xml(AUDIT_SYSTEM_SOURCE_PHRS, xml));
            } else {
                LOGGER.error("Audit is null, invalid host,configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
            e.printStackTrace();
        }

    }

    public void sendAuditMessageForPatientRegistration(String patientId)
            throws Exception {

        try {
            if (atnalog && audit != null) {
                String xml = Audit.createMessage("register", patientId, null, null);

                audit.send_udp(audit.create_syslog_xml(AUDIT_SYSTEM_SOURCE_PHRS,
                        xml));
            } else {
                LOGGER.error("Audit is null, invalid host, configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
            e.printStackTrace();
        }

    }
}
