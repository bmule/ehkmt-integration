package at.srfg.kmt.ehealth.phrs.model.baseform


import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Transient
/**
 * User Contact information
 */
@Entity
public class  ProfileContactInfo extends  BasePhrsModel {
	String contactType
	String personTitle
	/**
	 * Display name
	 */
	String name
	
	String firstName
	String middleName
	String lastName
	Date birthdate
	String primaryLanguage
	
	//role identifiers from vocabulary or system definition
	// used for patient healthcare_user, healthcare_provider
	String healthcareRole
	
	Set<String> secondaryRoles
	
	Boolean activeStatus
	//TODO replace activeStatus with statusCode in radio buttons
	
	String statusCode
	
	String addressText
	String postalCode
	String city
	String country
	
	String primaryTelephone
	String primaryTelephoneMobile
	
	@Embedded
	Set<PhoneInfo> secondaryPhones

    
/**
 *@deprecated
 */
    String pixQueryIdUser
 /**
 *@deprecated
 */   
    String pixQueryIdType

	/*
	 * voip - initial support to facilitate google talk, skype, etc
	 */
	//@Reference
	//Set<PhoneSimpleVoip> secondaryInternetPhones= []
	
	String email
	public Map getEmail_obj(){
		Map<String,String> result= [
			'provider':'email',
			'id':email,'email':email,
			'href':ProfileContactInfo.makeHtml('email', 'email',email)]
		
		return result
	}
	//unique - federated email (openId), etc Used to uniquely identify and authenticate this person

	String internetSystemIdProvider //open id eg. google
	String internetSystemFederatedId //icardea
	
	@Transient
	String internetSystemFederatedId_obj
	
	public Map getInternetSystemFederatedId_obj(){
		Map<String,String> result= [
			'provider':internetSystemIdProvider,
			'id':internetSystemFederatedId,
			'href':ProfileContactInfo.makeHtml('internetFederatedId', internetFederatedIdProvider,internetFederatedId)]
		
		return result
	}

	String internetFederatedIdProvider //open id eg. google
	String internetFederatedId
	@Transient
	String internetFederatedId_obj
	
	public Map getInternetFederatedId_obj(){
		Map<String,String> result=
		['email': internetFederatedId,
		'provider':internetFederatedIdProvider,
		'id':internetFederatedId,
		'href': ProfileContactInfo.makeHtml('internetFederatedId', internetFederatedIdProvider,internetFederatedId)
		]
		return result
	}

	
	String internetChatProvider //skpe, google
	String internetChatId
	@Transient
	String internetChatId_obj
	public String getInternetChatId_obj(){
		Map<String,String> result=
		['provider':internetChatProvider,
		'id':internetChatId,
		'href': ProfileContactInfo.makeHtml('internetChatId', internetChatProvider,internetChatId)
		]
		return result
	}
	
		//when google voice allows people to port their phone numbers into google voice
	String internetVoiceProvider //skype, google
	String internetVoiceId //skype, google
	@Transient
	String internetVoiceId_obj
	public Map getInternetVoiceId_obj(){
		Map<String,String> result=
		['provider':internetVoiceProvider,
		'id':internetVoiceId,
		'href': ProfileContactInfo.makeHtml('internetVoiceId', internetVoiceProvider,internetVoiceId)
		]
		return result
	}
	
	String internetPhone //future...might be google enabled phone number
	@Transient
	String internetPhone_obj
	public Map getInternetPhone_obj(){
		Map<String,String> result=
			['provider':'',
			'id':internetPhone,
			'phone':internetPhone,
			'href': ProfileContactInfo.makeHtml('internetPhone', '',internetPhone)
		]
		return result
	}
	
	//a professional system identifier
	String authorityIdentifier
	
	String website
	
	String organization
	
	Map<String,String> properties
	


	public ProfileContactInfo(){
		super()
		//feedbackToolInfo = new FeedbackAccessInfo()
		
		secondaryPhones = new ArrayList<PhoneInfo>()
		
		secondaryRoles= new HashSet<String>()
		
		primaryLanguage = true
		internetSystemIdProvider="iCARDEA"
	
		contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER
		properties = [:]
		//deactiviate event
		eventActivated=Boolean.FALSE
		
		//protocolId = new HashSet<String>()
		properties = [:]
		
		eventStatus=	PhrsConstants.EVENT_STATUS_NOT_EVENTABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
		
		//pixIdentifier= new PixIdentifier(null,true,true,PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID)
	
		//ciedIdentifier= new PixIdentifier(null,false,false,PhrsConstants.IDENTIFIER_TYPE_CIED_NUMBER)
		
	}
	/*
	<a label="email" href="mailto: sge@forsthaus.de" />
<a href="callto://skypeusername/">Skype Username</a>
	 */
	public static String makeLinkReference(String provider, String id){
		String out=""
		if(provider){
			String theProvider = provider.toLowerCase()
	
			switch(provider){
				case ['google','email']:
					out='mailto://'
				break
				case 'skype':
					out='callto://'
				break
				case 'icq':
					out=""
				break
				default:
				
				break
			}
		}
		return out
	}
	public static makeHtml(String type, String provider, String id){
		String out=""
		
		if(id){
		switch(type){
				case ['internetFederatedId','email']:
				break
					String temp=makeLinkReference(provider,id)
					out="href='${temp} $id'"
                    break
				case 'internetChatId':
					String temp=makeLinkReference(provider,id)
					"href='${temp} $id'"
				break
				
				case 'internetVoiceId':
					String temp=makeLinkReference(provider,id)
					out="href='${temp} $id'"
				break
				
				case 'internetPhone':
					String temp=makeLinkReference(provider,id)
					out="href='${temp} $id'"
				break
				
				default:
					out="href='mailto: $id"
				break
			}
		}
		return out
	}
}
