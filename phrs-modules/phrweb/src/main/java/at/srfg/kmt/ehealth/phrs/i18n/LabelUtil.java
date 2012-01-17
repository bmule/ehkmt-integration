package at.srfg.kmt.ehealth.phrs.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService;

	/*
	 <application>
		 <locale-config>
		 	<default-locale>en</default-locale>
		 	<supported-locale>de</supported-locale>
		 </locale-config>
		 <resource-bundle>
		 	<base-name>at.srfg.kmt.ehealth.phrs.i18n.Labels</base-name>
		 	<var>text</var>
		 </resource-bundle>
	 </application>
	 */

public class LabelUtil extends ResourceBundle {

	protected static final String BUNDLE_NAME = "at.srfg.kmt.ehealth.phrs.i18n.messagelabels";
	protected static final String BUNDLE_EXTENSION = "properties";
	protected static final Control UTF8_CONTROL = new UTF8Control();

	public LabelUtil() {
		// get locale from servlet thread
		Locale locale = Locale.ENGLISH;
		
		try {
			locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		} catch (Exception e){
			
		}
		//Executions.getCurrent().
		
		setParent(ResourceBundle.getBundle(BUNDLE_NAME, 
				locale, UTF8_CONTROL));
		
		
	}
	public LabelUtil(Locale locale) {
		if(locale==null)  locale = Locale.ENGLISH;
		setParent(ResourceBundle.getBundle(BUNDLE_NAME, locale, UTF8_CONTROL));
		
	}
	/*
	 * lookup
	 */
	//static VocabularyService vocabService = new VocabularyService();
	public String getLabel(String key){
		//need language ? how?
		String lang = this.getLocale().getLanguage();
		String label = VocabularyService.getTermLabel(key, lang);
		if(label !=null) return label;
		return super.getString(key);
	}
	@Override
	protected Object handleGetObject(String key) {
		
		//TODO can lookup from another source
		return parent.getObject(key);
	}

	@Override
	public Enumeration getKeys() {
		return parent.getKeys();
	}

	protected static class UTF8Control extends Control {
		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {
			// The below code is copied from default Control#newBundle()
			// implementation.
			// Only the PropertyResourceBundle line is changed to read the file
			// as UTF-8.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, BUNDLE_EXTENSION);
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					bundle = new PropertyResourceBundle(new InputStreamReader(
							stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}

	}
}
