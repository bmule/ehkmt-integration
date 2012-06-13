package at.srfg.kmt.ehealth.phrs.jsf.managedbean

import javax.faces.context.FacesContext

import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService

class FaceCommon implements Serializable{

	String language
	Locale locale

	public void initLanguage(){

		locale= locale ? locale : FacesContext.getCurrentInstance().getViewRoot().getLocale();

		println('default req locale='+locale)
		language=locale ? locale.getLanguage() :'en'
	}
	/**
	* Forward, but might have issue with relative CSS or JS and browser bookmarking because browser displays original URL
	* @param uri
	*/
   public void dispatch(String uri){
	   FacesContext.getCurrentInstance().getExternalContext().dispatch(uri);
   }
   /**
	* Redirect
	* @param uri
	*/
   public void redirect(String uri){
	   //implicit call to FacesContext.getCurrentInstance().responseComplete(). If use response.sendRedirect then must call ..responseComplete afterwards
   		UserSessionService.redirect(uri)
   }
    public String getConfigPrivacyLevel(){
        return ConfigurationService.getInstance().getProperty('privacy.level','1');
    }
    public String getConfigImportEhr(){
        return ConfigurationService.getInstance().getProperty('import.ehr','1');
    }

}
