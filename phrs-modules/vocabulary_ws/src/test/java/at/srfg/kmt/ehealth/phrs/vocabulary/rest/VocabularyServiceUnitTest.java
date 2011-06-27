/*
 * Project :iCardea
 * File : VocabularyServiceUnitTest.java
 * Encoding : UTF-8
 * Date : May 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.rest;


import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
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


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class VocabularyServiceUnitTest {

    private static final String HOST = "localhost";

    private static final int PORT = 8080;

    private static final String CONTEXT_PATH = "test";

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.VocabularyServiceUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VocabularyServiceUnitTest.class);

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
    public static Archive<?> createDeployment() throws MalformedURLException {
        final WebArchive war =
                ShrinkWrap.create(WebArchive.class, "test.war");

        final Package wsPackage = VocabularyService.class.getPackage();
        war.addPackage(wsPackage);

        final Set<File> libs = ArchiveHelper.getLibs();
        for (File lib : libs) {
            war.addLibraries(lib);
        }

        // this web descriptor contains the RESTEasy servlet listenter.
        war.addWebResource("web-test.xml", "web.xml");

        final EnterpriseArchive ear =
                ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
        ear.addModule(war);

        final String earStructure = ear.toString(true);
        LOGGER.debug("EAR jar structure on deploy is :");
        LOGGER.debug(earStructure);
        System.out.println("-->" + earStructure);

        final String warStructure = war.toString(true);
        LOGGER.debug("War structure on deploy is :");
        LOGGER.debug(warStructure);

        return ear;
    }

    /**
     * Loads all the controlled items and prove if this is properly done.
     */
    @Test
    public void getLoad() throws URISyntaxException, IOException {
        final String applicationPath =
                "phrs/vocabulary/load";
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
