package at.srfg.kmt.ehealth.phrs.presentation.services;

import java.io.Serializable;


import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsRepositoryClient;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.security.services.ConsentMgrService;

/**
 * 
 * Common Services and clients
 * 
 */

@SuppressWarnings("serial")
public class PhrPortalFactory implements Serializable{
	/**
	 * 
	 * Initialization-on-demand holder idiom
	 * 
	 */
	private static class LazyHolder {
		public static final PhrPortalFactory m_instance = new PhrPortalFactory();
	}

	public static PhrPortalFactory getInstance() {
		
		
		return LazyHolder.m_instance;
	}

	private PhrPortalFactory() {
		init();
	}

	private void init() {
		// configuration
	}

	public ConfigurationService getConfigurationService() {
		return ConfigurationService.getInstance();
	}

	public PhrsStoreClient getPhrsStoreClient() {
		return PhrsStoreClient.getInstance();
	}

	public PhrsRepositoryClient getPhrsRepositoryClient() {
		return PhrsStoreClient.getInstance().getPhrsRepositoryClient();
	}

	public CommonDao getCommonDao() {
		return PhrsStoreClient.getInstance().getPhrsRepositoryClient()
				.getCommonDao();
	}

	public UserService getUserService() {
		return PhrsStoreClient.getInstance().getPhrsRepositoryClient()
				.getUserService();
	}

	public ConsentMgrService getConsentMgrService() {
		return new ConsentMgrService();
	}

	public InteropAccessService getInteropAccessService() {
		return PhrsStoreClient.getInstance().getInteropService();
	}





}
