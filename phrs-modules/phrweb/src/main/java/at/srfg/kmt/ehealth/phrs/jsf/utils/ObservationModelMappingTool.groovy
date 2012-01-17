package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;

/**
 * 
 * Make models available through map tool
 * used by JSF, etc
 *
 */
public class ObservationModelMappingTool implements Map, Serializable {
	private UserService userService=null;
	/**
	 * By default get language from Faces
	 */
	public ObservationModelMappingTool(UserService userService) {
		this.userService = userService;
	}
	/**
	 * refresh and cache the model by this name
	 * @param modelKey
	 */
	public void refresh(String modelKey){
		
	}
	/**
	 *  key loads particular model, cache it, but make a way to refresh it coz the 
	 *  view mode will store the same mapper...
	 * 
	 */
	@Override
	public ModelLabelValue get(Object key) {
		
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
		return false;
	}

	@Override
	public boolean containsValue(Object arg) {
		return false;
	}

	@Override
	public Set entrySet() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set keySet() {
		return null;
	}

	@Override
	public Object put(Object key, Object value) {
		return null;
	}

	@Override
	public Object remove(Object arg) {
		return null;
	}

	@Override
	public void putAll(Map arg) {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection values() {
		return null;
	}

}
