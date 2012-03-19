package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Embedded
/**
 *  
 * PhrsConstants.IDENTIFIER_TYPE_PIX_* identifier types
 * When the contact info is saved, the validity of the PIX identifier is determined from the PIX server 
 *
 */
import com.google.code.morphia.annotations.Entity

@Entity
class PixIdentifier extends BasePhrsModel{
    //clean namespace
    String namespaceBase
    //includes extra notations &....
    String namespaceFull

    //use inherited type

    String idType
    //join the prefix with the identifier to compose the Pix Query
    //Model:xxx serial:
    String idPrefix
    String identifier


	public PixIdentifier(){
		super()
	}
	

}
