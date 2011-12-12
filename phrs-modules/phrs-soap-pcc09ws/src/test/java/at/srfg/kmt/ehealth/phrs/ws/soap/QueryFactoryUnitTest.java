/*
 * Project :iCardea
 * File : QueryFactoryUnitTest.java
 * Encoding : UTF-8
 * Date : Apr 11, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import javax.xml.bind.JAXBException;
import org.hl7.v3.CD;
import org.hl7.v3.QUPCIN043100UV01;
import org.hl7.v3.QUPCIN043100UV01QUQIMT020001UV01ControlActProcess;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class QueryFactoryUnitTest {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.QueryFactoryUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QueryFactoryUnitTest.class);

    public QueryFactoryUnitTest() {
        // UNIMPLEMENTED
    }

    @Test
    public void testBuildQUPCIN043100UV01() throws JAXBException {
        final QUPCIN043100UV01 query = QueryFactory.buildQUPCIN043100UV01();
        assertNotNull(query);
        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess = 
                query.getControlActProcess();
        final CD code = controlActProcess.getCode();
        final String codeStr = code.getCode();
        assertEquals("QUPC_TE043100UV", codeStr);
    }
}
