package at.srfg.kmt.ehealth.phrs.jsf.utils

class SeriesItem {
	String category
	String categoryPrefix
	String categorySuffix
	
	List columnLabels=[]
	List columnValues = []

	
	public SeriesItem(String category){
		this.category = category
	}

	public SeriesItem(String category, List values){
		this.category=category
		this.values = values
	}
	
	public void addValue(def value){
		columnValues.add(value)
	}
	public int getColumnNumber(){
		return columnValues.size()
		
	}
	
	
	
	
}
