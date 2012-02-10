package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Indexed
@Entity
class BasePhrOpenId {
	@Id
	ObjectId id

	@Indexed
	String ownerUri;
	String creatorUri;
	Date createDate;
	Date modifyDate;
	//not needed if this is an embedded object, keep in case the object is not embedded
	String resourceUri;
	String type;


	String identifier;
	String identity;
	String claimedId;
	boolean claimedIdAuthenticated =false

	String protocolId;
	String protocolNamespace;
	String protocolRole;
	
	Map<String,String> sregMap
	Map<String,String> axschemaMap
	Map<String,String> attributes
	
	boolean defaultHealthId=false

	public void addAttributes(Map map){
		if(map){
			if(!attributes) attributes = [:]
			attributes.putAll(map)
		}
	}

	public BasePhrOpenId(){
		attributes=[:]
		axschemaMap=[:]
		sregMap=[:]
	}
	public BasePhrOpenId(Map openIdAttributes){
		if(!attributes) attributes=[:]
		if(openIdAttributes) attributes.putAll(openIdAttributes)
	}
/**
 * Gets attribute from either attribute map, axschemaMap or sreqMap
 * @param paramName
 * @return
 */
	public String getAttribute(String paramName){
		String value=null
		if(paramName){			
			if( attributes && attributes.containsKey(paramName)){
				 value= attributes.get(paramName)
			}
			value= getAxschemaAttribute(paramName)
			value= getSregAttribute(paramName)	
		}
		return value
	}
	
	public String getAxschemaAttribute(String paramName){
		if(paramName && axschemaMap && axschemaMap.containsKey(paramName)) return axschemaMap.get(paramName)
		return null
	}
	public String getSregAttribute(String paramName){
		if(paramName && sregMap && sregMap.containsKey(paramName)) return sregMap.get(paramName)
		return null
	}
}
