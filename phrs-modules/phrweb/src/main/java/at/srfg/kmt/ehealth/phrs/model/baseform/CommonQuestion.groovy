package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.List

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
public class CommonQuestion extends CommonContentSegment{
  //from, to targets. 
   DirectedMessage directedMessage
   
   @Reference
   List<CommonAnswer> answers = new ArrayList<CommonAnswer>()
   
   public CommonQuestion(){
	   super()
   }
   
}
