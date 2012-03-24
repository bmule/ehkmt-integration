package at.srfg.kmt.ehealth.phrs.security.services.login;


import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.*;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public class RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class.getName());

    /**
     * Perform discovery on the User-Supplied identifier and return the
     * DiscoveryInformation object that results from Association with the
     * OP. This will probably be needed by the caller (stored in Session
     * perhaps?).
     * <p/>
     * I'm not thrilled about ConsumerManager being static, but it is
     * very important to openid4java that the ConsumerManager object be the
     * same instance all through a conversation (discovery, auth request,
     * auth response) with the OP. I didn't dig terribly deeply, but suspect
     * that part of the key exchange or the nonce uses the ConsumerManager's
     * hash, or some other instance-specific construct to do its thing.
     *
     * @param userSuppliedIdentifier The User-Supplied identifier. It may already
     *                               be normalized.
     * @return DiscoveryInformation - The resulting DisoveryInformation object
     *         returned by openid4java following successful association with the OP.
     */
    @SuppressWarnings("unchecked")
    public static DiscoveryInformation performDiscoveryOnUserSuppliedIdentifier(String userSuppliedIdentifier) {
        DiscoveryInformation ret = null;
        //
        ConsumerManager consumerManager = getConsumerManager();
        try {
            // Perform discover on the User-Supplied Identifier
            LOGGER.debug("performDiscoveryOnUserSuppliedIdentifier userSuppliedIdentifier "+userSuppliedIdentifier);
            List<DiscoveryInformation> discoveries = consumerManager.discover(userSuppliedIdentifier);

            LOGGER.debug("performDiscoveryOnUserSuppliedIdentifier AFTER Discovery discoveries = "+discoveries);

            // Pass the discoveries to the associate() method...
            ret = consumerManager.associate(discoveries);
            LOGGER.debug("performDiscoveryOnUserSuppliedIdentifier AFTER Association DiscoveryInformation  "+ret);


        } catch (DiscoveryException e) {
            String message = "Error occurred during discovery!";
            LOGGER.error(" performDiscoveryOnUserSuppliedIdentifier FAILED "+message, e);
            throw new RuntimeException(message, e);
        }
        return ret;
    }

    /**
     * Create an OpenID Auth Request, using the DiscoveryInformation object
     * return by the openid4java library.
     * <p/>
     * This method also uses the Simple Registration Extension to grant
     * the Relying Party (RP).
     *
     * @param discoveryInformation The DiscoveryInformation that should have
     *                             been previously obtained from a call to
     *                             performDiscoveryOnUserSuppliedIdentifier().
     * @param returnToUrl          The URL to which the OP will redirect once the
     *                             authentication call is complete.
     * @return AuthRequest - A "good-to-go" AuthRequest object packed with all
     *         kinds of great OpenID goodies for the OpenID Provider (OP). The caller
     *         must take this object and forward it on to the OP. Or call
     *         processAuthRequest() - part of this Service Class.
     */
    public static AuthRequest createOpenIdAuthRequest(DiscoveryInformation discoveryInformation, String returnToUrl) {
        AuthRequest ret = null;
        //
        try {
            // Create the AuthRequest object
            ret = getConsumerManager().authenticate(discoveryInformation, returnToUrl);
            // Create the Simple Registration Request
            SRegRequest sRegRequest = SRegRequest.createFetchRequest();
            FetchRequest ax = FetchRequest.createFetchRequest();
            //ax.addAttribute("http://www.w3.org/2006/vcard/ns#role",true);
            //expect list

            //TODO can't use all OPs, coz role required. Non medical users might not have role!
            //false -> if available
            ax.addAttribute("label", "http://www.w3.org/2006/vcard/ns#role", false, 0);
            //ax.addAttribute("label", "http://www.w3.org/2006/vcard/ns#role", true, 0);

            ax.addAttribute("firstname", "http://axschema.org/namePerson/first", false, 0);
            ax.addAttribute("lastname", "http://axschema.org/namePerson/last", false, 0);
            ax.addAttribute("nickname", "http://axschema.org/namePerson/friendly", false, 0);
            ax.addAttribute("fullname", "http://axschema.org/namePerson", false, 0);
            ax.addAttribute("email", "http://axschema.org/contact/email", false, 0);
            ax.addAttribute("dob", "http://axschema.org/birthDate", false, 0);
            //TODO add complementary sreq for ax??

            sRegRequest.addAttribute("email", false);
            sRegRequest.addAttribute("fullname", false);
            sRegRequest.addAttribute("dob", false);
            sRegRequest.addAttribute("postcode", false);

            //FIXXME phrs   axfullname axemail  axdob
            sRegRequest.addAttribute("nickname", false);

            ret.addExtension(sRegRequest);
            ret.addExtension(ax);

        } catch (Exception e) {
            String message = "Exception occurred while building AuthRequest object!";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
        return ret;
    }
//
//    role = http://www.w3.org/2006/vcard/ns#role
//    email = http://axschema.org/contact/email
//    firstname = http://axschema.org/namePerson/first
//    lastname = http://axschema.org/namePerson/last
//    country = http://axschema.org/contact/country/home
//    language = http://axschema.org/pref/language
//    fullname = http://axschema.org/namePerson
//    nickname = http://axschema.org/namePerson/friendly
//    dob = http://axschema.org/birthDate
//    gender = http://axschema.org/person/gender
//    postcode = http://axschema.org/contact/postalCode/home
//    timezone = http://axschema.org/pref/timezone

    /**
     * Processes the returned information from an authentication request
     * from the OP.
     *
     * @param discoveryInformation DiscoveryInformation that was created earlier
     *                             in the conversation (by openid4java). This will need to be verified with
     *                             openid4java to make sure everything went smoothly and there are no
     *                             possible problems. This object was probably stored in session and retrieved
     *                             for use in calling this method.
     * @param pageParameters       PageParameters passed to the page handling the
     *                             return verificaion.
     * @param returnToUrl          The "return to" URL that was passed to the OP. It must
     *                             match exactly, or openid4java will issue a verification failed message
     *                             in the logs.
     * @return RegistrationModel - null if there was a problem, or a RegistrationModel
     *         object, with parameters filled in as compeletely as possible from the
     *         information available from the OP. If you are using MyOpenID, most of the
     *         time what is returned is from your "Default" profile, so if you need more
     *         information returned, make sure your Default profile is completely filled
     *         out.
     * @throws AssociationException
     * @throws DiscoveryException
     * @throws MessageException
     */
    public static RegistrationModel processReturn(HttpServletRequest req) throws MessageException, DiscoveryException, AssociationException {
        RegistrationModel ret = null;
        ParameterList response = new ParameterList(req.getParameterMap());
        DiscoveryInformation discovered = (DiscoveryInformation)
                req.getSession().getAttribute("openid-disc");

        // extract the receiving URL from the HTTP request
        StringBuffer receivingURL = req.getRequestURL();
        String queryString = req.getQueryString();
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(req.getQueryString());

        // verify the response; ConsumerManager needs to be the same
        // (static) instance used to place the authentication request
        VerificationResult verification = getConsumerManager().verify(
                receivingURL.toString(),
                response, discovered);

        // examine the verification result and extract the verified identifier
        Identifier verified = verification.getVerifiedId();
        if (verified != null) {

            ret = new RegistrationModel();
            AuthSuccess authSuccess =
                    (AuthSuccess) verification.getAuthResponse();

            if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                FetchResponse fetchResp = (FetchResponse) authSuccess
                        .getExtension(AxMessage.OPENID_NS_AX);

                //Is SREG always supported?
                //ret.setOpenId(verified.getIdentifier());
                //ret.setIs_verified("true");
                //ret.setClaimedId();
                try {
                    Object roleObj = fetchResp.getAttributeValues("label");

                    String role = LoginUtils.processRole(roleObj);

                    if(role != null){
                        ret.setRole(role);
                        LOGGER.debug("RegistrationService SREG using role{} from SREG roles {} ",role,roleObj);

                    } else {
                        if(roleObj==null){
                            LOGGER.debug("RegistrationService SREG role roleObj NULL ");
                        } else {
                            LOGGER.debug("RegistrationService SREG found roles, but no doctor or nurse roleObj {} ",roleObj);
                        }

                    }

                } catch (Exception e) {
                    LOGGER.error("error processing AX Role from fetchResp ", e);
                }

                try {
                    String value =  fetchResp.getAttributeValue("firstname");
                    if(value!=null) {
                        ret.setFirstName(value);
                        LOGGER.debug("RegistrationService SREG firstname "+value);
                    }

                    value =   fetchResp.getAttributeValue("lastname");
                    if(value!=null)  {
                        ret.setLastName(value);
                        LOGGER.debug("RegistrationService SREG lastname "+value);
                    }

                    value =        fetchResp.getAttributeValue("dob");
                    if(value!=null)   {
                        ret.setDateOfBirth(value);
                        LOGGER.debug("RegistrationService SREG dob "+value);
                    }

                    value =      fetchResp.getAttributeValue("email");
                    if(value!=null)    {
                        ret.setEmailAddress(value);
                        LOGGER.debug("RegistrationService SREG email "+value);
                    }

                    value =   fetchResp.getAttributeValue("fullname");
                    if(value!=null)  {
                        ret.setEmailAddress(value);
                        LOGGER.debug("RegistrationService SREG fullname "+value);
                    }

                    value =   fetchResp.getAttributeValue("nickname");
                    if(value!=null) {
                        ret.setNickname(value);
                        LOGGER.debug("RegistrationService SREG nickname "+value);
                    }

               } catch (Exception e) {
                    LOGGER.error("error processing AX params ", e);

                }
            }
            if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG)) {
                MessageExtension extension = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);
                if (extension instanceof SRegResponse) {
                    //TODO check claimedId vs OP's??
                    ret.setOpenId(verified.getIdentifier());
                    ret.setIs_verified("true");


                    SRegResponse sRegResponse = (SRegResponse) extension;

                    String value = sRegResponse.getAttributeValue("dob");
                    if (value != null) {
                        ret.setDateOfBirth(value);
                        LOGGER.debug("RegistrationService AUX dob "+value);
                    }

                    value = sRegResponse.getAttributeValue("email");
                    if (value != null) {
                        ret.setEmailAddress(value);
                        LOGGER.debug("RegistrationService AUX email "+value);
                    }

                    value = sRegResponse.getAttributeValue("fullname");
                    if (value != null) {
                        ret.setFullName(value);
                        LOGGER.debug("RegistrationService AUX fullname "+value);
                    }

                    value = sRegResponse.getAttributeValue("nickname");
                    //overwrite Ax nickname is ok...likely won't happen
                    if (value != null) {
                        ret.setNickname(value);
                        LOGGER.debug("RegistrationService AUX nickname "+value);
                    }
                }
            }
        }
        return ret;
    }


    private static ConsumerManager consumerManager;

    /**
     * Retrieves an instance of the ConsumerManager object. It is static
     * (see note in Class-level JavaDoc comments above) because openid4java
     * likes it that way.
     * <p/>
     * Note: if you are planning to debug the code, set the lifespan parameter
     * of the InMemoryNonceVerifier high enough to outlive your debug cycle, or
     * you may notice Nonce Verification errors. Depending on where you are
     * debugging, this might pose an artificial problem for you (of your own
     * making) that has nothing to do with either your code or openid4java.
     *
     * @return ConsumerManager - The ConsumerManager object that handles
     *         communication with the openid4java API.
     */
    private static ConsumerManager getConsumerManager() {
        if (consumerManager == null) {
            consumerManager = new ConsumerManager();
            consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
            consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
        }
        return consumerManager;
    }

    /**
     * @deprecated Use alternative, this should not happen, but in case of failure elsewhere.
     * Generates the returnToUrl parameter that is passed to the OP.
     * Alternatively, a returnUrl can be provided to the calling method
     * <p/>
     * The User Agent (i.e., the browser) will be directed to this page following
     * authentication.
     *
     * @param representedPage The RegistrationPage object whose cover is to be
     *                        cracked open to get at the raw HttpServlet goodies inside.
     * @return String - the returnToUrl to be used for the authentication request.
     */
    //we do not use this. The context path is known

    public static String getReturnToUrl() {
        return LoginUtils.getOpenIdReturnToUrl();

       //ResourceBundle properties = ResourceBundle.getBundle("icardea");
       //String salkServer = properties.getString("salk.server");
       //return salkServer + ":" + securePort + uri; //only valid for SALK server
       //	  String url = salkServer + ":"+securePort+"/phrweb/servlet/loginServlet?"; //only valid for SALK server


//        try {
//              InetAddress addr = InetAddress.getLocalHost();
//
//              // Get IP Address
//              byte[] ipAddr = addr.getAddress();
//              String ipadd = ipAddr.toString();
//              // Get hostname
//              String hostname = addr.getHostAddress();
//              url = "https://"+ hostname + ":"+securePort+"/icardea_careplaneditor/servlet/loginServlet?";
//          } catch (UnknownHostException e) {
//          }


    }


}

