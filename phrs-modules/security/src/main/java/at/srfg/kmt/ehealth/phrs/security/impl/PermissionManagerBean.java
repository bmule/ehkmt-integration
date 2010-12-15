/*
 * Project :iCardea
 * File : PermissionManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;

import at.srfg.kmt.ehealth.phrs.security.api.Action;
import at.srfg.kmt.ehealth.phrs.security.api.Item;
import at.srfg.kmt.ehealth.phrs.security.api.PermissionManager;
import at.srfg.kmt.ehealth.phrs.security.model.Group;
import at.srfg.kmt.ehealth.phrs.security.model.Role;
import at.srfg.kmt.ehealth.phrs.security.model.User;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Mihai
 */

@Stateless
@Local(PermissionManager.class)
public class PermissionManagerBean implements PermissionManager {

    @Override
    public Group getAllowdGroupFor(Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Role getAllowdRoleFor(Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(User user, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(User user, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(Role role, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(Role role, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(Group role, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean prove(Group role, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
