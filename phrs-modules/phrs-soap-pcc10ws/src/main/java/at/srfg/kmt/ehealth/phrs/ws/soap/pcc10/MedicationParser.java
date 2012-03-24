/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to transform HL7 Medication messages in PHRS information. This must
 * begin with the CareProvisionEvent to get the II ids and Pertinent Info
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

    /**
     * The connection to the triple store.
     */
    private final GenericTriplestore triplestore;

    /**
     * The client used to store the medication related information.
     */
    private final MedicationClient client;

    /**
     * Builds
     * <code>MedicationParser</code> instance.
     */
    public MedicationParser() {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        client = new MedicationClient(triplestore);
        client.setCreator(Constants.EHR_OWNER);
    }
    //TODO why doesnt this check for templateId extension?
    @Override
    public boolean canParse(REPCMT004000UV01PertinentInformation5 toParse) {
        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
                toParse.getSubstanceAdministration();
        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;

        return hasSubstanceAdministration;
    }

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse, String userId)
            throws ParserException {
        LOGGER.debug("Medication parser Tries to parse {}", toParse);
        LOGGER.warn("PCC-10 Medication received for parsing, wait for store message");

        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB = toParse.getSubstanceAdministration();
        final POCDMT000040SubstanceAdministration substanceAdministration =
                substanceAdministration_JAXB.getValue();
        final List<SXCMTS> effectiveTime =
                substanceAdministration.getEffectiveTime();

        final CS statusCode = substanceAdministration.getStatusCode();
        final CE routeCode = substanceAdministration.getRouteCode();
        final IVLPQ doseQuantity = substanceAdministration.getDoseQuantity();

        final String dosageUnit =getUnitURI(doseQuantity);

        
        final POCDMT000040Consumable consumable = substanceAdministration.getConsumable();
        final POCDMT000040ManufacturedProduct manufacturedProduct = consumable.getManufacturedProduct();
        final POCDMT000040LabeledDrug manufacturedLabeledDrug = manufacturedProduct.getManufacturedLabeledDrug();
        final CE code = manufacturedLabeledDrug.getCode();
        final ED originalText = code.getOriginalText();
        
        final String statusURI = getStatusURI(statusCode);


        final IVLTS times = (IVLTS) effectiveTime.iterator().next();
        final List<JAXBElement<? extends QTY>> rest = times.getRest();
        
        final JAXBElement<? extends QTY> t1_JAXB = rest.get(0);
        final IVXBTS t1 = (IVXBTS) t1_JAXB.getValue();
        final String t1Str = t1.getValue();
        
        final JAXBElement<? extends QTY> t2_JAXB = rest.get(1);
        final IVXBTS t2 = (IVXBTS) t2_JAXB.getValue();
        final String t2Str = t2.getValue();

        // I presume that the t1 is low and t2 is high 
        
        final String adminRouteURI = getAdminRouteURI(routeCode);
        
        final String dosageValue = doseQuantity.getValue();
        //TODO  dosageUnit     ivlpq
        //final String dosageUnit = Constants.PILL;
        
        final String drugCode = code.getCode();
        final String drugName = getDrugName(code);

        try {
            final String frequencyURI = client.buildNullFrequency();
            client.addMedicationSign(userId,
                    "Imported EHR system",
                    statusURI,
                    t1Str,
                    t2Str, 
                    frequencyURI,
                    adminRouteURI,
                    dosageValue,
                    dosageUnit,
                    drugName,
                    drugCode);

        } catch (Exception exception) {
            final ParserException parserException = new ParserException(exception);
            LOGGER.error(parserException.getMessage(), parserException);
            throw parserException;
        }
        //warn shows on console
        LOGGER.warn("PCC-10 Medication stored");
        LOGGER.debug("PCC-10 Medication stored");
    }

    private String getStatusURI(CS statusCode) {
        //default
        if(statusCode == null){
            LOGGER.error("statusCode element null, return STATUS_ACTIVE");
            return Constants.STATUS_ACTIVE;
        }
        //This appears in pcc 10
        String code = statusCode.getCode();
        if(code != null && !code.isEmpty()) {
            //
        } else {
            code = statusCode.getDisplayName();
        }

        if(code == null ) {
            LOGGER.error("statusCode.getDisplayName()  null, return STATUS_ACTIVE");
            return Constants.STATUS_ACTIVE;
        }

        if ("complete".equalsIgnoreCase(code)) {
            return Constants.STATUS_COMPELETE;
        }

        if ("active".equalsIgnoreCase(code)) {
            return Constants.STATUS_ACTIVE;
        }

        return Constants.STATUS_ACTIVE;
    }

    private String getAdminRouteURI(CE routeCode) {
        //resolve nullpointer exception on live dataa
        if(routeCode == null){
            LOGGER.error("routeCode null, use default");
            return Constants.HL7V3_ORAL_ADMINISTRATION;
        }

        try {
            final String code = routeCode.getCode();

            if ("PO".equalsIgnoreCase(code)) {
                return Constants.HL7V3_ORAL_ADMINISTRATION;
            }

            if ("PO".equalsIgnoreCase(code)) {
                return Constants.HL7V3_INJECTION_ADMINISTRATION;
            }
        } catch (Exception e) {
            LOGGER.error("routeCode error, use default");
        }


        return Constants.HL7V3_ORAL_ADMINISTRATION;
    }
    
    private String getUnitURI(IVLPQ ivlpq) {

        if(ivlpq == null){
            LOGGER.error("getUnitURI ivlpq null, use pill ");
            return Constants.PILL;
        }

        final String unit = ivlpq.getUnit();

        if(unit==null) {
            LOGGER.error("getUnitURI ivlpq.getUnit() null, use pill ");
            return Constants.PILL;
        }

        
        if ("pill".equalsIgnoreCase(unit)) {
            return Constants.PILL;
        }
        
        if ("mg".equalsIgnoreCase(unit)) {
            return Constants.MILLIGRAM;
        }

        // TODO : there are more units int the constants but I don't think that
        return Constants.PILL;
    }
    
    private String getDrugName(CE ce) {
        if(ce == null){
            LOGGER.error("getDrugName ce null: label=missing drug name");
            return "missing drug name ? ";

        }
        String displayName= ce.getDisplayName();
        if(displayName != null){
            return displayName;
        }
        
        final ED originalText = ce.getOriginalText();
        if (originalText == null) {
            LOGGER.error("getDrugName ED originalText null: label=unknown. ");
            return "unknown.";
        }
        //This is null
        final TEL reference = originalText.getReference();
        if(reference==null){
            String tmp =originalText.toString();
            if(tmp!=null) return tmp;

            LOGGER.error("getDrugName TEL reference null: label=unknown ");
            return "unknown";
        }
        
        return reference.getValue();
    }

    @Override
    public String toString() {
        return "MedicationParser";
    }
}
