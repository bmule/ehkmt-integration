/*
 * Project :iCardea
 * File : NotifyRestWS.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


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
     * GET based REST full web service used to trigger a PCC10 transaction.
     * 
     * @param in
     * @return 
     */
    @GET
    @Path("/notify")
    @Produces("application/json")
    public Response notify(@QueryParam("q") String q) {
        LOGGER.debug("Inputs : {}", q);

        return Response.status(Status.OK).build();
    }
}
