package at.srfg.kmt.ehealth.phrs.model.baseform

import java.io.Serializable

import com.google.code.morphia.annotations.Embedded

@Embedded
public class FeedbackAccessInfo implements Serializable {
	String type
	String feedbackProvider
	String feedbackProviderUrl
	/**
	 * could be user ID, phone depending on type
	 */
	String feedbackAccessId
	Integer priority
	
	public FeedbackAccessInfo(){
		
	}
}
