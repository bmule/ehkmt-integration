package at.srfg.kmt.ehealth.phrs.model.baseform

class PixIdentifierBase implements Serializable{

	String idType
    
    String part1
    String part2
	String identifier
	
	String domain 
	
	String status

    boolean verified

    String pid

	boolean pixServerQuery= false

}
