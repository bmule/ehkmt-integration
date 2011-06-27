/*
 * Project :iCardea
 * File : ControlledItemRepositoryRestWSUnitTest.java
 * Encoding : UTF-8
 * Date : May 6, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import java.net.MalformedURLException;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelClassFactory;



/**
 * Unit test used to prove the <code>ControlledItemRepositoryRestWS</code>, 
 * this class exposes RESTFull Web services, JSON based. <br>
 * The test uses the RestEasy client libraries.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see ControlledItemRepositoryRestWS
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class ControlledItemRepositoryRestWSUnitTest {

    public static final String HOST = "localhost";

    public static final int PORT = 8080;
    
    final String CONTEXT_PATH = "test";

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ControlledItemRepositoryRestWSUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ControlledItemRepositoryRestWSUnitTest.class);

    /**
     * Builds a <code>WebArchive</code> that contains
     * all the required beans and libraries;
     * the Arquillian deploy it in to the running container
     * under the name test.war when the test starts.
     *
     * @return a <code>WebArchive</code> which contains all the needed calsses.
     * @throws MalformedURLException if the test war jar can not be created
     * from any reasons.
     */
    @Deployment
    public static WebArchive createDeployment() throws MalformedURLException {
        final WebArchive war =
                ShrinkWrap.create(WebArchive.class, "test.war");
        final Package modelPackage = ModelClassFactory.class.getPackage();
        war.addPackage(modelPackage);

        final Package wsPackage = ControlledItemRepositoryRestWS.class.getPackage();
        war.addPackage(wsPackage);

        final Package dcPackage = DynamicClassRepository.class.getPackage();
        war.addPackage(dcPackage);
        
        final Package utilPackage = JBossJNDILookup.class.getPackage();
        war.addPackage(utilPackage);

        final Set<File> libs = ArchiveHelper.getLibs();
        for (File lib : libs) {
            war.addLibraries(lib);
        }

        // this web descriptor contains the RESTEasy servlet listenter.
        war.addWebResource("web-test.xml", "web.xml");


        final String warStructure = war.toString(true);
        LOGGER.debug("WAR  structure on deploy is :");
        LOGGER.debug(warStructure);

        return war;
    }

    /**
     * Loads all the controlled items and prove if this is properly done.
     */
    @Test
    public void getLoad() throws URISyntaxException, IOException {
        final String applicationPath = 
                "dynamic_class_repository/loadDefaultClasses";
        final HttpGet getMeth = 
                HttpHelper.buildGET(HOST, PORT, CONTEXT_PATH, applicationPath, null);
        LOGGER.debug("Tries to executes {} ", toString(getMeth));
        
        final HttpResponse resounse = HttpHelper.excecute(getMeth);
        final int statusCode = resounse.getStatusLine().getStatusCode();
        // according with the specification I expect a 200
        assertEquals("Wrong HTTP status code;", 200, statusCode);
    }
    
    private String toString(HttpRequestBase base) {
        final StringBuilder result = new StringBuilder();
        final String method = base.getMethod();
        final String uri = base.getURI().toString();
        result.append("HTTP Method : ");
        result.append(method);
        result.append(", uri :");
        result.append(uri);
        
        return result.toString();
    }
}
