package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.dispatch.api.Dispatcher;
import at.srfg.kmt.ehealth.phrs.dispatch.impl.SingleDistpatcher;
import java.io.Serializable;

import java.net.UnknownHostException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.icardea.atnalog.client.Audit;
/*
 * TODO create new thread for each send*
 */
//buildNullFrequency()
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
            secure = false;
            e.printStackTrace();
        } catch (Exception e) {
            secure = false;
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

    /**
     * Either the configuration in icardea.properties affects this response or
     * if there has been an exception during the setup (SSL setup, missing
     * icardea.properties file, or malformed properties file, etc)
     *
     * @return
     */
    public boolean isSecure() {
        return secure;
    }

    public boolean isAtnalogRequired() {
        return atnalog;
    }

    /**
     * Send audit message when granting access. In most cases, it is the access
     * to a document but the consent mgr can be used to determine if a UI list
     * should be shown although that list might not contain health data, just
     * the links to the health data. In the latter case, only when the link is
     * access e.g. a PDF document, should there be an audit message sent.
     *
     * @param patientId
     * @param resource
     * @param requestorRole
     */
    public void sendAuditMessageGrantForRole(final String patientId, final String resource, final String requestorRole) {

        final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();

        final Runnable task = new Runnable() {

            @Override
            public void run() {

                boolean success = doAuditMessageGrantForRole(patientId, resource, requestorRole);

            }
        };
        dispatcher.dispatch(task);

    }

    /**
     *
     * Only use this method if no separate thread is needed. Normally, use
     * threaded
     * <code>sendAuditMessageGrantForRole</code> There is no separate thread
     * made using the Message Dispatcher, however, this is used by the
     * <code>sendAuditMessageGrantForRole</code> and by the unit tests
     *
     */
    public boolean doAuditMessageGrantForRole(final String patientId, final String resource, final String requestorRole) {
        boolean success = false;
        try {
            if (atnalog && audit != null) {
                String xml = Audit.createMessage("GRM",
                        patientId, resource, requestorRole);
                audit.send_udp(audit.create_syslog_xml(
                        AUDIT_SYSTEM_SOURCE_PHRS,
                        xml));
                LOGGER.debug("ANTA for patientId= " + patientId + " access of resource=" + resource + " by role=" + requestorRole);
                System.out.println("sendAuditMessageGrantForRole completed");
            } else {
                LOGGER.error("ANTA Audit is null, invalid host,configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
            e.printStackTrace();
        }

        return success;
    }

    /**
     * When contacting the PIX or identity server about patient identifiers,
     * provide an audit message
     *
     * @param patientId
     */
    public void sendAuditMessageForPatientRegistration(final String patientId) {
        final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();

        final Runnable task = new Runnable() {

            @Override
            public void run() {
                final boolean success = doAuditMessageForPatientRegistration(patientId);
            }
        };

        dispatcher.dispatch(task);
    }

    /**
     * Only use this method if no separate thread is needed. Normally, use
     * threaded
     * <code>sendAuditMessageForPatientRegistration</code>
     *
     * @param patientId
     * @return
     */
    public boolean doAuditMessageForPatientRegistration(final String patientId) {
        boolean success = false;

        try {

            if (atnalog && audit != null) {
                String xml = Audit.createMessage("register", patientId, null, null);

                audit.send_udp(audit.create_syslog_xml(
                        AUDIT_SYSTEM_SOURCE_PHRS,
                        xml));
                LOGGER.debug("ANTA for patientId= " + patientId);
                System.out.println("sendAuditMessageGrantForRole completed");
            } else {
                LOGGER.error("ANTA Audit is null, invalid host, configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
            e.printStackTrace();
        }
        return success;
    }
    /*
     * public void myMethod(final String... args) {
     *
     * final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();
     *
     * final Runnable task = new Runnable() {
     *
     * @Override public void run() { System.out.println("Here it goes"); for
     * (String s : args) { System.out.println("--" + s); } } };
     *
     * dispatcher.dispatch(task); }
     */
}
