/*
 * Project :iCardea
 * File : QUPCIN043100UV01Processor.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import static at.srfg.kmt.ehealth.phrs.pcc09ws.impl.Constants.PC10_NOTIFY_END_POINT;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.hl7.v3.*;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to process a PCC09 query (QUPCIN043100UV01).
 * The process can produce an acknowledge, this acknowledge can be obtained with 
 * the <code>getMCCIIN000002UV01</code>. The acknowledge contain information 
 * about the query processing (e.g. the success or failure).
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class QUPCIN043100UV01Processor {

    /**
     * Format date using the pattern : <code>yyyyMMddHHmmss</code>.
     */
    private final static DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Format date using the pattern : <code>EEE, d MMM yyyy HH:mm:ss</code>.
     */
    private final static DateFormat HR_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCIN043100UV01Processor</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCIN043100UV01Processor.class);
    
    /**
     * This is the end point where the pcc10 transaction can be triggered.
     */
//    private static final String PC10_NOTIFY_END_POINT = 
//            "http://localhost:8080/pcc10ws/restws/pcc10/notify";

    /**
     * The acknowledge for this processes, the acknowledge result after a query
     * is process.
     */
    private MCCIIN000002UV01 acknowledgement;

    /**
     * Builds a <code>QUPCIN043100UV01Processor</code> instance.
     */
    QUPCIN043100UV01Processor() {
        try {
            acknowledgement = QueryFactory.buildMCCIIN000002UV01();
        } catch (JAXBException jaxBException) {
            LOGGER.error("The acknoledge is null becuase "
                    + jaxBException.getClass().getName());
            LOGGER.error(jaxBException.getMessage(), jaxBException);
            acknowledgement = null;
        }
    }

    /**
     * Process a given PCC09 query (QUPCIN043100UV01)
     *  
     * @param qupcin043100UV01 the query to process, it can not be null.
     * @throws NullPointerException if the <code>qupcin043100UV01</code>
     * argument is null.
     */
    void process(QUPCIN043100UV01 qupcin043100UV01) {

        if (qupcin043100UV01 == null) {
            final NullPointerException nullException =
                    new NullPointerException("The qupcin043100UV01 argument can not be null");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }
        
        boolean isAcknowledgementAlways =
                QueryFactory.isAcknowledgementAlways(qupcin043100UV01);
        if (isAcknowledgementAlways) {
            String ackMsg = "The query acknowledgement type is always.";
            LOGGER.debug(ackMsg);
            // send this to em
        }

        final MCCIMT000100UV01Sender sender = qupcin043100UV01.getSender();
        process(sender);

        final List<MCCIMT000100UV01Receiver> receivers =
                qupcin043100UV01.getReceiver();
        process(receivers);

        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess =
                qupcin043100UV01.getControlActProcess();

        final List<QUPCMT040300UV01ParameterList> parameterList =
                controlActProcess.getQueryByParameter().getValue().getParameterList();
        String toNotify = null;
        for (QUPCMT040300UV01ParameterList param : parameterList) {
            toNotify = process(param);
        }
        
        sendNotiffy(toNotify);
    }

    private void process(MCCIMT000100UV01Sender sender) {
        CommunicationFunctionType typeCode = sender.getTypeCode();

    }

    private void process(List<MCCIMT000100UV01Receiver> receivers) {
        for (MCCIMT000100UV01Receiver receiver : receivers) {
            process(receiver);
        }
    }

    private void process(MCCIMT000100UV01Receiver receiver) {
    }

    private String processAll(QUPCMT040300UV01ParameterList parameter) {
        
        final StringBuffer result = new StringBuffer();

        // the patient id for this list element, the list can contain more
        // patients.
        final II patientIdIntanceId = parameter.getPatientId().getValue();
        final String patientIdMsg =
                String.format("Patient id :%s ", toString(patientIdIntanceId));
        LOGGER.debug(patientIdMsg);
        // send this to EM
        result.append(patientIdIntanceId.getExtension());
        result.append("-");


        final QUPCMT040300UV01CareProvisionCode provisionCode =
                parameter.getCareProvisionCode().getValue();

        // obtains the Care Provision Code (it can be 0..1)
        final CD provCodeConceptDescriptor = provisionCode.getValue();
        final String provCodeMsg = String.format("Care Provision Code : %s",
                toString(provCodeConceptDescriptor));
        LOGGER.debug(provCodeMsg);
        // send this to EM
        result.append(provCodeConceptDescriptor.getCode());

        // obtains the Care Provision Reasons (it can be 0..*)
        // This element identifies the reason why the result was recorded.
        final List<QUPCMT040300UV01CareProvisionReason> careProvisionReasons =
                parameter.getCareProvisionReason();

        if (careProvisionReasons.isEmpty()) {
            LOGGER.warn("No care provision reason available.");
            return null;
        }

        // I know that I have only one reason.
        // TODO : care about more reasons(reasons lists)
        final QUPCMT040300UV01CareProvisionReason provisionReason =
                careProvisionReasons.get(0);
        final CD provReasonConceptDescriptor = provisionReason.getValue();
        final String provReasonMsg = String.format("Care Provision reason : %s",
                toString(provReasonConceptDescriptor));
        LOGGER.debug(provReasonMsg);
        // send this to EM

        // obtains the Care Record Time Period
        final QUPCMT040300UV01CareRecordTimePeriod careRecordTimePeriod =
                parameter.getCareRecordTimePeriod().getValue();

        final List<JAXBElement<? extends QTY>> careRecordIntervals =
                careRecordTimePeriod.getValue().getRest();

        final StringBuffer careRecordTimePeriodMsg =
                new StringBuffer("Care Record Time Period : ");
        // iterate over all Care Record Time Period intervals
        for (JAXBElement<? extends QTY> interval : careRecordIntervals) {

            // QTY - Abstract Type Quantity
            QTY intervalValue = interval.getValue();
            if (intervalValue instanceof IVXBTS) {
                try {
                    careRecordTimePeriodMsg.append(toString((IVXBTS) intervalValue));
                    careRecordTimePeriodMsg.append(", ");
                } catch (ParseException e) {
                    // FIXME : inform the client about this !
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        final int length = careRecordTimePeriodMsg.length();
        if (length > 2) {
            careRecordTimePeriodMsg.delete(length - 2, length - 1);
        }
        LOGGER.debug(careRecordTimePeriodMsg.toString());
        // send this to EM


        // obtains the Clinical Statement Time Period
        final QUPCMT040300UV01ClinicalStatementTimePeriod clinicalStatementTimePeriod =
                parameter.getClinicalStatementTimePeriod().getValue();

        final List<JAXBElement<? extends QTY>> clinicalStatementIntervals =
                clinicalStatementTimePeriod.getValue().getRest();
        final StringBuffer clinicalStatementMsg =
                new StringBuffer("Clinical Statement Time Period : ");
        // iterate over all Clinical Statements Time Period intervals
        for (JAXBElement<? extends QTY> interval : clinicalStatementIntervals) {

            // QTY - Abstract Type Quantity
            QTY intervalValue = interval.getValue();
            if (intervalValue instanceof IVXBTS) {
                try {
                    clinicalStatementMsg.append(toString((IVXBTS) intervalValue));
                    clinicalStatementMsg.append(", ");
                } catch (ParseException e) {
                    // FIXME : inform the client about this !
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        LOGGER.debug(clinicalStatementMsg.toString());
        // send this to EM


        // obtains the Patient Administrative Gender
        QUPCMT040300UV01PatientAdministrativeGender administrativeGender =
                parameter.getPatientAdministrativeGender().getValue();

        // CE - Coded data (Coded With Equivalents)
        final CE gender = administrativeGender.getValue();
        final String genderMsg = String.format("Gender : %s",
                toString(gender));
        // send this to EM
        LOGGER.debug(genderMsg);

        // obtains the Patient Birth Time
        final QUPCMT040300UV01PatientBirthTime patientBirthTime =
                parameter.getPatientBirthTime().getValue();

        // TD - point in time
        final TS birthTime = patientBirthTime.getValue();
        final String birthTimeMsg = String.format("Patient Birth Time : %s",
                toString(birthTime));
        // send this to EM
        LOGGER.debug(birthTimeMsg);

        // obtains the Patient Name
        final QUPCMT040300UV01PatientName value =
                parameter.getPatientName().getValue();
        final PN pn = value.getValue();
        final String personNameMsg = String.format("Patient name : %s",
                toString(pn));
        // send this to EM
        LOGGER.debug(personNameMsg);

        authorizePerson(pn);
        
        return  result.toString();
    }
    
    /**
     * Care only about the patient id and care provision code.
     * 
     * @param parameter
     * @return 
     */
    private String process(QUPCMT040300UV01ParameterList parameter) {
        
        final StringBuffer result = new StringBuffer();

        // the patient id for this list element, the list can contain more
        // patients.
        final II patientIdIntanceId = parameter.getPatientId().getValue();
        final String patientIdMsg =
                String.format("Patient id :%s ", toString(patientIdIntanceId));
        LOGGER.debug(patientIdMsg);
        // send this to EM
        result.append(patientIdIntanceId.getExtension());
        result.append("-");


        final QUPCMT040300UV01CareProvisionCode provisionCode =
                parameter.getCareProvisionCode().getValue();

        // obtains the Care Provision Code (it can be 0..1)
        final CD provCodeConceptDescriptor = provisionCode.getValue();
        final String provCodeMsg = String.format("Care Provision Code : %s",
                toString(provCodeConceptDescriptor));
        LOGGER.debug(provCodeMsg);
        // send this to EM
        result.append(provCodeConceptDescriptor.getCode());
        
        return  result.toString();
    }

    private boolean provePerson(PN pn, String name) {
        final List<Serializable> persons = pn.getContent();
        for (Serializable person : persons) {
            if (name.equals(person.toString())) {
                return true;
            }
        }

        return false;
    }

    private void authorizePerson(PN person) {
        // here must be the care manager or other authorisation related tools
        // able to do any authentification related tasks.
        boolean isLumpkins = provePerson(person, "Mopuffus Lumpkins");
        if (isLumpkins) {
            QueryFactory.setAcceptAckCodeError(acknowledgement);
        }
    }

    private String toString(CD cd) {
        final StringBuffer result = new StringBuffer();

        if (cd.getCode() != null) {
            result.append("Code : ");
            result.append(cd.getCode());
            result.append(", ");
        }

        if (cd.getCodeSystem() != null) {
            result.append("CodeSystem : ");
            result.append(cd.getCodeSystem());
            result.append(", ");
        }


        if (cd.getCodeSystemName() != null) {
            result.append("CodeSystemName : ");
            result.append(cd.getCodeSystemName());
            result.append(", ");
        }

        if (cd.getCodeSystemVersion() != null) {
            result.append("CodeSystemVersion : ");
            result.append(cd.getCodeSystemVersion());
            result.append(", ");
        }

        int length = result.length();
        if (length > 2) {
            result.delete(length - 2, length - 1);
        }

        return result.toString();
    }

    private String toString(II ii) {
        final StringBuffer result = new StringBuffer();
        final String extension = ii.getExtension();

        if (extension != null) {
            result.append("Extension (UUID) : ");
            result.append(extension);
        }

        return result.toString();
    }

    private String toString(IVXBTS ivxbts) throws ParseException {
        final String value = ivxbts.getValue();
        final Date date = DATE_FORMAT.parse(value);
        String result = HR_FORMAT.format(date);
        return result;
    }

    private String toString(CE ce) {
        final StringBuffer result = new StringBuffer();
        final String code = ce.getCode();
        result.append("Code : ");
        result.append(code);

        return result.toString();
    }

    private String toString(TS ts) {
        final StringBuffer result = new StringBuffer();
        final String value = ts.getValue();
        result.append("Value : ");
        result.append(value);

        return result.toString();
    }

    private String toString(PN pn) {
        final StringBuffer result = new StringBuffer();

        final List<Serializable> names = pn.getContent();
        for (Serializable name : names) {
            result.append("Name : ");
            result.append(name);
            result.append(", ");
        }
        int length = result.length();
        if (length > 2) {
            result.delete(length - 2, length - 1);
        }


        return result.toString();
    }

    /**
     * Returns the acknowledge code resulted after this processor process a
     * given query.
     * 
     * @return the acknowledge code resulted after this processor process a
     * given query.
     */
    MCCIIN000002UV01 getMCCIIN000002UV01() {
        return acknowledgement;
    }

    private void sendNotiffy(String toSend) {
        LOGGER.debug("Send GET request on : {}", PC10_NOTIFY_END_POINT);

        final ClientRequest request = new ClientRequest(PC10_NOTIFY_END_POINT);
        // FIXME : j'toh nohh use constants here
        request.queryParameter("q", toSend);
        request.queryParameter("sender", "pcc09");

        final ClientResponse response;
        try {
            response = request.get();
        } catch (Exception exception) {
            final String msg = 
                    PC10_NOTIFY_END_POINT + " GET request can not be solved.";
            final IllegalStateException illegalException =
                    new IllegalStateException(exception);
            LOGGER.error(msg, illegalException);
            throw illegalException;
        }

        final int statusCode = response.getStatus();
        LOGGER.debug("Status Code = {}", statusCode);
        if (statusCode != 200) {
            String msg = PC10_NOTIFY_END_POINT + " GET request can not be solved.";
            final IllegalStateException illegalException =
                    new IllegalStateException(msg);
            LOGGER.error(msg, illegalException);
            throw illegalException;
        }
    }
}
