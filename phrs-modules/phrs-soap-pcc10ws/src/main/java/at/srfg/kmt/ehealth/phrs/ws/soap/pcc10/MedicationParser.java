/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import javax.xml.bind.JAXBElement;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.hl7.v3.*;
//import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01Subject5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;


/**
 * Used to transform HL7 Medication messages in PHRS information.
 * This must begin with the CareProvisionEvent to get the II ids and Pertinent Info
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */

final class MedicationParser implements Parser<REPCMT004000UV01PertinentInformation5> {

    //Parser<REPCMT004000UV01PertinentInformation5>
    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.MedicationParser</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicationParser.class);
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * Builds <code>MedicationParser</code> instance.
     */
    public MedicationParser() {
        // UNIMPLEMENTED
    }


    /**
     * Must have pertinent info and Patient ID
     *
     * @param toParse the object to be parsed.
     * @return
     */
    //see also TaskUtil and MedicationSignPCC10
   
    public boolean canParse(REPCMT004000UV01CareProvisionEvent toParse) {
        boolean hasPertinentInfo = false;
        boolean hasPatientId = false;
        try {
            //CHECK info

            List<II> ids = toParse.getRecordTarget().getValue().getPatient().getValue().getPatientPerson().getValue().getId();

            if (ids != null) {
                String patientId = TaskUtil.getPatientId(toParse);
                hasPatientId = patientId != null;   //can useTaskUtil.hasPatientId(toParse) method but we want to log patientId

                if (hasPatientId) {
                    LOGGER.debug("canParse REPCMT004000UV01CareProvisionEvent patientID=" + patientId);
                } else {
                    LOGGER.error("canParse REPCMT004000UV01CareProvisionEvent error, Message is missing II ids");

                }
            } else {
                LOGGER.error("canParse REPCMT004000UV01CareProvisionEvent error, Message is missing II ids");
            }

            //There are info parts 1...x
            JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration = getSubstanceAdministration(toParse);

            if (substanceAdministration != null) {
                hasPertinentInfo = true;
            }


        } catch (Exception e) {
            LOGGER.error("canParse REPCMT004000UV01CareProvisionEvent error, Message is missing required structures", e);
        }

        return hasPertinentInfo && hasPatientId;
    }

//        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
//         toParse.getSubstanceAdministration();
//        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;
//        return hasSubstanceAdministration;

//    private static SXCMTS buildTimePeriod(String begin, String end) {
//        final IVLTS resul = new IVLTS();
//
//        final IVXBTS ivxbtsBegin = new IVXBTS();
//        ivxbtsBegin.setValue(begin);
//        JAXBElement<IVXBTS> ivltsLow = OBJECT_FACTORY.createIVLTSLow(ivxbtsBegin);
//
//        final IVXBTS ivxbtsEnd = new IVXBTS();
//        ivxbtsEnd.setValue(end);
//        JAXBElement<IVXBTS> ivltsHigh = OBJECT_FACTORY.createIVLTSHigh(ivxbtsEnd);
//
//        resul.getRest().add(ivltsLow);
//        resul.getRest().add(ivltsHigh);
//
//        return resul;
//  }


    private String getDateHigh(SXCMTS input) {
        String dateStr = null;
        if (input != null) {
            try {
                IVLTS ivlts = (IVLTS) input;

                List list = ivlts.getRest();

                if (list.size() > 0) {
                    JAXBElement obj = (JAXBElement) list.get(0);
                    //JAXBElement<IVXBTS> ivlts_JE = (JAXBElement<IVXBTS>) list.get(0);
                    if (obj.getValue() instanceof IVXBTS) {
                        IVXBTS ivxbts = (IVXBTS) obj.getValue();
                        dateStr = ivxbts.getValue();

                    }
                }
            } catch (Exception e) {
                LOGGER.debug("Tries to parse getDateHigh ivlts_JE {}", input);
                LOGGER.error("getDateHigh error {}", e);
            }
        }
        if (dateStr == null) {
            LOGGER.error("getDateHigh NULL ivlts_JE {}", input);
        }
        return dateStr;
    }

    private String getDrugName(POCDMT000040SubstanceAdministration substanceAdministration) {
        String value = null;
        try {
            EN enName = substanceAdministration.getConsumable().getManufacturedProduct()
                    .getManufacturedLabeledDrug().getName();
            List<Serializable> content = enName.getContent();
            if (content.size() > 0) {
                // drugName = (String) content.get(0);
                value = (String) content.get(0);
            }

        } catch (Exception exception) {
            LOGGER.warn("getDrugName getName" + exception.getMessage(), exception);
        }

        if (value != null && !value.isEmpty()) {
            //ok
        } else {
            try {
                value = substanceAdministration.getConsumable().getManufacturedProduct()
                        .getManufacturedLabeledDrug().getCode().getDisplayName();

            } catch (Exception exception) {
                LOGGER.warn("getDrugName getDisplayName " + exception.getMessage(), exception);
            }
        }
        //POCDMT000040LabeledDrug labeledDrug= (POCDMT000040LabeledDrug)enName.getContent().get(0);

        if (value == null) {
            LOGGER.warn("getDrugName  NULL");
        }
        return value;
    }

    private String getDrugCode(POCDMT000040SubstanceAdministration substanceAdministration) {
        String value = null;
        try {
            value = substanceAdministration.getConsumable().getManufacturedProduct()
                    .getManufacturedLabeledDrug().getCode().getCode();


        } catch (Exception exception) {
            LOGGER.error("getDrugCode " + exception.getMessage(), exception);
        }
        if (value == null) {
            LOGGER.warn("getDrugCode  NULL");
        }
        return value;
    }

    public String getRouteAdmin(POCDMT000040SubstanceAdministration substanceAdministration) {
        // Constants.HL7V3_ORAL_ADMINISTRATION
        String value = null;
        try {
            value = substanceAdministration.getRouteCode().getCode();


        } catch (Exception exception) {
            LOGGER.error("getRouteAdmin " + exception.getMessage(), exception);
        }
        if (value == null) {
            LOGGER.warn("getRouteAdmin  NULL");
        }
        return value;

    }

    public void parse(REPCMT004000UV01CareProvisionEvent toParse) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);
        GenericTriplestore triplestore = null;
        try {
            final TriplestoreConnectionFactory connectionFactory =
                    TriplestoreConnectionFactory.getInstance();
            triplestore = connectionFactory.getTriplestore();

            final MedicationClient client = new MedicationClient(triplestore);
            String patientId = TaskUtil.getPatientId(toParse);
            //do not need root domain of identity

            String quantity = "1";
            String units = null;

            String status = Constants.STATUS_COMPELETE; //defautl is completed

            String drugName = null;
            String drugCode = null;

            String dateStart = null;
            String dateEnd = null;
            String routeAdmin = null;
            String theNote = "";


            JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JE = getSubstanceAdministration(toParse);

            if (substanceAdministration_JE != null) {

                POCDMT000040SubstanceAdministration substanceAdministration = substanceAdministration_JE.getValue();

                quantity = substanceAdministration.getDoseQuantity().getValue();

                units = substanceAdministration.getDoseQuantity().getUnit();

                drugName = getDrugName(substanceAdministration);
                drugCode = getDrugCode(substanceAdministration);
                //TODO Date issues
                //SXCMTS times = substanceAdministration.getEffectiveTime().get(0);

                //final SXCMTS startStopTime = buildTimePeriod(dateStart, dateEnd);
                //substanceAdministration.getEffectiveTime().add(startStopTime);
            }

            //Identity
            //PertinentInfo - issue API does not have REPCMT004000UV01PertinentInformation5, only 1..3
            //client.buildFrequency("morgen", -1, 8, Constants.HOUR),

            client.addMedicationSign(
                    patientId,
                    theNote,//"Free text note for the medication.",
                    status != null && !status.isEmpty() ? status : Constants.STATUS_COMPELETE,
                    dateStart != null && !dateStart.isEmpty() ? dateStart : "201011030000",
                    dateEnd != null && !dateEnd.isEmpty() ? dateEnd : "201105051010",
                    client.buildNullFrequency(),
                    routeAdmin != null && !routeAdmin.isEmpty() ? routeAdmin : Constants.HL7V3_ORAL_ADMINISTRATION,
                    quantity != null && !quantity.isEmpty() ? quantity : "1",
                    units != null && !units.isEmpty() ? units : Constants.MILLIGRAM,
                    drugName != null && !drugName.isEmpty() ? drugName : "UNDEFINED EHR",
                    drugCode != null && !drugCode.isEmpty() ? drugCode : "UNDEFINED EHR"
            );


        } catch (Exception e) {
            LOGGER.error("MedicationClient for parsing", e);
        } finally {
            if (triplestore != null) {
                try {
                    ((GenericTriplestoreLifecycle) triplestore).shutdown();
                } catch (Exception exception) {
                    LOGGER.warn(exception.getMessage(), exception);
                }
            }
        }
    }

    /**
     * From MedicatonSignPCC10, get this from PertinentInformation3
     * careProvisionEvent.getPertinentInformation3().addAll(pertinentInformations);
     *
     * @param toParse
     * @return
     */
    public REPCMT004000UV01PertinentInformation5 getPertinentInfo(REPCMT004000UV01CareProvisionEvent toParse) {
        REPCMT004000UV01PertinentInformation5 info = null;
        try {
            info = toParse.getPertinentInformation3().get(0);
        } catch (Exception e) {
            LOGGER.error("Medication Parse REPCMT004000UV01PertinentInformation5 getPertinentInformation3 error", e);
        }
        return info;

    }

    public JAXBElement<POCDMT000040SubstanceAdministration> getSubstanceAdministration(REPCMT004000UV01CareProvisionEvent toParse) {
        REPCMT004000UV01PertinentInformation5 pertinentInformation5 = getPertinentInfo(toParse);

        JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB = null;

        try {
            substanceAdministration_JAXB = pertinentInformation5.getSubstanceAdministration();
        } catch (Exception e) {
            LOGGER.error("substanceAdministration_JAXB error", e);
        }

        return substanceAdministration_JAXB;
    }


    //@Override

    /**
     * @param toParse
     * @return
     * @deprecated because parsing on higher level element
     */
    public boolean canParseXX(REPCMT004000UV01PertinentInformation5 toParse) {

        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
                toParse.getSubstanceAdministration();
        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;
        return hasSubstanceAdministration;
    }
//
//    @Override
//    public void parse(REPCMT004000UV01PertinentInformation5 toParse) throws ParserException {
//        LOGGER.debug("Tries to parse {}", toParse);
//    }


//         final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
//                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
//        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
//                subject2.getCareProvisionEvent();
//
//        //FIXXME Insert patient ID into query template
//        //FIXXME OWNER BOB
//        TaskUtil.createPatientIdNode(careProvisionEvent, TaskUtil.getdefaultRoot(), patientId, true);//clear


    @Override
    public String toString() {
        return "MedicationParser";
    }
//<subject2>
//    <careProvisionEvent>
//        <recordTarget>
//            <patient>
//                <id root='1' extension='_PATIENT_ID_'/>
//                <addr></addr>
//                <telecom value='1' use='1'/>
//                <statusCode code='active'/>
//                <patientPerson>
//                <name>
//                    <given>Andreas</given>
//                    <family>Schmidt</family>
//                </name>
//                <administrativeGenderCode code='M' displayName='' codeSystem='2.16.840.1.113883.5.1' codeSystemName='AdministrativeGender'/>
//                <birthTime value='19530422153118'/>
//                </patientPerson>
//             </patient>
//        </recordTarget>
//    </careProvisionEvent>
//</subject2>


    @Override
    public boolean canParse(REPCMT004000UV01PertinentInformation5 toParse) {

        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
                toParse.getSubstanceAdministration();
        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;
        return hasSubstanceAdministration;
    }

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);
    }
    
    
}
