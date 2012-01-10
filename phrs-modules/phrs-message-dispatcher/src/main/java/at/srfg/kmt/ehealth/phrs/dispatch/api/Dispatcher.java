/*
 * Project :iCardea
 * File : DistpacherTask.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dispatch.api;


/**
 * Define a Dispatcher functionality. The only Dispatcher functionality is to
 * execute asynchronous tasks.
 *
 * @author the Mihai
 * @version 0.1
 * @since 0.1
 */
public interface Dispatcher {

    /**
     * Registers a new tasks to the dispatcher.
     *
     * @param task the task to be dispatch.
     */
    void dispatch(Runnable task);
    
    /**
     * Close this Dispatcher.
     */
    void shutDown();
}
