/*
 * Project :iCardea
 * File : SingleDistpatcher.java
 * Encoding : UTF-8
 * Date : Jan 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dispatch.impl;


import at.srfg.kmt.ehealth.phrs.dispatch.api.Dispatcher;


/**
 * Builds a single <code>Dispatcher</code> instance and make it available via
 * static methods.<br/>
 * This class is not design to be extended.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public final class SingleDistpatcher {

    /**
     * The unique instance for this factory.
     */
    private static final Dispatcher DISPATCHER = new DispatcherImpl();

    /**
     * Don't let anybody to instantiate this class.
     */
    private SingleDistpatcher() {
        // UNIMPLEMENTED
    }

    /**
     * Returns the unique <code>Dispatcher</code> instance. This methods
     * returns the same instance, always.
     *
     * @return the unique <code>Dispatcher</code> instance.
     */
    public static Dispatcher getDispatcher() {
        return DISPATCHER;
    }
}
