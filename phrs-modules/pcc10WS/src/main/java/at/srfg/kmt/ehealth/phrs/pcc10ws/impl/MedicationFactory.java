/*
 * Project :iCardea
 * File : MedicationFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class MedicationFactory implements PCC10Factory<QUPCIN043200UV01> {

    private final String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final DateFormat dateFormat;

    /**
     * It registers the <code>PatternBasedDateConverter</code> to the 
     * <code>org.apache.commons.beanutils.ConvertUtils.ConvertUtils</code>.
     * This is used to convert string to date.
     */
    {
        dateFormat = new SimpleDateFormat(pattern);
    }

    private final static String PCC10_OUTPUT_FILE = "PCC-10-Empty-Input.xml";

    private final static ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private Set<DynaBean> medication;

    MedicationFactory() {
        // UNIMPLEMENTED
    }

    @Override
    public QUPCIN043200UV01 build() throws PCC10BuildException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Set<DynaBean> getMedication() {
        return medication;
    }

    void setMedication(Set<DynaBean> medication) {
        this.medication = medication;
    }

    private QUPCIN043200UV01 build(String labeledDrugName, float dose,
            String doseUnit, XDocumentSubstanceMood mood) throws JAXBException {

        final QUPCIN043200UV01 query = buildQuery(PCC10_OUTPUT_FILE);


        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess = query.getControlActProcess();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                careProvisionEvent.getPertinentInformation3().get(0);

        // get the substance admin
        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JE =
                pertinentInformation.getSubstanceAdministration();

        final POCDMT000040SubstanceAdministration substanceAdministration =
                substanceAdministration_JE == null
                ? new POCDMT000040SubstanceAdministration()
                : substanceAdministration_JE.getValue();

        substanceAdministration.setMoodCode(mood);

        final POCDMT000040Consumable pocdmt000040Consumable =
                buildPOCDMT000040Consumable(labeledDrugName);
        substanceAdministration.setConsumable(pocdmt000040Consumable);

        final IVLPQ ivlpq = buildIVLPQ(dose, doseUnit);
        substanceAdministration.setDoseQuantity(ivlpq);

        //FIXME : I am not sure about this.
        // for the moment I add the
        final List<II> templateIds = buildTemplateIds();
        substanceAdministration.getTemplateId().addAll(templateIds);

        final CE ce = buildRouteCode();
        substanceAdministration.setRouteCode(ce);

        final CS statusCode = buildStatusCodeActive();
        substanceAdministration.setStatusCode(statusCode);

        // The code element is used to supply a code that describes the
        // substanceAdminstration act or may describe the method of
        // medication administration, such as by intravenous injection.
        final CD code = buildCode();
        substanceAdministration.setCode(code);

        // A top level <substanceAdministration> element must be uniquely identified.
        // If there is no explicit identifier for this observation in the source
        // EMR system, a GUID may be used for the root attribute,
        // and the extension may be omitted.
        // Although HL7 allows for multiple identifiers, this profile requires
        // that one and only one be used. Subordinate <substanceAdministration>
        // elements may, but need not be uniquely identified.
        final II id = buildUUIDBasedId();
        substanceAdministration.getId().add(id);

        //final List<SXCMTS> frequency = buildExactTimePeriod();
        // final List<SXCMTS> frequency = buildUnexactTimePeriods();
        //final List<SXCMTS> frequency = buildUnexactAMTimePeriod();
        final List<SXCMTS> frequency = buildUnexactEvningTimePeriod();
        substanceAdministration.getEffectiveTime().addAll(frequency);

        List<POCDMT000040EntryRelationship> entryRelationship =
                substanceAdministration.getEntryRelationship();

        final List<POCDMT000040EntryRelationship> instructions =
                buildInstructions();

        entryRelationship.addAll(instructions);

        if (substanceAdministration_JE == null) {
            final JAXBElement<POCDMT000040SubstanceAdministration> newSubstanceAdministration_JE =
                    OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5SubstanceAdministration(substanceAdministration);
            pertinentInformation.setSubstanceAdministration(newSubstanceAdministration_JE);
        }

        return query;
    }

    private REPCMT004000UV01PertinentInformation5 buildPertinentInformation(DynaBean medication) {
        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();

        // get the substance admin
        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JE =
                pertinentInformation.getSubstanceAdministration();

        final POCDMT000040SubstanceAdministration substanceAdministration =
                substanceAdministration_JE == null
                ? new POCDMT000040SubstanceAdministration()
                : substanceAdministration_JE.getValue();
        final XDocumentSubstanceMood moodCode = XDocumentSubstanceMood.EVN;
        substanceAdministration.setMoodCode(moodCode);

        final String labeledDrugName =
                (String) medication.get("medicationNameText");
        final POCDMT000040Consumable pocdmt000040Consumable =
                buildPOCDMT000040Consumable(labeledDrugName);
        substanceAdministration.setConsumable(pocdmt000040Consumable);

        final Double dose =
                (Double) medication.get("medicationQuantity");

        final String doseUnit =
                (String) medication.get("medicationQuantityUnit");

        final IVLPQ ivlpq = buildIVLPQ(dose.floatValue(), doseUnit);
        substanceAdministration.setDoseQuantity(ivlpq);

        //FIXME : I am not sure about this.
        // for the moment I add the
        final List<II> templateIds = buildTemplateIds();
        substanceAdministration.getTemplateId().addAll(templateIds);

        final CE ce = buildRouteCode();
        substanceAdministration.setRouteCode(ce);

        final CS statusCode = buildStatusCodeActive();
        substanceAdministration.setStatusCode(statusCode);

        // The code element is used to supply a code that describes the
        // substanceAdminstration act or may describe the method of
        // medication administration, such as by intravenous injection.
        final CD code = buildCode();
        substanceAdministration.setCode(code);

        // A top level <substanceAdministration> element must be uniquely identified.
        // If there is no explicit identifier for this observation in the source
        // EMR system, a GUID may be used for the root attribute,
        // and the extension may be omitted.
        // Although HL7 allows for multiple identifiers, this profile requires
        // that one and only one be used. Subordinate <substanceAdministration>
        // elements may, but need not be uniquely identified.
        final II id = buildUUIDBasedId();
        substanceAdministration.getId().add(id);

        final Date dateStart = (Date) medication.get("observationDateStart");
        Date dateEnd = (Date) medication.get("observationDateEnd");
        dateEnd = dateEnd == null ? new Date() : dateEnd;

        final SXCMTS startStopTime = buildTimePeriod(dateEnd, dateEnd);


        //final List<SXCMTS> frequency = buildExactTimePeriod();
        // final List<SXCMTS> frequency = buildUnexactTimePeriods();
        //final List<SXCMTS> frequency = buildUnexactAMTimePeriod();
        final List<SXCMTS> frequency = buildUnexactEvningTimePeriod();
        substanceAdministration.getEffectiveTime().add(startStopTime);
        substanceAdministration.getEffectiveTime().addAll(frequency);

        List<POCDMT000040EntryRelationship> entryRelationship =
                substanceAdministration.getEntryRelationship();

        final List<POCDMT000040EntryRelationship> instructions =
                buildInstructions();

        entryRelationship.addAll(instructions);

        if (substanceAdministration_JE == null) {
            final JAXBElement<POCDMT000040SubstanceAdministration> newSubstanceAdministration_JE =
                    OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5SubstanceAdministration(substanceAdministration);
            pertinentInformation.setSubstanceAdministration(newSubstanceAdministration_JE);
        }

        return pertinentInformation;
    }

    /**
     * Builds the drug consumable, a consumable contains a  ManufacturedProduct.
     *
     * @param labeledDrugName
     * @return
     */
    private POCDMT000040Consumable buildPOCDMT000040Consumable(String labeledDrugName) {
        final POCDMT000040Consumable consumable =
                OBJECT_FACTORY.createPOCDMT000040Consumable();

        final POCDMT000040ManufacturedProduct manufacturedProduct =
                OBJECT_FACTORY.createPOCDMT000040ManufacturedProduct();

        final POCDMT000040LabeledDrug labeledDrug =
                buildPOCDMT000040LabeledDrug(labeledDrugName);
        manufacturedProduct.setManufacturedLabeledDrug(labeledDrug);

        consumable.setManufacturedProduct(manufacturedProduct);

        return consumable;
    }

    /**
     * Builds the name of the Drug.
     *
     * @param name
     * @return
     */
    private POCDMT000040LabeledDrug buildPOCDMT000040LabeledDrug(String name) {
        final POCDMT000040LabeledDrug result = OBJECT_FACTORY.createPOCDMT000040LabeledDrug();

        final EN en = new EN();
        final List<Serializable> content = en.getContent();
        content.add(name);
        result.setName(en);

        return result;
    }

    /**
     * Builds the dose quantity.
     *
     * @return
     */
    private IVLPQ buildIVLPQ(float dose, String doseUnit) {

        // this is the doseQuantity
        final IVLPQ ivlpq = new IVLPQ();
        final IVXBPQ physicalQuantity = buildPhysicalQuantity_IVXBPQ(dose, doseUnit);

        final JAXBElement<IVXBPQ> ivlpqHigh =
                OBJECT_FACTORY.createIVLPQHigh(physicalQuantity);

        final JAXBElement<IVXBPQ> ivlpqLow =
                OBJECT_FACTORY.createIVLPQLow(physicalQuantity);

        ivlpq.getRest().add(ivlpqHigh);
        ivlpq.getRest().add(ivlpqLow);

        return ivlpq;
    }

    private PQ buildPhysicalQuantity_PQ(float value, String unit) {
        final PQ pq = buildPhysicalQuantity_PQ(String.valueOf(value), unit);
        return pq;
    }

    private PQ buildPhysicalQuantity_PQ(String value, String unit) {
        final PQ physicalQuantity = new PQ();
        physicalQuantity.setUnit(unit);
        physicalQuantity.setValue(value);

        return physicalQuantity;
    }

    private IVXBPQ buildPhysicalQuantity_IVXBPQ(String value, String unit) {
        final IVXBPQ physicalQuantity = new IVXBPQ();
        physicalQuantity.setUnit(unit);
        physicalQuantity.setValue(value);

        return physicalQuantity;
    }

    private IVXBPQ buildPhysicalQuantity_IVXBPQ(float value, String unit) {
        final IVXBPQ physicalQuantity = buildPhysicalQuantity_IVXBPQ(String.valueOf(value), unit);

        return physicalQuantity;
    }

    private QUPCIN043200UV01 buildQuery(String inputFile) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        Unmarshaller unmarshaller = context.createUnmarshaller();

        final InputStream stream = getStream(inputFile);

        final QUPCIN043200UV01 query =
                (QUPCIN043200UV01) unmarshaller.unmarshal(stream);
        return query;
    }

    private InputStream getStream(String name) {
        final ClassLoader classLoader =
                DefaultPCC10RequestFactory.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg =
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }

    private List<II> buildTemplateIds() {
        final List<II> templateIds = new ArrayList<II>(2);
        final II normalDosing = new II();
        normalDosing.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.7.1");
        templateIds.add(normalDosing);
        return templateIds;
    }

    /**
     * Builds a once every every 12 hours Frequency
     *
     * @return
     */
    private List<SXCMTS> buildExactTimePeriod() {
        final List<SXCMTS> effectiveTime = new ArrayList<SXCMTS>(2);
        final PIVLTS every12hours = OBJECT_FACTORY.createPIVLTS();
        every12hours.setInstitutionSpecified(false);

        // The period indicates how often the dose is given
        final PQ period = new PQ();
        period.setUnit("h");
        period.setValue("12");
        every12hours.setPeriod(period);

        effectiveTime.add(every12hours);
        return effectiveTime;
    }

    private List<SXCMTS> buildUnexactTimePeriods() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLEvent dinerEvent = buildLunchEvent();
        final EIVLTS timePeriods = buildUnexactTimePeriod(dinerEvent);
        result.add(timePeriods);

        return result;
    }

    private EIVLTS buildUnexactTimePeriod(EIVLEvent event, IVLPQ offset) {
        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();

        eivlts.setOffset(offset);
        eivlts.setEvent(event);
        return eivlts;
    }

    private EIVLTS buildUnexactTimePeriod(EIVLEvent event) {
        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();

        eivlts.setEvent(event);

        return eivlts;
    }

    private IVLPQ buildOffset(String value, String unit) {
        final IVLPQ offset = OBJECT_FACTORY.createIVLPQ();
        offset.setUnit(unit);
        offset.setValue(value);
        return offset;
    }

    private List<SXCMTS> buildUnexactAMTimePeriod() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        // before breakfast (from lat. ante cibus matutinus)
        event.setCode("ACM");
        eivlts.setEvent(event);
        result.add(eivlts);

        return result;
    }

    private List<SXCMTS> buildUnexactEvningTimePeriod() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        // before breakfast (from lat. ante cibus matutinus)
        event.setCode("PCV");
        eivlts.setEvent(event);
        result.add(eivlts);

        return result;
    }

    private SXCMTS buildTimePeriod(Date begin, Date end) {
        final IVLTS resul = new IVLTS();


        IVXBTS ivxbts = new IVXBTS();
        final String beginStr = dateFormat.format(begin);
        ivxbts.setValue(beginStr);

        JAXBElement<IVXBTS> ivltsLow = OBJECT_FACTORY.createIVLTSLow(ivxbts);
        //IVXBTS ivxbts = new IVXBTS();
        final String endStr = dateFormat.format(end);
        ivxbts.setValue(endStr);


        JAXBElement<IVXBTS> ivltsHigh = OBJECT_FACTORY.createIVLTSHigh(ivxbts);
        resul.getRest().add(ivltsLow);
        resul.getRest().add(ivltsHigh);

        return resul;
    }

    private EIVLEvent buildDinerEvent() {
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        event.setDisplayName("Evening meal");
        event.setCode("307163004");
        event.setCodeSystemName("SNOMED-CT");
        event.setCodeSystem("2.16.840.1.113883.6.96");
        return event;
    }

    private EIVLEvent buildLunchEvent() {
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        event.setDisplayName("Lunch time");
        event.setCode("307162009");
        event.setCodeSystemName("SNOMED-CT");
        event.setCodeSystem("2.16.840.1.113883.6.96");
        return event;
    }

    private CE buildRouteCode() {
        final CE ce = new CE();

        // FIXME : find the codes here
        ce.setCode("");
        ce.setCodeSystem("2.16.840.1.113883.5.11");
        ce.setCodeSystemName("RouteOfAdministration");
        ce.setDisplayName("by mouth");
        return ce;

    }

    private CS buildStatusCodeComplete() {
        final CS cs = buildStatusCode("completed", "CS Completed");
        return cs;
    }

    private CS buildStatusCodeActive() {
        final CS cs = buildStatusCode("active", "CS Active");
        return cs;
    }

    private CS buildStatusCodeSuspended() {
        final CS cs = buildStatusCode("suspended", "CS Suspended");
        return cs;
    }

    private CS buildStatusCodeAborted() {
        final CS cs = buildStatusCode("aborted", "CS Aborted");
        return cs;
    }

    /**
     * Possible vaules for code : 'active|suspended|aborted|completed'.
     *
     * @param code
     * @param dispalyName
     * @return
     */
    private CS buildStatusCode(String code, String dispalyName) {
        final CS cs = new CS();
        cs.setCode(code);
        cs.setDisplayName(dispalyName);
        return cs;
    }

    private CD buildCode() {
        final CD cd = new CD();
        cd.setCode("");
        cd.setDisplayName("");
        cd.setCodeSystem("");
        cd.setCodeSystemName("");

        return cd;
    }

    private II buildUUIDBasedId() {
        final UUID uuid = UUID.randomUUID();
        final II result = new II();

        //result.setRoot(uuid.toString());
        //FIXME : this must be unique. This value '12122' origins from the 
        // SRDC tempalte.
        result.setExtension("12122");

        return result;
    }

    private List<POCDMT000040EntryRelationship> buildInstructions() {
        final List<POCDMT000040EntryRelationship> result =
                new ArrayList<POCDMT000040EntryRelationship>();
        final POCDMT000040EntryRelationship relationship =
                OBJECT_FACTORY.createPOCDMT000040EntryRelationship();

        final POCDMT000040Act act = OBJECT_FACTORY.createPOCDMT000040Act();

        final CD actCode = new CD();
        actCode.setCodeSystem("1.3.6.1.4.1.19376.1.5.3.2");
        actCode.setCodeSystemName("IHEActCode");
        // PINSTRUCT - This is the act of providing instructions to a patient regarding the use of medication.
        actCode.setCode("PINSTRUCT");

        act.setCode(actCode);

        final CS activeStatus = buildStatusCodeActive();
        act.setStatusCode(activeStatus);

        final ED text = new ED();
        // TODO : get the right language
        text.setLanguage(Locale.ENGLISH.getLanguage());
        text.setRepresentation(BinaryDataEncoding.TXT);

        final TEL reference = OBJECT_FACTORY.createTEL();
        reference.setValue("#DummyRefrence");
        text.setReference(reference);

        // TODO : do the link to the text
        act.setText(text);

        relationship.setAct(act);
        result.add(relationship);
        return result;
    }
}
