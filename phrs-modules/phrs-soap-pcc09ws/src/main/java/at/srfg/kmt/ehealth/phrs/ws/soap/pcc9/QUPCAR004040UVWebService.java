/*
 * Project :iCardea
 * File : QUPCAR004040UVWebService.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingType;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The IHE CM PCC9 web SOAP based web service.
 * <b>Note : </b> This WS is according with the 
 * <a href="http://www.w3.org/TR/soap12-part0/">SOAP 1.2</a> specifications.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
@WebService(endpointInterface = "org.hl7.v3.QUPCAR004040UVPortType")
@HandlerChain(file = "handler-chain.xml")
@BindingType(value="http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class QUPCAR004040UVWebService implements QUPCAR004040UVPortType {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004040UVWebService</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004040UVWebService.class);

    /**
     * Builds a
     * <code>QUPCAR004040UVWebService</code> instance.
     */
    public QUPCAR004040UVWebService() {
        // UNIMPLEMENTED
    }

    /**
     * Handles a PCC09 query and returns the corresponding acknowledge
     * if the query was proper processed or null by if the query
     * can not be process from any reason.
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

        try {
            final MCCIIN000002UV01 ack = QueryFactory.buildPCC9Acknowledge();
            return ack;
        } catch (JAXBException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }
}
