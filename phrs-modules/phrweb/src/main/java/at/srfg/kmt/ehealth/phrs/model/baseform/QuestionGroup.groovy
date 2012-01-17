package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.List
import java.util.Set


import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
public class  QuestionGroup extends  BasePhrsModel {
	
	String groupType
	@Reference
	List<CommonQuestion> questions
	
	/**
	 * Can be the ownerUri, but not necessarily.
	 * These groups might be issued by one or more users or application (profile URIs)
	 */
	Set<String> senderProfileUris
	Set<String> targetProfileUris

	
	public QuestionGroup(){
		super()
		questions= new ArrayList<CommonQuestion>()
		senderProfileUris = []
		targetProfileUris = []
	}
}
