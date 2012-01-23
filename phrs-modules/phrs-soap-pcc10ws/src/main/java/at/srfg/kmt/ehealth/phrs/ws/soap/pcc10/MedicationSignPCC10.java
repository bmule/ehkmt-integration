/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import static at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.QUPCAR004030UVUtil.buildQUPCIN043200UV01;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * It is used to generate a <a
 * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Medications</a>
 * according with the IHE standards for a given input.
 *
 * @author M1s
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class MedicationSignPCC10 {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ProblemEntryPCC10</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicationSignPCC10.class);

    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * Don't let anybody to instantiate this class.
     */
    private MedicationSignPCC10() {
        // UNIMPLEMENTD
    }

    /**
     * Builds a <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Medications</a>
     * for a Set of dyna-beans.
     *
     * @param beans the set of dyna beans used to build the <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Medications</a>
     * , it can not be null.
     * @return a <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Medications</a>
     * for the given set of dyna-beans.
     */
    static QUPCIN043200UV01 getPCC10Message(Set<DynaBean> beans) throws TripleException {

        if (beans == null) {
            final NullPointerException exception =
                    new NullPointerException("The beans argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final QUPCIN043200UV01 query;
        try {
            query = buildQUPCIN043200UV01("PCC-10-Empty-Input.xml");
        } catch (JAXBException jaxException) {
            final RuntimeException exception = new RuntimeException(jaxException);
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (beans == null || beans.isEmpty()) {
            LOGGER.debug("Input set is empty - the resunt will be also empty.");
            return query;
        }


        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final MFMIMT700712UV01QueryAck queryAck = controlActProcess.getQueryAck();
        II queryId = buildQueryId();
        queryAck.setQueryId(queryId);

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        final List<REPCMT004000UV01PertinentInformation5> pertinentInformations =
                new ArrayList<REPCMT004000UV01PertinentInformation5>();

        for (DynaBean medBean : beans) {
            final String beanToString = DynaBeanUtil.toString(medBean);
            LOGGER.debug("Tries to handle this medication {}", beanToString);

            // Here I do type check before any other task take place.
            final String type = (String) medBean.get(Constants.RDFS_TYPE);
            if (!Constants.PHRS_MEDICATION_CLASS.equals(type)) {
                final String msg = String.format("This dynabean %s has the wrong type, its type is %s.", beanToString, type);
                final IllegalArgumentException exception = new IllegalArgumentException(msg);
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            }

            final String rootId =
                    (String) medBean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
            final String note = (String) medBean.get(Constants.SKOS_NOTE);
            final DynaBean status = (DynaBean) medBean.get(Constants.HL7V3_STATUS);
            final String dateStart = (String) medBean.get(Constants.HL7V3_DATE_START);
            final String dateEnd = (String) medBean.get(Constants.HL7V3_DATE_END);
            final DynaBean frequency = (DynaBean) medBean.get(Constants.HL7V3_FREQUENCY);
            final DynaBean adminRoute = (DynaBean) medBean.get(Constants.HL7V3_ADMIN_ROUTE);
            final DynaBean dosage = (DynaBean) medBean.get(Constants.HL7V3_DOSAGE);
            // final String drugName = (String) medBean.get(Constants.HL7V3_DRUG_NAME);
            final DynaBean manufacturedProduct =
                    (DynaBean) medBean.get("http://www.icardea.at/phrs/hl7V3#manufacturedProduct");

            final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                    buildPertinentInformation(rootId,
                    note,
                    status,
                    dateStart,
                    dateEnd,
                    frequency,
                    adminRoute,
                    dosage,
                    manufacturedProduct);
            pertinentInformations.add(pertinentInformation);
        }
        careProvisionEvent.getPertinentInformation3().addAll(pertinentInformations);

        return query;
    }

    private static REPCMT004000UV01PertinentInformation5 buildPertinentInformation(
            String rootIds,
            String note,
            DynaBean status,
            String dateStart,
            String dateEnd,
            DynaBean frequency,
            DynaBean adminRoute,
            DynaBean dosage,
            DynaBean manufacturedProduct) {

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
        
        final ED subsAdmTxt = buildED("test");
        substanceAdministration.setText(subsAdmTxt);

        final POCDMT000040Consumable pocdmt000040Consumable =
                buildPOCDMT000040Consumable(manufacturedProduct);
        substanceAdministration.setConsumable(pocdmt000040Consumable);

        final IVLPQ ivlpq = buildIVLPQ(dosage);
        substanceAdministration.setDoseQuantity(ivlpq);

        LOGGER.debug("Sets the template id : {}", rootIds);
        final List<II> templateIds = buildTemplateIds(rootIds);
        substanceAdministration.getTemplateId().addAll(templateIds);

        // FIXME : ask the UI for it
        //final CE ce = buildOralRouteCode();
        final CE ce = buildRouteCode(adminRoute);
        substanceAdministration.setRouteCode(ce);

        final CS statusCode = buildStatus(status);
        substanceAdministration.setStatusCode(statusCode);

        // FIXME care about substance administration 
        // substanceAdministration.setCode(code);

        // A top level <substanceAdministration> element must be uniquely identified.
        // If there is no explicit identifier for this observation in the source
        // EMR system, a GUID may be used for the root attribute,
        // and the extension may be omitted.
        // Although HL7 allows for multiple identifiers, this profile requires
        // that one and only one be used. Subordinate <substanceAdministration>
        // elements may, but need not be uniquely identified.
        final II id = buildUUIDBasedId();
        substanceAdministration.getId().add(id);

        final SXCMTS startStopTime = buildTimePeriod(dateStart, dateEnd);
        substanceAdministration.getEffectiveTime().add(startStopTime);

        final List<POCDMT000040EntryRelationship> entryRelationship =
                substanceAdministration.getEntryRelationship();

//        final List<POCDMT000040EntryRelationship> instructions =
//                buildInstructions();

//        entryRelationship.addAll(instructions);

        if (substanceAdministration_JE == null) {
            final JAXBElement<POCDMT000040SubstanceAdministration> newSubstanceAdministration_JE =
                    OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5SubstanceAdministration(substanceAdministration);
            pertinentInformation.setSubstanceAdministration(newSubstanceAdministration_JE);
        }

        return pertinentInformation;
    }

    private static List<II> buildTemplateIds(String rootId) {
        final List<II> iis = new ArrayList<II>(1);
        final II ii1 = new II();
        ii1.setExtension(rootId);
        iis.add(ii1);
        return iis;
    }

    /**
     * Builds the drug consumable, a consumable contains a ManufacturedProduct.
     *
     * @param labeledDrugName
     * @return
     */
    private static POCDMT000040Consumable buildPOCDMT000040Consumable(DynaBean bean) {
        final DynaBean manufacturedLabeledDrug =
                (DynaBean) bean.get("http://www.icardea.at/phrs/hl7V3#manufacturedLabeledDrug");
        final DynaBean code =
                (DynaBean) manufacturedLabeledDrug.get(Constants.HL7V3_CODE);
        final String labeledDrugName = (String) code.get(Constants.SKOS_PREFLABEL);
        final DynaBean codeSystem = (DynaBean) code.get(Constants.CODE_SYSTEM);


        final POCDMT000040Consumable consumable =
                OBJECT_FACTORY.createPOCDMT000040Consumable();

        final POCDMT000040ManufacturedProduct manufacturedProduct =
                OBJECT_FACTORY.createPOCDMT000040ManufacturedProduct();
        final String classCode = (String) bean.get("http://www.icardea.at/phrs/hl7V3#classCode");
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        final POCDMT000040LabeledDrug labeledDrug =
                buildPOCDMT000040LabeledDrug(labeledDrugName);
        // FIXMe get this from UI.
        labeledDrug.setClassCode(EntityClassManufacturedMaterial.MMAT);
        labeledDrug.setDeterminerCode(EntityDeterminerDetermined.KIND);
        final CE ce = buildCode(code);
        labeledDrug.setCode(ce);


        manufacturedProduct.setManufacturedLabeledDrug(labeledDrug);

        consumable.setManufacturedProduct(manufacturedProduct);

        return consumable;
    }

    private static CE buildCode(DynaBean bean) {
        final String toString = DynaBeanUtil.toString(bean);
        LOGGER.debug("Tries to transform this [{}] Dynamic Bean in to a HL7 V3 CE instance.", toString);

        final DynaBean codeSystem =
                (DynaBean) bean.get(Constants.CODE_SYSTEM);
        final String codeSystemName =
                (String) codeSystem.get(Constants.CODE_SYSTEM_NAME);
        final String codeSystemCode =
                (String) codeSystem.get(Constants.CODE_SYSTEM_CODE);

        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);
        final String value = (String) bean.get(Constants.HL7V3_VALUE);

        final CE result = new CE();
        result.setCodeSystem(codeSystemCode);
        result.setCodeSystemName(codeSystemName);
        result.setCode(value);
        final ED originalText = buildED(prefLabel);
        result.setOriginalText(originalText);

        return result;
    }

    private static ED buildED(String text) {
        LOGGER.debug("Tries to build a HL7 V3 DE instance with the following text : {} like content.", text);
        
        final ED result = new ED();
        // TODO : get the right language
        result.setLanguage(Locale.ENGLISH.getLanguage());
        result.setRepresentation(BinaryDataEncoding.TXT);

        final TEL reference = OBJECT_FACTORY.createTEL();
        reference.setValue(text);
        result.setReference(reference);

        return result;

    }

    /**
     * Builds the name of the Drug.
     *
     * @param name
     * @return
     */
    private static POCDMT000040LabeledDrug buildPOCDMT000040LabeledDrug(String name) {
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
//    private IVLPQ buildIVLPQ(float dose, String doseUnit) {
//
//        // this is the doseQuantity
//        final IVLPQ ivlpq = new IVLPQ();
//        final IVXBPQ physicalQuantity = buildPhysicalQuantity_IVXBPQ(dose, doseUnit);
//
//        final JAXBElement<IVXBPQ> ivlpqHigh =
//                OBJECT_FACTORY.createIVLPQHigh(physicalQuantity);
//
//        final JAXBElement<IVXBPQ> ivlpqLow =
//                OBJECT_FACTORY.createIVLPQLow(physicalQuantity);
//
//        ivlpq.getRest().add(ivlpqHigh);
//        ivlpq.getRest().add(ivlpqLow);
//
//        return ivlpq;
//    }
    private static IVLPQ buildIVLPQ(float dose, String doseUnit) {

        final IVLPQ ivlpq = new IVLPQ();
        ivlpq.setValue(String.valueOf(dose));
        ivlpq.setUnit(doseUnit);

        return ivlpq;
    }

    private static IVLPQ buildIVLPQ(DynaBean bean) {
        final String toString = DynaBeanUtil.toString(bean);
        LOGGER.debug("Tries to build a Dosage for {}", toString);

        final String value =
                (String) bean.get(Constants.HL7V3_DOSAGE_VALUE);

        final DynaBean unit =
                (DynaBean) bean.get(Constants.HL7V3_DOSAGE_UNIT);
        final String unitPrefLabel = (String) unit.get(Constants.SKOS_PREFLABEL);

        final IVLPQ ivlpq = new IVLPQ();
        ivlpq.setValue(value);
        ivlpq.setUnit(unitPrefLabel);

        return ivlpq;
    }

    private static PQ buildPhysicalQuantity_PQ(float value, String unit) {
        final PQ pq = buildPhysicalQuantity_PQ(String.valueOf(value), unit);
        return pq;
    }

    private static PQ buildPhysicalQuantity_PQ(String value, String unit) {
        final PQ physicalQuantity = new PQ();
        physicalQuantity.setUnit(unit);
        physicalQuantity.setValue(value);

        return physicalQuantity;
    }

    private static IVXBPQ buildPhysicalQuantity_IVXBPQ(String value, String unit) {
        final IVXBPQ physicalQuantity = new IVXBPQ();
        physicalQuantity.setUnit(unit);
        physicalQuantity.setValue(value);

        return physicalQuantity;
    }

    private static IVXBPQ buildPhysicalQuantity_IVXBPQ(float value, String unit) {
        final IVXBPQ physicalQuantity = buildPhysicalQuantity_IVXBPQ(String.valueOf(value), unit);

        return physicalQuantity;
    }

    private static QUPCIN043200UV01 buildQuery(String inputFile) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        Unmarshaller unmarshaller = context.createUnmarshaller();

        final InputStream stream = getStream(inputFile);

        final QUPCIN043200UV01 query =
                (QUPCIN043200UV01) unmarshaller.unmarshal(stream);
        return query;
    }

    private static InputStream getStream(String name) {
        final ClassLoader classLoader =
                MedicationSignPCC10.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg =
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }

    /**
     * Builds a once every every 12 hours Frequency
     *
     * @return
     */
    private static List<SXCMTS> buildExactTimePeriod() {
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

    private static List<SXCMTS> buildUnexactTimePeriods() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLEvent dinerEvent = buildLunchEvent();
        final EIVLTS timePeriods = buildUnexactTimePeriod(dinerEvent);
        result.add(timePeriods);

        return result;
    }

    private static EIVLTS buildUnexactTimePeriod(EIVLEvent event, IVLPQ offset) {
        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();

        eivlts.setOffset(offset);
        eivlts.setEvent(event);
        return eivlts;
    }

    private static EIVLTS buildUnexactTimePeriod(EIVLEvent event) {
        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();

        eivlts.setEvent(event);

        return eivlts;
    }

    private static IVLPQ buildOffset(String value, String unit) {
        final IVLPQ offset = OBJECT_FACTORY.createIVLPQ();
        offset.setUnit(unit);
        offset.setValue(value);
        return offset;
    }

    private static List<SXCMTS> buildUnexactAMTimePeriod() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        // before breakfast (from lat. ante cibus matutinus)
        event.setCode("ACM");
        eivlts.setEvent(event);
        result.add(eivlts);

        return result;
    }

    private static List<SXCMTS> buildUnexactEvningTimePeriod() {
        final List<SXCMTS> result = new ArrayList<SXCMTS>(2);

        final EIVLTS eivlts = OBJECT_FACTORY.createEIVLTS();
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        // before breakfast (from lat. ante cibus matutinus)
        event.setCode("PCV");
        eivlts.setEvent(event);
        result.add(eivlts);

        return result;
    }

    private static SXCMTS buildTimePeriod(String begin, String end) {
        final IVLTS resul = new IVLTS();

        final IVXBTS ivxbtsBegin = new IVXBTS();
        ivxbtsBegin.setValue(begin);
        JAXBElement<IVXBTS> ivltsLow = OBJECT_FACTORY.createIVLTSLow(ivxbtsBegin);

        final IVXBTS ivxbtsEnd = new IVXBTS();
        ivxbtsEnd.setValue(end);
        JAXBElement<IVXBTS> ivltsHigh = OBJECT_FACTORY.createIVLTSHigh(ivxbtsEnd);

        resul.getRest().add(ivltsLow);
        resul.getRest().add(ivltsHigh);

        return resul;
    }

    private static EIVLEvent buildDinerEvent() {
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        event.setDisplayName("Evening meal");
        event.setCode("307163004");
        event.setCodeSystemName("SNOMED-CT");
        event.setCodeSystem("2.16.840.1.113883.6.96");
        return event;
    }

    private static EIVLEvent buildLunchEvent() {
        final EIVLEvent event = OBJECT_FACTORY.createEIVLEvent();
        event.setDisplayName("Lunch time");
        event.setCode("307162009");
        event.setCodeSystemName("SNOMED-CT");
        event.setCodeSystem("2.16.840.1.113883.6.96");
        return event;
    }

    private static CE buildRouteCode(DynaBean bean) {
        final String toString = DynaBeanUtil.toString(bean);
        LOGGER.debug("Tries to build a Route Code for {}", toString);
        final String prefLabel =
                (String) bean.get("http://www.w3.org/2004/02/skos/core#prefLabel");
        final DynaBean code = (DynaBean) bean.get(Constants.CODE);
        final String codeValue = (String) code.get(Constants.CODE_VALUE);

        final DynaBean codeSystem = (DynaBean) code.get(Constants.CODE_SYSTEM);
        final String codeSystemName = (String) codeSystem.get(Constants.CODE_SYSTEM_NAME);
        final String codeSystemCode = (String) codeSystem.get(Constants.CODE_SYSTEM_CODE);


        CE ce = new CE();
        ce.setCode(codeValue);
        ce.setCodeSystem(codeSystemCode);
        ce.setCodeSystemName(codeSystemName);
        ce.setDisplayName(prefLabel);
        return ce;
    }

    private static CS buildStatus(DynaBean dynaBean) {
        final String toString = DynaBeanUtil.toString(dynaBean);
        LOGGER.debug("Tries to transform this [{}] Dynamic Bean in to a HL7 V3 CS instance.", toString);

        final String prefLabel = (String) dynaBean.get(Constants.SKOS_PREFLABEL);

        final CS statusCode = new CS();
        statusCode.setDisplayName(prefLabel);

        final DynaBean codeBean = (DynaBean) dynaBean.get(Constants.CODE);
        final String codeValue = (String) codeBean.get(Constants.CODE_VALUE);
        statusCode.setCode(codeValue);

        final DynaBean codeSystemBean = (DynaBean) codeBean.get(Constants.CODE_SYSTEM);

        final String codeSystemCode =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        statusCode.setCode(codeSystemCode);

        final String codeSystemName =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_NAME);
        statusCode.setCodeSystem(codeSystemName);

        return statusCode;
    }

    private static II buildUUIDBasedId() {
        final II result = new II();

        // TODO : this must be unique. This value '12122' origins from the SRDC tempalte.
        result.setExtension("12122");

        return result;
    }

    private static List<POCDMT000040EntryRelationship> buildInstructions() {
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
        final CS activeStatus = buildStatus(null);
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

    private static II buildQueryId() {
        final II queryId = new II();
        queryId.setRoot("1");
        queryId.setExtension("3");
        return queryId;
    }
}
