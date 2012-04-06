package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import org.opensaml.xml.parse.LoggingErrorHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

/**
 * Queries various sources of labels
 * Can be used in JSF 
 * SiteReferencesTool that extends Map
 * A UI using JSF etc can lookup a key and get the URL or the key is a URL
 * Here the 'pages' is the SiteReferencesTool
 * #{portalBean.pages.portalBean.pages.glossary}
 * 
 * prefix or without prefix
 * identifier might be a URL (ontology) or i18N file
 * 
 * en|http://xxx/xfdljsdfls/
 * or http:xxx
 */
public class SiteReferencesTool implements Map, Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);
	String portalUrl
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
	Map query = [
		//'home'		:'display/phrcontent/Home',
		'feeds':'pages/listpages-dirview.action?key=phrcontent',
		'rss'		:'labels/listlabels-heatmap.action?key=phrcontent',

		'home'		:'pages/listpages-dirview.action?key=phrcontent',
		'pages'		:'pages/listpages-dirview.action?key=phrcontent',

		'wiki'		:'pages/listpages-dirview.action?key=phrcontent',
		'blog'		:'pages/viewrecentblogposts.action?key=phrcontent',

		'forum'		:'spaces/forums/spaceForums.action?key=phrcontent',

		'community'	   :'pages/recentlyupdated.action?key=phrcontent',
		'mycommunities':'users/bubbles/user-community-profile.action',

		'tags'		:'labels/listlabels-heatmap.action?key=phrcontent',
		'mytags'	:'users/viewmylabels.action',

		'bookmarks'	:'spaces/space-bookmarks.action?spaceKey=phrcontent',
		'mail'		:'spaces/viewmailarchive.action?key=phrcontent',

		//education Patient+Education
		'cied_patient_education':'display/phrcontent/Patient+Education',

		'glossary':'display/phrcontent/Glossary',

		'basic_information':'display/phrcontent/Basic+Information',

		'new_habits':'display/phrcontent/New+Habits',

		'precautions':'display/phrcontent/Precautions',

		'warning_signs':'display/phrcontent/Warning+Signs',

		'useful_links':'display/phrcontent/Links',

		'other_resources':'display/phrcontent/Other+Resources',
		'decision_aids':'display/phrcontent/Decision+Aid+for+Telemonitoring'

	]

	Map queryLocal = [
		'privacy_consent_manager':'/jsf/iframe_privacy_consent_editor.xhtml'
	]
	/**
	 * By default get language from Faces
	 */
	public SiteReferencesTool() {
		portalUrl ='http://qviz-dev.salzburgresearch.at/confluence/'
		initConfig()
	}

	/**
	 * Base portal URL
	 * @param baseUrl
	 */
	public SiteReferencesTool(String baseUrl) {
		portalUrl=baseUrl
		initConfig()
	}

	void initConfig(){
		//Config file, replace keys where necessary in maps
		//privacy_consent_manager iframe_privacy_consent_editor.xhtml
	}


	//@Override
	public String xxget(Object key) {
		String ref = portalUrl
		try{
			if(key && key instanceof String){
				//check for site page or could be on same server
				String value=  doGetSitePage(key)
				//check for local app pages
				value = value ? value : doGetLocalPage(key)
				
				if(value){
//                    if(value.contains('http')){
//                        ref =  query.get(key)
//                    }  else {
//                        ref = ref + query.get(key)
//                    }

				} else if(((String)key).contains('/')){
					//remove first
					//ref = ref + '/' + key

					if(key) ref = key.replace('//', '/')
				}
			}
		} catch(Exception e){
			println("exception "+e)
		}
		//println("get ref="+ref)
		return ref
	}
    @Override
    public String get(Object key) {
        //String ref = portalUrl
        //String ref = configGetLocalPage('portalUrl')
        LOGGER.debug("get key="+key)
        String ref=null
        try{
            if(key && key instanceof String){
                //check for site page or could be on same server
                ref=  configGetSitePage(key)
                LOGGER.debug("getconfigGetSitePage ref="+ref)


                if( !ref && ((String)key) .contains('/')){
                    if(key) ref = key.replace('//', '/')
                }
            } else {
               if( !key ) LOGGER.debug("getconfigGetSitePage key null emtpy="+key)
               if( key  && !( key instanceof String)) LOGGER.debug("getconfigGetSitePage key not string="+key)
            }
        } catch(Exception e){
            LOGGER.debug("getconfigGetSitePage error"+e)
        }
        if( !ref) return '#'
        return ref
    }
	
	protected String doGetLocalPage(String key){
		if(queryLocal.containsKey(key)){
			return queryLocal.get(key)
		} 
		return null
	}
	protected String doGetSitePage(String key){
		if(query.containsKey(key)){
			return query.get(key)
		}
		return null
	}

    protected String configGetLocalPage(String key){
        LOGGER.debug("configGetLocalPage key="+key+" val="+ConfigurationService.getInstance().getContentLink(key));
        return ConfigurationService.getInstance().getContentLink(key)
    }
    protected String configGetSitePage(String key){
        LOGGER.debug("configGetSitePage key="+key+" val="+ConfigurationService.getInstance().getContentLink(key));
        return ConfigurationService.getInstance().getContentLink(key)
    }
	/*
	 private String parseLanguage(String key){
	 if(key!=null && key.length() > 0) {
	 String [] temp = key.split("|");
	 if(temp.length > 1){
	 }
	 }
	 return "en";
	 }*/

	@Override
	public void clear() {
	}

	@Override
	public boolean containsKey(Object arg) {
		if(arg && query.containsKey(arg)) return true
		return false;
	}

	@Override
	public boolean containsValue(Object arg) {
		if(arg && query.containsValue(arg)) return true
		return false;
	}

	@Override
	public Set entrySet() {
		return query.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return query.isEmpty()

	}

	@Override
	public Set keySet() {
		return query.keySet();
	}

	@Override
	public Object put(Object key, Object value) {
		return query.put(key, value);
	}

	@Override
	public Object remove(Object arg) {
		return null;
	}

	@Override
	public void putAll(Map arg) {
		query.putAll(arg);
	}

	@Override
	public int size() {
		return query.size();
	}

	@Override
	public Collection values() {
		return query.values()
	}

}
