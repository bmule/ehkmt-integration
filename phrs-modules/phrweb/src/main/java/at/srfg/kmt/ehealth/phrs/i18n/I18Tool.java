package at.srfg.kmt.ehealth.phrs.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class I18Tool {
	private static I18Tool m_instance;
	String language;
	
	private I18Tool(){
		
	}
	private I18Tool(String language){
		
	}
	
	public static I18Tool getInstance(){
		if(m_instance==null) m_instance = new I18Tool();
		return m_instance;
	}
	/*
	public static I18Tool getInstance(String language){
		if(m_instance==null) m_instance = new I18Tool(language);
		return m_instance;
	}
	 */
	public  Locale getLocale(String language){
		Locale locale = new Locale(language);
		return locale;
	}
	public  Locale getLocale(){
		Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
		return locale;
	}
	public  String getLabelFromi18Tool(String termId){
		Locale locale= getLocale();
		if(locale!=null) return locale.getLanguage();
		String label = getLabelFromi18Tool(termId,getLocale());
		return label;
		
	}
	public  String getLabelFromi18Tool(String termId, Locale locale){
		String label=null;
		//resources folder but under default classes 
		if(termId != null && termId.length()>1 ){
			try{
				if(locale==null) locale = new Locale("en");
				ResourceBundle introLabels = ResourceBundle.getBundle("Messagelabels", locale);
				String theTermId=termId;
				if(theTermId.contains("/")) {
					theTermId = normalizeUrl(theTermId);
				} 
				if(introLabels.containsKey(theTermId)) label = introLabels.getString(theTermId);		
			} catch(Exception e){
				System.out.println("exception "+e);
			}
		}
		return label;
		
	}
	/**
	 * Removes relevant Namespaces e.g. http://www.icardea.at/ or 'http://', replace '/' with '_'
	 * Customise labels in I18 files
	 * http://www.icardea.at/phrs/instances/Drug --> phrs_instances_Drug
	 * @param input
	 * @return
	 */
	public String normalizeUrl(String input){
		String out=input;
		if(out != null && out.contains("/") ){
			//TODO replace other namespaces
			out = out.replace("http://www.icardea.at/", "");
			out = out.replace("http://", "");
			out = out.replace("/", "_");
		}
		return out;
	}
	public  String getLabelFromi18Tool(String termId, String language){
		String label=null;
		
		try{
			if(language==null) language="en";
			Locale locale = new Locale(language);
			label = getLabelFromi18Tool(termId,locale);	
		} catch(Exception e){
			//println("ex "+e)
		}
	
		return label;
		
	}
}
