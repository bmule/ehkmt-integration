package at.srfg.kmt.ehealth.phrs.jsf.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;


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
public class CountryConverter implements Converter {
	
	public CountryConverter(){
		
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
}