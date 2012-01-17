package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Set


import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
public class  SubscriptionFeedGroup extends BasePhrsModel{
	
	@Reference
	Set<SubscriptionFeed> subscriptionFeed
	// use common model props for  tags and categories, status, deleted
	
	public SubscriptionFeedGroup(){
		super()
		subscriptionFeed = new HashSet<SubscriptionFeed>()
	}
}
