package at.srfg.kmt.ehealth.phrs.jsf.support;

import java.io.Serializable

import org.primefaces.model.TreeNode

public class DocumentReference implements Serializable {
	
	String name;
	String size ="10 kb";	
	String type; //external, ""
	
	String label;
	
	String labelCode;
	String description
	String content
	String fileName
	boolean codedLabel
	String refersTo
	TreeNode parent
	boolean root
	
	String resourceUri 
	
	public DocumentReference(String label, String fileName, String type,TreeNode parent) {
		this.name = label;
		this.label = label
		this.fileName = fileName;
		this.type = type;
		this.parent=parent
		init()
	}
	public DocumentReference(String label, String fileName, String type,boolean codedLabel,TreeNode parent) {
		this.name = label;
		this.label = label
		this.fileName = fileName;
		this.type = type;
		this.codedLabel=codedLabel
		this.parent=parent
		init()
	}
	
	private void init(){
		resourceUri =  Integer.toString(hashCode())
	}
	public String getUrl(){
		//if fileName contains http://
		String url = "/jsf/${fileName}"
		return url
	}
	public DocumentReference(){
		
	}


	//Eclipse Generated hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentReference other = (DocumentReference) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}