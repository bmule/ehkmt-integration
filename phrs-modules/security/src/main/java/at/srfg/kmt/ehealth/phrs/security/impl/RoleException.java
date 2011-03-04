/*
 * Project :iCardea
 * File : RoleException.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;

/**
 * Indicates a problem during a <code>RHRRole</code> related
 * action. <br>
 * In most of the cases this exception is caused by an other exception,
 * this cause can be obtained using the getCause() method.<br>
 * This exception can also transport the <code>Role</code> instance
 * that create the problem, this can be obtained by using the
 * <code>getRole</code> method.<br>
 * This exception can be only instantiated <b>only</b> in the
 * <code>at.srfg.kmt.ehealth.phrs.security.impl</code> package.
 *
 * @author Mihai
 */
public class RoleException extends RuntimeException {

    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * The role that may cause the exception.
     */
    private PhrRole role;

    /**
     * Constructs an <code>RoleException</code>
     * without a detail message or a specified cause.
     */
    RoleException() {
        // UNIMPLEMENTED
    }

    /**
     * Constructs an <code>RoleException</code>
     * with a detail message.
     *
     * @param message the detail message.
     */
    RoleException(String string) {
        super(string);
    }

    /**
     * Constructs an <code>RoleException</code>
     * with a specified <code>Throwable</code>
     * cause, this cause can be obtained using the <code>getCause()</code>
     * method, this cause can be obtained using the getCause()
     * method.
     *
     * @param cause the cause for this exception.
     * @see         Exception#getCause()
     */
    RoleException(Throwable thrwbl) {
        super(thrwbl);
    }

    /**
     * Constructs an <code>RoleException</code>
     * with a detail message and a specified
     * <code>InterruptedException</code> cause, this cause
     * can be obtained using the getCause() method.
     *
     * @param message   the detail message.
     * @param cause     the cause for this exception.
     * @see             Exception#getCause()
     */
    RoleException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    /**
     * Returns the <code>RHRRole</code> that may creates the problem.
     *
     * @return the <code>RHRRole</code> that may creates the problem.
     */
    public PhrRole getRole() {
        return role;
    }

    public void setRole(PhrRole role) {
        this.role = role;
    }
}
