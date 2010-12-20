package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.ConsentRule;



public class ConsentRuleResponse extends GUIResponse{
	public ConsentRuleResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected List<ConsentRule> consentRules;
	protected String basedOnTemplate;
	protected String id;
	protected String definition;
	protected String expirationDate;
	public List<ConsentRule> getConsentRules() {
		return consentRules;
	}
	public void setConsentRules(List<ConsentRule> consentRules) {
		this.consentRules = consentRules;
	}
	public String getBasedOnTemplate() {
		return basedOnTemplate;
	}
	public void setBasedOnTemplate(String basedOnTemplate) {
		this.basedOnTemplate = basedOnTemplate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
}
