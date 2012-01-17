package at.srfg.kmt.ehealth.phrs.model.basesupport

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

@Entity
class ThingType {
	@Id
	ObjectId id
	String property
	String status
	String jtype
	
	
	boolean commonPropery
	Map<String,String>  interopCode
	
	//@Reference
	Set<String> paired //e.g. blood pressure
	
	Set<String> typedView
	Set<String> typedFormInputRequired
	Map<String,String> presentationFormConstaint
	
	

}
