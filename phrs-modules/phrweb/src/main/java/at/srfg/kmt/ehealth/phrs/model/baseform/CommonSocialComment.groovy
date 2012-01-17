package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity


@Entity
public class CommonSocialComment extends CommonContentSegment{
	
   /**
    * Use refersTo, group or parent identifier to refer to particular resourceUri

    * 
    */
   public CommonSocialComment(){
	   super()
   }
   
}
