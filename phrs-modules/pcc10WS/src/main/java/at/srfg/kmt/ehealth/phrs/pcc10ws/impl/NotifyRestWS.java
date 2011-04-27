/*
 * Project :iCardea
 * File : NotifyRestWS.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.pcc10ws.api.Processor;
import at.srfg.kmt.ehealth.phrs.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This REST web service is call when a PCC10 transaction is required.
</br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>/pcc10ws/restws/pcc10/notify?q=XXX - used to 
 * notify that a cerytain PCC09 was processed.
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Path("/restws/pcc10")
public class NotifyRestWS {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.NotifyRestWS</code>.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyRestWS.class);

    /**
     * The chain of processor used to process the service input.
     */
    private final List<Processor> processors;

    /**
     * Builds a <code>NotifyRestWS</code> instance.
     */
    public NotifyRestWS() {
        processors = new ArrayList<Processor>();
        processors.add(new MedicationPorcessor());
        // it handles the blood preasure and body weight
        processors.add(new VitalSignsProcessor()); 
        processors.add(new ProblemProcessor());
    }

    /**
     * GET based REST full web service used to trigger a PCC10 transaction.<br>
     * This web service can be access on :  
     * <JBOSS URI>/pcc10ws/restws/pcc10/notify?q=XXX
     * 
     * @param sender used to identify the sender.
     * @param q the query. Its syntax is user id - care provision code.
     * 
     * @return <code>javax.ws.rs.core.Response.Status.OK</code>, always.
     */
    @GET
    @Path("/notify")
    @Produces("application/json")
    public Response notify(@QueryParam("sender") String sender,
            @QueryParam("q") String q) {

        final Object[] forLog = Util.forLog(q, sender);
        LOGGER.debug("Tries to process input : {} from sender : {}", forLog);

        // this is just a hot fox for the review, it only sends a default file
        // according wiht an paramter.
//        if ("pcc09".equals(sender)) {
//            final FileBasedProcessor processor = new FileBasedProcessor();
//            processor.process(q);
//            final Set<Exception> exceptions = processor.getExceptions();
//            logExceptions(exceptions);
//            final Response result = processor.getResult();
//            return result;
//        }

        if (q == null) {
            LOGGER.warn("No query to process.");
            return Response.status(Status.NO_CONTENT).build();
        }
        
        final int indexOf = q.indexOf("-");
        if (indexOf == -1) {
            LOGGER.error("Bad syntax for the request");
            return Response.status(Status.BAD_REQUEST).build();
        }

        final String userId = q.substring(0, indexOf);
        LOGGER.debug("User id : {}", userId);
        String provisionCode = q.substring(indexOf + 1, q.length());
        LOGGER.debug("Provision Code  : {}", provisionCode);

        Response result = Response.status(Status.NOT_FOUND).build();
        for (Processor processor : processors) {
            final boolean canProcess = processor.canProcess(q);
            if (canProcess) {
                processor.process(q);
                result = (Response) processor.getResult();
                final Set<Exception> exceptions = processor.getExceptions();
                logExceptions(exceptions);
                break;
            }
        }

        return result;
    }

    private void logExceptions(Set<Exception> exceptions) {
        for (Exception exception : exceptions) {
            LOGGER.error(exception.getMessage(), exception);
        }
    }
}
