package at.srfg.kmt.ehealth.phrs.presentation.services;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.i18n.I18Tool
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient

public class  VocabularyService implements Serializable{

	//private final static Logger logger = LoggerFactory.getLogger(VocabularyService.class);


	/**
	 * Check incoming modelMain for missing codes. A query is performed by query tag
	 * and the vocab item id is matched against the object code from the model 
	 * @param vocabQueryTag
	 * @param modelMain
	 * @param language
	 * @return  
	 */
	public static List extendModelWithUnselectedData(String vocabQueryTag, Collection modelMain, Class clazz){

		if( ! modelMain)  modelMain = []
		List<ModelLabelValue> vocab =
				VocabularyService.getTermsByTag(vocabQueryTag, null) //language doesn't matter

		//Set<ModelLabelValue> toAdd = []

		boolean found=false
		vocab.each() {  lv ->
			found=false
			for(def mm:modelMain){
				//check one for null or blank
				if( mm.code && lv.id && (lv.id == mm.code)) {
					found=true
				}
				if(!mm.code){
					println("error null code retrieved drom DB")
				}
			}
			if(!found){
				BasePhrsModel obj = clazz.newInstance()
				obj.code = lv.id
				obj.status = PhrsConstants.SELECTION_NO_ANSWER
				//obj.atest="test123"
				modelMain.add(obj)
			}
		}
		return modelMain
	}

	/**
	 * Query against skos:related
	 * @param tag
	 * @param language
	 * @return
	 */
	public static Collection<ModelLabelValue> getTermsByTag(String tag, String language){
		//TODO do special lookup for particular tags

		Collection<ModelLabelValue> lvs	=new ArrayList<ModelLabelValue>()
		Collection<Map<String, Collection<String>>> colmaps  = PhrsStoreClient.getInstance().getTermResourcesBySkosRelated(tag,language)

		for(Map<String, Collection<String>> map:colmaps){
			ModelLabelValue tempLv = transformPropertyMapToLabelValue(map,language)
			if(tempLv &&  tempLv.getId()){
				//sorting
				if( ! tempLv.sortOrder) tempLv.sortOrder=tempLv.label
				lvs.add(tempLv)
			}
		}

		return lvs
	}

	public static Collection<String> getTermValuesByTag(String tag, String language){

		Collection<Map<String, Collection<String>>> colmaps  =
				PhrsStoreClient.getInstance().getTermResourcesBySkosRelated(tag,language)
		Set<String> set= new HashSet()

		for(Map<String, Collection<String>> map:colmaps){
			//set.add(map.keySet())
            set.addAll(map.keySet())
		}
		return set
	}

	/*
	 * 
	 * @param propertyMap
	 * @param language
	 * @return
	 static Collection<ModelLabelValue> xtransformPropertyMapsToLabelValues(Collection propertyMaps,String language){
	 Collection<ModelLabelValue> lvs = new ArrayList<ModelLabelValue>()
	 if(propertyMaps){
	 propertyMaps.each() {key,value ->
	 //ModelLabelValue lv = transformPropertyMapToLabelValue()
	 if(key != PhrsConstants.RDF_MAP_KEY_SUBJECT) {
	 //TODO remove null?
	 if(value){
	 ModelLabelValue lv = new ModelLabelValue(key,value,language)	
	 lvs.add(lv)
	 }
	 }
	 }
	 }
	 return lvs
	 }*/
	public static ModelLabelValue transformPropertyMapToLabelValue(Map propertyMap,String language){
		ModelLabelValue lv =null

		if(propertyMap){
			def res1 = propertyMap.containsKey(PhrsConstants.RDF_MAP_KEY_SUBJECT) ? propertyMap.get(PhrsConstants.RDF_MAP_KEY_SUBJECT): null
			String key=null
			//depending on the implementation, the results might be string or collection

			if( res1){
				if(res1 instanceof Collection){
	
					if( res1 && ! ((Collection)res1).isEmpty()) {
						key = ((Collection)res1).iterator().hasNext() ? ((Collection)res1).iterator().next() : null
					}

				} else {
					key = res1
				}
			}
			if(key){
				//not PhrsConstants.SKOS_RELATED
				def res2 = propertyMap.containsKey(PhrsConstants.SKOS_PROPERTY_PREFERRED_LABEL) ? propertyMap.get(PhrsConstants.SKOS_PROPERTY_PREFERRED_LABEL) : null
				String value =null

				if( res2){
					if(res2 instanceof Collection){
						if( res2 && !  ((Collection)res2).isEmpty()) {
							value = ((Collection)res2).iterator().hasNext() ? ((Collection)res2).iterator().next() : null
						}
						//value = ( res2 && ! res2.isEmpty())  ? res2.getAt(0) : null
					} else {
						value = res2
					}
					//String lang = language && language instanceof String : language ? language.getLanguage():null
					if(key && value) lv = new ModelLabelValue(key,value,language ? language:null)
				}
			}

		}
		return lv
	}
	/**
	 * 
	 * @param tagList - match either
	 * @param language
	 * @return
	 */
	public static Collection<ModelLabelValue> getTermsByTags(Collection tagList, String language){
		Collection<ModelLabelValue> allLvs = new ArrayList<ModelLabelValue>()

		tagList.each() { tag ->
			Collection<ModelLabelValue> termLvs= getTermsByTag(tag,language)
			if(termLvs && ! termLvs.isEmpty()) allLvs.addAll(termLvs)
		}
		return allLvs
	}
	public static Collection<String> getTermLabelsById(Collection idList, String language){
		Collection<String> allLabels = new ArrayList<String>()

		idList.each() { termId ->
			ModelLabelValue lv= getTerm(termId,language)
			if(lv){
				if(lv.getLabel())  allLabels.add(lv.getLabel())
				else allLabels.add(lv.getId())
			}
		}
		return allLabels
	}

	/**
	 *  Performs lookup into I18 labels and vocabulary source
	 * @param termId
	 * @param language
	 * @return
	 */
	public static ModelLabelValue getTerm(String termId, String language){

		String label = I18Tool.getInstance().getLabelFromi18Tool(termId,language)
		ModelLabelValue lv

		if(label) {
			lv = new ModelLabelValue(termId,label)
		}else {
			Map map = PhrsStoreClient.getInstance().getResource(termId,language)
			lv = transformTermPropertyMap(map,language)
		}

		return lv
	}

	/**
	 * Same as  getTerm(String termId, def language)
	 * @param termId
	 * @param language
	 * @return
	 */
	public static ModelLabelValue getTermLabelValue(String termId, String language){
		return getTerm(termId,language)
	}
	/**
	 * Performs lookup into I18 labels and vocabulary source
	 * @param termId
	 * @param language
	 * @return
	 */
	public static String getTermLabel(String termId, String language){

		ModelLabelValue lv = VocabularyService.getTerm(termId,language)

		return lv ? lv.label : termId ? termId :"?"
	}
	/**
	 * 
	 * @param map
	 * @param language
	 * @return
	 */
	public static ModelLabelValue transformTermPropertyMap(Map map,String language){
		ModelLabelValue lv

		if(map){

			def idSet = map.containsKey(PhrsConstants.RDF_MAP_KEY_SUBJECT) ? map.get(PhrsConstants.RDF_MAP_KEY_SUBJECT) : null


			if(idSet){
				String id
				if(idSet instanceof Collection){
					if( idSet && !  ((Collection)idSet).isEmpty()) {
						id = ((Collection)idSet).iterator().hasNext() ? ((Collection)idSet).iterator().next() : null
					}
					//value = ( res2 && ! res2.isEmpty())  ? res2.getAt(0) : null
				} else {
					id = idSet
				}
				if(id){
					String value
					def labelSet = map.containsKey((PhrsConstants.SKOS_PROPERTY_PREFERRED_LABEL)) ? map.get(PhrsConstants.SKOS_PROPERTY_PREFERRED_LABEL) : ".."+id
					if( labelSet){
						if(labelSet instanceof Collection){
							if( labelSet && !  ((Collection)labelSet).isEmpty()) {
								value = ((Collection)labelSet).iterator().hasNext() ? ((Collection)labelSet).iterator().next() : null
							}
							//value = ( res2 && ! res2.isEmpty())  ? res2.getAt(0) : null
						} else {
							value = labelSet
						}
						if(id && value) lv = new ModelLabelValue(id,value,language)

						//String label = labelSet && ! labelSet.isEmpty() ? (String)(labelSet) :""
						//lv = new ModelLabelValue(id, label, language)
					}
				}
			} else {
				//
			}
		}

		return lv
	}

	/*
	 def static displayJsonMap(String jsonString) {
	 net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(jsonString);
	 Iterator i = obj.entrySet().iterator();
	 while (i.hasNext()) {
	 Map.Entry e = (Map.Entry) i.next();
	 System.out.println("Key: " + e.getKey());
	 System.out.println("Value: " + e.getValue());
	 }
	 } */
	public static Collection<ModelLabelValue> getVocabTaggedBy(String tag,int sortType){
		return getVocabTaggedBy(tag,sortType,false, null);
	}
	public static Collection<ModelLabelValue> getVocabTaggedBy(String tag,int sortType,boolean choose){
		return getVocabTaggedBy(tag,sortType,choose, null);
	}
	public static Collection<ModelLabelValue> getVocabTaggedBy(String tag,int sortType,String language){
		return getVocabTaggedBy(tag,sortType, false,language);
	}
	public static Collection<ModelLabelValue> getVocabTaggedBy(String tag,String language){
		return getVocabTaggedBy(tag,0, false,language);
	}
	/**
	 * 
	 * @param tag
	 * @param sortType
	 * @param choose
	 * @param language
	 * @return
	 */
	public static Collection<ModelLabelValue> getVocabTaggedBy(String tag,int sortType,boolean choose,String language){
		Collection<ModelLabelValue>  list=[]
		//check for special tag ordering
		Collection<ModelLabelValue> temp = VocabularyEnhancer.specialSortedLabelValues(tag,language)
		if(temp && ! temp.isEmpty()){
			//special sorted labels
		} else {
			// lookup tag and sort alphabetically, but need language
			temp = this.getTermsByTag(tag, language)
			if(temp) {
				temp.sort{ ((ModelLabelValue)it).sortOrder}
			}
		}
		if(choose){
			ModelLabelValue lv = new ModelLabelValue()
			lv.id=PhrsConstants.SELECTION_BOX_PLEASE_CHOOSE
			//lv.label=Labels.getLabel(PhrsConstants.SELECTION_BOX_PLEASE_CHOOSE);//lookup from I18
			lv.label= I18Tool.getInstance().getLabelFromi18Tool(PhrsConstants.SELECTION_BOX_PLEASE_CHOOSE,language)
			lv.sortOrder=lv.label
			list.add(lv)
			if(temp && !temp.isEmpty()) list.addAll(temp)

		} else {
			list = temp
		}

		return list

	}
	/**
	 * @deprecated 
	 * @param tag
	 * @param sortType
	 * @param choose
	 * @param language
	 * @return
	 */
	public static Collection getVocabValuesTaggedBy(String tag,int sortType,boolean choose,String language){
		Collection list=[]
		//check for special tag ordering
		Collection<String> temp = VocabularyEnhancer.alternativeVocabValues(tag)

		if(temp && ! temp.isEmpty()){
             //
		} else {
			// lookup tag and sort alphabetically, but need language
			temp = this.getTermsByTag(tag, language)

		}
		if(choose){
			list.add(PhrsConstants.SELECTION_BOX_PLEASE_CHOOSE)
			if(temp && !temp.isEmpty()) list.addAll(temp)

		} else {
			list = temp
		}

		return list

	}
	/**
	 * gets a label for a code or list of codes;  and format them simply as a string
	 *
	 * @param code
	 * @param label  string or stringbuffer
	 * @param delimiter use the delimiter, otherwise a space is used
	 *
	 * @param merge - merge this with incoming label using the delimiter
	 * @param language
	 * returns stringbuffer
	 */

	public static def getVocabLabelFormatted(String code, def label,String delimiter,boolean merge, String language){

		StringBuilder origLabel

		if( ! label) origLabel= new StringBuilder()
		else if(label instanceof StringBuilder) origLabel = label
		else  origLabel = new StringBuilder(label)

		String foundLabel

		String theDelim = delimiter ? delimiter : " "

		if(code){

			// foundLabel = Labels.getLabel(code)
			foundLabel =  I18Tool.getInstance().getLabelFromi18Tool(code,language)
			//continue ..check Vocabularies
			if(! foundLabel) {
				ModelLabelValue lv = VocabularyService.getTerm(code,language)
				//use code as label
				foundLabel = lv ? lv.label :code
			}

			//
			if(foundLabel){

				if(merge) {
					//add delimiter if something already
					if( origLabel && origLabel.length() > 1) origLabel.append(theDelim)
					origLabel.append(foundLabel)

				} else {
					origLabel.append(foundLabel)
				}
			}

		}

		return origLabel
	}
	/*
	 * returns blank if nothing found so that the UI does not break
	 * Write label in UI when code is found
	 * This only write the label, the value is already there.
	 */
	public static String getLabelsFormatted(def codes) {
		return getLabelsFormatted(codes,'en')
	}
	public static String getLabelsFormatted(def codes, String language) {
		def labels //stringbuffer or string
		try{
			if(codes){
				if(codes instanceof Collection){
					Collection collectionCodes = (Collection)codes
					if(collectionCodes){
						collectionCodes.each(){ theCode->
							labels =
									getVocabLabelFormatted(
									theCode,
									labels,
									PhrsConstants.DELIMITER_VOCAB,
									true,language)
							//if(label && label.size()>3) {
							//	label <<= '.'
							//}
						}
					}
				} else {
					String code = (String)codes
					labels = getVocabLabelFormatted(
							code,
							labels,
							null,
							false,language)
				}

			}
		} catch(Exception e){
			println("error "+e)
		}

		if(labels) return labels.toString()

		return ""

	}
	/**
	 * does NOT include "please choose" in list
	 * @param tag
	 * @param language
	 * @return
	 */
	public static List getVocabTaggedByAsStrings(String tag,String language){
		return getVocabTaggedByAsStrings(tag,false,null,language)
	}
	public static List getVocabTaggedByAsStrings(String tag){
		return getVocabTaggedByAsStrings(tag,false,null,null)
	}
	public static List getVocabTaggedByAsStrings(String tag,boolean choose,String language){
		return getVocabTaggedByAsStrings(tag,choose,null,language)
	}
	/**
	 * 
	 * @param tag
	 * @param additionalSelections
	 * @param language
	 * @return
	 */

	public static List getVocabTaggedByAsStrings(String tag,Collection additionalSelections,String language){
		getVocabTaggedByAsStrings(tag,false,additionalSelections,language)
	}
	/**
	 * 
	 * @param tag
	 * @param choose
	 * @param additionalSelections
	 * @param language
	 * @return
	 */
	public static List getVocabTaggedByAsStrings(String tag,boolean choose,Collection additionalSelections,String language){
		LinkedHashSet set=[]

		Collection<ModelLabelValue> lvs =  VocabularyService.getVocabTaggedBy(tag,1,choose, language);
		if(lvs) {
			lvs.each(){lv ->
				set.add(lv.getId())
				//println("lv "+lv.getId())
			}
		}
		if(additionalSelections){
			set.addAll(additionalSelections)//new HashSet(additionalSelections))
		}

		return set.asList()
	}


}
