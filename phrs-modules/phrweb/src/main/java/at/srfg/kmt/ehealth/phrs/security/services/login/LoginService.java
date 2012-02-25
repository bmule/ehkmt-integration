package at.srfg.kmt.ehealth.phrs.security.services.login;



public interface LoginService {


	String createRedirect(String username);
    //redirectUrl is servlet to process returning OP
    String createRedirect(String username, String redirectUrl);
    //not used
	RegistrationModel handleValidation();

	void doLogout();
	String getCurrentIP();

    String getUriLoginPage();
    String getUriApplicationHome();

    boolean isLocalLogin(String username,String loginType);
	
}
