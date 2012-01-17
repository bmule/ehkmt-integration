package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Embedded

@Embedded
class EmbeddedWikiSegment {

   //String ownerUri
   //String creatorUri
   String type
   String tags
   String categories
   String text
   String format
  
   public EmbeddedWikiSegment(){
	   
   } 
   
}
