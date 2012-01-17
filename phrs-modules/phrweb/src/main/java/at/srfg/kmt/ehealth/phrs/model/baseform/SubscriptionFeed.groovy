package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Map


import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
public class  SubscriptionFeed extends BasePhrsModel{

	
	String feedUrl
	
	
	Map<String,String> accessParams 
	//use common prop status whether this is active or not and deleted field when needed
	
	public SubscriptionFeed(){
		super()
		accessParams=new HashMap<String,String>()
	}
}
