/*
 * Project :iCardea
 * File : QueryFactory.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.*;

/**
 * Used to build PCC9 requests (queries) and responses (acknowledge).<br/>
 * This class is not designed to be extend.
 * 
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class QueryFactory {

    /**
     * Holds all care provision codes supported in this factory.
     */
    private static final Map<String, String> CARE_PROVISION_CODES;

    /**
     * Used to initialize the care provision codes supported in this factory.
     */
    static {
        CARE_PROVISION_CODES = new HashMap<String, String>();
        CARE_PROVISION_CODES.put("9279-1", "RESPIRATION RATE");
        CARE_PROVISION_CODES.put("8867-4", "HEART BEAT");
        CARE_PROVISION_CODES.put("2710-2", "OXYGEN SATURATION");
        CARE_PROVISION_CODES.put("8480-6", "INTRAVASCULAR SYSTOLIC");
        CARE_PROVISION_CODES.put("8462-4", "INTRAVASCULAR DIASTOLIC");
        CARE_PROVISION_CODES.put("8310-5", "BODY TEMPERATURE");
        CARE_PROVISION_CODES.put("8302-2", "BODY HEIGHT (MEASURED)");
        CARE_PROVISION_CODES.put("8306-3", "BODY HEIGHT^LYING");
        CARE_PROVISION_CODES.put("8287-5", "CIRCUMFRENCE.OCCIPITAL-FRONTAL");
        CARE_PROVISION_CODES.put("3141-9", "BODY WEIGHT (MEASURED)");

        CARE_PROVISION_CODES.put("COBSCAT", "All Vital Signs");
        CARE_PROVISION_CODES.put("MEDCCAT", "All problem entries");
        CARE_PROVISION_CODES.put("CONDLIST", "All Concern Entries");
        CARE_PROVISION_CODES.put("PROBLIST", "All Problem Concerns");
        CARE_PROVISION_CODES.put("INTOLIST", "All Allergy Concerns");
        CARE_PROVISION_CODES.put("RISKLIST", "All Risks");
        CARE_PROVISION_CODES.put("LABCAT", "All Lab Results");
        CARE_PROVISION_CODES.put("DICAT", "All Imaging Results");
        CARE_PROVISION_CODES.put("RXCAT", "All Medications");
        CARE_PROVISION_CODES.put("MEDLIST", "All Medications");
        CARE_PROVISION_CODES.put("CURMEDLIST", "All active medications");
        CARE_PROVISION_CODES.put("DISCHMEDLIST", "Discharge Medications");
        CARE_PROVISION_CODES.put("HISTMEDLIST", "All Historical Medications");
        CARE_PROVISION_CODES.put("IMMUCAT", "All Immunizations");
        CARE_PROVISION_CODES.put("PSVCCAT", "All professional service entries");
        // I add this (ODLS) proves if this is correct
        CARE_PROVISION_CODES.put("ODLS", "All Observations Of daily livings");
    }
    /**
     * Format date using the pattern :
     * <code>yyyyMMddHHmmss</code>.
     */
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * The only instance for
     * <code>ObjectFactory</code> - JAXB related.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * Used to identify the "always" acknowledge code.
     */
    private static final String ALWAYS = "AL";

    /**
     * The file used to build an empty
     * <code>QUPCIN043100UV01</code> query.
     */
    private static final String PCC9_EMPTY_INPUT_FILE = "PCC-9-Empty-Input.xml";

    /**
     * The file used to build an empty
     * <code>MCCIIN000002UV01</code> acknowledge.
     */
    private static final String PCC9_EMPTY_OUTPUT_FILE = "PCC-9-Empty-Output.xml";

    /**
     * Don't let anybody to instantiate this class.
     */
    private QueryFactory() {
        // UNIMPLEMENTED
    }

    /**
     * Builds an empty PCC09 (QUPCIN043100UV01) query based on a default
     * template, the default template represents an empty query.
     *
     * @return an empty PCC09 (QUPCIN043100UV01) query.
     * @throws JAXBException by any XML parsing problem encounter during the
     * template parsing.
     */
    public static QUPCIN043100UV01 buildEmptyPCC9Request() throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(PCC9_EMPTY_INPUT_FILE);

        // this is a Care Record Event Profile Query
        final QUPCIN043100UV01 query =
                (QUPCIN043100UV01) unmarshaller.unmarshal(inputStream);
        return query;
    }

    /**
     * Builds an empty PCC09 (QUPCIN043100UV01) query acknowledge based on a
     * default template, the default template represents an empty query
     * acknowledge.
     *
     * @return an empty PCC09 (QUPCIN043100UV01) query.
     * @throws JAXBException by any XML parsing problem encounter during the
     * template parsing.
     */
    public static MCCIIN000002UV01 buildPCC9Acknowledge() throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(PCC9_EMPTY_OUTPUT_FILE);

        final MCCIIN000002UV01 result =
                (MCCIIN000002UV01) unmarshaller.unmarshal(inputStream);

        return result;
    }

    private static InputStream getStream(String name) {
        final ClassLoader classLoader = QueryFactory.class.getClassLoader();
        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg = 
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }

    public static MCCIIN000002UV01 buildMCCIIN000002UV01WithAcceptAckCodeError()
            throws JAXBException {
        final MCCIIN000002UV01 result = buildPCC9Acknowledge();
        // Coded Simple Value
        // Coded data in its simplest form, where only the code is not
        // predetermined.
        // CE = Code Error

        final CS cs = new CS();
        cs.setCode("CE");

        result.setAcceptAckCode(cs);

        return result;
    }

    public static void setAcceptAckCodeError(MCCIIN000002UV01 result) {

        final MCCIMT000200UV01Acknowledgement akAcknowledgement =
                OBJECT_FACTORY.createMCCIMT000200UV01Acknowledgement();

        final AcknowledgementType typeCode = AcknowledgementType.CE;
        akAcknowledgement.setTypeCode(typeCode);
        result.getAcknowledgement().add(akAcknowledgement);
    }

    /**
     * Returns true if the specified
     * <code>QUPCIN043100UV01</code> contains an "always" acknowledge code.
     *
     * @param query the query to prove.
     * @return true if the specified
     * <code>QUPCIN043100UV01</code> contains an "always" acknowledge code.
     * @throws NullPointerException if the
     * <code>query</code> argument is null.
     */
    public static boolean isAcknowledgementAlways(QUPCIN043100UV01 query) {

        if (query == null) {
            throw new NullPointerException("The query can nul be null.");
        }

        final CS acceptAckCodeCS = query.getAcceptAckCode();
        final String acceptAckCode = acceptAckCodeCS.getCode();
        return ALWAYS.equals(acceptAckCode);
    }

    /**
     * Builds a
     * <code>QUPCIN043100UV01</code> for the given list of parameters.
     *
     * @param careProvisionCode the provision code.
     * @param careProvisionReason
     * @param careRecordTimePeriodBegin
     * @param careRecordTimePeriodEnd
     * @param clinicalStatementTimePeriodBegin
     * @param clinicalStatementTimePeriodEnd
     * @param includeCarePlanAttachment
     * @param maximumHistoryStatements
     * @param patientAdministrativeGender
     * @param patientBirthTime
     * @param patientID the patient id, it can not be null.
     * @param patientName
     * @param patientSurname
     * @return
     * @throws JAXBException by any JAXB related errors.
     * @throws IllegalStateException if the specified
     * <code>careProvisionCode</code> is not supported. You can prove if a given
     * careProvisionCode is supported by using the
     * <code>isCareProvisionCodeSupported</code> method.
     * @throws NullPointerException if the
     * <code>patientID</code> is null.
     */
    public static QUPCIN043100UV01 buildPCC9Request(
            String careProvisionCode,
            String careProvisionReason,
            String careRecordTimePeriodBegin,
            String careRecordTimePeriodEnd,
            String clinicalStatementTimePeriodBegin,
            String clinicalStatementTimePeriodEnd,
            String includeCarePlanAttachment,
            String maximumHistoryStatements,
            String patientAdministrativeGender,
            String patientBirthTime,
            String patientID,
            String patientName,
            String patientSurname) throws JAXBException {

        // FIXME : this method is to long - make it smaller
        // FIXME : this method has to many arguments - use a map.


        if (!CARE_PROVISION_CODES.containsKey(careProvisionCode)) {
            final String msg = String.format("This care provision code %s is not sopported.", careProvisionCode);
            final IllegalStateException illegalException = new IllegalStateException(msg);
            throw illegalException;
        }

        if (patientID == null) {
            final NullPointerException exception = 
                    new NullPointerException("The patientID argument can not be null");
            throw exception;
        }

        // this is a Care Record Event Profile Query
        QUPCIN043100UV01 query = buildEmptyPCC9Request();

        final Date now = new Date();
        final String creationTime = DATE_FORMAT.format(now);

        // time point for the query
        final TS ts = new TS();
        ts.setValue(creationTime);
        query.setCreationTime(ts);

        // II is Instance Identifier for the Query
        final II messageInstanceId = new II();
        // FIXME : I don't know if this is ok ? 
        final String messageID = UUID.randomUUID().toString();
        messageInstanceId.setExtension(messageID);
        // in this way the query has an unique id.
        query.setId(messageInstanceId);

        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlAct =
                query.getControlActProcess();
        // TODO : why the controled act has more ids ?
        // TODO : why it has the same id like the query
        controlAct.getId().add(messageInstanceId);

        // the query parameter list
        // I call getQueryByParameter.getValue.getParameterList because the
        // xpath is   controlActProcess/queryByParameter/parameterList
        final List<QUPCMT040300UV01ParameterList> parameters =
                controlAct.getQueryByParameter().getValue().getParameterList();

        final QUPCMT040300UV01ParameterList parameterList =
                new QUPCMT040300UV01ParameterList();
        // adds the new parameter list to the existing one.
        parameters.add(parameterList);

        final QUPCMT040300UV01PatientId patientIdParameter =
                OBJECT_FACTORY.createQUPCMT040300UV01PatientId();
        final II patientInstanceId = new II();
        patientInstanceId.setExtension(patientID);
        patientIdParameter.setValue(patientInstanceId);
        parameterList.setPatientId(patientIdParameter);

        if (careProvisionCode != null) {
            // This element describes the information that is being looked for
            // in the <value> element. It can be used to customise / restricts
            // the result amount.
            final QUPCMT040300UV01CareProvisionCode value =
                    new QUPCMT040300UV01CareProvisionCode();

            // CD - ConceptDescriptor
            final CD provisionCodeConceptDescriptor = new CD();
            provisionCodeConceptDescriptor.setCode(careProvisionCode);
            value.setValue(provisionCodeConceptDescriptor);

            final JAXBElement<QUPCMT040300UV01CareProvisionCode> careProvisionParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListCareProvisionCode(value);
            parameterList.setCareProvisionCode(careProvisionParameter);
        }

        if (careProvisionReason != null) {
            // This element identifies the reason why the result was recorded.
            final QUPCMT040300UV01CareProvisionReason value =
                    new QUPCMT040300UV01CareProvisionReason();
            final CD provisionReasonConceptDescriptor = new CD();
            provisionReasonConceptDescriptor.setCode(careProvisionReason);
            value.setValue(provisionReasonConceptDescriptor);

            // TODO : why here I add the value and up I add the JAXBElement ?
            parameterList.getCareProvisionReason().add(value);
        }

        if (careRecordTimePeriodBegin != null
                && careRecordTimePeriodEnd != null) {
            // This element describes the time period over which the results
            // were recorded.

            final QUPCMT040300UV01CareRecordTimePeriod period =
                    new QUPCMT040300UV01CareRecordTimePeriod();

            // IVLTS time interval
            final IVXBTS beginInterval = new IVXBTS();
            beginInterval.setValue(careRecordTimePeriodBegin);
            final JAXBElement<IVXBTS> ivltsLow = OBJECT_FACTORY.createIVLTSLow(beginInterval);

            final IVXBTS endInterval = new IVXBTS();
            endInterval.setValue(careRecordTimePeriodEnd);
            final JAXBElement<IVXBTS> ivltsHigh = OBJECT_FACTORY.createIVLTSHigh(endInterval);

            final IVLTS careRecordTimePeriod = new IVLTS();
            careRecordTimePeriod.getRest().add(ivltsLow);
            careRecordTimePeriod.getRest().add(ivltsHigh);
            period.setValue(careRecordTimePeriod);

            final JAXBElement<QUPCMT040300UV01CareRecordTimePeriod> timePeriodParamenter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListCareRecordTimePeriod(period);
            parameterList.setCareRecordTimePeriod(timePeriodParamenter);
        }

        if (clinicalStatementTimePeriodBegin != null
                && clinicalStatementTimePeriodEnd != null) {
            // This element describes the effective time for the clinical
            // statement

            // IVLTS - time interval
            final IVXBTS beginTimeIterval = new IVXBTS();
            beginTimeIterval.setValue(clinicalStatementTimePeriodBegin);
            final JAXBElement<IVXBTS> ivltsLow =
                    OBJECT_FACTORY.createIVLTSLow(beginTimeIterval);

            final IVXBTS endTimeIterval = new IVXBTS();
            endTimeIterval.setValue(clinicalStatementTimePeriodEnd);
            final JAXBElement<IVXBTS> ivltsHigh =
                    OBJECT_FACTORY.createIVLTSHigh(endTimeIterval);

            final IVLTS ivlts = new IVLTS();
            // TODO : why you use rest here ? search in the specs.
            ivlts.getRest().add(ivltsLow);
            ivlts.getRest().add(ivltsHigh);

            final QUPCMT040300UV01ClinicalStatementTimePeriod period =
                    new QUPCMT040300UV01ClinicalStatementTimePeriod();
            period.setValue(ivlts);

            final JAXBElement<QUPCMT040300UV01ClinicalStatementTimePeriod> timePeriodParamenter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListClinicalStatementTimePeriod(period);
            parameterList.setClinicalStatementTimePeriod(timePeriodParamenter);
        }

        if (includeCarePlanAttachment != null) {
            final QUPCMT040300UV01IncludeCarePlanAttachment icpa = new QUPCMT040300UV01IncludeCarePlanAttachment();

            // BL - boolean
            final BL bl = new BL();
            bl.setValue(Boolean.parseBoolean(includeCarePlanAttachment));
            icpa.setValue(bl);

            final JAXBElement<QUPCMT040300UV01IncludeCarePlanAttachment> icpaParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListIncludeCarePlanAttachment(icpa);
            parameterList.setIncludeCarePlanAttachment(icpaParameter);
        }

        if (maximumHistoryStatements != null) {
            final QUPCMT040300UV01MaximumHistoryStatements maxHistory =
                    new QUPCMT040300UV01MaximumHistoryStatements();

            // INT integer :)
            final INT hl7int = new INT();
            hl7int.setValue(new BigInteger(maximumHistoryStatements));
            maxHistory.setValue(hl7int);

            final JAXBElement<QUPCMT040300UV01MaximumHistoryStatements> maxHistoryParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListMaximumHistoryStatements(maxHistory);
            parameterList.setMaximumHistoryStatements(maxHistoryParameter);
        }

        if (patientAdministrativeGender != null) {

            final QUPCMT040300UV01PatientAdministrativeGender gender =
                    new QUPCMT040300UV01PatientAdministrativeGender();

            // CE - Coded data (Coded With Equivalents)
            final CE ce = new CE();
            ce.setCode(patientAdministrativeGender);
            gender.setValue(ce);
            JAXBElement<QUPCMT040300UV01PatientAdministrativeGender> genderParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListPatientAdministrativeGender(gender);
            parameterList.setPatientAdministrativeGender(genderParameter);
        }

        if (patientBirthTime != null) {
            final QUPCMT040300UV01PatientBirthTime birthTime =
                    new QUPCMT040300UV01PatientBirthTime();
            // TD - point in time
            final TS brithTime = new TS();
            brithTime.setValue(patientBirthTime);
            birthTime.setValue(brithTime);
            final JAXBElement<QUPCMT040300UV01PatientBirthTime> birthTimeParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListPatientBirthTime(birthTime);
            parameterList.setPatientBirthTime(birthTimeParameter);
        }

        if (patientName != null && patientSurname != null) {
            final QUPCMT040300UV01PatientName name =
                    new QUPCMT040300UV01PatientName();
            final PN pn = new PN();

            final EnGiven given = new EnGiven();
            final EnFamily family = new EnFamily();
            // FIXME : figure it out how you set the given and the family name.

//                given.setContent(patientName);
//                family.setContent(patientSurname);

            pn.getContent().add(patientName);
            pn.getContent().add(patientSurname);
//            pn.getContent().add(OBJECT_FACTORY.createENGiven(given));
//            pn.getContent().add(OBJECT_FACTORY.createENFamily(family));

            name.setValue(pn);

            final JAXBElement<QUPCMT040300UV01PatientName> nameParameter =
                    OBJECT_FACTORY.createQUPCMT040300UV01ParameterListPatientName(name);
            parameterList.setPatientName(nameParameter);
        }

        return query;
    }
}
