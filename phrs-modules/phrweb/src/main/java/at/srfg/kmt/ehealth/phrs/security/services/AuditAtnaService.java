package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dispatch.api.Dispatcher;
import at.srfg.kmt.ehealth.phrs.dispatch.impl.SingleDistpatcher;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.icardea.atnalog.client.Audit;

import java.io.Serializable;
import java.net.UnknownHostException;


@SuppressWarnings("serial")
public class AuditAtnaService implements Serializable {

    public final static String AUDIT_SYSTEM_SOURCE_PHRS = "phrs";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAtnaService.class);
    private boolean atnalog = false;
    private boolean secure = false;
    private Audit audit = null;
    private String host = "127.0.0.1";
    private int port = 2861;
    private boolean useMessageDispatcher=false;

    public AuditAtnaService() {

        init();
    }

    /**
     * Sends a test message - for Use by Test cases
     * 1. patient: testMessage_phruser,
     * 2. resource: PhrsConstants.AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH,
     * 3. subjectCode (role) PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE
     *
     * @param threaded - whether to use the threaded version or not.
     *                 If threaded normally true, depends on ATNA audit config settings also
     * @throws Exception
     */
    public boolean sendTestMessage(boolean threaded) throws Exception {
        boolean success=false;
        if (threaded) {

            sendAuditMessageGrant("testMessage_phruser",
                    PhrsConstants.AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH,
                    PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);
        } else {
            success = doAuditMessageGrant("testMessage_phruser",
                    PhrsConstants.AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH,
                    PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);

        }
        return success;
    }

    /*
    * try { AuditAtnaService aas = new AuditAtnaService();
    * aas.sendAuditMessageForPatientRegistration(protocolId) } catch (Exception
    * e) { e.printStackTrace(); }
    */
    private void init() {
        try {
            ConfigurationService config = ConfigurationService.getInstance();
            atnalog = Boolean.parseBoolean(config.getProperty("atna.log", "true"));

            secure = Boolean.parseBoolean(config.getProperty("atna.tls", "false"));

            //atna.tls is true, but ignore...
            if (atnalog) {
                port = Integer.parseInt(config.getProperty("atna.log.port", "2861"));

                if (secure) {

                    //port = 8443;
                    //setupSSL(sslConfigSetting);
                }

                host = config.getProperty("atna.log.server","127.0.0.1");

                // String xml = Audit.createMessage("GRM", patientId, resource,
                // requesterRole);
                //TODO: Grant Request Message

                audit = new Audit(host, port);

                // a.send_udp( a.create_syslog_xml("caremanager", xml) );
            }
            
            //messageDispatcher = 
            //ConfigurationService.getInstance().getProperty("atna.message.dispatcher");

        } catch (UnknownHostException e) {
            LOGGER.error("", e);
            secure = false;

        } catch (Exception e) {
            secure = false;
            LOGGER.error("", e);

        }

        LOGGER.debug("send atna?=" + atnalog + " host=" + host + " port=" + port);

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

                boolean success = doAuditMessageGrant(patientId, resource, requestorRole);

            }
        };
        dispatcher.dispatch(task);

    }

    /**
     * Send Audit message
     * over a new thread
     *
     * @param patientId
     * @param resource
     * @param requestorRole
     */
    public void sendAuditMessageGrant(final String patientId, final String resource, final String requestorRole) {
        if( ! useMessageDispatcher){
            boolean flag=this.doAuditMessageGrant(patientId, resource, requestorRole);
        
        }else {
            final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();

            final Runnable task = new Runnable() {

                @Override
                public void run() {

                    boolean success = doAuditMessageGrant(patientId, resource, requestorRole);

                }
            };
            dispatcher.dispatch(task);
        }

    }

    /**
     * Only use this method if no separate thread is needed. Normally, use
     * threaded
     * <code>sendAuditMessageGrantForRole</code> There is no separate thread
     * made using the Message Dispatcher, however, this is used by the
     * <code>sendAuditMessageGrantForRole</code> and by the unit tests
     */
    public boolean doAuditMessageGrant(final String patientId, final String resource, final String requestorRole) {
        boolean success = false;
        LOGGER.debug("prepare doAuditMessageGrant");
        try {
            if (atnalog && audit != null) {
                String xml = Audit.createMessage("GRM",
                        patientId, resource, requestorRole);
                audit.send_udp(audit.create_syslog_xml(
                        AUDIT_SYSTEM_SOURCE_PHRS,
                        xml));
                LOGGER.debug("ANTA for patientId= " + patientId + " access of resource=" + resource + " by role=" + requestorRole);
            } else {
                LOGGER.error("ANTA Audit is null, invalid host,configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
        }
        LOGGER.debug("end doAuditMessageGrant");
        return success;
    }

    /**
     * When contacting the PIX or identity server about patient identifiers,
     * provide an audit message
     *
     * @param patientId
     */
    public void sendAuditMessageForPatientRegistration(final String patientId) {
        
        if( ! useMessageDispatcher){
            boolean result= this.doAuditMessageForPatientRegistration(patientId);
        } else {
            final Dispatcher dispatcher = SingleDistpatcher.getDispatcher();

            final Runnable task = new Runnable() {

                @Override
                public void run() {
                    final boolean success = doAuditMessageForPatientRegistration(patientId);
                }
            };

            dispatcher.dispatch(task);
        } 
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
                
            } else {
                LOGGER.error("ANTA Audit is null, invalid host, configuration or property file flag is false");
            }
        } catch (Exception e) {
            LOGGER.error("patientId= " + patientId, e);
        }
        return success;
    }

}
