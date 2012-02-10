package at.srfg.kmt.ehealth.phrs;

import java.io.Serializable;
//Constants.java is used by other Classes from PhrsConstants, not just this one. PhrsConstants provides one access point for constants

@SuppressWarnings("serial")
public class PhrsConstants implements Serializable {

    public final static String MENU_CONTROL_REQUEST_PARAMETER_SELECTED_NODE = "requestnode";

    public final static String MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI = Constants.ICARDEA_HL7V3_NS + "#manufacturedProduct";
    //"http://www.icardea.at/phrs/hl7V3#manufacturedProduct";
    /**
     * The OPEN_ID_PARAM_ constants must match the alias names found in:
     * resources/com.dyuproject.openid.ext/axschema.properties
     * resources/com.dyuproject.openid.ext/sreg.properties
     */

    public static final String OPEN_ID_PARAM_OP_IDENTIFIER = "url";
    public static final String OPEN_ID_PARAM_IDENTITY = "identity";
    public static final String OPEN_ID_PARAM_CLAIM_ID = "claimid";

    public static final String OPEN_ID_PARAM_NAME_LOGIN = "openid_identifier";
    public static final String OPEN_ID_PARAM_NAME_LOGIN_WITH = "loginWith";
    public static final String OPENID_USER_ID_PREFIX_KEY = "openid.claimed_id.user";

    public static final String OPENID_DISCOVERY_IDENTIFIER_KEY = "openid.identity.discovery";
    public static final String OPENID_DISCOVERY_CLAIM_ID_KEY = "openid.claimed_id.discovery";
    public static final String OPENID_ICARDEA_PROVIDER_USER_SUFFIX = "/idp/u=";

    /**
     * Alias names, see axschema.properties for alias= URI and sreq.properties
     */
    //From Axschemea http://www.w3.org/2006/vcard/ns#role From Simple registration: postcode dob fullname email
    public static final String OPEN_ID_PARAM_ROLE = "role";//http://www.w3.org/2006/vcard/ns#role"
    //From Simple registration: postcode dob fullname email
    public static final String OPEN_ID_PARAM_EMAIL = "email";
    public static final String OPEN_ID_PARAM_FULL_NAME = "fullname";
    public static final String OPEN_ID_PARAM_NICK_NAME = "nickname";
    public static final String OPEN_ID_PARAM_DATE_OF_BIRTH = "dob";
    public static final String OPEN_ID_PARAM_GENDER = "gender";

    public static final String OPEN_ID_PARAM_POST_CODE = "postcode";
    public static final String OPEN_ID_PARAM_LANGUAGE = "language";
    public static final String OPEN_ID_PARAM_COUNTRY = "country";
    //unused
    public static final String OPEN_ID_PARAM_FIRST_NAME = "firstname";
    //unused
    public static final String OPEN_ID_PARAM_LAST_NAME = "lastname";
    public static final String OPEN_ID_PARAM_TIME_ZONE = "timezone";

    public static final String SESSION_USER_OPENID_OBJECT = "openid_user_object";
    public static final String SESSION_USER_PHR_OBJECT = "phr_user_object";
    public static final String SESSION_USER_PHR_OWNER_URI = "phr_user_owneruri";

    public static final String SESSION_USER_AUTHORITY_ROLE = "phr_user_authority_role";
    public static final String SESSION_USER_PRINCIPAL = "app_user_principal";
    public static final String SESSION_USER_AUTHENTICATION_NAME = "app_user_auth_name";
    public static final String SESSION_USER_LOGIN_ID = "app_user_user_id";


    public static final String SESSION_MENU_CURRENT_NODE = "menu_current_node";
    public static final String SESSION_USER_GREET_NAME="session.user.greet.name";
    public static final String SESSION_USER_PHR_FILTER_PROTOCOL_ID = "protocolid";
    public static final String SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE = "protocolns";
    //set by UI a means to see UI of another user, this was found in a request URL, but this is not authorized unless permitted

    public static final String REQUEST_USER_PHR_FILTER_OWNER_URI = "filterowner";
    public static final String SESSION_USER_PHR_FILTER_OWNER_URI = REQUEST_USER_PHR_FILTER_OWNER_URI;

    public static final String REQUEST_USER_PHR_FILTER_PROTOCOL_ID = "protocolid";
    public static final String REQUEST_USER_PHR_FILTER_PROTOCOL_NAMESPACE = "protocolns";

    public static final String PROTOCOL_ID_NAME = SESSION_USER_PHR_FILTER_PROTOCOL_ID;//"protocolId";
    public static final String PROTOCOL_NS_NAME = SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE;//"protocolNs";
    public static final String PROTOCOL_ROLE_NAME = OPEN_ID_PARAM_ROLE;//"role";

    public static final String PROTOCOL_ID_TEST_VALUE = "testprotocolid";
    public static final String PROTOCOL_NAMESPACE_TEST_VALUE = "icardea";
    public static final String PROTOCOL_ROLE_TEST_VALUE = "patient";

    public static final String PROFILE_USER_IDENTIFIER_PROTOCOL_ID = "protocolid";
    public static final String PROFILE_USER_IDENTIFIER_PROTOCOL_NAMESPACE = "protocolns";
    public static final String PROFILE_USER_CIED_IDENTIFIER = "ciedid";
    /**
     * PIX identifiers can be used to query for a patient and to get the protocol Identifier
     */
    public static final String IDENTIFIER_TYPE_PIX_PROTOCOL_ID = PROTOCOL_ID_NAME;
    public static final String IDENTIFIER_TYPE_PIX_PATIENT_NAME = "pix_patient_name";
    public static final String IDENTIFIER_TYPE_PIX_DATE_OF_BIRTH = "pix_patient_dob";

    public static final String IDENTIFIER_TYPE_PIX_DEVICE_SERIAL_NUMBER = "identifier_device_serial_number";
    public static final String IDENTIFIER_TYPE_PIX_HOSPITAL_ID = "identifier_hospital_patient";
    public static final String IDENTIFIER_TYPE_PIX_PHRS_PIN = "identifier_phrs_pin";

    public static final String INTEROP_RESOURCE_REFERENCE = "resource_reference";
    public static final String MESSAGING_TYPE_INTEROP = "interop";
    public static final String AUTHORIZE_RESOURCE_CODE_PHRS_PREFIX = "PHRSRESOURCECODE";
    public static final String AUTHORIZE_RESOURCE_CODE_PHRS_UNKNOWN = "PHRSRESOURCECODE_UNKNOWN";

    public static final String AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH = "RESOURCECODE:BASICHEALTH";
    public static final String AUTHORIZE_RESOURCE_CODE_MEDICATION = "RESOURCECODE:MEDICATION";
    public static final String AUTHORIZE_RESOURCE_CODE_CONDITION = "RESOURCECODE:CONDITION";
    public static final String AUTHORIZE_RESOURCE_CODE_ALLERGY = "RESOURCECODE:ALLERGY";
    public static final String AUTHORIZE_RESOURCE_CODE_TESTRESULT = "RESOURCECODE:TESTRESULT";
    public static final String AUTHORIZE_RESOURCE_CODE_IMMUNIZATION = "RESOURCECODE:IMMUNIZATION";

    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN = "ROLECODE:DOCTOR";
    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_NURSE = "ROLECODE:NURSE";

    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_FAMILY_MEMBER = "ROLECODE:FAMILY_MEMBER";
    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_PSYCHIATRIST = "ROLECODE:PSYCHIATRIST";
    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_PHARMACIST = "ROLECODE:PHARMACIST";
    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_DENTIST = "ROLECODE:DENTIST";
    public static final String AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR = "ROLECODE:DOCTOR";

    public static final String AUTHORIZE_ROLE_CONSENT_MGR_PREFIX = "ROLECODE";
    public static final String AUTHORIZE_ROLE_PHRS_PREFIX = "PHRSROLECODE";
    public static final String AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN = "PHRSROLECODE:ADMIN";
    public static final String AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER = "PHRSROLECODE:USER";
    public static final String AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN = "PHRSROLECODE:USERLOCAL";
    public static final String AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST = "PHRSROLECODE:TEST";

    public static final String AUTHORIZE_ACTION_CODE_READ = "READ";
    public static final String AUTHORIZE_ACTION_CODE_WRITE = "WRITE";
    public static final String AUTHORIZE_ACTION_CODE_UPDATE = "UPDATE";

    /**
     * Local accounts are created for admin, test*, or user*
     * For the pilot application, passwords are not used on local accounts.
     */
    public static final String AUTHORIZE_USER_ADMIN = "phradmin";
    //with this prefix a local account is setup
    public static final String AUTHORIZE_USER_PREFIX_TEST = "phrtest";
    public static final String AUTHORIZE_USER_PREFIX_TEST_1 = "phrtest1";
    public static final String AUTHORIZE_USER_PREFIX_TEST_2 = "phrtest2";
    //not used AUTHORIZE_USER_PREFIX_TEST_ELLEN
    public static final String AUTHORIZE_USER_PREFIX_TEST_ELLEN = "nurse";
    //with this prefix a local account is setup if non-existing, until the scenario is completed with added OpenId, passwords are not strictly needed
    public static final String AUTHORIZE_USER_PREFIX_AUTO_USER = "phruser";
    //loginId
    public static final String AUTHORIZE_USER_VT_SCENARIO_NURSE = "nurse";

    public static final String IDENTIFIER_TYPE_CIED_NUMBER = "identifier_cied_number";

    /**
     * Can be used to state that the identifier is relevant to any healthcare organization
     * e.g. device serial number.
     */
    public static final String IDENTIFIER_TYPE_ORGANIZATION_ANY = "identifier_organization_any";
    public static final String IDENTIFIER_TYPE_ORGANIZATION_DEFAULT = "default";

    public static final String IDENTIFIER_STATUS_VALID = "identifier_status_valid";
    public static final String IDENTIFIER_STATUS_INVALID = "identifier_status_invalid";
    public static final String IDENTIFIER_STATUS_UNKNOWN = "identifier_status_unknown";

    public static final String TYPE_ITEM_LINK = "link";
    public static final String TYPE_ITEM_NODE = "node";
    public static final String TYPE_ITEM_NODE_ROOT = "noderoot";
    // public static final String TYPE_ITEM_NODE_HOME="nodehome"; use header
    // link
    public static final String TYPE_ITEM_NODE_HEADER_LINK = "nodeheaderlink";
    public static final String TYPE_ITEM_NODE_HEADER = "nodeheader";
    public static final String TYPE_ITEM_NODE_LINK = "nodelink";
    public static final String IDENTIFIER_CATEGORY_CLINICAL = "clinical";
    public static final String IDENTIFIER_PROVIDER_ICARDEA = "icardea";

    public static final String DATE_STATUS_BEGIN = "date.status.begin";
    public static final String DATE_STATUS_END = "date.status.end";
    public static final String STATUS_COMPLETE = Constants.STATUS_COMPELETE;
    public static final String STATUS_INCOMPLETE = Constants.STATUS_INCOMPELETE;
    public static final String STATUS_RUNNING = Constants.STATUS_RUNNING;

    public static final String TAG_ROLES_MEDICAL_PROFESSIONAL = "TAG_ROLES_MEDICAL_PROFESSIONAL";
    public static final String TAG_ROLES_NON_MEDICAL_USER = "TAG_ROLES_NON_MEDICAL_USER";
    public static int LIST_ROW_COUNT = 5;
    /**
     * event status labels are also I18 labels until they are registered in the
     * vocabulary - they might be treated as restful strings
     */
    public static final String UI_MGT_STATUS_EDIT_MODE = "editMode";
    public static final String UI_MGT_STATUS_CREATION_MODE = "creationMode";
    public static final String UI_MGT_STATUS_VIEW_MODE = "viewMode";
    public static final String UI_MGT_STATUS_PARAM_NAME = "mgtStatus";

    public static final String EVENT_STATUS_ACTION_PLANNED = "event.status.planned.action";

    public static final String EVENT_STATUS_ACTION_PLANNED_NO_TIMEPLAN = "event.status.planned.timeplan.falseaction";

    public static final String EVENT_STATUS_ACTION_EXECUTED_EXPLICIT = "event.status.executed.true.explicit.action";
    public static final String EVENT_STATUS_ACTION_EXECUTED_IMPLICIT = "event.status.executed.true.implicit.action";
    public static final String EVENT_STATUS_ACTION_NOT_EXECUTED = "event.status.executed.false.action";
    public static final String EVENT_STATUS_ACTION_NOT_EXECUTED_IMPLICIT = "event.status.executed.false.implicit.action";

    public static final String EVENT_STATUS_EXECUTED_CONFIRMED = "event.status.executed.change.label";

    /**
     * Status not known, not certain if this can be shown in cale
     */
    public static final String EVENT_STATUS_UNKNOWN = "event.status.unknown.label";
    /**
     * not treatable as event in calendar
     */
    public static final String EVENT_STATUS_NOT_EVENTABLE = "event.status.eventable.false.label";
    // public static final String EVENT_STATUS_TEMPORAL_INDICATOR =
    // "event.status.temporal";
    /**
     * can record as eventable in calendar, no more status info needed e.g diary
     * item, blog entry, medication change, etc
     */
    public static final String EVENT_STATUS_EVENTABLE_NOT_ACTIONABLE = "event.status.eventable.true.actionable.false.label";

    public static final String EVENT_THEME_EVENT = "event.theme.event";

    public static final String EVENT_THEME_NOTIFY_HEALTH_INFO_CHANGE = "event.theme.notify.healthinfo.change";
    public static final String EVENT_THEME_NOTIFY_HEALTH_INFO_CHANGE_MEDICATION = "event.theme.notify.healthinfo.change.medication";
    public static final String EVENT_THEME_ODL = "event.theme.odl";

    public static final String EVENT_THEME_DATA_ENTRY = "event.theme.dataentry";
    // leave null public static final String EVENT_THEME_UNKNOWN =
    // "event.theme.unknown";

    public static final String CALENDAR_EVENT_HEADER_COLOR_DEFAULT = "#007FA5";
    public static final String CALENDAR_EVENT_CONTENT_COLOR_DEFAULT = "#398CA3";

    // use 10, 15 minute or half hour intervals, any thing less causes problems
    public final static int TIME_INTERVAL_LIST = 15;

    public final static String TAG_ACTION_CATEGORIES = "action.categories"; // used
    // as
    // attribute
    // name																			// in
    // URL

    public final static String PARAM_NAME_CONTACT_TYPE = "typecontact"; // used
    // as																		// attribute
    // name
    // in
    // URL
    public final static String PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER = "healthcare_provider";
    public final static String PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER = "healthcare_user";

    public final static String DELIMITER_VOCAB = ", ";

    //public final static String KEY_USER_MODEL = UserCredentialManager.KEY_USER_MODEL;
    public final static String SELECTION_BOX_PLEASE_CHOOSE = "default.combobox.pleasechoose.label";
    /**
     * 3 main topics for pub sub and associated actions
     */
    public final static String PUBSUB_TOPIC_CRUD = "PHRS.CRUD";
    public final static String PUBSUB_ACTION_CRUD_CANCEL = "PHR_CRUD.CANCEL";
    public final static String PUBSUB_ACTION_CRUD_UPDATE = "PHR_CRUD.UPDATE";
    public final static String PUBSUB_ACTION_CRUD_CREATE = "PHR_CRUD.CREATE";
    public final static String PUBSUB_ACTION_CRUD_WRITE_AUDIT_DATA = "PHR_CRUD.AUDIT_DATA";
    public final static String PUBSUB_ACTION_CRUD_WRITE_AUDIT_ACTION = "PHR_CRUD.AUDIT_ACTION";
    public final static String PUBSUB_ACTION_CRUD_DELETE = "PHR_CRUD.DELETE";
    public final static String PUBSUB_ACTION_CRUD_READ = "PHR_CRUD.READ";
    public final static String PUBSUB_ACTION_CRUD_LIST = "PHR_CRUD.LIST";
    public final static String PUBSUB_ACTION_CRUD_LIST_PRINTABLE = "PHR_CRUD.LIST_PRINT";
    public final static String PUBSUB_ACTION_CRUD_CHART = "PHR_CHART.READ";
    // public final static String
    // PUBSUB_ACTION_CALENDAR_READ="PHRS_CALENDAR.READ";

    public final static String PUBSUB_TOPIC_PHRS_OTHER = "PHRS.OTHER";

    public final static String PUBSUB_ACTION_CHAT = "PHRS_USER.CHAT";
    public final static String PUBSUB_ACTION_SHARE_PHR = "PHRS_USER.SHARE";
    public final static String PUBSUB_ACTION_PORTAL_EDUCATION_ACCESS = "PHRS_EDUCATION.READ";
    public final static String PUBSUB_ACTION_PORTAL_FORUM_ACCESS = "PHRS_FORUM.ACCESS";
    public final static String PUBSUB_ACTION_MESSAGES_LIST = "PHRS_MESSAGES.LIST";
    public final static String PUBSUB_ACTION_CONSENT_EDITOR = "PHRS_CONSENT.ACCESS";

    public final static String PUBSUB_TOPIC_INTEROP_ACCESS = "PHRS_INTEROP.ACCESS";

    public final static String PUBSUB_ACTION_INTEROP_PHR_CREATE = "PHRS_INTEROP.CREATE";
    public final static String PUBSUB_ACTION_INTEROP_PHR_UPDATE = "PHRS_INTEROP.CREATE";
    public final static String PUBSUB_ACTION_INTEROP_PHR_DELETE = "PHRS_INTEROP.DELETE";
    public final static String PUBSUB_ACTION_INTEROP_PHR_READ_EHR = "PHRS_INTEROP_EHR.READ";
    public final static String PUBSUB_ACTION_INTEROP_PHR_READ = "PHRS_INTEROP.READ";
    public final static String PUBSUB_ACTION_INTEROP_PHR_IMPORT = "PHRS_INTEROP.IMPORT";
    public final static String INTEROP_ORIGIN_STATUS_IMPORTED = PUBSUB_ACTION_INTEROP_PHR_IMPORT;
    public final static String INTEROP_ORIGIN_DEFAULT_EHR = "PHRS_INTEROP.ORIGIN.EHR";
    public final static String INTEROP_CREATOR_DEFAULT_PHR = "PHRS_INTEROP.ORIGIN.PHR";


    //public final static String SESSION_PARAM_NAME_AUTHENTICATED_USER = "authuser";// liferayuser
    //public final static String SESSION_PARAM_NAME_AUTHENTICATED_USER_LIFERAY = "liferayuser";


    /**
     * phrid prefix is "phrid"
     */
    public final static String USER_HEALTH_PROFILE_PREFIX = "phrid";
    public final static String PORTAL_NAME_DEMO = "demo1";
    public final static String PHRS_DATABASE_NAME = "phrsdata1";
    public final static String PHRS_VERSIONING_DATABASE_NAME = "phrsupdates1";
    public final static String PHRS_AUDIT_DATABASE_NAME = "phrsaudits1";

    public final static String SESSION_PARAM_NAME_FILTER_HEALTH_PROFILE_ID = "filterHealthProfileId";
    public final static String USER_ID_TEST = AUTHORIZE_USER_PREFIX_TEST;//"testuser";
    public final static String USER_ID_GUEST = "phrsguest";

    public final static String USER_TEST_HEALTH_PROFILE_ID = Constants.OWNER_URI_CORE_PORTAL_TEST_USER;
    public final static String USER_IDENTIFIER_DELIMITER = ".";
    public final static String USER_TEST_HEALTH_PROFILE_ID_1 = "portal" + USER_IDENTIFIER_DELIMITER
            + PORTAL_NAME_DEMO + USER_IDENTIFIER_DELIMITER + USER_HEALTH_PROFILE_PREFIX
            + USER_IDENTIFIER_DELIMITER + "testuser1";

    public final static String USER_GUEST_HEALTH_PROFILE_ID = "portal" + USER_IDENTIFIER_DELIMITER
            + PORTAL_NAME_DEMO + USER_IDENTIFIER_DELIMITER + USER_HEALTH_PROFILE_PREFIX
            + USER_IDENTIFIER_DELIMITER + USER_ID_GUEST;

    public static final String PROPERTY_HEALTH_PROFILE_IDENTIFIER = "ownerUri";

    public final static String USER_SYSTEM_HEALTH_PROFILE_ID = "portal."
            + PORTAL_NAME_DEMO + USER_IDENTIFIER_DELIMITER + USER_HEALTH_PROFILE_PREFIX
            + USER_IDENTIFIER_DELIMITER + "systemuser";

    public static final String PHRS_MONGO_DB_NAME = "phrs_healthapp1";
    public static final String ERROR_TERM_NOT_FOUND = "error-term-not-found";
    public static final String RDF_MAP_KEY_SUBJECT = "subject";
    public static final String PROPERTY_VOCABULARY_TERM_RELATION = Constants.SKOS_RELATED;// "http://www.w3.org/2004/02/skos/core#related";//"http://www.w3.org/2004/02/skos/core#related";
    public static final String SKOS_RELATED = PROPERTY_VOCABULARY_TERM_RELATION;
    public static final String SKOS_PROPERTY_PREFERRED_LABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";

    public static final String TAG_RISK_SMOKING_DURATION = "http://www.icardea.at/phrs/instances/SmokingDuration";
    public static final String TAG_RISK_SMOKING_QUANTITY = "http://www.icardea.at/phrs/instances/SmokingQuantity";
    public static final String TAG_RISK_SMOKING_TYPES = "http://www.icardea.at/phrs/instances/SmokingTypesCigars_Cigs_Pip_TERM_RISK_SMOKING_TYPES";

    public static final String TAG_PROBLEM_SYMPTOM = "http://www.icardea.at/phrs/instances/Problem/Symptom";

    public static final String TAG_RISK_TREATMENTS_DIABETES = "http://www.icardea.at/phrs/instances/TreatmentsForDiabetes";
    public static final String TAG_RISK_TREATMENTS_HYPERTENSION = "http://www.icardea.at/phrs/instances/TreatmentsForHypertension";
    public static final String TAG_RISK_TREATMENTS_CHOLESTEROL = "http://www.icardea.at/phrs/instances/TreatmentsForCholesterol";
    public static final String TAG_RISK_FACTORS = "http://www.icardea.at/phrs/instances/RiskFactors";

    public static final String TAG_RISK_FACTORS_SMOKING_STATUS = "TAG_RISK_FACTORS_SMOKING_STATUS";
    public static final String TAG_RISK_FACTORS_DIABETES = "http://www.icardea.at/phrs/instances/DiabetesMellitus";

    public static final String TAG_RISK_FACTORS_CHOLESTEROL = "http://www.icardea.at/phrs/instances/Cholesterol";

    public static final String TAG_RISK_FACTORS_HYPERTENSION = "http://www.icardea.at/phrs/instances/Hypertension";

    public static final String TAG_RISK_FACTORS_SMOKING = "http://www.icardea.at/phrs/instances/Smoker";

    public static final String PROPERTY_DC_DESCRIPTION_NOTE = "http://purl.org/dc/elements/1.1/description";

    public static final String TAG_ACTIVITIES_OF_DAILY_LIVING = "http://www.icardea.at/phrs/instances/ActivitiesOfDailyLiving";
    public static final String TAG_ACTIVITIES_OF_DAILY_LIVING_STATUS = "http://www.icardea.at/phrs/instances/ActivityStatus";

    public static final String SELECTION_NO_ANSWER = "default.no_answer.label";

    public static final String TAG_PHYSICAL_ACTIVITIES = "http://www.icardea.at/phrs/instances/PhysicalActivities";

    public static final String TAG_PHYSICAL_ACTIVITY_FREQUENCY = "http://www.icardea.at/phrs/instances/HowOftenFrequency";
    public static final String TAG_PHYSICAL_ACTIVITY_DURATION = "http://www.icardea.at/phrs/instances/FrequencyOfActivity";
    /**
     * @deprecated
     */
    public static final String TAG_MEDICATION_DOSAGE_FREQUENCY_QUANTITY = "TAG_DRUG_FREQUENCY_QUANTITY";
    public static final String TAG_MEDICATION_DOSAGE_FREQUENCY_INTERVAL_1 = "http://www.icardea.at/phrs/instances/HowOftenFrequency";
    public static final String TAG_MEDICATION_DOSAGE_TIME_OF_DAY_1 = "http://www.icardea.at/phrs/instances/WhenDuringTheDay";

    public static final String TAG_MEDICATION_DOSAGE_DOSAGE_UNITS = "http://www.icardea.at/phrs/instances/MedicationUnits";
    public static final String TAG_MEDICATION_COMPLIANCE_STATUS = "http://www.icardea.at/phrs/instances/MedicationComplianceStatus";
    public static final String TAG_MEDICATION_REASON_WITH_RISK_FACTORS = "http://www.icardea.at/phrs/instances/RiskFactors";

    public static final String HL7V3_MEDICATION_DOSE_UNIT_TABLET = "http://www.icardea.at/phrs/instances/MeasureSystem#Tablet";

    public static final String HL7V3_MEDICATION_DOSE_UNIT_PILLS = "http://www.icardea.at/phrs/instances/MeasureSystem#Pill";

    public static final String HL7V3_MEDICATION_DOSE_UNIT_PILLS_PHRS = "http://www.icardea.at/phrs/instances/pills";

    // http://www.icardea.at/phrs/instances/pills
    public final static String HL7V3_CODE_CATEGORY_RISK = Constants.HL7V3_FINDING;
    public final static String HL7V3_CODE_CATEGORY_ADL = Constants.HL7V3_FINDING;

    public final static String HL7V3_CODE_CATEGORY_PHYS_ACTIVITY = Constants.HL7V3_FINDING;

    // public static final String NS_HGTAGS="";
    public static final int SORT_ALPHABETIC = 1;
    // public static final String NS_ACTION_PLAN = Constants.ICARDEA_NS +
    // "/actionPlan";
    public static final String TAGS = Constants.ICARDEA_NS + "#tags"; // replace
    // with
    // hgtag
    // TODO assign an rdf:type either generic type for phrs form objects. This
    // allows more flexibility

    public static final String PROPERTY_FORM_TYPE = Constants.ICARDEA_NS
            + "#formType";
    // JSON wrappers - useful for changing model and to later add to better
    // search engine.
    public static final String ANNOTATIONS_NOTES_PROPERTY_JSON = Constants.ICARDEA_NS
            + "#annotationNotesJson";// can have note elements. These have no
    // semantic elements for organizing or
    // searching for later
    public static final String ANNOTATIONS_NOTES_QUESTION_JSON = Constants.ICARDEA_NS
            + "#annotationQuestionJson";// primarily one or more questions with
    // one receiver; zero or more about
    // property e.g. question about topic
    // dosage
    public static final String ANNOTATIONS_NOTES_MONITOR_JSON = Constants.ICARDEA_NS
            + "#annotationMonitorJson";// primarily one or more questions with
    // one receiver; zero or more about
    // property e.g. question about topic
    // dosage
    public static final String ANNOTATIONS_NOTES_NOTIFY_JSON = Constants.ICARDEA_NS
            + "#annotationNotifyJson";// primarily one or more questions with
    // one receiver; zero or more about
    // property e.g. question about topic
    // dosage

    // these are onto resources where the model is RDF and not a wrapped JSON.
    // With json, more flexibility but must get all first for patient and
    // process them instead of query first the inner elements
    public static final String ANNOTATIONS_NOTES_PROPERTY = Constants.ICARDEA_NS
            + "#annotationNotes";// can have note elements. These have no
    // semantic elements for organizing or
    // searching for later
    public static final String ANNOTATIONS_NOTES_QUESTION = Constants.ICARDEA_NS
            + "#annotationQuestion";// primarily one or more questions with one
    // receiver; zero or more about property
    // e.g. question about topic dosage
    public static final String ANNOTATIONS_NOTES_MONITOR = Constants.ICARDEA_NS
            + "#annotationMonitor";// primarily one or more questions with one
    // receiver; zero or more about property
    // e.g. question about topic dosage
    public static final String ANNOTATIONS_NOTES_NOTIFY = Constants.ICARDEA_NS
            + "#annotationNotify";// primarily one or more questions with one
    // receiver; zero or more about property
    // e.g. question about topic dosage

    public static final String NOTE = Constants.ICARDEA_NS + "#note"; // Constants.NOTE;
    public static final String NOTE_2 = Constants.ICARDEA_NS + "#note2";

    public static final String EVENT_ACTIONPLAN = Constants.ICARDEA_NS
            + "#action_event";
    public static final String EVENT_GENERIC = Constants.ICARDEA_NS
            + "#generic_event";

    public static final String EVENT_TITLE = Constants.ICARDEA_NS
            + "#generic_title";

    public static final String EVENT_DATE_IS_DURATION = Constants.ICARDEA_NS
            + "#event_date_is_duration";
    public static final String EVENT_DATE_FROM = Constants.ICARDEA_NS
            + "#event_date_from";
    public static final String EVENT_DATE_TO = Constants.ICARDEA_NS
            + "#event_date_to";
    public static final String EVENT_DATE_WHOLE_DAY = Constants.ICARDEA_NS
            + "#event_date_is_whole_day";
    // an event can be associated to more than one action
    public static final String EVENT_ACTION_REFERENCE = Constants.ICARDEA_NS
            + "#event_action_reference";
    public static final String EVENT_IMAGE = Constants.ICARDEA_NS
            + "#event_image";
    public static final String EVENT_HREF = Constants.ICARDEA_NS
            + "#event_href"; // text CDATA

    public static final String EVENT_COLOR = Constants.ICARDEA_NS
            + "#event_color";
    // ical action.
    public static final String EVENT_CALENDAR_ACTION = Constants.ICARDEA_NS
            + "#event_cal_action";

    public static final String RDF_TYPE = "rdfs:type";
    // public static final COMMENT="comment"

    // we do not need the attributes map or list containers because
    // TODO replace with Constants properties
    public static final String RISK_SMOKING_TYPE = Constants.ICARDEA_NS
            + "#risk_smoke_type";// what did patient smoke? cigars, etc
    public static final String RISK_SMOKING_QUANTIY = Constants.ICARDEA_NS
            + "#risk_smoke_quantity";// how much? Unit of measure must be
    // assigned in semantic model
    public static final String RISK_DIABETES_TYPE_MEDICATION = Constants.ICARDEA_NS
            + "#risk_diabetes_type_medication";
    public static final String TAG_DIABETES_MEDICATIONS = "http://www.icardea.at/phrs/instances/DiabetesMedication";
    // http://www.icardea.at/phrs/instances/DiabetesMedication
    // contact info for patient or for medical contacts
    public static final String PORTABLE_CONTACT_JSON = Constants.ICARDEA_NS
            + "#portable_contact_json";


}
