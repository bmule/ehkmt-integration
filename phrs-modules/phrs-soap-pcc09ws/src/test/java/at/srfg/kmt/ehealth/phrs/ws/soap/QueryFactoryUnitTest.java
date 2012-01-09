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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests the functionality for the <code>QueryFactory</code> class. <br/>
 * This class can not be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @see QueryFactory
 */
public final class QueryFactoryUnitTest {

    /**
     * Builds a <code>QueryFactoryUnitTest</code> instance.
     */
    public QueryFactoryUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds an empty PCC9 request and proves if the Control Act Process Code
     * (from the new builded PCC9 request) has the right value.
     *
     * @throws JAXBException by any JAXB related exception, if this exception
     * occurs then this test fails.
     */
    @Test
    public void testBuildEmptyPCC9Request() throws JAXBException {
        final QUPCIN043100UV01 query = QueryFactory.buildEmptyPCC9Request();
        assertNotNull("The PCC9 request can not be null.", query);
        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess =
                query.getControlActProcess();
        final CD code = controlActProcess.getCode();
        final String codeStr = code.getCode();
        final String expectedCode = "QUPC_TE043100UV";
        assertEquals("This PCC9 message must contains the code : " +  codeStr,
                expectedCode, codeStr);
        
    }
}
