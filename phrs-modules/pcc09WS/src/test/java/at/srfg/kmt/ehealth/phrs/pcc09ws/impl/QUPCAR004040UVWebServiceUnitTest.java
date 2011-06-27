/*
 * Project :iCardea
 * File : TestJ.java
 * Encoding : UTF-8
 * Date : May 5, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;
import java.util.Set;
import java.net.MalformedURLException;
import javax.ejb.EJB;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class QUPCAR004040UVWebServiceUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.VocabularyLoaderBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004040UVWebServiceUnitTest.class);

    /**
     * Builds a <code>JavaArchive</code> that contains
     * all the required beans and libraries;
     * the Arquillian deploy it in to the running container
     * under the name test.ebj when the test starts.
     *
     * @return a <code>JavaArchive</code> which contains all the needed calsses.
     * @throws MalformedURLException if the test ejb jar can not be created
     * from any reasons.
     */
    @Deployment
    public static Archive<?> createDeployment() throws MalformedURLException {

        final WebArchive war =
                ShrinkWrap.create(WebArchive.class, "test.war");

        final String warStructure = war.toString(true);
        LOGGER.debug("WAR jar structure on deploy is :");
        LOGGER.debug(warStructure);
        System.out.println("-->" + warStructure);


        return war;
    }
    
    @Test
    public void dd() {
        System.out.println("---");
        System.out.println("---");
        System.out.println("---");
        System.out.println("---");
        System.out.println("---");
    }
            
    
    
}
