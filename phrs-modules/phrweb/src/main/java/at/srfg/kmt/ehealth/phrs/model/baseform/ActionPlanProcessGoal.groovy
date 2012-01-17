package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity


/**
 * Big goals are outcome goals
 * Process goals are the mini achievements to attain, short plan
 * http://www.thewisdomjournal.com/Blog/outcome-goals-and-process-goals-you-need-both/
 *
 */
@Entity
public class ActionPlanProcessGoal extends ActionPlanBaseGoal{

	//The following can be used from the Base
	//String label
	//String description - use content
	//String tags
	//String status
	//Integer priority
	//Date beginDate
	//Date endDate

	public ActionPlanProcessGoal(){
		super();
		priority=new Integer(0)
	}
}
