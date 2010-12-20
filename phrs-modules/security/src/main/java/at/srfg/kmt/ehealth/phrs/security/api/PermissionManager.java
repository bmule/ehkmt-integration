/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;

/**
 *
 * @author mradules
 */
public interface PermissionManager {

    boolean validate(PhrUser user, Action action, Item item);
    boolean validate(PhrUser user, Action action);

    boolean validate(PhrRole role, Action action, Item item);
    boolean validate(PhrRole role, Action action);

    boolean validate(PhrGroup role, Action action, Item item);
    boolean validate(PhrGroup role, Action action);

    PhrGroup getAllowdGroupFor(Action action, Item item);
    PhrRole getAllowdRoleFor(Action action, Item item);
}
