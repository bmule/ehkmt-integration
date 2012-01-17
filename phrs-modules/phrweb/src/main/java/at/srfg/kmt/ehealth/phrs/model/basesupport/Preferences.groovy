package at.srfg.kmt.ehealth.phrs.model.basesupport

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

import com.google.code.morphia.annotations.Entity
//not phrsdocument
//('phrs_preferences')
@Entity
public class Preferences extends CommonModelProps {
	
	public static final String TARGET_MAIN_APP_USER_PATIENT="/app/dashboard/role/patient";
	public static final String TARGET_MAIN_APP_USER_CARETAKER="/app/dashboard/role/caretaker/";
	public static final String TARGET_MAIN_APP="/app/dashboard/role/medical_professional";
	
	String settingsType
	HealthApplicationEntry healthApplicationProfile = new HealthApplicationEntry()
	
	Map<String,String> params=new HashMap<String,String>()	
	

}
