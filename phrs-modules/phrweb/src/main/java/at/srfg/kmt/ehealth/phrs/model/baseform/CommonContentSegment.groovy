package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.TreeMap


import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Property

@Entity
public class CommonContentSegment extends BasePhrsModel{

	/**
	 * Use BasePhrsModel {status, label, description, tags}
	 */
   
   /**
	* semantically separated
	*/
   @Embedded
   EmbeddedWikiSegment wikiTexts
   /**
    * can be used to include multiple choice answers,radio, check.
    * Key can be vocabulary or I18 key
    * TreeMap preserves order froms form
    */
   @Property(concreteClass = java.util.TreeMap.class)
   TreeMap<String,String> choices = new  TreeMap<String,String>()
   
   @Property(concreteClass = java.util.TreeMap.class)
   TreeMap<String,Boolean> choicesBoolean = new  TreeMap<String,Boolean>()
   
   public CommonContentSegment(){
	   super()
   }
   
}
