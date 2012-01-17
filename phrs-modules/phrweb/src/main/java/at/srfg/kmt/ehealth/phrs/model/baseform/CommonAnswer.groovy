package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Map

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
public class CommonAnswer extends CommonContentSegment{
	public static final String ANSWER_TYPE_TEXT="text";
	public static final String ANSWER_TYPE_RADIO="radio";
	public static final String ANSWER_TYPE_CHECK="radio";

  
   @Reference
   CommonQuestion parentQuestion 
   
   String answerType
   /**
    * the answer type determines how to 
    */
   @Embedded(concreteClass = java.util.TreeMap.class)
   Map<String,String> answerParts
   
   public CommonAnswer(){
	   super()
	   answerParts= new TreeMap<String,String>()
   }
   
}
