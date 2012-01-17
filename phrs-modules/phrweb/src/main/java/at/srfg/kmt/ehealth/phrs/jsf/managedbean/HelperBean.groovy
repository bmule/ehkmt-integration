package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.io.Serializable

public class HelperBean  implements Serializable {    
	/**
	 * 
	 * @return
	 */
	public String getLabels(String label){
		//TODO get label for key from bundle or ?
		//should know locale
		
		
	}
	
	public String getLabels(String label, String language){
	//TODO get label for key from bundle or ?
	
	
	
	}
	/*
	public LabelLookupConverter getLabelValueConverter() {
		
		return exchangeConverter;
	}
	//http://magicmonster.com/kb/prg/java/jsf/select_one_menu_converter.html
	public List<SelectItem> getExchanges() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		List<Exchange> exchanges = exchangeService.getExchanges();
		for (Exchange exchange : exchanges) {
			SelectItem item = new SelectItem(exchange, exchange.getCode());
			selectItems.add(item);
		}
	   return selectItems;

	}
	
	<h:selectOneMenu value="#{stockBean.exchange}" converter="#{stockBean.exchangeConverter}" >
          <f:selectItems value="#{stockBean.exchanges}"/>
        </h:selectOneMenu>
    
	*/
	

}
