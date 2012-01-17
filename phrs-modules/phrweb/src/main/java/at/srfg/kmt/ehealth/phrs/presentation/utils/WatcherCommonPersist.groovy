package at.srfg.kmt.ehealth.phrs.presentation.utils

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

import com.google.code.morphia.annotations.PostPersist

/*
	@EntityListeners(WatcherCommonPersist.class)
	http://code.google.com/p/morphia/wiki/LifecycleMethods
*/
class WatcherCommonPersist {
	
	//private final static Logger logger = LoggerFactory.getLogger(BasePhrFormComposer.class);
	
	@PostPersist
	public void postPersist(CommonModelProps resource){
		//check to from, send to desktop of user X?
		//polling
		println("CommonModelProps postPersist")	
	}
	
	

}
