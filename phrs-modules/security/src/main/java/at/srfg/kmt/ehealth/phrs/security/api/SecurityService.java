/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import java.security.Principal;

/**
 * Used to prove if a principal can be involved in a certain action on
 * a given item.
 *  
 * @author mradules
 */
public interface SecurityService {

    /**
     * Proves if a given action can be accomplished on a given item by a given
     * principal.
     *
     * @param principal the principal involved in the check.
     * @param action the principal involved in the check.
     * @param item the principal involved in the check.
     * @return true if the a given action can be accomplished on a given
     * item by a given principal.
     *
     * @throws SecurityException by any kind of problems. Most of the
     * times it warps (chains) the real cause for the exception.
     * The cause can be obtained using the <code>getCause()</code> method.
     */
    boolean prove(Principal principal, Action action, Item item)
            throws SecurityException;
}
