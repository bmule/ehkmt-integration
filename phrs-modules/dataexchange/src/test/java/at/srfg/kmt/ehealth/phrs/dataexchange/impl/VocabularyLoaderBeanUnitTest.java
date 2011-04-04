/*
 * Project :iCardea
 * File : VocabularyLoaderBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 25, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;


import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import static org.junit.Assert.*;
import java.util.Set;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.VocabularyLoader;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
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
 * Provides test for the <code>VocabularyLoader</code> implementation.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class VocabularyLoaderBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.VocabularyLoaderBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VocabularyLoaderBeanUnitTest.class);

    /**
     * The <code>VocabularyLoader</code> instance to test.
     */
    @EJB
    private VocabularyLoader vocabularyLoader;

    /**
     * The <code>ControlledItemRepository</code> instance to test.
     */
    @EJB
    private ControlledItemRepository controlledItemRepository;

    /**
     * Builds a <code>VocabularyLoaderBeanUnitTest</code> instance.
     */
    public VocabularyLoaderBeanUnitTest() {
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
        ejbJar.addPackage(VocabularyLoaderBeanUnitTest.class.getPackage());

        final Package apiPackage = VocabularyLoader.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = ControlledItem.class.getPackage();
        ejbJar.addPackage(modelPackage);


        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        // This file contains the items to load.
        ejbJar.addResource("phrs.snomed-ct-test.properties",
                "phrs.snomed-ct.properties");
        // This file contains the tag relations for the loaded relations.
        ejbJar.addResource("phrs.snomed-ct-tags-test.properties",
                "phrs.snomed-ct-tags.properties");

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
     * It loads the test properties files and proves if the items are really
     * loaded and if the tag relations are correct done.
     */
    @Test
    public void testLoad() {
        vocabularyLoader.load();

        // this details (codes) are according with (the test file) 
        // phrs.snomed-ct.properties
        final String SNOMED = Constants.SNOMED;
        final String syntomCode = "19019007";
        final String syntomDisplayName = "Symptom";
        final String chestPainCode = "29857009";
        final String chestPainDisplayName = "Chest pain";

        // proves the symptom item
        final ControlledItem symptomItem =
                controlledItemRepository.getByCodeSystemAndCode(SNOMED, syntomCode);
        assertNotNull(symptomItem);

        final String getSyntomDispayName = symptomItem.getPrefLabel();
        assertEquals(syntomDisplayName, getSyntomDispayName);

        final String getSyntomCode = symptomItem.getCode();
        assertEquals(syntomCode, getSyntomCode);

        // proves the chest pain item
        final ControlledItem chestPainItem =
                controlledItemRepository.getByCodeSystemAndCode(SNOMED, chestPainCode);
        assertNotNull(chestPainItem);

        final String getChestPainDisplayName = chestPainItem.getPrefLabel();
        assertEquals(chestPainDisplayName, getChestPainDisplayName);

        final String getChestPainCode = chestPainItem.getCode();
        assertEquals(chestPainCode, getChestPainCode);

        // prove the tagging

        // frist I prove the tagged item
        final Set<ControlledItem> byTag =
                controlledItemRepository.getByTag(symptomItem);
        // the test files contains two items and one tag the other, 
        // so I have only one tag. The symptom is the tag and 
        // the chest pain is the tagged item
        assertEquals(1, byTag.size());

        // and then I prove the tag
        // this is the tagged item - it must be chest pain item
        final ControlledItem item = byTag.iterator().next();
        assertEquals(chestPainItem, item);

        final Set<ControlledItem> tags =
                controlledItemRepository.getTags(chestPainItem);
        // the test files contains two items and one tag the other, 
        // so I have only one tag. The symptom is the tag and 
        // the chest pain is the tagged item
        assertEquals(1, tags.size());

        final ControlledItem tag = tags.iterator().next();
        assertEquals(symptomItem, tag);
    }
}
