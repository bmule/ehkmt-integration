/*
 * Project :iCardea
 * File : QUPCAR004030UVWebService.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import javax.jws.WebService;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This web service end point is able to process PCC10 queries.
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

    @Override
    public MCCIIN000002UV01 qupcAR004030UVQUPCIN043200UV(QUPCIN043200UV01 qpcnv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
