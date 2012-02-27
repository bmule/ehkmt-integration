package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue;
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService;


/*
 http://magicmonster.com/kb/prg/java/jsf/select_one_menu_converter.html
 
 
 public class StockBean {
   ...
    public ExchangeConverter getExchangeConverter() {
        return exchangeConverter;
    }
   ...
}
 */
/*
        public class PhoneConverter implements Converter {
                
                public Object getAsObject(FacesContext context, UIComponent component, String value){
                        if(StringUtils.isEmpty(value)){
                            return null;
                        }
                        
                        PhoneNumber phone = new PhoneNumber();
                        
                        String [] phoneComps = StringUtils.split(value," ,()-");
                        
                        String countryCode = phoneComps[0];
                        
                        phone.setCountryCode(countryCode);
                        
                        if(countryCode.equals("1")){
                            String areaCode = phoneComps[1];
                            String prefix = phoneComps[2];
                            String number = phoneComps[3];
                            phone.setAreaCode(areaCode);
                            phone.setPrefix(prefix);
                            phone.setNumber(number);
                        }
                        else {
                            phone.setNumber(value);
                        }
                        
                        return phone;
               }
               
  	public LabelLookupConverter(){
		
	}
    // returns a Country object http://magicmonster.com/kb/prg/java/jsf/converter.html
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String code) {
        Country country = new Country();
        
        country.setCode(code);
     
       
        return country;
    }
 
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o instanceof Country) {
            Country c = (Country) o;
            return c.getCode();
        }
        return "";
    }             
          
 */

// @ FacesConverter( value="labelLookup" )
public class LabelLookupConverter implements Converter {
/**
 * from UI to data store...
 */
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {
		if(arg2 !=null) {
			return arg2;
		}
		return arg2;

	}
	
	/**from value to UI view **/

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
	/*	String val="";
		if(arg2!=null && arg2 instanceof ModelLabelValue){
			ModelLabelValue lv = (ModelLabelValue)arg2;
			val = lv.getId();
			
		}
		return val;*/
		//String label="";
	
		if(arg2 != null){
			String language="en";
			try {
				if (FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().getLanguage()!=null){
					language = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().getLanguage();		
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
			try {


				if( arg2 instanceof String){
							ModelLabelValue lv= VocabularyService.getTerm((String)arg2, language);
							if(lv!=null) {
								return lv.getLabel();
							}
			
				} else if( arg2 instanceof Collection  ){
					
					ModelLabelValue lv=null;
					StringBuffer sb=new StringBuffer();
					Collection<String> temp= (Collection<String>)arg2;
					for(String termId:temp){
						lv = VocabularyService.getTerm(termId, language);
						if(lv!=null && lv.getLabel()!=null) {	
							if(sb.length() > 0) sb.append(",");
							sb.append(lv.getLabel());
						}
					}
					return sb.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(arg2 != null) return arg2.toString();
		return "?";
	}
	

    
    
}