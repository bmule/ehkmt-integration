package at.srfg.kmt.ehealth.phrs.model.basesupport

import java.util.Map
import java.util.Set

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference
/**
 * Common vitals and healthProfile Uri
 *
 */
//ntity("health_profile") ("phrsdocument") //ntity('entity_profile')
@Entity
public class HealthProfile extends  EntityProfile{
	//TODO set the common types and root here!
	//public static final String XXX="";
	
	
	String healthProfileUri //this is different from the resourceUri, but it is initially the ownerUri, but might change
	
	@Reference
	UserIdentityProfile identityProfile = new UserIdentityProfile()
	
	//VitalSignOverview basicVitals =new VitalSignOverview()
	/**
	 * current summary, updated for latest creation of a vital sign
	 * can use coded or display friendly key
	 * need for height
	 * body_height
	 * body_weight
	 * blood_pressure
	 * heart_rate
	 * 
	 * assume measure measure metric unless
	 * key for xxxx
	 */
	
	//Map<String,VitalSign> currentVitalSigns = new HashMap<String,VitalSign>()
	@Embedded
	Map<String,String> currentVitalSigns = new HashMap<String,String>()
	
	public String getHealthProfileUri(){
		return profileUri
	}
	public void setHealthProfileUri(String healthProfileUri){
		profileUri = healthProfileUri 
	}
	/**
	 * user id of embedded portal
	 */
	String portalUserId

	Set<String> portalUserIdHistory = new HashSet<String>()
	/**
	* restful uri provides means to lookup more information
	* for example, the uri includes the EHR applicationIdentifier from HealthApplicationEntry
	*/
	Set<String> patientIdEhr = new HashSet<String>()
	/**
	 * restful uri provides means to lookup more information
	 */
	Set<String> patientIdPhrExternal = new HashSet<String>()
	
	Set<String> trustedUsers = new HashSet<String>()
	Set<String> trustedMedicalProfessionals = new HashSet<String>()
	
	
	//height --> to VitalCommon.height
	
	
	public HealthProfile(){
		
	}
}
