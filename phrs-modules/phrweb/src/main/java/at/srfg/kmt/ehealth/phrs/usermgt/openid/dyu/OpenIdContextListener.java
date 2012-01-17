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

package at.srfg.kmt.ehealth.phrs.usermgt.openid.dyu;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * 
 * Methods derived from Dyuproject's context listener sample This listener sets
 * up the RelyingParty and listeners for particular OpenId attributes,etc and
 * stores in ServletContext
 * 
 * Axschema and simple registration properties are stored under the 'info' key
 * in the OpenIdUser and also saved to the PhrFederatedUser
 * See Axschema properties and sreq properties for relevant OpenId attributes to
 * extract, set up listener
 * 
 * Relevant PhrsContants.OPEN_ID_PARAM_ must match the 'alias' names found in:
 * resources/com.dyuproject.openid.ext/axschema.properties
 * resources/com.dyuproject.openid.ext/sreg.properties
 * 
 * Simple Registration: fullname, dob, email, language
 * Ax : role
 */
public class OpenIdContextListener implements ServletContextListener,
		RelyingParty.Listener {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenIdContextListener.class);


	/**
	 * _relyingParty = (RelyingParty)
	 * config.getServletContext().getAttribute(RelyingParty.class.getName());
	 */
	private RelyingParty _relyingParty;

	public void contextDestroyed(ServletContextEvent event) {

	}

/**
 * 
 */
	public void onAccess(OpenIdUser user, HttpServletRequest request) {
		System.out.println("OpenIdContextListener user access: "
				+ user.getIdentity());
		System.out.println("info: " + user.getAttribute("info"));
	}

	public void contextInitialized(ServletContextEvent event) {
		// use default. See the http://code.google.com/p/dyuproject/wiki/openid
		// for more info.
		_relyingParty = RelyingParty.getInstance();

		// listen to the authentication events
		_relyingParty.addListener(this);
		addPhrListener(true);
		// this relying party will be used by OpenIdServletFilter
		event.getServletContext().setAttribute(RelyingParty.class.getName(),
				_relyingParty);
		System.out.println("OpenIdContextListener contextInitialized ");
	}

	/**
	 * See Dyuproject properties file to set the alias and URI of each attribute
	 * for the See Axschema properties and sreq properties for relevant OpenId
	 * attributes to extract, set up listener
	 * 
	 * resources/com.dyuproject.openid.ext/axschema.properties
	 * resources/com.dyuproject.openid.ext/sreg.properties Relevant OpenId
	 * PhrsContants must match the 'alias' names!!!!
	 * 
	 * @param healthAttrs
	 */
	// TODO decide whether to read properties directly to setup listener
	/**
	 * add listener to RelyingParty instance that is saved into the user session
	 */
	public void addPhrListener(boolean healthAttrs) {

		try {
			//if (healthAttrs) {
				// see axschema.properties in iCARDEA we expect Role
				_relyingParty.addListener(new AxSchemaExtension()
						.addExchange(PhrsConstants.OPEN_ID_PARAM_EMAIL)
						.addExchange(PhrsConstants.OPEN_ID_PARAM_COUNTRY)
						.addExchange(PhrsConstants.OPEN_ID_PARAM_LANGUAGE)
						.addExchange(PhrsConstants.OPEN_ID_PARAM_ROLE));
/*			} else {
				// std set
				_relyingParty.addListener(new AxSchemaExtension()
						.addExchange(PhrsConstants.OPEN_ID_PARAM_EMAIL)
						.addExchange(PhrsConstants.OPEN_ID_PARAM_COUNTRY)
						.addExchange(PhrsConstants.OPEN_ID_PARAM_LANGUAGE));

			}
			*/
			// Simple Registration see
			// com.dyuproject.openid.ext.sreq.properties. These are declared
			// optional in the properties file
			_relyingParty.addListener(new SRegExtension()
					.addExchange(PhrsConstants.OPEN_ID_PARAM_FULL_NAME)
					.addExchange(PhrsConstants.OPEN_ID_PARAM_EMAIL)
					.addExchange(PhrsConstants.OPEN_ID_PARAM_POST_CODE)
					.addExchange(PhrsConstants.OPEN_ID_PARAM_DATE_OF_BIRTH));
			// .addExchange("language")
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("error adding listener AxSchemaExtension healthAttrs="
					+ healthAttrs, e);
		}
	}

	/*
	 * .addExchange(PhrsConstants.OPEN_ID_PARAM_PROTOCOL_ID) .addExchange(
	 * PhrsConstants.OPEN_ID_PARAM_PROTOCOL_NAMESPACE)
	 */

	public void onDiscovery(OpenIdUser user, HttpServletRequest request) {
		System.out.println("OpenIdContextListener discovered user claimId: "
				+ user.getClaimedId());
	}

	public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
			UrlEncodedParameterMap params) {
		System.out
				.println("OpenIdContextListener pre-authenticate user claimId: "
						+ user.getClaimedId());
	}

	public void onAuthenticate(OpenIdUser user, HttpServletRequest request) {
		
		if (user != null) {
			System.out
					.println("OpenIdContextListener onAuthenticate newly authenticated openid user.identity: "
							+ user.getIdentity());
			LOGGER.debug("OpenIdContextListener onAuthenticate newly authenticated openid user.identity: "
					+ user.getIdentity());
			try {
				PhrFederatedUser phrUser = UserSessionService
						.managePhrUserSessionByOpenIdUserLoginScenario(user,request);
				LOGGER.debug("OpenIdContextListener onAuthenticate newly authenticated openid phrUser ownerUri: "
						+ phrUser.getOwnerUri());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.debug("OpenIdContextListener onAuthenticate error Null OpenIdUser: ");

		}

	}
	/*
	 * By simple registration Map<String, String> sreg =
	 * SRegExtension.remove(user); Map<String, String> axschema =
	 * AxSchemaExtension .remove(user); if (sreg != null && !sreg.isEmpty()) {
	 * System.err.println("sreg: " + sreg); user.setAttribute("info", sreg); }
	 * else if (axschema != null && !axschema.isEmpty()) {
	 * System.err.println("axschema: " + axschema); user.setAttribute("info",
	 * axschema); }
	 */

}
