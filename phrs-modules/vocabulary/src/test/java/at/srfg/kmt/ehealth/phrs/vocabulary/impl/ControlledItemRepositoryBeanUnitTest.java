/*
 * Project :iCardea
 * File : ControlledItemRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.impl;


import at.srfg.kmt.ehealth.phrs.vocabulary.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.vocabulary.model.ControlledItem;
import java.io.File;
import static org.junit.Assert.*;
import java.util.Set;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
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
 * Provides test for the <code>ControlledItemRepository</code> implementation.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see ControlledItemRepository
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class ControlledItemRepositoryBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ControlledItemRepositoryBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ControlledItemRepositoryBeanUnitTest.class);

    /**
     * The <code>ControlledItemRepository</code> instance to test.
     */
    @EJB
    private ControlledItemRepository controlledItemRepository;

    /**
     * Builds a <code>DymanicClassRepositoryBeanUnitTest</code> instance.
     */
    public ControlledItemRepositoryBeanUnitTest() {
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
        ejbJar.addPackage(ControlledItemRepositoryBeanUnitTest.class.getPackage());

        final Package apiPackage = ControlledItemRepository.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = ControlledItem.class.getPackage();
        ejbJar.addPackage(modelPackage);


        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final EnterpriseArchive ear =
                ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
        ear.addModule(ejbJar);

        final String earStructure = ear.toString(true);
        LOGGER.debug("EAR jar structure on deploy is :");
        LOGGER.debug(earStructure);

        final String ejbStructure = ejbJar.toString(true);
        LOGGER.debug("EJB jar structure on deploy is :");
        LOGGER.debug(ejbStructure);

        return ear;
    }

    /**
     * Creates and persists an <code>ControlledItem</code> item and proves if
     * the persist was properly done.
     * 
     * 
     * @see ControlledItemRepository#add(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     */
    @Test
    public void testAdd() {
        createItem();
    }

    /**
     * Creates and persists a <code>ControlledItem</code>.
     * 
     * @return the persisted <code>ControlledItem</code>.
     */
    private ControlledItem createItem() {
        final ControlledItem item = DummyModelFactory.createControlledItem();
        final boolean add = controlledItemRepository.add(item);
        assertTrue(add);
        final String codeSystem = item.getCodeSystem();
        final String code = item.getCode();

        final boolean contains = controlledItemRepository.contains(codeSystem, code);
        assertTrue(contains);

        final ControlledItem getItem =
                controlledItemRepository.getByCodeSystemAndCode(codeSystem, code);
        assertNotNull(code);
        // assertEquals(item, getItem);

        return getItem;
    }

    /**
     * Creates , persists and retrieve a <code>ControlledItem</code>.
     * The retrieve is done after the code and codeSystem attributes.
     * 
     * @see ControlledItemRepository#getByCodeSystemAndCode(java.lang.String, java.lang.String) 
     */
    @Test
    public void testGetByCodeSystemAndCode() {
        // create and persist the item
        final ControlledItem item = createItem();
        final String code = item.getCode();
        final String codeSystem = item.getCodeSystem();

        // searches the item
        final ControlledItem getItem =
                controlledItemRepository.getByCodeSystemAndCode(codeSystem, code);
        assertNotNull(getItem);

        // prove if I have the same item
        assertEquals(item, getItem);
    }

    /**
     * Create, persist and tag a given <code>ControlledItem</code>.
     * This test proves also if the search after a given tags works properly,
     * more precisely it create/persist a item, it tag it and it search it
     * (the item) by using the tag.
     * 
     * @see #testTagged() 
     * @see ControlledItemRepository#tag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem, at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     * @see ControlledItemRepository#getByTag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     */
    @Test
    public void testTag() {
        final TagTransporter transporter = addTag();
        final ControlledItem tag = transporter.getTag();
        final ControlledItem toTag = transporter.getToTag();

        // I obtain the tag instance using the code and code system
        final String tagCodeSystem = tag.getCodeSystem();
        final String tagCode = tag.getCode();
        final ControlledItem getTag =
                controlledItemRepository.getByCodeSystemAndCode(tagCodeSystem, tagCode);
        assertNotNull(getTag);

        // I search for a item tagged with a given tag
        final Set<ControlledItem> byTag = controlledItemRepository.getByTag(getTag);

        // I only tag one item
        assertEquals(1, byTag.size());

        // here I obtain the tagged item (tagged == a item tag with a given tag)
        final ControlledItem toTagGet = byTag.iterator().next();

        assertEquals(toTag, toTagGet);
    }

    /**
     * Create, persist and tag a given <code>ControlledItem</code>.
     * This test also proves the searches for tags that are applied on a given
     * item works properly, more precisely it create/persist a item, it tag it 
     * and it search all the tags that are applied to the item.
     * 
     * @see #testTag() 
     * @see ControlledItemRepository#tag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem, at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     * @see ControlledItemRepository#getTags(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     */
    @Test
    public void testTagged() {
        final TagTransporter transporter = addTag();
        final ControlledItem tag = transporter.getTag();
        final ControlledItem toTag = transporter.getToTag();

        // I obtain the tagged item instance using the code and code system.
        // (tagged == a item tag with a given tag)
        final String toTagCodeSystem = toTag.getCodeSystem();
        final String toTagCode = toTag.getCode();

        final ControlledItem getItem =
                controlledItemRepository.getByCodeSystemAndCode(toTagCodeSystem, toTagCode);
        assertNotNull(getItem);

        final Set<ControlledItem> tags = controlledItemRepository.getTags(getItem);

        // I  have only one tag
        assertEquals(1, tags.size());

        // here I obtain all the tags for a given item
        final ControlledItem getTag = tags.iterator().next();

        assertEquals(tag, getTag);
    }

    private class TagTransporter {

        private ControlledItem tag;

        private ControlledItem toTag;

        public TagTransporter(ControlledItem tag, ControlledItem toTag) {
            this.tag = tag;
            this.toTag = toTag;
        }

        public ControlledItem getTag() {
            return tag;
        }

        public ControlledItem getToTag() {
            return toTag;
        }
    }

    private TagTransporter addTag() {
        // the next lines builds and register the tag
        final String tagCode = DummyModelFactory.createUniqueString(".tag.code");
        final ControlledItem tag =
                DummyModelFactory.createControlledItem("tag", tagCode);
        final boolean addTag = controlledItemRepository.add(tag);
        assertTrue(addTag);

        // the next lines builds and register the item to be tag
        final String toTagCode = DummyModelFactory.createUniqueString(".totag.code");
        final ControlledItem toTag =
                DummyModelFactory.createControlledItem("toTag", toTagCode);
        final boolean addToTag = controlledItemRepository.add(toTag);
        assertTrue(addToTag);

        // I tag the item
        controlledItemRepository.tag(toTag, tag);

        final ControlledItem resTag =
                controlledItemRepository.getByCodeSystemAndCode(tag.getCodeSystem(), tag.getCode());
        assertNotNull(resTag);

        final ControlledItem resToTag =
                controlledItemRepository.getByCodeSystemAndCode(toTag.getCodeSystem(), toTag.getCode());
        assertNotNull(resToTag);

        return new TagTransporter(resTag, resToTag);
    }

    /**
     * Create, persist, tag a given <code>ControlledItem</code> and untag it .
     * More precisely it create/persist a item, it tag it, untag it  
     * and it search all the tags that are applied to the item. The search must
     * return an empty collection.
     * 
     * @see ControlledItemRepository#tag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem, at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     * @see ControlledItemRepository#untag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem, at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     * @see ControlledItemRepository#getByTag(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     * @see ControlledItemRepository#getTags(at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem) 
     */
    @Test
    public void testUntag() {
        final TagTransporter transporter = addTag();

        final ControlledItem tag = transporter.getTag();
        final ControlledItem toTag = transporter.getToTag();

        // removes the tag from the toTag item
        controlledItemRepository.untag(toTag, tag);

        // no more tags for the given item
        final Set<ControlledItem> byTag = controlledItemRepository.getByTag(tag);
        assertTrue(byTag.isEmpty());

        // the given itm has no more tags
        final Set<ControlledItem> tags = controlledItemRepository.getTags(toTag);
        assertTrue(tags.isEmpty());
    }

    /**
     * Tests the getByPrefLabel method. More precisely this test add a controlled
     * item with a given pref label and after this it search it using its pref 
     * label.
     */
    @Test
    public void testGetByPrefLabel() {
        final ControlledItem item = createItem();
        final String prefLabel = item.getPrefLabel();
        final Set<ControlledItem> byPrefLabel =
                controlledItemRepository.getByPrefLabel(prefLabel);
        assertNotNull(byPrefLabel);
        assertEquals(1, byPrefLabel.size());
    }

    /**
     * Tests the getByPrefLabel method. More precisely this test add a controlled
     * item with a given pref label and after this it search it using its pref 
     * label prefix.
     */
    @Test
    public void testGetByPrefLabelPrefix() {
        final ControlledItem item = createItem();
        final String prefLabel = item.getPrefLabel();
        final Set<ControlledItem> byPrefLabel =
                controlledItemRepository.getByPrefLabelPrefix(prefLabel);
        assertNotNull(byPrefLabel);
        assertEquals(1, byPrefLabel.size());
    }
}
