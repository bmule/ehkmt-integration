package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue;

/**
 * Queries various sources of labels
 * Can be used in JSF 
 * 
 * 
 * prefix or without prefix
 * identifier might be a URL (ontology) or i18N file
 * 
 * en|http://xxx/xfdljsdfls/
 * or http:xxx
 */
public class VocabMappingTool implements Map, Serializable {
	/**
	 * By default get language from Faces
	 */
	public VocabMappingTool() {

	}

	/**
	 * Provide language
	 * @param language
	 */
	public VocabMappingTool(String language) {

	}
	
	public VocabMappingTool(Locale locale) {

	}


	@Override
	public ModelLabelValue get(Object key) {
		String inputKey = (String)key;
		String id=null;
		String lang="en";
		
		if(inputKey!=null && inputKey.length() > 0) {
			String [] temp = inputKey.split("|");
			if(temp.length > 1){
				lang = temp[0];
				id = temp[1];
			} else {
				id = temp[0];
			}
		}
		
		return null;
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
