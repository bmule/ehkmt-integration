package tr.com.srdc.icardea.consenteditor.temp;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import tr.com.srdc.icardea.consenteditor.ConfigurationParser;
import tr.com.srdc.icardea.consenteditor.model.ActionElements;
import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;
import tr.com.srdc.icardea.consenteditor.model.ResourceElements;
import tr.com.srdc.icardea.consenteditor.model.SubjectElements;
import tr.com.srdc.icardea.consenteditor.util.JAXBUtil;

public class CreateDB {

	public static void main(String[] args) {
		ConfigurationParser confParser = new ConfigurationParser();
		
		URL myURL = CreateDB.class.getClassLoader().getResource(
				confParser.getConsentDocuments());
		File directory = null;
		try {
			directory = new File(myURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("tr.com.srdc.icardea.consenteditor.model");
	    EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();	  
		String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
			System.out.println("File names:" + files[i]);
			ConsentDocument cd = (ConsentDocument) JAXBUtil
					.unmarshall(confParser.getConsentDocuments() + files[i]);
			if (cd.getConsentDocumentMetaData().getPatientId() != null) {
				em.persist(cd);
			}
		}
		SubjectElements se = (SubjectElements) JAXBUtil.unmarshall(confParser.getSubjectsFile());
		em.persist(se);
		
		
		ResourceElements re = (ResourceElements) JAXBUtil.unmarshall(confParser.getResourcesFile());
		em.persist(re);
		
		ActionElements ae = (ActionElements) JAXBUtil.unmarshall(confParser.getActionsFile());
		em.persist(ae);
		
	    em.getTransaction().commit();
	    em.close();
	}

}
