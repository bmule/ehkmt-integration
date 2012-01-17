package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Set;


import com.google.code.morphia.annotations.Entity;

@Entity
public class DirectedMessage extends BasePhrsModel {
	
	String 		messageSender
	Set<String> messageReceivers
	
	public DirectedMessage(){
		super()
	}
	
}
