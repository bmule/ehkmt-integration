/*
 * Project :iCardea
 * File : DispatcherImpl.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dispatch.impl;


import at.srfg.kmt.ehealth.phrs.dispatch.api.Dispatcher;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * It is used to execute task based on a thread pool. <br/>
 * <b>Usage : </b> Consider the following snippet :
 * <pre>
 * DispatcherImpl d = new DispatcherImpl();
 * d.dispatch(new MyTask());
 * </pre>
 * where the MyTask is Runnable implementation.
 * 
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
final class DispatcherImpl implements Dispatcher {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dispatch.impl.DispatcherImpl</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DispatcherImpl.class);

    /**
     * Holds all the threads used to by this dispatcher.
     */
    private final ThreadPoolExecutor pool;

    /**
     * The queue used by the thread pool.
     */
    private final ArrayBlockingQueue<Runnable> queue;

    /**
     * Builds a
     * <code>DispatcherImpl</code> for the following arguments : <ul> <li>
     * poolSize = 2. <li> maxPoolSize = 5. <li> keepAliveTime = 10 seconds. <li>
     * </ul>
     *
     * @see #DispatcherImpl(int, int, int)
     */
    DispatcherImpl() {
        this(2, 5, 10);
    }

    /**
     * Builds a
     * <code>DispatcherImpl</code> for a given list of arguments.
     *
     * @param poolSize the thread pool start size.
     * @param maxPoolSize the maximal amount of threads.
     * @param keepAliveTime the amount of time (in seconds) for thread to be
     * keep active.
     */
    public DispatcherImpl(int poolSize, int maxPoolSize, int keepAliveTime) {
        queue = new ArrayBlockingQueue<Runnable>(5);
        pool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, queue);
    }

    /**
     * Executes a task.
     *
     * @param task the task to execute, it can not be null.
     * @throws NullPointerException if the <code>task</code> is null.
     */
    @Override
    public void dispatch(Runnable task) {

        if (task == null) {
            final NullPointerException exception =
                    new NullPointerException("The task argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        pool.execute(task);
        LOGGER.debug("Actual task count is : {}", queue.size());
    }

    /**
     * Shot down this dispatcher, after this this dispatcher can not be used.
     */
    @Override
    public void shutDown() {
        pool.shutdown();
        LOGGER.debug("Actual task count is : {}", queue.size());
        LOGGER.debug("This dispatcher is now shut down");
    }
}
