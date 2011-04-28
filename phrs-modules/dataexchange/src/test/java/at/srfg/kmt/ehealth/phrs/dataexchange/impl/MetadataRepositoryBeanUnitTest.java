/*
 * Project :iCardea
 * File : MetadataRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Apr 26, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.MetadataRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.File;
import java.util.Set;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
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
 * Provides test for the <code>MetadataRepository</code> implementation.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see MetadataRepository
 * @see MetadataRepositoryBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class MetadataRepositoryBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.MetadataRepositoryBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MetadataRepositoryBeanUnitTest.class);
    /**
     * The <code>MetadataRepositoryBean</code> instance to test.
     */
    @EJB
    private MetadataRepository metadataRepositoryBean;

    /**
     * Builds a <code>MetadataRepositoryBeanUnitTest</code> instance.
     */
    public MetadataRepositoryBeanUnitTest() {
        // UNIMPLEMENTED
    }

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
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.dataexchage.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.dataexchange.impl and
        // at.srfg.kmt.ehealth.phrs.dataexchange.api and
        // at.srfg.kmt.ehealth.phrs.dataexchange.model are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure.
        ejbJar.addPackage(MetadataRepositoryBeanUnitTest.class.getPackage());

        final Package apiPackage = MetadataRepository.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = DynamicBean.class.getPackage();
        ejbJar.addPackage(modelPackage);

        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final EnterpriseArchive ear =
                ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
        ear.addModule(ejbJar);
        final File lib = ArchiveHelper.getCommonsBeanUtils();
        ear.addLibraries(lib);

        final String earStructure = ear.toString(true);
        LOGGER.debug("EAR jar structure on deploy is :");
        LOGGER.debug(earStructure);

        final String ejbStructure = ejbJar.toString(true);
        LOGGER.debug("EJB jar structure on deploy is :");
        LOGGER.debug(ejbStructure);

        return ear;
    }
    
    /**
     * Associates a given metadata to an existent class and try to obtain all 
     * the class properties for a given class that have a certain metadata. 
     * The class is identified after its (unique) uri.
     * The metdata is identified after its (non-unique) name.
     * 
     * @see MetadataRepository#getByMetadataName(java.lang.String, java.lang.String) 
     */
    @Test
    public void testGetMetadataByClassUriAndName() {
        final String classURI = "at.srfg.kmt.ehealth.phrs.datamodel.impl.Problem";
        final String metadataName = "isObservationCode";
        final Set<DynamicPropertyType> byMetadataName = 
                metadataRepositoryBean.getByMetadataName(classURI, metadataName);
        System.out.println("-->" + byMetadataName);
    }
}
