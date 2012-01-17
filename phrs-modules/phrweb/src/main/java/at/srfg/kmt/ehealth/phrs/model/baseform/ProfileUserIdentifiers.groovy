package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity
/**
 * 
 * A means to collect identifiers for each organization
 * 
 * PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID //PhrsConstants.PROTOCOL_ID_NAME;
 * PhrsConstants.IDENTIFIER_TYPE_PIX_DEVICE_SERIAL_NUMBER
 * PhrsConstants.IDENTIFIER_TYPE_PIX_HOSPITAL_ID
 * PhrsConstants.IDENTIFIER_TYPE_PIX_PHRS_PIN
 * 
 * Organization
 * IDENTIFIER_TYPE_ORGANIZATION_ANY
 * IDENTIFIER_TYPE_ORGANIZATION_DEFAULT
 * 
 * Checks ConfigurationService for using test protocolIDs in the User Interface
 * There might be a scheme to match test patients to a particular user etc
 * 
 * Unique where 
 * ownerUri, identifierType, namespace, identifier
 * or ownerUri, identifierType, identifier
 * 
 */
@Entity
public class ProfileUserIdentifiers extends BasePhrsMetadata{
	
	String identifierType
	//use  local code that is use derive the IHE PCC message
	//String organizationDomainHL7
	
	//either the hospital or ANY
	String domainLocalCode
	String namespace
	
	String identifier
	
	public ProfileUserIdentifiers(){
		super();
			
		
	}
	public void makeProtocolId(String ownerUri,String protocolId,String protocolNamespace,String organizationLocalCode){
		this.ownerUri=ownerUri
		this.identifier=protocolId
		this.namespace=protocolNamespace
		this.identifierType=PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID
	
		this.domainLocalCode=organizationLocalCode
		
	}
	
}
