package at.srfg.kmt.ehealth.phrs.model.baseform

class PhrMessageLog extends BasePhrsMetadata{
	//ownerUri
	//externalReference from interop client or any other application
	//resourceUri ....unique identifier of any "stored" object across DB. Is null unless resource is stored
	//parentId = resourceUri of parent form object resourceUri. Details might include 3 vital signs associated with one form object
	//createId    ....of any created resource. Exists before object stored
	
	//code
	//status
	//createDate
	
	// weight obj X
	// type= interop message
	
	
	String messageType
	
	//Use code for HL7 Body weight and the value= 71 for the amount 

	
	 public PhrMessageLog(){
		super()
		
	 }
}
