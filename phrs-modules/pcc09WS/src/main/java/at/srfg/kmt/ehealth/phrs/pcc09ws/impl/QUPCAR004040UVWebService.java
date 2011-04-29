/*
 * Project :iCardea
 * File : QUPCAR004040UVImpl.java 
 * Encoding : UTF-8
 * Date : Apr 6, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This web service end point is able to process PCC09 queries.
 *  
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@WebService(endpointInterface = "org.hl7.v3.QUPCAR004040UVPortType")
public class QUPCAR004040UVWebService implements QUPCAR004040UVPortType {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004040UVWebService</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004040UVWebService.class);

    /**
     * Handles a PCC09 query and returns a acknowledge if the query was proper
     * processed or null by if the query can not be process from any reason.
     * 
     * @param body the query, it can not be null.
     * @return and PCC09 specific acknowledge or null if the the query can not 
     * be process from any reason.
     */
    @Override
    public MCCIIN000002UV01 qupcAR004040UVQUPCIN043100UV(QUPCIN043100UV01 body) {
        LOGGER.debug("Query [{}] was received. The query preocess starts.", body);
        
        if (body == null) {
            final NullPointerException nullException =
                    new NullPointerException("The body argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }
        
        final QUPCIN043100UV01Processor processor =
                new QUPCIN043100UV01Processor();
        processor.process(body);
        
        PCC09DroneAgent.send(body);
        
        try {
            final MCCIIN000002UV01 ack = QueryFactory.buildMCCIIN000002UV01();
            final Object [] toLog  = {body, ack};
            LOGGER.debug("The query [{}] was succesfully procesed, its acknowledge is  [{}]", toLog);
            return ack;
        } catch (JAXBException exception) {
            LOGGER.error(exception.getMessage(), exception);
        }
        
        
        // FIXME : singals the error back to the client.
        return null;
    }
}
