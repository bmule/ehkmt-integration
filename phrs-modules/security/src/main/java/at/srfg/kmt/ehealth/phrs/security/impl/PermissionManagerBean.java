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
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
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
    public PhrGroup getAllowdGroupFor(Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhrRole getAllowdRoleFor(Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrUser user, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrUser user, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrRole role, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrRole role, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrGroup role, Action action, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrGroup role, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
