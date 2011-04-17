/*
 * Project :iCardea
 * File : QUPCAR004030UVWebService.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import javax.jws.WebService;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This web service end point is able to process PCC10 queries. </br>
 * The main purpose for this class is a test one, this calss is able to accept 
 * PCC10 requests and to log them using the using the underlying logging 
 * mechanisms. If the request is successfully logged then a PCC10 specific 
 * acknowledge is send it back.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@WebService(endpointInterface = "org.hl7.v3.QUPCAR004030UVPortType")
public class QUPCAR004030UVWebService implements QUPCAR004030UVPortType {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004030UVWebService</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004030UVWebService.class);
    
    /**
     * Used to create PCC10 specific acknowledges.
     */
    private final static PCC10AcknowledgeFactory ACKNOWLEDGE_FACTORY;
    
    /**
     * Used to initialize the <code>ACKNOWLEDGE_FACTORY</code>.
     */
    static {
        // FIXME : this construct can create problems on application server with 
        // mutiple JVMs. use life cycle method in the future.
        ACKNOWLEDGE_FACTORY = new PCC10AcknowledgeFactory();
    }
    
    
    /**
     * Builds a <code>QUPCAR004030UVWebService</code> instance.
     */
    public QUPCAR004030UVWebService() {
        // UNIMPLEMENED
    }

    /**
     * Receive the PCC10 request and log it (using the underlying logging 
     * mechanisms).
     * 
     * @param request the incoming request.
     * @return an success (PCC10 specific) acknowledge.
     */
    @Override
    public MCCIIN000002UV01 qupcAR004030UVQUPCIN043200UV(QUPCIN043200UV01 request) {
        LOGGER.debug("Query [{}] was received. The query preocess starts.", request);
        
        final MCCIIN000002UV01 acknoledge;
        try {
            acknoledge = ACKNOWLEDGE_FACTORY.build();
        } catch (PCC10BuildException exception) {
            LOGGER.error(exception.getMessage(), exception);
            // FIXME : notify the client, padawan.
            return null;
        }
        
        return acknoledge;
    }
}
