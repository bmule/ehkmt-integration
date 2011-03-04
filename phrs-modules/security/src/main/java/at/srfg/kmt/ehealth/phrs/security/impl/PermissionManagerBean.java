/*
 * Project :iCardea
 * File : PermissionManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.PermissionManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrItem;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
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
    public boolean validate(PhrActor user, PhrAction action, PhrItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrActor user, PhrAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrRole role, PhrAction action, PhrItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrRole role, PhrAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrGroup role, PhrAction action, PhrItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean validate(PhrGroup role, PhrAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhrGroup getAllowdGroupFor(PhrAction action, PhrItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhrRole getAllowdRoleFor(PhrAction action, PhrItem item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
