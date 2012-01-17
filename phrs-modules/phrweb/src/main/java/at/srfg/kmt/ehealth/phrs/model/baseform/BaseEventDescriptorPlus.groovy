package at.srfg.kmt.ehealth.phrs.model.baseform


import com.google.code.morphia.annotations.Entity


/**
 * All observations should inherit the base event or the BasePlusEvent
 * so that the calendar solution will integrate
 * 
 * TODO should commonModelProperties inherit the Base event? it contains date
 * related info
 */
@Entity("event_base2")
public class BaseEventDescriptorPlus extends BasePhrsModel{
	//use type form base model props
	String subtype
	
	

	public BaseEventDescriptorPlus(){
		super();
	}
	

}
