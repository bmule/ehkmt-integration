/*
 * Project :iCardea
 * File : VitalSignsFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.PCC10_OUTPUT_FILE;
import static at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup.lookupLocal;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to build Vital Signs according with the HL7 v3. <br>
 * This class can not be extended.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class VitalSignsFactory implements PCC10Factory<QUPCIN043200UV01> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.VocabularyServlet</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicationFactory.class);

    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final DateFormat dateFormat = new SimpleDateFormat(pattern);

    /**
     * All vital signs to be transformed medication according with the HL7 v3.
     */
    private Set<DynaBean> vitalSigns;

    VitalSignsFactory() {
        // UNIMPLEMENTED
    }

    @Override
    public QUPCIN043200UV01 build() throws PCC10BuildException {

        final QUPCIN043200UV01 query;
        try {
            query = buildQuery(PCC10_OUTPUT_FILE);
        } catch (JAXBException exception) {
            throw new PCC10BuildException(exception);
        }

        if (vitalSigns == null || vitalSigns.isEmpty()) {
            return query;
        }

        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        final List<REPCMT004000UV01PertinentInformation5> pertinentInformations =
                new ArrayList<REPCMT004000UV01PertinentInformation5>();

        for (DynaBean medication : vitalSigns) {
            final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                    buildPertinentInformation(medication);
            pertinentInformations.add(pertinentInformation);
        }

        careProvisionEvent.getPertinentInformation3().addAll(pertinentInformations);

        return query;
    }

    Set<DynaBean> getVitalSigns() {
        return Collections.unmodifiableSet(vitalSigns);
    }

    void setVitalsigns(Set<DynaBean> medication) {
        this.vitalSigns = medication;
    }

    private REPCMT004000UV01PertinentInformation5 buildPertinentInformation(DynaBean vitalSign) {
        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();

        // get the observation
        final JAXBElement<POCDMT000040Observation> observation_JE =
                pertinentInformation.getObservation();

        final POCDMT000040Observation observation =
                observation_JE == null
                ? new POCDMT000040Observation()
                : observation_JE.getValue();

        // FIXME : ask the ui for it
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(XActMoodDocumentObservation.EVN);

        // the templates id I copyed from the specs, I hope they are correct
        final List<II> templateIds = buildTemplateIds();
        observation.getTemplateId().addAll(templateIds);



        // the vital sign status
        final String medStatus = (String) vitalSign.get("medicationStatus");
        final CS statusCode = buildCS(medStatus);
        observation.setStatusCode(statusCode);


        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JE =
                pertinentInformation.getSubstanceAdministration();

        final POCDMT000040SubstanceAdministration substanceAdministration =
                substanceAdministration_JE == null
                ? new POCDMT000040SubstanceAdministration()
                : substanceAdministration_JE.getValue();
        final XDocumentSubstanceMood moodCode = XDocumentSubstanceMood.EVN;
        substanceAdministration.setMoodCode(moodCode);

        final String labeledDrugName =
                (String) vitalSign.get("medicationNameText");
        final POCDMT000040Consumable pocdmt000040Consumable =
                buildPOCDMT000040Consumable(vitalSign);
        substanceAdministration.setConsumable(pocdmt000040Consumable);

        final Double dose =
                (Double) vitalSign.get("medicationQuantity");
        // FIXME : this is because the class definition is wrong (BUG).
        // This is a bug, dose is from type string. 
        final float doseFloat = dose.floatValue();


        final String doseUnit =
                (String) vitalSign.get("medicationQuantityUnit");

        final IVLPQ ivlpq = buildIVLPQ(doseFloat, doseUnit);
        substanceAdministration.setDoseQuantity(ivlpq);

        // FIXME : ask the UI for it
        final CE ce = buildOralRouteCode();
        substanceAdministration.setRouteCode(ce);

        return pertinentInformation;
    }

    /**
     * Builds the drug consumable, a consumable contains a  ManufacturedProduct.
     *
     * @param labeledDrugName
     * @return
     */
    private POCDMT000040Consumable buildPOCDMT000040Consumable(DynaBean medication) {

        final POCDMT000040Consumable consumable =
                OBJECT_FACTORY.createPOCDMT000040Consumable();

        final POCDMT000040ManufacturedProduct manufacturedProduct =
                OBJECT_FACTORY.createPOCDMT000040ManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);
        final String labeledDrugName = (String) medication.get("medicationNameText");
        final POCDMT000040LabeledDrug labeledDrug =
                buildPOCDMT000040LabeledDrug(labeledDrugName);
        // FIXMe get this from UI.
        labeledDrug.setClassCode(EntityClassManufacturedMaterial.MMAT);
        labeledDrug.setDeterminerCode(EntityDeterminerDetermined.KIND);

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
        normalDosing.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.13");
        normalDosing.setExtension("2.16.840.1.113883.10.20.1.31");
        normalDosing.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.13.2");
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

        final IVXBTS ivxbtsBegin = new IVXBTS();
        final String beginStr = dateFormat.format(begin);
        ivxbtsBegin.setValue(beginStr);
        JAXBElement<IVXBTS> ivltsLow = OBJECT_FACTORY.createIVLTSLow(ivxbtsBegin);

        final IVXBTS ivxbtsEnd = new IVXBTS();
        final String endStr = dateFormat.format(end);
        ivxbtsEnd.setValue(endStr);
        JAXBElement<IVXBTS> ivltsHigh = OBJECT_FACTORY.createIVLTSHigh(ivxbtsEnd);

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

    private CE buildOralRouteCode() {
        CE ce = new CE();
        ce.setCode("PO");
        ce.setCodeSystem("2.16.840.1.113883.5.112");
        ce.setCodeSystemName("HL7 RouteOfAdministration Vocabulary");
        ce.setDisplayName("oral");
        return ce;
    }

    private CS buildCS(String codeSystemAndCode) {
        final ControlledItem item = getContentItem(codeSystemAndCode);
        if (item == null) {
            LOGGER.warn("No match for {} ", codeSystemAndCode);
            return buildStatusCode("phf", codeSystemAndCode, codeSystemAndCode);
        }

        final CS cs = new CS();
        cs.setCodeSystem(item.getCodeSystem());
        cs.setCode(item.getCode());
        cs.setDisplayName(item.getPrefLabel());
        return cs;
    }

    private CS buildStatusCodeActive() {
        final CS cs = new CS();
        cs.setCodeSystem("UMLS");
        cs.setCode("C0205177");
        cs.setDisplayName("Active");
        return cs;
    }

    /**
     * Possible vaules for code : 'active|suspended|aborted|completed'.
     *
     * @param code
     * @param dispalyName
     * @return
     */
    private CS buildStatusCode(String codeSystem, String code, String dispalyName) {
        final CS cs = new CS();
        cs.setCodeSystem(codeSystem);
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

        // FIXME : ask the UI for it
        final CS activeStatus = buildStatusCodeActive();
        act.setStatusCode(activeStatus);

        final ED text = new ED();
        // TODO : get the right language
        text.setLanguage(Locale.ENGLISH.getLanguage());
        text.setRepresentation(BinaryDataEncoding.TXT);

        final TEL reference = OBJECT_FACTORY.createTEL();
        reference.setValue("#xxx");
        text.setReference(reference);

        // TODO : do the link to the text
        act.setText(text);

        relationship.setAct(act);
        result.add(relationship);
        return result;
    }

    private ControlledItem getContentItem(String in) {
        final int indexOf = in.indexOf(":");
        String codeStystemCode = in.substring(0, indexOf);
        String code = in.substring(indexOf + 1, in.length());

        final ControlledItemRepository repository;

        try {
            repository = lookupLocal(ControlledItemRepository.class);
            final ControlledItem item =
                    repository.getByCodeSystemAndCode(codeStystemCode, code);
            return item;
        } catch (Exception exception) {
            return null;
        }

    }

    private CD getVitalSignCode(DynaBean vitalSign) {
        final String type = (String) vitalSign.get("_phrsBeanClassURI");

        // FIXME : isType must be a geric method !
        boolean isBodyWeight =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight".equals(type);
        boolean isBloodPressure =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure".equals(type);

        if (!isBloodPressure && !isBodyWeight) {
        }

        if (isBloodPressure) {
            return getVitalSignCodeForBodyWeight(vitalSign);
        }

        if (isBodyWeight) {
            return getVitalSignCodeForBodyWeight(vitalSign);
        }

        throw new IllegalArgumentException("The type : " + type + " is not supported.");
    }

    private CD getVitalSignCodeForBodyWeight(DynaBean vitalSign) {
        final CD result = new CD();
        vitalSign.get("bodyBMI");
        vitalSign.get("bodyweight");
        vitalSign.get("height");
        vitalSign.get("msystem");


        return result;
    }

    private CD getVitalSignCodeForBloodPressure(DynaBean vitalSign) {
        final CD result = new CD();
        vitalSign.get("");

        vitalSign.get("bpDiastolic");
        vitalSign.get("bpHeartRate");
        vitalSign.get("bpSystolic");



        return result;

    }
}
