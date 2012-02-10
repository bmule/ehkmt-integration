//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package at.srfg.kmt.ehealth.phrs.usermgt.openid.dyu.example;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.srfg.kmt.ehealth.phrs.security.services.OpenIdConstants;
import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;
import org.mortbay.util.ajax.JSON;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * For popup window option ...sample not used
 * @author David Yu
 * @created Jun 17, 2009
 */

public final class OpenIdService extends AbstractService {

	static {
		RelyingParty
				.getInstance()
				.addListener(
						new SRegExtension().addExchange("email")
								.addExchange("country").addExchange("language"))
				.addListener(
						new AxSchemaExtension().addExchange("email")
								.addExchange("country").addExchange("language"))
				.addListener(new RelyingParty.Listener() {
					public void onDiscovery(OpenIdUser user,
							HttpServletRequest request) {
					}

					public void onPreAuthenticate(OpenIdUser user,
							HttpServletRequest request,
							UrlEncodedParameterMap params) {
						if ("true".equals(request.getParameter("popup"))) {
							String returnTo = params
									.get(OpenIdConstants.OPENID_TRUST_ROOT)
									+ request.getContextPath()
									+ "/popup_verify.html";
							params.put(OpenIdConstants.OPENID_RETURN_TO, returnTo);
							params.put(OpenIdConstants.OPENID_REALM, returnTo);
							params.put("openid.ns.ui",
									"http://specs.openid.net/extensions/ui/1.0");
							params.put("openid.ui.mode", "popup");
						}
					}

					public void onAuthenticate(OpenIdUser user,
							HttpServletRequest request) {
						Map<String, String> sreg = SRegExtension.remove(user);
						Map<String, String> axschema = AxSchemaExtension
								.remove(user);
						if (sreg != null && !sreg.isEmpty()) {
							System.err.println("sreg: " + sreg);
							user.setAttribute("info", sreg);
						} else if (axschema != null && !axschema.isEmpty()) {
							System.err.println("axschema: " + axschema);
							user.setAttribute("info", axschema);
						} else {
							System.err.println("identity: "
									+ user.getIdentity());
						}
					}

					public void onAccess(OpenIdUser user,
							HttpServletRequest request) {

					}
				});
	}

	final RelyingParty _relyingParty = RelyingParty.getInstance();

	protected void init() {

	}

	@HttpResource(location = "/openid")
	@Get
	public void rootGet(RequestContext rc) throws IOException, ServletException {
		rc.getResponse().setContentType("text/html");
		getWebContext().getJSPDispatcher().dispatch("openid/index",
				rc.getRequest(), rc.getResponse());
	}

	@HttpResource(location = "/openid")
	@Post
	public void rootPost(RequestContext rc) throws IOException,
			ServletException {
		rc.getResponse().setContentType("text/html");
		getWebContext().getJSPDispatcher().dispatch("openid/index",
				rc.getRequest(), rc.getResponse());
	}

	@HttpResource(location = "/logout/openid")
	@Get
	public void logout(RequestContext rc) throws IOException {
		HttpServletRequest request = rc.getRequest();
		HttpServletResponse response = rc.getResponse();

		_relyingParty.invalidate(request, response);

		if ("true".equals(request.getParameter("popup")))
			response.setStatus(200);
		else
			response.sendRedirect(request.getContextPath() + "/openid");
	}

	@HttpResource(location = "/verify/openid")
	@Post
	public void verify(RequestContext rc) throws IOException {
		checkOrVerify(rc);
	}

	@HttpResource(location = "/check/openid")
	@Get
	public void check(RequestContext rc) throws IOException {
		checkOrVerify(rc);
	}

	public void checkOrVerify(RequestContext rc) throws IOException {
		HttpServletRequest request = rc.getRequest();
		HttpServletResponse response = rc.getResponse();

		try {
			OpenIdUser user = _relyingParty.discover(request);
			if (user != null) {
				if (user.isAuthenticated()
						|| (user.isAssociated()
								&& RelyingParty.isAuthResponse(request) && _relyingParty
								.verifyAuth(user, request, response))) {
					response.setContentType("text/json");
					response.getWriter().write(JSON.toString(user));
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setStatus(401);
	}

}
