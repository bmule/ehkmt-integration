package at.srfg.kmt.ehealth.phrs.model.basesupport

import org.bson.types.ObjectId

import at.srfg.kmt.ehealth.phrs.model.baseform.ICommonProps

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

@Entity
class PropertyValue  implements ICommonProps {
	@Id
	ObjectId id

	Boolean deleted
	/*

	public Boolean isDeleted() {
		// TODO Auto-generated method stub
		return deleted;
	}*/
	String ownerUri
	String creatorUri
	
	String resourceUri
	String createDate
	String modifyDate
	
	String parentId //form item or ? 
	String groupId
	
	//String context
	//String aboutType

	String type
	String resourceStatus
	/**
	 * code is "property"
	 */
	String code
	

	/**
	 * code is "property" and content is the value
	 */
	public String getProperty(){
		return code
	}
	public void setProperty(String property){
		code = property
	}
	
	
	String content //could store String value and details into attributes
	/**
	 * Value is formatted and attributes are stored in attributes map e.g.
	 * Blood pressure value="systolic / diastolic", attribute: systolic and diastolic
	 */
	Boolean formattedValue
	String formaterId
	
	@Embedded
	Map<String,String> attributes
	/**
	 * key=relation, value=text or reference (a resourceUri or )
	 */
	@Embedded
	Map<String,String> annotations
	
	//String tags
	//extra besides content, this is not the main query param
	String note
	
	public PropertyValue(){
		attributes= [:]
		annotations= [:]
		
	}

}
