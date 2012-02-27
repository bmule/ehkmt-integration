package at.srfg.kmt.ehealth.phrs.security.services.login;



public interface LoginService {


	String createRedirect(String username);

    /**
     *
     * @param username     shortName
     * @param redirectUrl
     * @return
     */
    String createRedirect(String username, String redirectUrl);
    //username is a short name! and the openId provider id build the full open ID Uri

    /**
     *
     * @param username  Short name
     * @param redirectUrl
     * @param openIdProviderId
     * @return
     */
    //TODO  Detect an email based OpenId or a normal openID.  openIdProviderId only if passed a real OpenID
    String createRedirect(String username,String redirectUrl,String openIdProviderId) ;
    //not used
	RegistrationModel handleValidation();

	void doLogout();
	String getCurrentIP();
	
}
