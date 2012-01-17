package at.srfg.kmt.ehealth.phrs.model.basesupport

import java.util.Map;
/**
 * 
 * Load items from the configuration service
 * Used by Login forms,etc
 * Used for PCC or GUI
 *
 */
class DomainSystemCode {
	String labelCode
	String domainCode
	String synonym
	Map attributes

	public DomainSystemCode(){
	}
	/**
	 * 
	 * @param labelCode - for i18 and input forms
	 * @param domainCode
	 * @param synonym
	 */
	public DomainSystemCode(String labelCode, String domainCode, String synonym ){
		this.labelCode=labelCode
		this.domainCode=domainCode
		this.synonym=synonym
		attributes=[:]
	}
	/**
	 * 
	 * @param labelCode - for i18 and input forms
	 * @param domainCode becomes the synonym also
	 */
	public DomainSystemCode(String labelCode, String domainCode ){
		this.labelCode=labelCode
		this.domainCode=domainCode
		this.synonym=labelCode
		attributes=[:]
	}
}
