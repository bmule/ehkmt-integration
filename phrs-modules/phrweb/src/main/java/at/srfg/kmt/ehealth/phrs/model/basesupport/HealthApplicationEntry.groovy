package at.srfg.kmt.ehealth.phrs.model.basesupport

import java.util.Set
import org.bson.types.ObjectId
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

// tity("health_application_descriptor")
@Entity
public class HealthApplicationEntry extends EntityProfile{
	
	/*
	 * matches identifer in patient ID found in HealthProfile 
	 */
	String applicationIdentifier
	
	Set<String> identities
	
	public HealthApplicationEntry(){
		//need for entity
	}
}
