package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity


/**
 * 
 * Outcomes can be assigned to a theme
 * Theme e.g. Losing weight, getting out of debt, develop a stronger network,
 * making more sales at work, finding a new job
 * A theme can have an outcome, but should not be overlapping temporally
 * Not specific to any user, can be reused 
 * Based on controlled vocabulary, that can be extended by users
 */
//ntity("action_plan_theme")
@Entity
public class ActionPlanOutcomeTheme extends CommonContentSegment{


	public ActionPlanOutcomeTheme(){
		super();
	}
}
