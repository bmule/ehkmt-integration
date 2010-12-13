/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import java.io.Serializable;
import java.security.Principal;

/**
 * Signals a security related problem. </br>
 * It is able to transport information about the possible exception
 * cause/circumstance. This information can be:
 * <ul>
 * <li> the principal, the action and the item involved.
 * <li> exception which may generate this exception.
 * The cause can be obtained using the <code>getCause()</code> method.
 * </ul>
 *
 * @author mihai
 */
public class SecurityServiceException extends Exception implements Serializable {

    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     *
     */
    private Principal principal;
    private Action action;
    private Item item;

    /**
     *
     */
    public SecurityServiceException() {
        // UNIMPLEMENTED
    }


    public SecurityServiceException(Principal principal, Action action, Item item) {
        this.principal = principal;
        this.action = action;
        this.item = item;
    }

    public SecurityServiceException(Throwable thrwbl) {
        super(thrwbl);
    }

    public SecurityServiceException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public SecurityServiceException(String string) {
        super(string);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public String toString() {
        final StringBuilder msg = new StringBuilder();
        msg.append("SecurityServiceException{ principal=");
        msg.append(principal);

        msg.append("action=");
        msg.append(action);

        msg.append("item=");
        msg.append(item);

        return msg.toString();
    }
}
