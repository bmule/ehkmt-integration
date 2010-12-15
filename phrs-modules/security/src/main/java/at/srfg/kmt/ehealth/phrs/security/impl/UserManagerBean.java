/*
 * Project :iCardea
 * File : UserManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;

import at.srfg.kmt.ehealth.phrs.security.api.UserManager;
import java.security.Principal;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Mihai
 */
@Stateless
@Local(UserManager.class)
public class UserManagerBean implements UserManager {

    @Override
    public boolean addUser(Principal principal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAllUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Principal removeUser(Principal principal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean userExist(Principal principal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
