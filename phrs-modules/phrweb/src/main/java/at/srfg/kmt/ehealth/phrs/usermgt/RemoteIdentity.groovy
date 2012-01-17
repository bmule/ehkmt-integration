package at.srfg.kmt.ehealth.phrs.usermgt

import java.io.Serializable

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Embedded

@Embedded
class RemoteIdentity implements Serializable{
	
	String category
	String provider
	
	String protocolId
	String protocolNamespaceId
	
	Map<String,String> attributes
	
	public RemoteIdentity(){
		attributes=[:]
		category=PhrsConstants.IDENTIFIER_CATEGORY_CLINICAL
		provider=PhrsConstants.IDENTIFIER_PROVIDER_ICARDEA	
		
		
	}
	public RemoteIdentity(String category,String provider, String protocolId, String protocolNamespaceId, Map attributes){

		this.category=category ? category :  PhrsConstants.IDENTIFIER_CATEGORY_CLINICAL
		this.provider = provider ? provider : PhrsConstants.IDENTIFIER_PROVIDER_ICARDEA
		this.protocolId = protocolId
		this.protocolNamespaceId = protocolNamespaceId
		this.attributes= attributes ? attributes : [:]
		
				
	}
	
}
