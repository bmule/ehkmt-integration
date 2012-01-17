package at.srfg.kmt.ehealth.phrs.jsf.managedbean
// collector observable lists or maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html

import java.util.Collection

import javax.faces.bean.ManagedBean
import javax.faces.bean.SessionScoped
import javax.faces.context.FacesContext

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyEnhancer
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService

/**
 * 
 * Includes UserServices and vocabulary helper methods to support child beans
 * Vocabulary lookup for labels can check vocabulary services or I18 files.
 * Give a UI bean the vocab service for a particular language
 * Get the UIs vocabs cached.
 * 
 * TODO: Use tool for JSF Map trick to handle setting of language
 * Issue: JSF directly access methods without providing a language parameter
 * The initializing bean should set the locale....
 * 
 * Session scoped
 * Application scope
 * Uses the Map trick
 * 
 *
 */

@ManagedBean(name = "vocabBean")
@SessionScoped
class VocabularyBean implements Serializable{

	Locale locale
	String language


	Map<String,Collection<ModelLabelValue>> internalVocabMap

	/*
	 @ManagedProperty(value='#{message}')
	 private MessageBean messageBean;
	 //must povide the setter method
	 public void setMessageBean(MessageBean messageBean) {
	 this.messageBean = messageBean;
	 }
	 */

	public VocabularyBean(){

		initLanguage()
	}

	/**
	 * Subclass should perform initialization in Postcontruct method
	 * @param filterClass
	 * @param language
	 */
	protected  void initVocabularies(Class filterClass){

		if( ! internalVocabMap) {
			internalVocabMap = VocabularyEnhancer.localVocabularies(filterClass,getLanguage())
		}
	}


	public void initLanguage(){

		locale= locale ? locale : FacesContext.getCurrentInstance().getViewRoot().getLocale();
		language=locale ? locale.getLanguage() :'en'
	}

	public Map<String,Collection<ModelLabelValue>> getVocabMap(Class filterClass){
		if( ! internalVocabMap) {
			initVocabularies(filterClass)
		}
		return internalVocabMap
	}

	public void setVocabMap(Map<String,Collection<ModelLabelValue>> map){
		internalVocabMap = map
	}

	/*
	 * 
	 protected void setDomainClazz(Class clazz, boolean created selected item){
	 domainClazz = clazz.getClass()
	 //setSelected(domainClazz.);
	 println('setDomainClazz'+domainClazz.class)
	 setSelected(domainClazz.newInstance())
	 }*/

	/**
	 * Should get language from locale
	 * @param String
	 * @return
	 */
	public String getTermLabel(String termId){
		return VocabularyService.getTermLabel(termId, language)
	}

	public Collection<String> getTermLabels(Collection termIds){

		return VocabularyService.getTermLabelsById(termIds, language)
	}


	public ModelLabelValue getTerm(String termId){
		ModelLabelValue lv = VocabularyService.getTerm(termId, language)
		return lv
	}


	public Collection<String> getTermIds(String tag){
		return VocabularyService.getTermValuesByTag(tag, language)
	}

/*
 * Special lookups
 */

	public Collection<ModelLabelValue> getRiskfactorTreatmentsList(String theCode){

		return VocabularyEnhancer.getRiskfactorTreatmentsList(theCode,vocabMap)
	}

}
