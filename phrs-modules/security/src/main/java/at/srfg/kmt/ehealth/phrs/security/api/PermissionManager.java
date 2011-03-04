/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrItem;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;


/**
 *
 * @author mradules
 */
public interface PermissionManager {

    boolean validate(PhrActor user, PhrAction action, PhrItem item);
    boolean validate(PhrActor user, PhrAction action);

    boolean validate(PhrRole role, PhrAction action, PhrItem item);
    boolean validate(PhrRole role, PhrAction action);

    boolean validate(PhrGroup role, PhrAction action, PhrItem item);
    boolean validate(PhrGroup role, PhrAction action);

    PhrGroup getAllowdGroupFor(PhrAction action, PhrItem item);
    PhrRole getAllowdRoleFor(PhrAction action, PhrItem item);
}
