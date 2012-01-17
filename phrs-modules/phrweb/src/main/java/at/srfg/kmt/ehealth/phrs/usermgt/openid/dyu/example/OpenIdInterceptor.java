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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.openid.OpenIdServletFilter;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.web.rest.AbstractInterceptor;
import com.dyuproject.web.rest.RequestContext;

/**
 * @deprecated
 * @author David Yu
 * @created Jun 17, 2009
 */

public final class OpenIdInterceptor extends AbstractInterceptor {
	// static final String FORWARD_URI = "openid/login";
	static final String FORWARD_URI = "openid/login";

	final RelyingParty _relyingParty = RelyingParty.getInstance();

	final OpenIdServletFilter.ForwardUriHandler _forwardUriHandler = new OpenIdServletFilter.ForwardUriHandler() {
		
		public void handle(String forwardUri, HttpServletRequest request,
				HttpServletResponse response) throws IOException,
				ServletException {
			System.out.println("OpenIdInterceptor ForwardUriHandler");
			getWebContext().getJSPDispatcher().dispatch(forwardUri, request,
					response);
		}
	};

	@Override
	protected void init() {

	}

	public void postHandle(boolean handled, RequestContext rc) {
		System.out.println("postHandle");
	}

	public boolean preHandle(RequestContext rc) throws ServletException,
			IOException {
		System.out.println("OpenIdInterceptor preHandle");
		return OpenIdServletFilter.handle(rc.getRequest(), rc.getResponse(),
				_relyingParty, _forwardUriHandler, FORWARD_URI);
	}

}
