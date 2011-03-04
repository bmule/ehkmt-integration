/*
 * Project :iCardea
 * File : ActorException.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;



import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;

/**
 * Indicates a problem during a <code>RHRActor</code> related
 * action. <br>
 * In most of the cases this exception is caused by an other exception,
 * this cause can be obtained using the getCause() method.<br>
 * This exception can also transport the <code>RHRActor</code> instance
 * that create the problem, this can be obtained by using the
 * <code>RHRActor</code> method.<br>
 * This exception can be only instantiated <b>only</b> in the
 * <code>at.srfg.kmt.ehealth.phrs.security.impl</code> package.
 *
 * @author Mihai
 */
public class ActorException extends RuntimeException {

    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * The actor that may cause the exception.
     */
    private PhrActor actor;

    /**
     * Constructs an <code>ActorException</code>
     * without a detail message or a specified cause.
     */
    ActorException() {
        // UNIMPLEMENTED
    }

    /**
     * Constructs an <code>ActorException</code>
     * with a detail message.
     *
     * @param message the detail message.
     */
    ActorException(String string) {
        super(string);
    }

    /**
     * Constructs an <code>ActorException</code>
     * with a specified <code>Throwable</code>
     * cause, this cause can be obtained using the <code>getCause()</code>
     * method, this cause can be obtained using the getCause()
     * method.
     *
     * @param cause the cause for this exception.
     * @see         Exception#getCause()
     */
    ActorException(Throwable thrwbl) {
        super(thrwbl);
    }

    /**
     * Constructs an <code>ActorException</code>
     * with a detail message and a specified
     * <code>InterruptedException</code> cause, this cause
     * can be obtained using the getCause() method.
     *
     * @param message   the detail message.
     * @param cause     the cause for this exception.
     * @see             Exception#getCause()
     */
    ActorException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    /**
     * Returns the <code>ActorException</code> that may creates the problem.
     *
     * @return the <code>ActorException</code> that may creates the problem.
     */
    public PhrActor getRole() {
        return actor;
    }

    public void setRole(PhrActor actor) {
        this.actor = actor;
    }
}
