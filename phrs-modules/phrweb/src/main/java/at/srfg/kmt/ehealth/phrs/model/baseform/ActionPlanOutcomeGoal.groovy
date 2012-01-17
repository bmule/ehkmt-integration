package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.List

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference
/**
 * Big goals are outcome goals
 * Process goals are the mini-achievements to attain, short plan
 * http://www.thewisdomjournal.com/Blog/outcome-goals-and-process-goals-you-need-both/
 * An outcome can be assigned to one theme.
 * Multiple outcomes to one theme but the outcome should not overlap temporally
 */
// ity("action_plan_outcome_goal")
@Entity
public class ActionPlanOutcomeGoal extends ActionPlanBaseGoal {

//priority and order issue - maybe these are reused with differencesâ

	//use Note from CommonModelProps
	//String note or description
	
	/**
	 * Choose from controlled vocabulary, but can also create own 
	 * alternative that might be added to the controlled vocabulary later
	 */
	
	ActionPlanOutcomeTheme outcomeTheme
	/**
	 * alternative that might be added to the controlled vocabulary later
	 */
	String otherThemeLabel
	
	//list with sequence, use tag to prioritize?
	@Reference
	List<ActionPlanProcessGoal> hasProcessGoals //	Collection<String> Collection<String> ActionPlanProcessGoal

	public ActionPlanOutcomeGoal(){
		super();
	}


}
