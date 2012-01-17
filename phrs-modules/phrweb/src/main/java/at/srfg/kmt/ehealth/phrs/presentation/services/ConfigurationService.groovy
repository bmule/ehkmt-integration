package at.srfg.kmt.ehealth.phrs.presentation.services;

import static at.srfg.kmt.ehealth.phrs.PhrsConstants.*

import java.io.Serializable

import javax.faces.context.FacesContext

import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.configuration.XMLConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.basesupport.OpenIdProviderItem
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;

/**
 * 
 * Exposes Configuration file settings and Context parameters (web.xml)
 *
 */

public class ConfigurationService implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);
	private  static  ConfigurationService m_instance;

	public static final String forwardRedirectIsAuthenticatedToPage = "/jsf/home.xhtml";
	public static final String formwardRedirectFilteredDirectory = "/jsf/";
	public static final String forwardRedirectLoginPage = "/WEB-INF/views/jsp/login.jsp";
	private List<String> rolesLocal=[]
	private List<String> rolesConsentMgr=[]
	private static XMLConfiguration xmlConfig;
	private static PropertiesConfiguration propertiesConfig
	private static PropertiesConfiguration icardeaConfig

	/*
	 * Initialization-on-demand holder idiom
	 *
	 private static class LazyHolder {
	 public static final ConfigurationService m_instance = new ConfigurationService();
	 }
	 public static ConfigurationService getInstance() {
	 return LazyHolder.m_instance;
	 } */

	public static ConfigurationService getInstance() {
		if(m_instance==null) {
			m_instance = new ConfigurationService()
		}
		return m_instance;
	}
	private ConfigurationService(){
		init()
		initRoles()
	}

	private void initRoles(){

		rolesLocal= [
			PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN,
			PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST,
		]
		rolesConsentMgr= [
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN,
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE,
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_FAMILY_MEMBER,
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PSYCHIATRIST,
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHARMACIST,
			PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DENTIST,
			PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN,
			PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST,
		]
	}
	private synchronized void init(){

		if(xmlConfig == null) {
			refreshXMLConfig()
		}
		if(propertiesConfig == null) {
			refreshPropertiesConfig()
		}
		if(icardeaConfig == null) {
			refreshPropertiesIcardeaConfig()
		}
	}

	public synchronized void refreshXMLConfig(){
		try {
			xmlConfig = new XMLConfiguration("phrs.config.xml");
		} catch(ConfigurationException e) {
			LOGGER.error("ConfigurationService error", e);
		}
	}
	public synchronized void refreshPropertiesConfig(){


		try {
			propertiesConfig = new PropertiesConfiguration("phrs.properties");
		} catch(ConfigurationException e) {
			LOGGER.error("ConfigurationService error", e);
		}
	}
	public synchronized void refreshPropertiesIcardeaConfig(){


		try {
			icardeaConfig = new PropertiesConfiguration("icardea.properties");
		} catch(ConfigurationException e) {
			LOGGER.error("ConfigurationService error", e);
		}
	}
	public static final String ENDPOINT_TYPE_PIX="pix.connectionhub.endpoint"
	public static final String ENDPOINT_TYPE_CONSENT_WS="consent.ws.endpoint"

	public String getEndPoint(String prop){

		return getProperty(prop)
	}
	public String getEndPoint(String prop,String defaultValue){

		return getProperty(prop,defaultValue)
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
	public String getProperty(String prop,String defaultValue){
		String value=getProperty(prop)
		return value ? value : defaultValue
	}
	public String getProperty(String prop){
		String value=null;

		if(prop){
			value = propertiesConfig.getProperty(prop)
			if(!value){
				switch(prop){

					case PhrsConstants.OPENID_ICARDEA_PROVIDER_KEY:
					value='https://localhost:8443/idp/'
					break
					case 'forwardRedirectIsAuthenticatedToPage':
					value='/jsf/home.xhtml'
					break
					case 'formwardRedirectFilteredDirectory':
					value='/jsf/'
					break
					case 'forwardRedirectIsAuthenticatedToPage':
					value='/jsf/home.xhtml'
					break
					case 'forwardRedirectLoginPage':
					value='/WEB-INF/views/jsp/login.jsp'
					break
					case 'forwardRedirectIndexPage':
					value='/index.xhtml'
					break
					case 'login_openid_forwardUri':
					value='/WEB-INF/views/jsp/login.jsp'
					break
					case ENDPOINT_TYPE_PIX:

					value='localhost:2575'
					break
					case ENDPOINT_TYPE_CONSENT_WS:

					value='localhost:8080'
					break
					default:
					//lookup
					break
				}
			}
		}
		return value
	}
	public String getSystemDomainCode(String tag){
		String label
		label=''
		return label
	}
	public static boolean isAppModeTest() {

		String testValue = getInitParameter("testmode");
		if (testValue != null
		&& ("true".equalsIgnoreCase(testValue))) {
			return true;
		}
		return false;
	}
	public static boolean isAppModeSingleUserTest() {

		String testValue = getInitParameter("testsingleusermode");
		if (testValue != null
		&& ("true".equalsIgnoreCase(testValue))) {
			return true;
		}
		return false;
	}
	//isAppModeSingleUserTest
	public static boolean isAppModePixTest() {

		String testValue = getInitParameter("testpixmode");
		if (testValue != null && ("true".equalsIgnoreCase(testValue))) {
			return true;
		}
		return false;
	}
	public static boolean isAppModeRoleTest() {

		String testValue = getInitParameter("testrolemode");
		if (testValue != null && ("true".equalsIgnoreCase(testValue))) {
			return true;
		}
		return false;
	}

	public static boolean isAppModeMonitorListAllUsers() {

		String testValue = getInitParameter("monitorlistall");
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
	public String  getUserManagementScenario() {
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
	public List<OpenIdProviderItem> getOpenIdProviderItem(){
		List<OpenIdProviderItem> items = []
		//read Dyuproject providers and

		return items
	}
	/**
	 * From Context web.xml
	 */
	public static String getOpenIdProvider(boolean local) {
		String value = null;
		if(local) 	value = getInitParameter("openid.provider");
		else  		value = getInitParameter("openid.provider.test");
		if (value != null ){
			if(value.trim().length()< 2) value=null;

		}
		return value;
	}
	/**
	 *  Consent Mgr codes
	 * @return
	 */
	public List<String> getConsentResourceCodes(){

		return rolesConsentMgr

	}
	/**
	 * Additional roles, but not used by consent mgr
	 * @return
	 */
	public List<String> getConsentLocalCodes(){

		return rolesLocal
	}
	/**
	 *  Consent Mgr codes and local PHR codes
	 * @return
	 */
	public List<String> getConsentAllCodes(){
		List list = rolesConsentMgr
		list.addAll(rolesLocal)
		return list

	}
	/**
	 * 
	 * @param tagFilter
	 * @return
	 */
	public List<String> getConsentResourceCodes(String tagFilter){

		List values=new ArrayList()
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
	public List<String> getConsentSubjectCodes(String tagFilter){
		List values
		switch(tagFilter){
			case 'phr':
			//fall through to default ,no case break
			default:
			values =  [PhrsConstants.AUTHORIZE_RESOURCE_CODE_BASIC_HEALTH,
				PhrsConstants.AUTHORIZE_RESOURCE_CODE_MEDICATION
				,PhrsConstants.AUTHORIZE_RESOURCE_CODE_CONDITION
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
	 * @param shortUserName
	 * @return
	 */
	public String makeIcardeaOpenIdentifier(String shortUserName){
		String name = shortUserName;
		if(name){
			if( ! name.startsWith('http')){
				name= this.getProperty(PhrsConstants.OPENID_ICARDEA_PROVIDER_KEY)+'u='+shortUserName
			}
		}
		return name
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public String extractIcardeaShortUserName(String value){
		String name
		if(value){
			String[] parts = value.split('=')

			if(parts.length() > 1){
				name=parts[1]
			} else {
				name=parts[0]
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

		if(role!=null){
			//role=role.toLowerCase();
			switch(role){
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
        public boolean isHealthInfoAccessibleByRole(){
            boolean flag = false
            String value = this.getProperty('isAllHealthinfoAccessibleByRole')
            if(value && value.trim() =='true') flag=true
            return flag
        }
    
        /**
         * First check if all info is accessible only by role
         * <code>isAllHealthInfoAccessibleByRole()</code>
         * @param role 
         */
        public boolean isHealthInfoAccessibleByThisRole(String role) {

		//from UserSessionService.getSessionAttributeRole();
                boolean flag = isHealthInfoAccessibleByRole()
		if(flag && role!=null){
			//role=role.toLowerCase();
			switch(role){
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
        

	public String convertLocalRoleToStandardRole(String role){
		String outRole

		if(role!=null){
			//role=role.toLowerCase();
			switch(role){
				case [
					'role_medical_physician',
					'role_medical_physician_gp',
					'role_medical_physician_cardiologist'
				]:
				outRole='ROLECODE:DOCTOR'
				break
				case [
					'role_medical_nurse',
					'role_medical_practictioner_healthcare',
					'role_medical_other_specialist'
				]:
				outRole='ROLECODE:NURSE'
				break
				case ['role_medical_dentist']:
				outRole='ROLECODE:DENTIST'
				break
				default:
				outRole=null
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


	public String getCertificatePath(){
		String certPath
		certPath=getProperty('phrs.certpath')

		return certPath
	}

}
