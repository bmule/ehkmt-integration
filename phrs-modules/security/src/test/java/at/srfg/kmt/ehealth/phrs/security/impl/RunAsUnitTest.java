/*
 * Project :iCardea
 * File : RunAsUnitTest.java
 * Date : Jan 16, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.util.Util;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
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
 * @author Mihai
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
    public class RunAsUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.UserManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(RunAsUnitTest.class);

    /**
     * The <code>MyService</code> instance to test.
     */
    @EJB
    private MyService myService;

    /**
     * Builds a <code>RunAsUnitTest</code> instance.
     */
    public RunAsUnitTest() {
        // UNIMPLMEMENTED
    }

    /**
     * Builds a <code>JavaArchive</code> that contains
     * all the required beans and libraries;
     * the Arquillian deploy it in to the running container
     * under the name test.ebj when the test starts.
     *
     * @return a <code>JavaArchive</code> which contains the <code>MyService</code>
     * interface and the <code>MyServiceBean</code> class.
     * @throws MalformedURLException if the test ejb jar can not be created
     * from any reasons.
     */
    @Deployment
    public static JavaArchive createDeployment() throws MalformedURLException {
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.security.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.security.impl and
        // at.srfg.kmt.ehealth.phrs.security are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure
        ejbJar.addPackage(RunAsUnitTest.class.getPackage());

        final Package apiPackage = Package.getPackage("at.srfg.kmt.ehealth.phrs.security.api");
        ejbJar.addPackage(apiPackage);

        Class c = PhrAction.class;
        final Package modelPackage = Package.getPackage("at.srfg.kmt.ehealth.phrs.security.model");
        ejbJar.addPackage(modelPackage);

        final Package utilsPackage = Util.class.getPackage();
        ejbJar.addPackage(utilsPackage);


        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final String ejbStructure = ejbJar.toString(true);
        logger.debug("EJB jar structure on deploy is :");
        logger.debug(ejbStructure);
        return ejbJar;
    }

    @Test
    public void testSecureCall() {
        final int value = 10;
        final int doStuffResult = myService.doStuff(value);
        assertEquals(value + 7, doStuffResult);
    }
}
