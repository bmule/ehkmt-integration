package at.srfg.kmt.ehealth.phrs.user;

/*
 * http://code.google.com/appengine/articles/openid.html#ui
 * http://jvance.com/pages/JQueryOpenIDPlugin.xhtml
 * 
 * http://wordpress.org/extend/plugins/openid/
 * http://wordpress.org/extend/plugins/http-authentication/faq/
 * http://willnorris.com/2009/03/authentication-in-wordpress-28
 * http://www.shareyourwork.org/roller/ralphsjavablog/entry/using_openid_for_jforum
 * http://www.shareyourwork.org/roller/ralphsjavablog/tags/openid
 * 
 * 
 
 * http://code.google.com/p/openid4java/
 * http://code.google.com/p/openid4java/wiki/SampleConsumer
 * 
 * openid server but not yet...can make simple server? openid4java
 * http://code.google.com/p/openid-server/
 * 
 * good overview
 * http://stackoverflow.com/questions/6002519/simple-sso-using-custom-authentication-cas-or-some-oauth-or-openid-server
 * http://stackoverflow.com/questions/22015/openid-as-a-single-sign-on-option
 */

	import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
	import com.google.appengine.api.users.User;
	import com.google.appengine.api.users.UserService;
	import com.google.appengine.api.users.UserServiceFactory;
*/
	@SuppressWarnings("serial")
	public class FederatedOpenIdType01 extends HttpServlet implements Serializable {
	//public class OpenIdDemoServlet extends HttpServlet {

	    private static final Map<String, String> openIdProviders;
	    static {
	        openIdProviders = new HashMap<String, String>();
	        openIdProviders.put("Google", "google.com/accounts/o8/id");
	        openIdProviders.put("Yahoo", "yahoo.com");
	        openIdProviders.put("MySpace", "myspace.com");
	        openIdProviders.put("AOL", "aol.com");
	        openIdProviders.put("MyOpenId.com", "myopenid.com");
	    }

	    @Override
	    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws IOException {
	    	/*
	        UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
	     
	        Set<String> attributes = new HashSet();

	        resp.setContentType("text/html");
	        PrintWriter out = resp.getWriter();

	        if (user != null) {
	            out.println("Hello <i>" + user.getNickname() + "</i>!");
	            out.println("[<a href=\""
	                    + userService.createLogoutURL(req.getRequestURI())
	                    + "\">sign out</a>]");
	        } else {
	            out.println("Hello world! Sign in at: ");
	            for (String providerName : openIdProviders.keySet()) {
	                String providerUrl = openIdProviders.get(providerName);
	                String loginUrl = userService.createLoginURL(req
	                        .getRequestURI(), null, providerUrl, attributes);
	                out.println("[<a href=\"" + loginUrl + "\">" + providerName + "</a>] ");
	            }
	        }
	           */
	    }
	}
