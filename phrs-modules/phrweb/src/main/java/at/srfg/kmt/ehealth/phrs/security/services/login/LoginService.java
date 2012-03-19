package at.srfg.kmt.ehealth.phrs.security.services.login;



public interface LoginService {


    public String createRedirect(String username);

    /**
     *
     * @param username     shortName
     * @param redirectUrl
     * @return
     */
    public String createRedirect(String username, String redirectUrl);
    //username is a short name! and the openId provider id build the full open ID Uri

    /**
     *
     * @param username  Short name
     * @param openIdProviderId
     * @return
     */
    //TODO  Detect an email based OpenId or a normal openID.  openIdProviderId only if passed a real OpenID
    public String createRedirectForLoginType(String username,String openIdProviderId) throws Exception;
    public String createRedirectForOpenID(String openId) throws Exception;;
    //not used
    public RegistrationModel handleValidation();

    public void doLogout();
    public String getCurrentIP();
	
}
