package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

public class PCC09Query {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC09Query.class);
    private final String CONFIG_FILE = "pcc-9.properties";

    private String endpointURI;
    private String responseEndpointURI;
    private String keystoreFile;
    private String keystorePassword;
     /*
      SendPcc09Client
      */
    public PCC09Query(String endpointURI, String responseEndpointURI, String keystoreFile, String password) {
        this.endpointURI = endpointURI;
        this.responseEndpointURI = responseEndpointURI;
        this.keystoreFile = keystoreFile;
        this.keystorePassword = password;
        //System.out.println("constructor params endpointURI="+endpointURI+" responseEndpointURI"+responseEndpointURI+" keystoreFile="+keystoreFile+" keystorePassword="+keystorePassword);
        LOGGER.debug("constructor params endpointURI="+endpointURI+" responseEndpointURI"+responseEndpointURI+" keystoreFile="+keystoreFile+" keystorePassword="+keystorePassword);
    }

    public PCC09Query() {

        final ClassLoader classLoader = PCC09Query.class.getClassLoader();
        final InputStream resourceAsStream =
                classLoader.getResourceAsStream(CONFIG_FILE);
        LOGGER.debug("PCC09Query constructor default ");

        if (resourceAsStream == null) {
            LOGGER.error("The PCC09 configunration file named {} must be placed in the classpath", CONFIG_FILE);
            LOGGER.error("NO PCC09 MESSAGES CAN BE SENT!");
            return;
        }
        LOGGER.debug("PCC09Query resourceAsStream ok resourceAsStream="+resourceAsStream);
        try {
            Properties config = new Properties();
            config.load(resourceAsStream);
            //https://localhost:8089/testws/pcc9 https://localhost:8989/testws/pcc10
            LOGGER.debug("PCC09Query Properties ok ");
            endpointURI = config.getProperty("endpointURI", "https://icardea-server.lksdom21.lks.local/ehrif/pcc/").trim();
            responseEndpointURI = config.getProperty("responseEndpointURI", "https://icardea-server.lksdom21.lks.local:8989/testws/pcc10").trim();

            keystoreFile = config.getProperty("keystore", "srfg-phrs-core-keystore.ks").trim();
            keystorePassword = config.getProperty("password", "icardea").trim();
            System.out.println("constructor default endpointURI="+endpointURI+" responseEndpointURI"+responseEndpointURI+" keystoreFile="+keystoreFile+" keystorePassword="+keystorePassword);
            LOGGER.debug("constructor default endpointURI="+endpointURI+" responseEndpointURI"+responseEndpointURI+" keystoreFile="+keystoreFile+" keystorePassword="+keystorePassword);
        } catch (Exception exception) {
            LOGGER.warn("NO pcc09 MESSAGES CAN BE SENT!");
            LOGGER.warn(exception.getMessage(), exception);
        }
    }

    public String getEndpointURI() {
        return endpointURI;
    }

    public String getResponseEndpointURI() {
        return responseEndpointURI;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     *
     * @param patientID
     * @param careProvisionCode
     * @return
     */
    public MCCIIN000002UV01 sendPcc09Message(final String patientID, final String careProvisionCode) {
        return sendPcc09Message(patientID, careProvisionCode, getEndpointURI(), getResponseEndpointURI(), getKeystoreFile(), getKeystorePassword());
    }

    /**
     *
     * @param patientID
     * @param careProvisionCode
     * @param sourceAddress  - if trigger to send a new PCC09 message was an incoming PCC-09, we must check for cycling - normally only for testing would we see this
     * @return
     */
    public MCCIIN000002UV01 sendPcc09Message(final String patientID, final String careProvisionCode,String pcc09WsaAddress) {
        if(pcc09WsaAddress != null){
              if(pcc09WsaAddress.equalsIgnoreCase(getResponseEndpointURI())) {
                LOGGER.debug("Stop processing PCC09 message, sourceAddress {} ==responseEndpointURI {}"+pcc09WsaAddress,getResponseEndpointURI());
                return null;
            }
           // if(pcc09WsaAddress.equalsIgnoreCase(getEndpointURI())) {
           //     LOGGER.debug("Stop processing PCC09 message, sourceAddress {} == getEndpointURI {}"+pcc09WsaAddress,getEndpointURI());
           //     return null;
           // }
        }

        return sendPcc09Message(patientID, careProvisionCode, getEndpointURI(), getResponseEndpointURI(), getKeystoreFile(), getKeystorePassword());
    }
    public MCCIIN000002UV01 sendPcc09Message(final String patientID, final String careProvisionCode,
                                             final String endpointURI, final String responseURI, final String keystore, final String password) {
        //final String careProvisionReason = "";
        //final String includeCarePlanAttachment = "true";
        final String maximumHistoryStatements = "30";

        //final String patientBirthTime = "197903111010";

        String patientName = "";
        String patientSurname = "";

        MCCIIN000002UV01 ack = null;
        try {
            /// + " careProvisionCode: " + careProvisionCode + " patientID: " + patientID
            LOGGER.info("Tries to send a PCC9 query ({}) to {}", endpointURI, responseURI);

            final QUPCIN043100UV01 pcc9Request =
                    QueryFactory.buildPCC9Request(careProvisionCode,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            maximumHistoryStatements,
                            null,
                            null,
                            patientID,
                            patientName,
                            patientSurname);


            if (responseURI.startsWith("https")) {
                LOGGER.info("Tries to send a Secure PCC9 query to responseURI:" +responseURI+" from endpointURI:" + endpointURI + "  careProvisionCode=" + careProvisionCode + " patientID: " + patientID + " keystore=" + keystore);
                ack = SendPcc09Message.sendSecureMessage(pcc9Request, endpointURI, responseURI, keystore, password);
            } else {
                LOGGER.info("Tries to send a Non-Secure PCC9 query to responseURI:" +responseURI+" from endpointURI:" + endpointURI + "  careProvisionCode=" + careProvisionCode + " patientID: " + patientID + " keystore=" + keystore);
                ack = SendPcc09Message.sendMessage(pcc9Request, endpointURI, responseURI);
            }
            LOGGER.info("Acknowledge (response) is : {}.", ack);

        } catch (JAXBException e) {
            LOGGER.info("JAXBException", e);
        } catch (MalformedURLException e) {
            LOGGER.info("JAXBException", e);
        } catch (Exception e) {
            LOGGER.info("Exception", e);
        }

        return ack;

    }

}
