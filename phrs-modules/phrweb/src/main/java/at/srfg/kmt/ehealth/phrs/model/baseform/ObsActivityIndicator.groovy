package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Map

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity

@Entity
public class  ObsActivityIndicator extends CommonModelProps{
	Integer moodLevel
	Integer activityLevel
	
	Map<String,String> indicators
	
	public ObsActivityIndicator(){
		super();
		indicators = [:]
		
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
}
