package tr.com.srdc.icardea.consenteditor.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import tr.com.srdc.icardea.consenteditor.model.ActionElements;
import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;
import tr.com.srdc.icardea.consenteditor.model.ConsentRule;
import tr.com.srdc.icardea.consenteditor.model.ConsentRuleTarget;
import tr.com.srdc.icardea.consenteditor.model.Individual;
import tr.com.srdc.icardea.consenteditor.model.ResourceElements;
import tr.com.srdc.icardea.consenteditor.model.SubjectElement;
import tr.com.srdc.icardea.consenteditor.model.SubjectElements;

public class PHRJPAImpl implements PHRInterface {

	private EntityManagerFactory emf;

	public PHRJPAImpl() {
		emf = Persistence.createEntityManagerFactory("tr.com.srdc.icardea.consenteditor.model");
	}

	@Override
	public String getPatientId() {
		return "e";
	}

	@Override
	public List<ConsentDocument> getConsentDocuments(String patientId) {
	
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select cd from ConsentDocument cd");
	    List<ConsentDocument> cdList = query.getResultList();
	    for(int i = 0;i < cdList.size();i++){
	    	initializeForHibernate(cdList.get(i));
	    }
	    em.close();
	    return cdList;
		
	}

	@Override
	public ConsentDocument getConsentDocumentById(String consentID) {
		EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();
		ConsentDocument cd = em.find(ConsentDocument.class, consentID);
		em.close();
		return cd;
	}

	@Override
	public void registerConsentDocument(ConsentDocument consentDocument) {
		try{
			boolean newDocument = true;
			EntityManager em = emf.createEntityManager();
		    em.getTransaction().begin();
		    ConsentDocument cd = em.find(ConsentDocument.class,consentDocument.getId());
		    if(cd != null){
		    	em.remove(cd);		    	
		    	em.flush();
		    	newDocument = false;
		    }
		    em.persist(consentDocument);		    
		    em.getTransaction().commit();		    
		    em.close();
		    if(newDocument){
		    	setCurrentConsentDocumentofPatient(consentDocument);
		    }
		    //setCurrentConsentDocumentofPatient(consentID, patientID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteConsentDocument(String consentID) {
		EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();
		ConsentDocument cd = em.find(ConsentDocument.class, consentID);
	    em.remove(cd);
	    em.flush();
	    em.getTransaction().commit();
	    em.close();	
	}
	
	private void initializeForHibernate(SubjectElements ses){
		for(int j = 0;j < ses.getSubjectElement().size();j++){
			SubjectElement se = ses.getSubjectElement().get(j);
			if(se.getGroup() != null){
				Hibernate.initialize(se.getGroup().getMembers().getIndividual());
			}
		}
	}
	private void initializeForHibernate(ConsentDocument cd){		
		for(int i = 0;i < cd.getConsentRule().size();i++){
			ConsentRule cr = cd.getConsentRule().get(i);
			Hibernate.initialize(cr.getCondition());
			if(cr.getObligation() != null){
				Hibernate.initialize(cr.getObligation().getSentEmailTo().getToItems());
			}
			ConsentRuleTarget crt = cr.getConsentRuleTarget();
			Hibernate.initialize(crt.getActionElements().getActionItems());
			Hibernate.initialize(crt.getResourceElements().getResourceElement());
			initializeForHibernate(crt.getSubjectElements());
			
		}
	}
	
	@Override
	public ConsentDocument getCurrentConsentDocumentByPatientId(String patientID) {
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select cd from ConsentDocument cd where cd.isCurrent='1'");
	    ConsentDocument cd = (ConsentDocument) query.getSingleResult();
	    initializeForHibernate(cd);
	    //System.out.println(cd.getConsentRule().size());
		if (cd == null) {
			System.out.println("No Current Consent found. Returning default template...");
			return getConsentDocumentById("8c209e6c-e1db-45e9-9931-4d70dd2ef69e");
		}
		em.close();
		return cd;
	}

	@Override
	public void setCurrentConsentDocumentofPatient(ConsentDocument consentDocument) {
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select cd from ConsentDocument cd");
	    List<ConsentDocument> cdList = query.getResultList();
	    em.getTransaction().begin();
	    for (int i = 0; i < cdList.size(); i++) {				
			cdList.get(i).setIsCurrent("0"); 
		}
	    em.getTransaction().commit();
	    em.getTransaction().begin();
	    ConsentDocument cd = em.find(ConsentDocument.class, consentDocument.getId());
	    cd.setIsCurrent("1");
	    em.getTransaction().commit();
	    em.close();
	}

	@Override
	public SubjectElements getSubjectList(String patientID) {
		SubjectElements se = null;
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select se from SubjectElements se where se.patientId='" + patientID + "'");
	    try{
	    	se = (SubjectElements) query.getSingleResult();
	    }catch(NoResultException e){
	    	System.out.println("User has not personal subjects. Default subjects will be used.");
	    }
	    if(se == null){
	    	query = em.createQuery("select se from SubjectElements se where se.patientId='default'");
	    	se = (SubjectElements) query.getSingleResult();
	    }
	    
	    initializeForHibernate(se);
	    em.close();
	    return se;
	}

	@Override
	public ResourceElements getResourceList(String patientID) {
		ResourceElements re = null;
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select re from ResourceElements re where re.patientId='" + patientID + "'");
	    try{
	    	re = (ResourceElements) query.getSingleResult();
	    }catch(NoResultException e){
	    	System.out.println("User has not personal resources. Default resources will be used.");
	    }
	    if(re == null){
	    	query = em.createQuery("select re from ResourceElements re where re.patientId='default'");
	    	re = (ResourceElements) query.getSingleResult();
	    }	    
	    Hibernate.initialize(re.getResourceElement());
	    em.close();
	    return re;
	}

	@Override
	public ActionElements getActionList(String patientID) {
		EntityManager em = emf.createEntityManager();
	    Query query = em.createQuery("select a from ActionElements a where a.patientId='default'");
	    ActionElements a = (ActionElements) query.getSingleResult();
	    Hibernate.initialize(a.getActionItems());
	    em.close();
	    return a;
	}

	@Override
	public void saveSubjectList(SubjectElements subjectElements) {		
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("select se from SubjectElements se where se.patientId='" + subjectElements.getPatientId() + "'");
		SubjectElements se = null;
		try{
	    	se = (SubjectElements) query.getSingleResult();
	    }catch(NoResultException e){
	    	System.out.println("User has not personal subjects.");
	    }
		if(se != null){
	    	em.remove(se);		    	
	    	em.flush();	    	
	    }
	    em.persist(subjectElements);		    
	    em.getTransaction().commit();		    
	    em.close();
	}

	@Override
	public void saveResourceList(ResourceElements resourceElements) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("select re from ResourceElements re where re.patientId='" + resourceElements.getPatientId() + "'");
		ResourceElements re = null;
		try{
	    	re = (ResourceElements) query.getSingleResult();
	    }catch(NoResultException e){
	    	System.out.println("User has not personal resources.");
	    }
	    
		if(re != null){
	    	em.remove(re);		    	
	    	em.flush();	    	
	    }
	    em.persist(resourceElements);		    
	    em.getTransaction().commit();		    
	    em.close();
	}
}
