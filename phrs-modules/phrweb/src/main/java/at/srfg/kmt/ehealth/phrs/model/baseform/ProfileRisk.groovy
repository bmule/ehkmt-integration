package at.srfg.kmt.ehealth.phrs.model.baseform


import java.util.List
import java.util.Map

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity

@Entity
public class  ProfileRisk extends CommonModelProps {

	/**
	 * BasePhrsModelcode as risk type and using Interop Code Uri
	 * BasePhrsModel.status - whether this is risk is active or not
	 */
	//User choose check boxes diet,
	Collection<String> treatmentTypes
	Collection<String> medicationTypes

	String answerQuestion_1
	String answerQuestion_2
	String answerQuestion_3
	String answerQuestion_4

	Collection<String> answerSet01Question
	Collection<String> answerSet02Question
	Collection<String> answerSet03Question
	Collection<String> answerSet04Question
	/**
	 * key = code of should be UMLS or other Interoperability code
	 * value = from vocabulary
	 */
	Map<String,String> properties

	Map<String,Collection<String>> propertyLists

	 
	public Collection<String> getSmokeType(){
		Collection<String> col
		if(propertyLists && propertyLists.containsKey('smokeType')){
			col= propertyLists.get('smokeType') 
		} 
		if(col==null) col=  []
		
		return []

	}

  	public Collection<String> getSmokeTypes(){
		return getSmokeType()
	} 
 	public void setSmokeTypes(Collection<String> vals){
		setSmokeType(vals)
	}
	public void setSmokeType(Collection<String> vals){

		if(vals) {

			//println('setSmokeType 1. answerSet01Question  ='+answerSet01Question.smokeType )

			if(!propertyLists) propertyLists=[:]
			List list = new ArrayList<String>();
			list.addAll(vals)
			propertyLists.put('smokeType',list)

		}

	}

	public String getSmokeDuration(){
		String val
		if(properties && properties.containsKey('smokeDuration')){
			//val=properties.smokeDuration ? properties.smokeDuration :null
			val=properties.containsKey('smokeDuration') ? properties.get('smokeDuration') :null
		}

		return val
	}
	public String getSmokeQuantity(){
		String val

		if(properties && properties.containsKey('smokeQuantity')){
			//val=properties.smokeQuantity ? properties.smokeQuantity :null
			val=properties.containsKey('smokeQuantity') ? properties.get('smokeQuantity') :null
		}

		return val
	}
	public void setSmokeQuantity(String val){
		if(!properties) properties=[:]
		properties.put('smokeQuantity',val)
		//properties.smokeQuantity= val

	}

	public void setSmokeDuration(String val){
		if(!properties) properties=[:]

		properties.put('smokeDuration',val)
		//properties.smokeDuration= val
		//println('setSmokeDuration ='+properties.smokeDuration)
	}


	public ProfileRisk(){
		super()
		properties  = new HashMap<String,String>()
		propertyLists = new HashMap<String,Collection<String>>()

		treatmentTypes = new ArrayList<String>();
		medicationTypes= new ArrayList<String>();

		answerSet01Question = new ArrayList<String>()
		answerSet02Question = new ArrayList<String>()
		answerSet03Question = new ArrayList<String>()
		answerSet04Question = new ArrayList<String>()

		category = PhrsConstants.HL7V3_CODE_CATEGORY_RISK

	}
	/*
	treatmentTypes = new HashSet<String>();
	medicationTypes= new HashSet<String>();
	answerSet01Question = new HashSet<String>()
	answerSet02Question = new HashSet<String>()
	answerSet03Question = new HashSet<String>()
	answerSet04Question = new HashSet<String>()
	*/


}
