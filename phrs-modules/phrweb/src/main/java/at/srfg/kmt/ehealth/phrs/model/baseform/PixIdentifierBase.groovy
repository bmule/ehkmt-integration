package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants
/**
 * 
 * PhrsConstants.IDENTIFIER_TYPE_PIX_* identifier types
 * When the contact info is saved, 
 *
 */
class PixIdentifierBase implements Serializable{

	//example PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID
	String type
	String identifier
	//application domain for example
	String domain //leave null for default
	//unknown - not validated, validated by PIX server, failed - invalid
	String status
	//Use this as default in a PIX query
	boolean pixServerDefault= false
	//Can be used in a PIX query
	boolean pixServerQuery= false

	public PixIdentifierBase(){
		super()
		assignStatusUnknown()
	}

	public PixIdentifierBase(String identifier, boolean pixServerDefault, boolean pixServerQuery){
		super()
		this.identifier= identifier
		this.pixServerDefault= pixServerDefault
		this.pixServerQuery= pixServerQuery

		if(pixServerDefault) this.pixServerQuery =true
		assignStatusUnknown()

	}
	public PixIdentifierBase(String identifier, boolean pixServerDefault, boolean pixServerQuery,String type){
		super()
		this.identifier= identifier
		this.pixServerDefault= pixServerDefault
		this.pixServerQuery= pixServerQuery

		if(pixServerDefault) this.pixServerQuery =true
		assignStatusUnknown()
		this.type=type

	}
	public void assignStatusInvolid(){
		status=PhrsConstants.IDENTIFIER_STATUS_INVALID
	}
	public void assignStatusValid(){
		status=PhrsConstants.IDENTIFIER_STATUS_VALID
	}
	public boolean isStatusValid(){
		if(status && status==PhrsConstants.IDENTIFIER_STATUS_VALID) return true
		return false
	}
	public boolean isStatusUnknown(){

		if(status && status==PhrsConstants.IDENTIFIER_STATUS_UNKNOWN) return true
		return false
	}
	public void assignStatusUnknown(){
		status=PhrsConstants.IDENTIFIER_STATUS_UNKNOWN
	}
}
