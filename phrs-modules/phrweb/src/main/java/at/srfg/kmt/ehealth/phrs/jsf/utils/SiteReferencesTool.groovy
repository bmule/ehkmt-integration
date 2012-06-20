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

 */
public class SiteReferencesTool implements Map, Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

   // Map<String,String> query
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
    /*
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

		'useful_links':'display/phrcontent/Links',// display/phrcontent/Links

		'other_resources':'display/phrcontent/Other+Resources',
		'decision_aids':'display/phrcontent/Decision+Aid+for+Telemonitoring'

	]
    */
	Map queryLocal = [
		'privacy_consent_manager':'/jsf/iframe_privacy_consent_editor.xhtml'
	]
	/**
	 * By default get language from Faces
	 */
	public SiteReferencesTool() {

		initConfig()
	}


	void initConfig(){
	}


    @Override
    public String get(Object key) {

        String ref=null
        try{
            if(key && key instanceof String){
                //check for site page or could be on same server
                ref = configGetSitePage(key)

            } else {
               if( !key ) LOGGER.debug("get(Object key) key null emtpy="+key)
               if( key  && !( key instanceof String)) LOGGER.debug(" get(Object key) key not string="+key)
            }
        } catch(Exception e){
            LOGGER.debug("getconfigGetSitePage error "+e)
        }
        LOGGER.debug('**ref='+ref+' key='+key)
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
		return configGetSitePage(key)
	}

    protected String configGetLocalPage(String key){
        String value= ConfigurationService.getInstance().getContentLink(key)

        return  value    }

    protected String configGetSitePage(String key){
        String value= ConfigurationService.getInstance().getContentLink(key)

        return  value
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
        //if(arg && query.containsKey(arg)) return true
		return false;
	}
    /**
     * @param arg
     * @return
     */
	@Override
	public boolean containsValue(Object arg) {

        //if(arg && query.containsValue(arg)) return true
		return false;
	}

	@Override
	public Set entrySet() {
        return null
		//return query.entrySet();
	}

	@Override
	public boolean isEmpty() {
        return false
		//return query.isEmpty()

	}

	@Override
	public Set keySet() {
        return null
        //return query.keySet();
	}

	@Override
	public Object put(Object key, Object value) {
        //if(key instanceof  String && value instanceof String)  {
        //    return query.put(key, value);
        //}
		//return query;

        return null

	}

	@Override
	public Object remove(Object arg) {
		return null;
	}

	@Override
	public void putAll(Map arg) {
		//query.putAll(arg);
	}

	@Override
	public int size() {
		//return query.size();
	}

	@Override
	public Collection values() {
		//return query.values()
	}

}
