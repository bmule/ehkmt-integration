package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Embedded
/**
 * 
 * PhrsConstants.IDENTIFIER_TYPE_PIX_* identifier types
 * When the contact info is saved, the validity of the PIX identifier is determined from the PIX server 
 *
 */
@Embedded
class PixIdentifier extends PixIdentifierBase{


	public PixIdentifier(){
		super()
	}
	
	public PixIdentifier(String identifier, boolean pixServerDefault, boolean pixServerQuery){
		super(identifier,pixServerDefault,pixServerQuery)
	
	}
	public PixIdentifier(String identifier, boolean pixServerDefault, boolean pixServerQuery,String type){
		super(identifier,pixServerDefault,pixServerQuery,type)
	
	}
}
