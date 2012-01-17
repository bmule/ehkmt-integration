package at.srfg.kmt.ehealth.phrs.user;

/**
 * @deprecated
 */
public class UserCredentialManager {
//http://books.zkoss.org/wiki/ZK_Essentials/Handling_the_Login_Process_using_ZK_MVC_and_Sessions/Managing_credentials_using_ZK_Sessions

	public static final String KEY_USER_MODEL = UserCredentialManager.class.getName()+"_MODEL";
    //private UserDAO userDAO;
    private UserAccountProfile userInfo;
     
    private UserCredentialManager(){
        //userDAO = new UserDAO();
    }
     /*
    public static UserCredentialManager getIntance(){
        return getInstance(Sessions.getCurrent());
    }*/
    /*
     * 
     * @return
     
    public static UserCredentialManager getInstance(Session zkSession){
    	
        //HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
         
         
//      Session session = Executions.getCurrent().getDesktop().getSession();
//      Session session = Executions.getCurrent().getSession();
//      Session session = Sessions.getCurrent();
        
        synchronized(zkSession){
            UserCredentialManager userModel = (UserCredentialManager) zkSession.getAttribute(KEY_USER_MODEL);
            if(userModel==null){
                zkSession.setAttribute(KEY_USER_MODEL, userModel = new UserCredentialManager());
            }
           
            return userModel;
        }

    }*/
    boolean isAuthenticated=false;
    public boolean isAuthenticated(){
    	return isAuthenticated;
    	
    }

	public UserAccountProfile getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserAccountProfile userInfo) {
		this.userInfo = userInfo;
	}
}
