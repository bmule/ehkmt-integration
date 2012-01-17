package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Set

import at.srfg.kmt.ehealth.phrs.model.basesupport.AnnotationDescription;

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity

@Entity
public class CommonModelProps extends BasePhrsModel{

	/**
	 * This form resource has added interop resources
	 * This is a collection of URIs because a form might write more than
	 * one type of interop resource 
	 * 
	 * e.g. body weight form adds more than one interop resource (height if changed, weight)
	 * Blood pressure: 2 interop resources: systolic+diastolic and heart rate 
	 * 
	 * These are always udpated when a form is changed
	 */

	Set<String> interopResourcesCurrent = []
	/**
	* This form resource has added interop resources
	* Both the current and historical are written
	* Upon updated this form, the history is updated.
	* The interop resources serve as the history of this form.
	*/

	Set<String> interopResourcesHistory = []


	/*
	* TODO annotations, controlled vocabulary and medical contact integration.
	* This carries the annotations for properties or other semantic annotations
	* See also the PhrsConstants and Annotea
	* for basic semantic annotations on property categories rather than only specific properties
	* "I have a question about medication dosage(category) for my doctor(actor category)
	* Dr. Smith (instance in Medical Contact or free text label that should be entered as a Medical contact)"
	*/
	//@Embedded
	//Set<AnnotationDescription> annotationDescription
 
	//Use social comments separately and refer the target instead
	//List<CommonSocialComment> socialComments = []

	public CommonModelProps(){
		super()
		//need for @Entity("...")
		//annotationDescription=[]
	}
}
