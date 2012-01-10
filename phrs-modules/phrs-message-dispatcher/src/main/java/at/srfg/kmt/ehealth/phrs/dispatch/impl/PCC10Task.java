/*
 * Project :iCardea
 * File : DistpacherTask.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dispatch.impl;


import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This
 * <code>Runnable</code> class is used to generate a PCC10 message for a given
 * set of arguments, the message will be send to a given end-point. All the
 * information about the PCC10 message are contained in a Key-Pair set specified
 * via the constructor.
 * <br/> This class was not designed to be extended.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
final class PCC10Task implements Runnable {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC10Task.class);

    /**
     * All the properties for the PCC10 message.
     */
    private final Map<PCC10TaskProperty, Object> properties;

    /**
     * Builds a <code>PCC10Task</code> for a given set of Key-Pair set.
     *
     * @param properties all the properties required for a PCC10 message,
     * it can not be null.
     * @throws NullPointerException if the <code>properties</code> argument is
     * null.
     */
    public PCC10Task(final Map<PCC10TaskProperty, Object> properties) {

        if (properties == null) {
            final NullPointerException exception =
                    new NullPointerException("The properties argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public void run() {
        LOGGER.debug("Tries to execute this tasks with the following properties : {}", properties);
    }
}
