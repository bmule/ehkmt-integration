package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity

@Entity
public class  ObsVitalsBodyHeight extends CommonModelProps {

	Double bodyHeight //store and retrieve from separate resource, not the body weight resource	
	String measurementUnit
	
	
	public ObsVitalsBodyHeight(){
		super();
		eventActivated=	Boolean.FALSE
	}
}
