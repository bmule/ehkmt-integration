package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date;

import org.bson.types.ObjectId;

import at.srfg.kmt.ehealth.phrs.presentation.utils.WatcherDialogMessage;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.EntityListeners;
import com.google.code.morphia.annotations.Id;

@EntityListeners(WatcherDialogMessage.class)
@Entity
public class  MessageCommon {
	@Id
	ObjectId id
	
	//ObjectId parent
	String messageThreadId
	
	//owner of this conversation, what if multiple owners?
	String ownerUri
	
	String sender
	String receiver
	Date created
	//List<String> receiverList = new ArrayList()
	String text
	

	
	ObjectId aboutResource
	String aboutResourceProperty
	String category
	String tags

	
	public MessageCommon(){
		super()
	}

}
