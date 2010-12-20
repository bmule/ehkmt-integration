package tr.com.srdc.icardea.consenteditor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import tr.com.srdc.icardea.consenteditor.temp.CreateDB;

public class ConfigurationParser {

	private String subjectsFile;
	private String resourcesFile;
	private String actionsFile;
	private String obligationFile;
	private String consentDocuments;
	private String xacmlDocuments;
	private String schemaLocation;
	private String userResources;
	private String userSubjects;
	private String abstractPath;
	
	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public ConfigurationParser() {
		properties = new Properties();
		try {
			//properties.load(CreateDB.class.getClassLoader().getResourceAsStream("configuration.properties"));
			URL myURL= CreateDB.class.getClassLoader().getResource("configuration.properties");
		    InputStream in = myURL.openStream();
		    properties.load(in);
		} catch (IOException e) {
			System.err.println("Could not construct ConfigurationParser instance.");
			System.err.println("Malformed URL location exists.");
			e.printStackTrace(System.err);
		}

		subjectsFile = properties.getProperty("subjectsFile");
		resourcesFile = properties.getProperty("resourcesFile");
		obligationFile = properties.getProperty("obligationFile");
		schemaLocation = properties.getProperty("schemaLocation");
		consentDocuments = properties.getProperty("consentDocuments");
		xacmlDocuments = properties.getProperty("xacmlDocuments");
		actionsFile = properties.getProperty("actionsFile");
		userResources = properties.getProperty("userResources");
		userSubjects = properties.getProperty("userSubjects");
		abstractPath = properties.getProperty("abstractPath");

	}

	public String getAbstractPath() {
		return abstractPath;
	}

	public void setAbstractPath(String abstractPath) {
		this.abstractPath = abstractPath;
	}

	public String getResourcesFile() {
		return resourcesFile;
	}

	public void setResourcesFile(String resourcesFile) {
		this.resourcesFile = resourcesFile;
	}

	public String getActionsFile() {
		return actionsFile;
	}

	public String getSubjectsFile() {
		return subjectsFile;
	}

	public void setSubjectsFile(String subjectsFile) {
		this.subjectsFile = subjectsFile;
	}

	public void setActionsFile(String actionsFile) {
		this.actionsFile = actionsFile;
	}

	public String getObligationFile() {
		return obligationFile;
	}

	public void setObligationFile(String obligationFile) {
		this.obligationFile = obligationFile;
	}

	public String getConsentDocuments() {
		return consentDocuments;
	}

	public void setConsentDocuments(String consentDocuments) {
		this.consentDocuments = consentDocuments;
	}

	public String getXacmlDocuments() {
		return xacmlDocuments;
	}

	public String getUserResources() {
		return userResources;
	}

	public void setUserResources(String userResources) {
		this.userResources = userResources;
	}

	public String getUserSubjects() {
		return userSubjects;
	}

	public void setUserSubjects(String userSubjects) {
		this.userSubjects = userSubjects;
	}

	public void setXacmlDocuments(String xacmlDocuments) {
		this.xacmlDocuments = xacmlDocuments;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
}
