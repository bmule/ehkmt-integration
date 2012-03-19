package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity

@Entity
public class CommonModelProps extends BasePhrsModel{

	/**
	 * This form resource has added interop resources
     * These are created the first time a message is sent
     *
	 * This is a collection of URIs because a form might write more than
	 * one type of interop resource 
     * The Code could be a prefix, otherwise a Map should be used
	 * 
	 * e.g. body weight form adds more than one interop resource (height if changed, weight)
	 * Blood pressure: 2 interop resources: systolic+diastolic and heart rate 
	 * 
	 * These are always udpated when a form is changed
     * TODO refactor, currently use an alternative approach
	 */

    //Currently we do not store the message URIs, until the JSF ViewScoped is working again
    //TODO a clean up method. When message URIs are not found, remove. The Interop component store might have been cleared
    Map<String,String> interopCodeMap=[:]



	public CommonModelProps(){
		super()
		//need for @Entity("...")
		//annotationDescription=[]
	}
}
