package tr.com.srdc.icardea.consenteditor.util;

import tr.com.srdc.icardea.consenteditor.model.Group;
import tr.com.srdc.icardea.consenteditor.model.Individual;
import tr.com.srdc.icardea.consenteditor.model.SubjectElement;

public class ModelUtil {
	 
	 public static String getName(SubjectElement se){
			String tempSubject="";
	           if(se.getGroup()!=null){
	        	    Group tempGroup=se.getGroup();
	            	tempSubject=tempGroup.getName();
	           }else{
	        	   Individual tempIndividual=se.getIndividual();
	               tempSubject=tempIndividual.getName();
	           }
	           return tempSubject;
		}

	    
	    public static String findSubjectId(SubjectElement se) {	    	
	    	if(se.getGroup()!=null){
        	    Group tempGroup=se.getGroup();
        	    return tempGroup.getId();
           }else{
        	   Individual tempIndividual=se.getIndividual();
        	   return tempIndividual.getId();
           }
	    	
	    }	    
}
