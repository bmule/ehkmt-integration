/*
 * Project :iCardea
 * File : MedicationClient.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.LITERAL;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.RESOURCE;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * /**
 * Used to persist and retrieve medicine related information. <br/> This class
 * can not be extended.
 *
 * @version 0.1
 * @since 0.1
 * @author Miahi
 */
public final class MedicationClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicinClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicationClient.class);

    /**
     * Holds the name for the creator, the instance responsible to create
     * medication instances with this client.
     */
    private String creator;

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    private final SchemeClient schemeClient;

    public MedicationClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();

        schemeClient = new SchemeClient(triplestore);
        creator = MedicationClient.class.getName();
    }

    /**
     * Builds a
     * <code>MedicinClient</code> instance for a given triplestrore.
     *
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the
     * <code>triplestore</code> argument is null.
     */
    public MedicationClient(GenericTriplestore triplestore) {

        if (triplestore == null) {
            throw new NullPointerException("The triplestore");
        }

        this.triplestore = triplestore;

        schemeClient = new SchemeClient(triplestore);
        creator = MedicationClient.class.getName();
    }

    public String addMedicationSign(String user, String note, String statusURI,
            String startDate, String endDate, String frequencyURI,
            String adminRouteURI, String dosageValue, String dosageUnit,
            String drugName) throws TripleException {

        final String result = addMedicationSign(user,
                note,
                statusURI,
                startDate,
                endDate,
                frequencyURI,
                adminRouteURI,
                dosageValue,
                dosageUnit,
                drugName,
                null);
        LOGGER.debug("New medication  was added, the new added URI is : {}", result);
        return result;
    }

    public String addMedicationSign(String user, String note, String statusURI,
            String startDate, String endDate, String frequencyURI,
            String adminRouteURI, String dosageValue, String dosageUnit,
            String drugName,
            String drugCode) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, user, LITERAL);

        // this can help to find a medication, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_MEDICATION_CLASS,
                RESOURCE);

        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        // I preffer to hang a specific name for the Creator only for test 
        // purposes. In this way I can follow the the origin for a certain 
        // resource. 
        triplestore.persist(subject,
                Constants.CREATOR,
                creator,
                LITERAL);

        // HL7 specific informations.
        // according with the specification the medcation requires this 
        // template root id.
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.MEDICATION_NORMAL_DOSING,
                LITERAL);

        triplestore.persist(subject,
                Constants.SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_STATUS,
                statusURI,
                RESOURCE);


        final String startDateStr = startDate == null
                ? DateUtil.getFormatedDate(new Date())
                : startDate;
        triplestore.persist(subject,
                Constants.HL7V3_DATE_START,
                startDateStr,
                LITERAL);

        final String endDateStr = endDate == null
                ? DateUtil.getFormatedDate(new Date())
                : endDate;
        triplestore.persist(subject,
                Constants.HL7V3_DATE_END,
                endDateStr,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_FREQUENCY,
                frequencyURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_ADMIN_ROUTE,
                adminRouteURI,
                RESOURCE);

        final String dosage = buildDosage(dosageValue, dosageUnit);
        triplestore.persist(subject,
                Constants.HL7V3_DOSAGE,
                dosage,
                RESOURCE);

        final String buildManufacturedProduct =
                buildManufacturedProduct(drugName, drugCode);
        triplestore.persist(subject,
                "http://www.icardea.at/phrs/hl7V3#manufacturedProduct",
                buildManufacturedProduct,
                RESOURCE);

        return subject;
    }

    public String buildDosage(String value, String unitURI) throws TripleException {

        final String subject =
                triplestore.persist(Constants.HL7V3_DOSAGE_VALUE, value, LITERAL);

        // this can help to find a medication, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_MEDICATION_DOSAGE_CLASS,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_DOSAGE_UNIT,
                unitURI,
                RESOURCE);

        return subject;

    }

    /**
     * Returns all the medication for all the users.
     *
     * @return all the medication for all the users.
     * @throws TripleException by any kind of triplestore related error.
     */
    public Iterable<Triple> getMedicationTriples() throws TripleException {
        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(Constants.RDFS_TYPE,
                Constants.PHRS_MEDICATION_CLASS, RESOURCE);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }

    /**
     * Returns all the medication for a given user.
     *
     * @param userId
     * @return
     * @throws TripleException
     */
    public Iterable<Triple> getMedicationTriplesForUser(String userId) throws TripleException {

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_MEDICATION_CLASS);
        queryMap.put(Constants.OWNER, userId);

        // here I search for all resources with 
        // rdf type == medication
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }

    public Iterable<String> getMedicationURIsForUser(String userId)
            throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_MEDICATION_CLASS);
        queryMap.put(Constants.OWNER, userId);

        // here I search for all resources with 
        // rdf type == medication
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        return resources;
    }

    public void updateMedication(String resourceURI, String predicate, String newValue)
            throws TripleException {

        final boolean resourceExists = triplestore.exists(resourceURI);
        if (!resourceExists) {
            final String msg =
                    String.format("There is no resource for this URI %s ", resourceURI);
            throw new IllegalArgumentException(msg);
        }

        final boolean propertyExists = schemeClient.propertyExists(predicate);
        if (!propertyExists) {
            final String msg =
                    String.format("The predicate %s does not exists like property", predicate);
            throw new IllegalArgumentException(msg);
        }

        // TODO : this mode to detect the value type is not the best one,
        // it has its limitations. E.G. no Bnode is supported, no expicit
        // classes are supported.
        // I chose to se this simple (and limited) solution becuase I it fits 
        // the needs for the medication
        final boolean isPropertyLiteral = schemeClient.isPropertyLiteral(predicate);
        final ValueType type = isPropertyLiteral
                ? ValueType.LITERAL
                : ValueType.RESOURCE;

        triplestore.delete(resourceURI, predicate);
        triplestore.persist(resourceURI, predicate, newValue, type);

        // NOTA BENE : the update operatin is responsible for the UPDATE_DATE property.
        // More preciselly the update must set the updated date to the date when
        // the resource was modified.
        setUpdateDate(resourceURI);
    }

    private void setUpdateDate(String resourceURI) throws TripleException {
        final boolean updateDateExists =
                triplestore.exists(resourceURI, Constants.UPDATE_DATE);
        if (updateDateExists) {
            triplestore.delete(resourceURI, Constants.UPDATE_DATE);
        }

        final String newDate = DateUtil.getFormatedDate(new Date());
        triplestore.persist(resourceURI, Constants.UPDATE_DATE, newDate, LITERAL);
    }

    public void deleteMedication(String resourceURI) throws TripleException {

        if (resourceURI == null) {
            throw new NullPointerException("The resourceURI argument can not be null.");
        }

        final boolean exists = triplestore.exists(resourceURI);
        if (!exists) {
            LOGGER.warn("No resource for this {} URI, remove has no effect.", resourceURI);
            return;
        }

        triplestore.deleteForSubject(resourceURI);
    }

    /**
     * Frequency graph used when the frequency information is now sufficent to
     * build a meningfully Frequency graph.
     *
     * @return
     * @throws TripleException
     */
    public String buildNullFrequency() throws TripleException {
        // TODO : build a singular instance of this and sore it in the rdf file.
        final String result =
                buildFrequency("No Event", -1, 0, Constants.MILLIGRAM);
        return result;
    }

    public String buildFrequency(String event, int offset, int value,
            String unitURI) throws TripleException {

        final String subject = triplestore.persist(Constants.RDFS_TYPE,
                Constants.HL7_FREQUECY_CLASS,
                LITERAL);

        triplestore.persist(subject,
                Constants.CREATOR,
                creator,
                LITERAL);

        if (event != null) {
            triplestore.persist(subject,
                    Constants.HL7_FREQUECY_EVENT,
                    event,
                    LITERAL);
        }

        if (offset > 0) {
            // with which offset
            triplestore.persist(subject,
                    Constants.HL7_FREQUECY_OFFSET,
                    String.valueOf(offset),
                    LITERAL);
        }

        if (value > 0) {
            // how offen will the the medication repeated (e.g. every 8 hour)
            triplestore.persist(subject,
                    Constants.HL7_FREQUECY_REPEAT,
                    buildValueObject(value, unitURI),
                    RESOURCE);
        }

        return subject;
    }

    public String buildValueObject(int value, String unitURI) throws TripleException {
        final String subject =
                triplestore.persist(Constants.RDFS_TYPE,
                Constants.PHRS_VALUE_OBJECT_CLASS,
                LITERAL);

        triplestore.persist(subject,
                Constants.CREATOR,
                creator,
                LITERAL);

        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_VALUE,
                String.valueOf(value),
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_UNIT,
                unitURI,
                RESOURCE);

        return subject;
    }

    public String buildManufacturedProduct(String drugName, String drugCode)
            throws TripleException {

        final String result =
                triplestore.persist(Constants.RDFS_TYPE,
                Constants.MANUFACTURED_PRODUCT_CLASS,
                LITERAL);

        triplestore.persist(result,
                Constants.CREATOR,
                creator,
                LITERAL);

        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(result,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        triplestore.persist(result,
                Constants.HL7_CLASS_CODE,
                "MANU",
                LITERAL);

        triplestore.persist(result,
                Constants.MANUFACTURED_LABEL_DRUG,
                buildManufacturedLabeledDrug(drugName, drugCode),
                RESOURCE);

        return result;
    }

    public String buildManufacturedLabeledDrug(String drugName, String drugCode)
            throws TripleException {

        final String result =
                triplestore.persist(Constants.RDFS_TYPE,
                Constants.MANUFACTURED_LABEL_DRUG_CLASS,
                LITERAL);

        triplestore.persist(result,
                Constants.CREATOR,
                creator,
                LITERAL);

        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(result,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        triplestore.persist(result,
                Constants.HL7_CLASS_CODE,
                "MANU",
                LITERAL);

        triplestore.persist(result,
                Constants.HL7_DETERMINER_CODE,
                "KIND",
                LITERAL);

        if (drugCode != null) {

            triplestore.persist(result,
                    Constants.HL7V3_CODE,
                    buildCode(drugName, drugCode),
                    RESOURCE);
        } else {
            triplestore.persist(result,
                    Constants.HL7V3_DRUG_NAME,
                    drugName,
                    LITERAL);
        }

        return result;
    }

    public String buildCode(String name, String code) throws TripleException {
        final String result =
                triplestore.persist(Constants.HL7V3_VALUE,
                code,
                LITERAL);

        triplestore.persist(result,
                Constants.SKOS_PREFLABEL,
                name,
                LITERAL);

        triplestore.persist(result,
                Constants.CODE_SYSTEM,
                "http://www.icardea.at/phrs/instances/codeSystem/Ulms",
                RESOURCE);


        return result;
    }

    /**
     * Registers a new creator for all the resources generated with this client.
     * All the generated resources will gain a triple with the predicate :
     * <code>Constants.CREATOR</code> and the value specified with the argument
     * <code>creator</code>.
     *
     * @param creator the new owner for this client, it can not be null.
     * @throws NullPointerException if the
     * <code>creator</code> argument is null.
     */
    public void setCreator(String creator) {
        if (creator == null) {
            final NullPointerException exception =
                    new NullPointerException("The creator argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }
}
