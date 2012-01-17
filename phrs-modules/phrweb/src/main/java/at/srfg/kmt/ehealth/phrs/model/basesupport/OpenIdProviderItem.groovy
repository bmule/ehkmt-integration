package at.srfg.kmt.ehealth.phrs.model.basesupport

/**
 * 
 * Load items from the configuration service
 * Used by Login forms,etc
 * There is also a specific list used by any OpenId library that needs to be
 * read by the<code> ConfigurationService</code>, 
 *
 */
class OpenIdProviderItem {

	String labelCode
	String url
	String otherAccess
	String note
	String order
	//to map a system to this provider e.g. icardea PCC domain code
	String domainCode

	Map attributes


	public OpenIdProviderItem(){
		attributes=[:]
	}
	/**
	 * 
	 * @param labelCode - for i18 and input forms
	 * @param url
	 * @param order - which order in the GUI? 0 or 1 is the default 
	 */
	public OpenIdProviderItem(String labelCode, String url,String  order){
		this.order=order
		this.url=url
		this.labelCode = labelCode
		attributes=[:]
	}
}
