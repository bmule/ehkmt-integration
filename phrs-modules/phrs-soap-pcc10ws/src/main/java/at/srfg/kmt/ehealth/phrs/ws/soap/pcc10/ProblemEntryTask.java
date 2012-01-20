/*
 * Project :iCardea
 * File : MedicationTask.java
 * Encoding : UTF-8
 * Date : Jan 18, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Builds and sends a <a
 * href="http://wiki.ihe.net/index.php?title=PCC-10">PCC10</a> that contains
 * problem entry information. <br/> <b>Note : <b/> this class caries no state -
 * this is very important because it allows (this class) to be used in multi
 * thread environment. <br/> This class was not designed to be extended.
 *
 * @author Mis
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class ProblemEntryTask implements PCCTask {

    public static final String ODL_CARE_PROVISION_CODE = "ODLS";

    public static final String RISKLIST_PROVISION_CODE = "RISKLIST";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProblemEntryTask</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryTask.class);

    /**
     * Builds a
     * <code>ProblemEntryTask</code> instance.
     */
    ProblemEntryTask() {
        // UNIMPLEMENTED
    }

    @Override
    public boolean canConsume(Map properties) {
        if (properties == null) {
            return false;
        }

        final Object code = properties.get("careProvisionCode");
        // TODO : use constants here
        final boolean isProblem =
                ODL_CARE_PROVISION_CODE.equals(code) || RISKLIST_PROVISION_CODE.equals(code);
        if (!isProblem) {
            LOGGER.debug("This code : {} is not a medication code.");
            return false;
        }

        final String patientId = (String) properties.get("patientId");
        final String patientNames = (String) properties.get("patientNames");
        final String careProvisionCode = (String) properties.get("careProvisionCode");
        final String responseURI = (String) properties.get("responseEndpointURI");

        if (responseURI == null
                || careProvisionCode == null
                || (patientId == null && patientNames == null)) {
            LOGGER.error("This properties map [{}] does not contain enought information. This properties map can not be consumed.",
                    properties);
            return false;
        }

        return isProblem;
    }

    /**
     * Builds and sends a <a
     * href="http://wiki.ihe.net/index.php?title=PCC-10">PCC10</a> message that
     * contains all the medications for the given properties map.
     *
     * @param properties the properties to be consumed.
     * @throws ConsumeException if the the specified map of parameters can not
     * be consumed from any reasons. Most of the times it wraps the real cause
     * for the exception, this cause can be obtained by using the
     * <code>getCause()</code> method.
     */
    @Override
    public void consume(Map properties) throws ConsumeException {

        LOGGER.debug("Tries to consumes {}", properties);
        final String responseURI = (String) properties.get("responseEndpointURI");

        try {
            final QUPCIN043200UV01 request = buildMessage();
            LOGGER.info("Tries to send this {} PCC10 query to the endpoint {}",
                    request, responseURI);

            if (!responseURI.startsWith("https")) {
                LOGGER.debug("No SSL");
            } else {
                LOGGER.debug("SSL used - logs the security properties.");
                logSecurity();
                enableSecurity();

            }
            final MCCIIN000002UV01 ack = SendPcc10Message.sendMessage(request, responseURI);
            LOGGER.info("Acknowledge (response) is : {}.", ack);

        } catch (Exception exception) {
            final ConsumeException consException =
                    new ConsumeException(exception);
            LOGGER.warn(consException.getMessage(), exception);
            throw consException;
        }

        LOGGER.debug("The properties {} was succefully consumed.", properties);
    }

    private void enableSecurity() {
        // TODO : I am not sure if this is right and if I need it !
        final Provider provider = new Provider();
        Security.addProvider(provider);
    }

    private void logSecurity() {
        // TODO : remove this logging in the prodiction mode !
        final String pkgsProp = "java.protocol.handler.pkgs";
        final String pgks = System.getProperty(pkgsProp);
        LOGGER.debug("{} = {}", pkgsProp, pgks);

        final String trustStoreProp = "javax.net.ssl.trustStore";
        final String trustStore = System.getProperty(trustStoreProp);
        LOGGER.debug("{} = {}", trustStoreProp, trustStore);

        final String passwdProp = "javax.net.ssl.trustStorePassword";
        final String passwd = System.getProperty(passwdProp);
        LOGGER.debug("{} = {}", passwdProp, passwd);
    }

    private QUPCIN043200UV01 buildMessage()
            throws TripleException, IllegalAccessException, InstantiationException {

        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();
        final ProblemEntryClient client = new ProblemEntryClient(triplestore);

        // this adds a problem-symptom named fever
        client.addProblemEntry(
                owner,
                Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "Free text note for the problem.",
                Constants.HL7V3_FEVER);

        final Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        // TAKE CARE !!!!!!
        // This lines wipe out the triple store repository files.
        try {
            ((GenericTriplestoreLifecycle) triplestore).shutdown();
            ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
        } catch (Exception exception) {
            LOGGER.warn(exception.getMessage(), exception);
        }
        
        final QUPCIN043200UV01 pcc10Message = ProblemEntryPCC10.getPCC10Message(beans);
        return pcc10Message;
    }

    /**
     * Returns a human readable string representation for this class.
     *
     * @return a string representation for this class.
     */
    @Override
    public String toString() {
        final String result = 
                String.format("ProblemEntryTask{%s or %s}", ODL_CARE_PROVISION_CODE, RISKLIST_PROVISION_CODE);
        return result;
    }
}
