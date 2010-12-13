package at.srfg.kmt.ehealth.phrs.security.impl;

import at.srfg.kmt.ehealth.phrs.security.api.Action;
import at.srfg.kmt.ehealth.phrs.security.api.Item;
import at.srfg.kmt.ehealth.phrs.security.api.SecurityService;

import java.security.Principal;

/**
 * The main implementation for the SecurityService.</br>
 * This class can not be extended. 
 */
final class JAASSecurityService implements SecurityService {


    public boolean prove(Principal principal, Action action, Item item) throws SecurityException {
        return false;
    }
}
