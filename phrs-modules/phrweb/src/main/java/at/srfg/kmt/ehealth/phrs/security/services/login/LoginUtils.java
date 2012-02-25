package at.srfg.kmt.ehealth.phrs.security.services.login;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class LoginUtils {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(LoginUtils.class);
    /**
     *
     * @param input
     * @return
     */
    public static String transformRole(String input) {
        String transformed = null;
        if (input != null) {
            String theRole = input.toLowerCase();
            if (theRole.contains("doctor")) {
                transformed = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR;

            } else if (theRole.contains("nurse")) {
                transformed = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE;

            }
        }

        return transformed;
    }

    /**
     * Normally, we expect one string, but this OP provides a list of roles
     *
     * @param roleObj
     * @return
     */
    public static String processRole(Object roleObj) {
        String theRole = null;

        try {
            if (roleObj != null) {
                if (roleObj instanceof String) {
                    theRole = transformRole((String) roleObj);
                } else {
                    //
                    List roles = (List) roleObj;
                    if (roles.size() > 0) {


                        try {
                            for (Object item : roles) {
                                if (item != null && item instanceof String) {
                                    try {
                                        String transformed = transformRole((String) item);
                                        if (transformed != null) {
                                            theRole = transformed;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        LOGGER.error("error processing Role ", e);
                                    }
                                }
                                
                            }
                        } catch (Exception e) {
                            LOGGER.error("error processing Role ", e);
                        }

                        if (theRole == null) {
                            Object out = roles.get(0);
                            theRole =(String)out;
                        }

                        LOGGER.debug("OpenID  role =" + theRole + " from list size: " + roles.size());

                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("error processing Role ", e);
        }

        return theRole;
    }
}
