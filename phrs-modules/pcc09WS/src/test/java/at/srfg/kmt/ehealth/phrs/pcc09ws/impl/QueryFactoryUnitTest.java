/*
 * Project :iCardea
 * File : QueryFactoryUnitTest.java 
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import static org.junit.Assert.*;
import javax.xml.bind.JAXBException;
import org.hl7.v3.QUPCIN043100UV01;
import org.junit.Test;


/**
 * Proves the <code>QueryFactoryUnit</code> implementation.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see QueryFactoryUnit
 */
public class QueryFactoryUnitTest {

    /**
     * Builds an empty PCC09 query and proves if the result is not null.
     * 
     * @throws JAXBException by any XML parsing problem.
     */
    @Test
    public void testBuildQUPCIN043100UV01() throws JAXBException {
        final QUPCIN043100UV01 query = QueryFactory.buildQUPCIN043100UV01();
        assertNotNull(query);
    }
}
