package at.srfg.kmt.ehealth.phrs.presentation.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.model.baseform.MessageCommon;


import com.google.code.morphia.annotations.PostPersist

/*
	@EntityListeners(WatcherDialogMessage.class)
	http://code.google.com/p/morphia/wiki/LifecycleMethods
*/
class WatcherDialogMessage {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(WatcherDialogMessage.class);
	
	@PostPersist
	public void postPersist(MessageCommon message){
		//check to from, send to desktop of user X?
		//polling
		println("postPersist WatcherDialogMessage watching message")	
	}
	
	

}
