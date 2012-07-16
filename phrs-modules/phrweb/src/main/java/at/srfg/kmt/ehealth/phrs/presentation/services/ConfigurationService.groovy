package at.srfg.kmt.ehealth.phrs.presentation.services;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.basesupport.OpenIdProviderItem
import javax.faces.context.FacesContext
import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.PropertiesConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static at.srfg.kmt.ehealth.phrs.PhrsConstants.*

/**
 *
 * Exposes Configuration file settings and Context parameters (web.xml)
 *
 */

public class ConfigurationService implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);
    private static ConfigurationService m_instance  //= new ConfigurationService();

    public static final String ENDPOINT_TYPE_PIX = "pix.connectionhub.endpoint"
    public static final String ENDPOINT_TYPE_CONSENT_WS = "consent.ws.endpoint"


    static {
        staticInit();
    }

    protected static void staticInit() {
        println('staticInit')
        try {
            m_instance = new ConfigurationService();
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);

        }
    }

    public static final String forwardRedirectIsAuthenticatedToPage = "/jsf/home.xhtml";
    public static final String formwardRedirectFilteredDirectory = "/jsf/";
    public static final String forwardRedirectLoginPage = "/WEB-INF/views/jsp/login.jsp";


    //private static XMLConfiguration xmlConfig;
    private static PropertiesConfiguration propertiesConfig
    private static PropertiesConfiguration menuLinksConfig
    private static PropertiesConfiguration icardeaConfig

    private List<String> rolesLocal = []
    private List<String> rolesConsentMgr = []


    private Map<String,String> localLoginCredentials = [:]

    public static ConfigurationService getInstance() {

        return m_instance;
    }

    private ConfigurationService() {
        init()
        initRoles()
        if(!propertiesConfig)  LOGGER.error('propertiesConfig failed to init')
        localLoginCredentials =  loadLocalLoginCredentials()


    }

    private void initRoles() {

        rolesLocal = [
                PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN,
                PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST
        ]
        rolesConsentMgr = [
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN,
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE,
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_FAMILY_MEMBER,
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PSYCHIATRIST,
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHARMACIST,
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DENTIST,
                PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN,
                PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST
        ]
    }

    private synchronized void init() {

        //        if(xmlConfig == null) {
        //            refreshXMLConfig()
        //        }
        if (propertiesConfig == null) {
            refreshPropertiesConfig()
        }
        if (icardeaConfig == null) {
            refreshPropertiesIcardeaConfig()
        }
        if (menuLinksConfig == null) {
            refreshMenuLinksConfig()
        }

    }

    public synchronized void refreshMenuLinksConfig() {

        try {
            menuLinksConfig = new PropertiesConfiguration("phrscontent.properties");
        } catch (ConfigurationException e) {
            LOGGER.error("ConfigurationService error MenuLinks phrscontent.properties", e);
        }
    }
    public synchronized void refreshPropertiesConfig() {

        try {
            propertiesConfig = new PropertiesConfiguration("phrs.properties");
        } catch (ConfigurationException e) {
            LOGGER.error("ConfigurationService error phrs.properties", e);
        }
    }
    /**
     * Use  the resource bundle approach
     */
    public synchronized void refreshPropertiesIcardeaConfig() {

        try {
            icardeaConfig = new PropertiesConfiguration("icardea.properties");
        } catch (ConfigurationException e) {
            LOGGER.error("ConfigurationService error icardea.properties" , e);
        }
    }


    public String getEndPoint(String prop) {

        return getProperty(prop)
    }

    public String getEndPoint(String prop, String defaultValue) {

        return getProperty(prop, defaultValue)
    }
    /*
    public void initConfigIni(){
    //pid.run(new String[]{"../../icardea-config/config.ini"});
    public static final String forwardRedirectIsAuthenticatedToPage = "/jsf/home.xhtml";
    public static final String formwardRedirectFilteredDirectory = "/jsf/";
    public static final String forwardRedirectLoginPage = "/WEB-INF/views/jsp/login.jsp";
    // public static final String forwardRedirectLoginPageAlternate =
    // "/WEB-INF/views/jsp/login.jsp";
    public static final String forwardRedirectIndexPage = "/index.xhtml";
    }*/

    public String getProperty(String prop, String defaultValue) {
        String value = getProperty(prop)
        return value ? value : defaultValue
    }

    public PropertiesConfiguration getPropertiesConfiguration() {
        return propertiesConfig
    }

    public String getContentLink(String key){
        String value
        if(menuLinksConfig && key){
              value = menuLinksConfig.getProperty(key) ;
            //LOGGER.debug('getContentLink key ='+ key +' value='+value)
        }  else {
            if(!key ) LOGGER.error('getContentLink key is null')
            if(!menuLinksConfig ) LOGGER.error('getContentLink menuLinksConfig is null')
        }
        return value
    }
    public List<String> getPropertyList(String prop) {
        List<String> value = null;

        if (prop) {

            value = propertiesConfig.getList(prop)
        }
        if(!value) value=[]
        return value
    }

    public String getProperty(String prop) {
        String value = null;

        if (prop) {

            value = propertiesConfig.getProperty(prop)
            if (!value) {
                switch (prop) {

                //case PhrsConstants.OPENID_DISCOVERY_IDENTIFIER_KEY:
                //value='https://localhost:8443/idp/'
                //break
                    case 'forwardRedirectIsAuthenticatedToPage':
                        value = '/jsf/home.xhtml'
                        break
                    case 'formwardRedirectFilteredDirectory':
                        value = '/jsf/'
                        break
                    case 'forwardRedirectLoginPage':
                        value = '/WEB-INF/views/jsp/login.jsp'
                        break
                    case 'forwardRedirectIndexPage':
                        value = '/index.xhtml'
                        break
                    case 'login_openid_forwardUri':
                        value = '/WEB-INF/views/jsp/login.jsp'
                        break
                    case ENDPOINT_TYPE_PIX:

                        value = 'localhost:2575'
                        break
                    case ENDPOINT_TYPE_CONSENT_WS:

                        value = 'localhost:8080'
                        break
                    default:
                        //lookup
                        break
                }
            }
        }
        return value
    }

    public String getSystemDomainCode(String tag) {
        String label
        label = ''
        return label
    }

    public static boolean isAppModeTest() {
        //application.testmode
        String testValue = ConfigurationService.getInstance().getProperty('application.testmode', 'true').trim()
        //was testmode
        if (testValue != null
                && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }

    public static boolean isAppModeSingleUserTest() {

        String testValue = ConfigurationService.getInstance().getProperty('user.mode.singleuser', 'false').trim()
        //was testsingleusermode
        if (testValue != null
                && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }
    //isAppModeSingleUserTest
    public static boolean isAppModePixTest() {
        String testValue = ConfigurationService.getInstance().getProperty('pix.mode.test', 'false').trim()
        //was testpixmode
        if (testValue != null && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }

    public static boolean isAppModeRoleTest() {
        String testValue = ConfigurationService.getInstance().getProperty('consent.mode.roletest', 'false').trim()
        //was testrolemode
        if (testValue != null && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }

    public static boolean isAppModeMonitorListAllUsers() {
        String testValue = ConfigurationService.getInstance().getProperty('consultation.reports.listall', 'true').trim()
        //was monitorlistall
        if (testValue != null && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }

    public boolean showTestPrototcolIdentifiers() {

        String testValue = getInitParameter("show-test-identifiers");
        if (testValue != null
                && ("true".equalsIgnoreCase(testValue))) {
            return true;
        }
        return false;
    }

    public String getAssociateIdentifiers() {
        return getInitParameter("associate-identifiers");

    }
    //user-gui patient-identifier-source
    public String getUserManagementScenario() {
        return getInitParameter("user-management-scenario");

    }
    /**
     *
     * @return
     */
    public String getPatientIdentifierSource() {
        return getInitParameter("patient-identifier-lookup-property");

    }
    /**
     *
     * @param initParam - context-param from web.xml
     * @return
     */
    public static String getInitParameter(String initParam) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null)
            return null;
        return context.getExternalContext().getInitParameter(initParam);

    }

    public List<OpenIdProviderItem> getOpenIdProviderItem() {
        List<OpenIdProviderItem> items = []
        //read Dyuproject providers and

        return items
    }
    /**
     * From Context web.xml
     */
    public static String getOpenIdProvider(boolean local) {
        String value = null;
        if (local) value = getInitParameter("openid.provider");
        else value = getInitParameter("openid.provider.test");
        if (value != null) {
            if (value.trim().length() < 2) value = null;

        }
        return value;
    }
    /**
     *  Consent Mgr codes
     * @return
     */
    public List<String> getConsentResourceCodes() {

        return rolesConsentMgr

    }
    /**
     * Additional roles, but not used by consent mgr
     * @return
     */
    public List<String> getConsentLocalCodes() {

        return rolesLocal
    }
    /**
     *  Consent Mgr codes and local PHR codes
     * @return
     */
    public List<String> getConsentAllCodes() {
        def list = []
        list.addAll(rolesConsentMgr)
        list.addAll(rolesLocal)
        return list

    }

    public List<String> getConsentAllActions() {
        List<String> list = [PhrsConstants.AUTHORIZE_ACTION_CODE_READ, PhrsConstants.AUTHORIZE_ACTION_CODE_WRITE, PhrsConstants.AUTHORIZE_ACTION_CODE_UPDATE]
        return list
    }

    public boolean isConsentAction(String actionCode) {

        if (actionCode != null
                && (PhrsConstants.AUTHORIZE_ACTION_CODE_READ.equals(actionCode)
                || PhrsConstants.AUTHORIZE_ACTION_CODE_WRITE.equals(actionCode) || PhrsConstants.AUTHORIZE_ACTION_CODE_UPDATE.equals(actionCode))) {
            return true;
        }
        return false;
    }
    /**
     *
     * @param tagFilter
     * @return
     */
    public List<String> getConsentResourceCodes(String tagFilter) {

        List values = new ArrayList()
        values.addAll(getConsentResourceCodes())

        values.addAll(getConsentLocalCodes())
        return values
    }
    /**
     *
     * @param tagFilter
     * @return
     */
    /*
    RESOURCECODE:BASICHEALTH
    RESOURCECODE:MEDICATION
    RESOURCECODE:CONDITION
    RESOURCECODE:ALLERGY
    RESOURCECODE:TESTRESULT
    RESOURCECODE:IMMUNIZATION
    ROLECODE:DOCTOR
    ROLECODE:NURSE
    ROLECODE:FAMILY_MEMBER
    ROLECODE:PSYCHIATRIST
    ROLECODE:PHARMACIST
    ROLECODE:DENTIST
    ROLECODE:DOCTOR
    resourceCodes.add(PhrsConstants.AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH) 
    resourceCodes.add(PhrsConstants.AUTHORIZE_RESOURCE_CODE_MEDICATION)
     */

    public List<String> getConsentSubjectCodes(String tagFilter) {
        List values
        switch (tagFilter) {
            case 'phr':
                //fall through to default ,no case break
            default:
                values = [AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH,
                        AUTHORIZE_RESOURCE_CODE_MEDICATION,
                        AUTHORIZE_RESOURCE_CODE_CONDITION
                ]
        }
        /*
        def values=[
        'RESOURCECODE:BASICHEALTH',
        'RESOURCECODE:MEDICATION',
        'RESOURCECODE:CONDITION',
        'RESOURCECODE:ALLERGY',
        'RESOURCECODE:TESTRESULT',
        'RESOURCECODE:IMMUNIZATION'
        ]*/
        return values
    }

    /**
     *
     * @param value
     * @return
     */
    public String extractIcardeaShortUserName(String value) {
        String name = null
        if (value) {
            String[] parts = value.split('=')

            if (parts.length > 1) {
                name = parts[1]
            } else {
                name = parts[0]
            }
        }
        return name
    }
    /**
     * Considers ROLECODE:DOCTOR or ROLECODE:NURSE
     * @param sessionAttributeRole
     * @return
     */
    public boolean isMedicalCareRole(String role) {

        //from UserSessionService.getSessionAttributeRole();

        if (role != null) {
            //role=role.toLowerCase();
            switch (role) {
                case [
                        'ROLECODE:DOCTOR',
                        'ROLECODE:NURSE']:
                    return true
                    break
                default:
                    break
            }

        }
        return false;

    }

    /**
     * true  - Simple access control by role
     * false - access control by Consent Manager
     */
    public boolean isHealthInfoAccessibleByRole() {
        boolean flag = false
        String value = this.getProperty('isAllHealthinfoAccessibleByRole')
        System.out.println("isHealthInfoAccessibleByRole property=" + value)
        value = value ? value = value.trim() : null
        if (value && value == 'true') flag = true

        return flag
    }

    /**
     * Allows simple checks for testing and demos without the setup
     * of additional tools.
     * 1. isAccessControlLocalForHealthInfo or use other ACL tool
     * 2. Allows  doctor or nurse to view reports
     * if all info is accessible only by role
     * <code>isAllHealthInfoAccessibleByRole()</code>
     * @param role
     */
    public boolean isHealthInfoAccessibleByThisRole(String role) {

        if (isAccessControlLocalForHealthInfo()) {


                switch (role) {
                    case [
                            'ROLECODE:DOCTOR',
                            'ROLECODE:NURSE']:
                        return true
                        break
                    default:
                        break
                }

            //}
        }
        return false;

    }

    public boolean isAccessControlLocalForHealthInfo() {
        boolean flag = false
        String value = this.getProperty('isAccessControlLocalForHealthInfo')
        value = value ? value = value.trim() : null
        if (value && value == 'true') flag = true
        return flag
    }

    public String getConsentUIEndpoint() {
        //
        String value = getProperty('consent.web.endpoint')
        return value ? value = value.trim() : null
    }

    public String getConsentServiceEndpoint() {
        //
        String value = getProperty('consent.service.endpoint')
        return value ? value = value.trim() : null

    }

    public String convertLocalRoleToStandardRole(String role) {
        String outRole = null

        if (role != null) {
            //role=role.toLowerCase();
            switch (role) {
                case [
                        'role_medical_physician',
                        'role_medical_physician_gp',
                        'role_medical_physician_cardiologist'
                ]:
                    outRole = 'ROLECODE:DOCTOR'
                    break
                case [
                        'role_medical_nurse',
                        'role_medical_practictioner_healthcare',
                        'role_medical_other_specialist'
                ]:
                    outRole = 'ROLECODE:NURSE'
                    break
                case ['role_medical_dentist']:
                    outRole = 'ROLECODE:DENTIST'
                    break
                default:
                    outRole = null
                    break
            }

        }
        /*
        list = [
        'role_medical_physician',
        'role_medical_physician_gp',
        'role_medical_physician_cardiologist',
        'role_medical_dentist',
        'role_medical_dietitian',
        'role_medical_practictioner_healthcare',
        'role_medical_nurse',
        'role_medical_other_specialist',
        'role_medical_professional_other'
        ]
         */
        return outRole
    }


    public String getCertificatePath() {

        String value = getProperty('phrs.certpath')

        return value ? value = value.trim() : null
    }


    public int getSubscriberSocketListnerPort() {
        int subscriberSocketListenerPort = 5578;
        try {
            String port = getProperty("socket.listener.port", "5578");
            if (port != null)
                port = port.trim();
            else
                port = "5578";
            subscriberSocketListenerPort = Integer.parseInt(port);
        } catch (Exception e) {
            LOGGER.error("error processing socket.listener.port property", e);
        }
        return subscriberSocketListenerPort;
    }
    /**
     * Load local credentials
     * comma separated login IDs. An ID can have a password by adding a delimeter '!' followed by the password.
     * @return
     */

    public  Map<String,String> loadLocalLoginCredentials(){
        LOGGER.debug('loadLocalLoginCredentials start');
        Map<String,String> ids= [:];
        List<String> demoLoginCred= getPropertyList('local.login.ids');

        if(demoLoginCred) {
            for(String row:demoLoginCred){
                String password=''
                String id=row
                try{
                    if(id.contains('!')){
                        String[] tokens= id.split('!');
                        for(int i =0; i < tokens.length ; i++) {
                            //System.out.println(tokens[i]);
                            if(i == 0)          id = tokens[0]
                            else if(i == 1)     password=tokens[1]
                        }
                        if(password == null) password=''
                        password=password.trim()
                    }
                    if(id){
                        ids.put(id.trim(),password)
                    }
                } catch(Exception e){
                    LOGGER.error("error reading loadLocalLoginCredentials on ID="+row, e);
                }
            }

            LOGGER.debug('loadTestLoginIds map prepared='+ids);
//            demoLoginIds = demoLoginIds.replace(' ','');
//            String[] tokens= demoLoginIds.split(',');
//            for (int i = 0; i < tokens.length; i++)  {
//                println('>'+tokens[i]+'<');
//                ids.add(tokens[i].trim());
//
//            }
        }
        return ids
    }
    public  Set<String> loadTestLoginIds(){
        LOGGER.debug('loadTestLoginIds start');
        Set<String> ids= new HashSet<String>();
        List<String> demoLoginIds= getPropertyList('local.login.ids');

        if(demoLoginIds) {
            ids=demoLoginIds.toSet();
            LOGGER.debug('loadTestLoginIds local.login.ids found='+ids);
//            demoLoginIds = demoLoginIds.replace(' ','');
//            String[] tokens= demoLoginIds.split(',');
//            for (int i = 0; i < tokens.length; i++)  {
//                println('>'+tokens[i]+'<');
//                ids.add(tokens[i].trim());
//
//            }
        }
        /*else {
            LOGGER.error('loadTestLoginIds demo.login.ids null');

            ids.add("phrsm");
            ids.add("phrdoctor");
            ids.add("nurse");
            ids.add("doctor");

            ids.add("phr0");
            ids.add("phr1");
            ids.add("phr2");
            ids.add("phr3");
            ids.add("phr4");
            ids.add("phr5");
            ids.add("phr6");
            ids.add("phr7");
            ids.add("phr8");
            ids.add("phr9");

            ids.add("phr1031");
            ids.add("phr1183");
            ids.add("phr1242");
            ids.add("phr1346");
            ids.add("phr1427");
            ids.add("phr1556");
            ids.add("phr1628");
            ids.add("phr1745");
            ids.add("phr1875");
            ids.add("phr1935");
            ids.add("phr2041");
            ids.add("phr2174");
            ids.add("phr2232");
            ids.add("phr2327");
            ids.add("phr2492");
            ids.add("phr2519");
            ids.add("phr2646");
            ids.add("phr2767");
            ids.add("phr2821");
            ids.add("phr2944");
            ids.add("phr3043");
            ids.add("phr3247");
            ids.add("phr3288");
            ids.add("phr3312");
            ids.add("phr3476");
            ids.add("phr3548");
            ids.add("phr3673");
            ids.add("phr3732");
            ids.add("phr3849");
            ids.add("phr3932");
            ids.add("phr4078");
            ids.add("phr4163");
            ids.add("phr4266");
            ids.add("phr4323");
            ids.add("phr4432");
            ids.add("phr4591");
            ids.add("phr4632");
            ids.add("phr4723");
            ids.add("phr4814");
            ids.add("phr4932");
            ids.add("phr5321");
            ids.add("phr5179");
            ids.add("phr5193");
            ids.add("phr5332");
            ids.add("phr5443");
            ids.add("phr5583");
            ids.add("phr5683");
            ids.add("phr5752");
            ids.add("phr5890");
            ids.add("phr5193");

        } */
        //if(ids) println('ids size='+ids.size())
        //else    println('ids null or empty')

        return ids;
    }

    public boolean isValidLocalLogin(String id, String password){
        LOGGER.debug('isValidLocalLogin start - id='+id+' pass='+password);
        if(id ){
            if(id.startsWith('phra')) return true;
            if(id.startsWith('phrsm')) return true;
            if(id.startsWith('phrdoctor')) return true;
            if(id.startsWith('phrnurse')) return true;
            if(localLoginCredentials){
                //println('testLoginIds and id valid size='+localLoginCredentials.size())
                //if(id.startsWith('pha')) return true;

                if( localLoginCredentials.keySet().contains(id)) {

                    LOGGER.debug('isValidLocalLogin ID  found, id='+id);
                    String pwd= localLoginCredentials.get(id)
                    if(pwd){   //then validate when not blank or null
                       if(!password){
                           LOGGER.debug('isValidLocalLogin password false, password=null'+' expected='+pwd);
                          return false

                       } else if(pwd.equals(password)){
                           LOGGER.debug('isValidLocalLogin password true, password='+password);
                           return true
                       }  else {
                           LOGGER.debug('isValidLocalLogin password false, password='+password+' expected='+pwd);
                           return false
                       }
                    } else {
                        LOGGER.debug('isValidLocalLogin password true, no password expected');
                    }

                    return true
                } else {

                    LOGGER.debug('isValidLocalLogin FALSE ID not found, id='+id);
                }
            } else {
                LOGGER.debug('isValidLocalLogin localLoginCredentials not assigned (null or empty), check properties file  at id='+id)
            }
        }
        return false;
    }

}
