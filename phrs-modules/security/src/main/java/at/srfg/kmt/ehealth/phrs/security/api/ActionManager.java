/*
 * Project :iCardea
 * File : ActionManager.java
 * Date : Jan 8, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.api;


import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;

/**
 *
 * @author Mihai
 */
public interface ActionManager {

    /**
     * Add/register a new PHRS Action. If the PHRS Action exists then this method
     * has no effect. The PHRS Action existence can proved with the
     * <code>actionExist</code> method.
     *
     * @param Action the action to add.
     * @return true if the action is successfully register.
     * @see ActionManager#actionExist(at.srfg.kmt.ehealth.phrs.security.model.PhrAction)
     */
    boolean addAction(PhrAction Action);

    /**
     * Proves if a PHRS action is registered or not.
     *
     * @param Action the PHRS action that presence is to be tested.
     * @return true if the PHRS action is registered false otherwise.
     */
    boolean ActionExist(PhrAction action);

    /**
     * Removes a PHRS action. It is not defined here how the method must behave
     * if the PHRS action is not registered, this will be defined on the
     * implementation.
     *
     * @param action the PHRS action to remove.
     * @return the removed Action.
     */
    PhrAction removeAction(PhrAction action);

    /**
     * Removes all the PHRS actions.
     */
    void removeAllActions();

    /**
     * Returns a Set that contains all the registered PHRS actions.
     *
     * @return a Set that contains all the registered PHRS actions.
     */
    Set<PhrAction> getAllActions();

    void assignUserToAction(PhrUser user, PhrAction Action);

    void assignUsersToAction(Set<PhrUser> users, PhrAction Action);

    void removeUserFromAction(PhrUser user, PhrAction Action);

    void removeUsersFromAction(Set<PhrUser> users, PhrAction Action);
}
