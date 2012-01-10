/*
 * Project :iCardea
 * File : DispatcherNotifier.java
 * Encoding : UTF-8
 * Date : Jan 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import at.srfg.kmt.ehealth.phrs.dispatch.api.Dispatcher;


/**
 * Thread target used to dispatch a task to a given Task Disposer.
 * A Thread that is using this target will use the specified dispatcher to execute
 * (dispatch) the given task. <br/> 
 * This class is not design to be extended. 
 *
 * @author The M1s
 * @version 0.1
 * @since 0.1
 */
final class DispatcherNotifierTarget implements Runnable {

    /**
     * The disposer used for the task.
     */
    private final Dispatcher dispatcher;

    /**
     * The task to be dispatch on the disposer.
     */
    private final Runnable toDispatch;

    /**
     * Builds a <code>DispatcherNotifierTarget</code> for a given dispatcher and
     * task (to be run on the given dispatcher). 
     * 
     * @param dispatcher the dispatcher user to dispatch the given task, it can
     * not be null.
     * @param toDispatch the task to be dispatch on the given dispatcher, it can
     * not be null.
     * @throws NullPointerException if the <code>dispatcher</code> or 
     * <code>toDispatch</code> arguments are null.
     */
    DispatcherNotifierTarget(Dispatcher dispatcher, Runnable toDispatch) {

        if (dispatcher == null) {
            final NullPointerException exception =
                    new NullPointerException("The dispatcher argumetn can not be null.");
            throw exception;
        }

        if (toDispatch == null) {
            final NullPointerException exception =
                    new NullPointerException("The toDispatch argumetn can not be null.");
            throw exception;
        }

        this.dispatcher = dispatcher;
        this.toDispatch = toDispatch;
    }

    @Override
    public void run() {
        dispatcher.dispatch(toDispatch);
    }
}
