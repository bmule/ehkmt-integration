package at.srfg.kmt.ehealth.phrs.jsf.managedbean

import javax.faces.bean.ApplicationScoped
import javax.faces.bean.ManagedBean

import at.srfg.kmt.ehealth.phrs.jsf.utils.SiteReferencesTool
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
/**
 * 
 * The 'pages' is the SiteReferencesTool that extends Map
 * A UI using JSF etc can lookup a key and get the URL or the key is a URL
 * 
 *
 */
@ManagedBean(name="portalBean")
@ApplicationScoped
class PortalReferences {

	String educationUrl
	String wikiUrl
	String forumUrl
	String decisionaids


	//Map pages
	SiteReferencesTool pages

	public PortalReferences(){
		/*
		 educationUrl='http://qviz-dev.salzburgresearch.at/confluence/'
		 wikiUrl='http://qviz-dev.salzburgresearch.at/confluence/'
		 forumUrl='http://qviz-dev.salzburgresearch.at/confluence/'
		 decisionaids= 'http://qviz-dev.salzburgresearch.at/confluence/'
		 pages = [education:educationUrl,
		 wiki:wikiUrl,
		 forum:forumUrl,
		 decisionaids:decisionaids
		 ]
		 */
		pages = new SiteReferencesTool()
	}

    public String getConsentUI(){

        String endPoint= getConsentUI(ConfigurationService.getInstance().getConsentUIEndpoint(),getProtocolId());

        if( ! endPoint){
            endPoint='#'
        }

        return endPoint;
    }

    public String getConsentUI(String baseEndPoint, String pid){

        String endPoint = baseEndPoint

        if(endPoint){
            if(pid) endPoint = endPoint + '?protocolid=' + pid
        }

        return endPoint;
    }

    public String getProtocolId(){
         return UserSessionService.getSessionAttributeProtocolId();
    }

}
