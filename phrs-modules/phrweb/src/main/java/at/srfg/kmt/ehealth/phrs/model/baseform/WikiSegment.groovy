package at.srfg.kmt.ehealth.phrs.model.baseform


import com.google.code.morphia.annotations.Entity

@Entity
public class  WikiSegment extends BasePhrsModel {

	String text
	
	String format
	
	public WikiSegment(){
		
	}
	

}
