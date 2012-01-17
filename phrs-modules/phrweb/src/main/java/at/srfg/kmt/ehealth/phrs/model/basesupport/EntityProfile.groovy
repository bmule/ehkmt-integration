package at.srfg.kmt.ehealth.phrs.model.basesupport

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id


//('entity_profile')
@Entity
public class EntityProfile {
	@Id
	ObjectId id	
	
	String profileUri
	String description
	Date createDate
	Date modifyDate
	String tags
	
	List<String> categories
	Map<String,String> params= new HashMap<String,String>()
	

}
