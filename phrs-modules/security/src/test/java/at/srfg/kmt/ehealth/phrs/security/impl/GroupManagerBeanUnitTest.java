/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import java.util.Set;
import static org.junit.Assert.*;

import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import java.net.MalformedURLException;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for the GroupManagerBean.
 * More precisely this test build and deploy a test ejb jar that
 * contains the GroupManagerBean and proves its functionality together
 * with other components in a running container.
 * 
 * @author Mihai
 * @see GroupManagerBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class GroupManagerBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.GroupManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(GroupManagerBeanUnitTest.class);

    /**
     * The <code>GroupManager</code> instance to test.
     */
    @EJB
    private GroupManager groupManager;

    /**
     * Builds a <code>GroupManagerBeanUnitTest</code> instance.
     */
    public GroupManagerBeanUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>JavaArchive</code> which contains :
     * the <code>MyService</code>
     * interface and the <code>MyServiceBean</code> class;
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
        ejbJar.addPackage(GroupManagerBeanUnitTest.class.getPackage());

        final Package apiPackage = Package.getPackage("at.srfg.kmt.ehealth.phrs.security.api");
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = Package.getPackage("at.srfg.kmt.ehealth.phrs.security.model");
        ejbJar.addPackage(modelPackage);

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

    /**
     * Adds a group (by using the <code>addGroup</code>
     * method) and proves if this operation was successfully.
     * The group existence is proved with the <code>groupExist</code>
     * method.
     *
     * @see GroupManager#addGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test
    public void testAddGroup() {
        addGroup();
    }

    /**
     * Builds a new <code>PhrGroup</code>, register it and proves if the
     * register operation was right done.
     *
     * @return the new created group.
     */
    private PhrGroup addGroup() {
        final PhrGroup group = createPhrGroup();
        final boolean addGroupResponse = groupManager.addGroup(group);
        assertTrue("The addGroup response must be true.", addGroupResponse);

        final boolean groupExistResponse = groupManager.groupExist(group);
        assertTrue("The addGroup groupExist must be true.", groupExistResponse);

        return group;
    }

    /**
     * Adds an already existing <code>PhrGroup</code>.
     *
     * @see GroupManager#addGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    @Test
    public void testAddGroupForExistGroup() {
        final PhrGroup group = addGroup();
        final boolean addGroupResponse = groupManager.addGroup(group);

        assertFalse("The addGroup response for existing group must be false.",
                addGroupResponse);
    }

    /**
     * Adds a null <code>PhrGroup</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     *
     * @see GroupManager#addGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    @Test(expected = EJBException.class)
    public void testAddGroupWithNullGroup() {
        final PhrGroup group = null;
        try {
            groupManager.addGroup(group);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Removes an registered <code>PhrGroup</code> and proves if the remove
     * operation was successfully.
     *
     * @see GroupManager#removeGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    @Test
    public void testRemoveGroup() {
        final PhrGroup group = addGroup();
        final PhrGroup removeGroupResponse = groupManager.removeGroup(group);
        assertNotNull("removeGroup response can not be null", removeGroupResponse);
        final boolean groupExist = groupManager.groupExist(group);
        assertFalse("The removed group can not exist after the removeGroup.",
                groupExist);

        final String name = group.getName();
        final String name1 = removeGroupResponse.getName();
        assertEquals(name, name1);
    }

    /**
     * Removes an registered <code>PhrGroup</code> and proves if the remove
     * operation was successfully.
     *
     * @see GroupManager#removeGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    @Test
    public void testRemoveGroupForUndegisterGroup() {
        final String newName = "Other Name_" + new Date().getTime();
        final PhrGroup group = new PhrGroup(newName);
        final boolean groupExist = groupManager.groupExist(group);
        assertFalse(groupExist);

        final PhrGroup removeGroup = groupManager.removeGroup(group);
        assertNull(removeGroup);
    }

    /**
     * Removes a null <code>PhrGroup</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#removeGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    //@Test(expected = EJBException.class)
    public void testRemoveGroupWithNullGroup() {
        final PhrGroup group = null;
        try {
            groupManager.removeGroup(group);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Registers  group and after this it removes all the groups and proves
     * if the new added group is removed. The group existence is prove with
     * the groupExist method and with the removeAllGroups method (this method
     * returns a empty string).
     *
     * @see GroupManager#removeGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     * @see GroupManager#getAllGroups() 
     */
    @Test
    public void testRemoveAllGroups() {
        final PhrGroup group = addGroup();
        groupManager.removeAllGroups();
        final boolean groupExistResponse = groupManager.groupExist(group);
        assertFalse("No more Groups expected after a removeAllGroups methodf call.",
                groupExistResponse);

        final Set<PhrGroup> allGroups = groupManager.getAllGroups();
        final boolean isEmpty = allGroups.isEmpty();
        assertTrue("No more groups expected after a removeAllGroups method call.",
                isEmpty);
    }

    /**
     * Builds a <code>PhrGroup</code> with a certain name and description.
     * The <code>PhrGroup</code> name is formed from the 'Group_' prefix
     * followed by the actual time in milliseconds.
     *
     * @return a <code>PhrGroup</code> with a certain name and description.
     */
    private PhrGroup createPhrGroup() {
        final StringBuffer name = new StringBuffer();
        name.append("Group_");
        final long time = new Date().getTime();
        name.append(time);

        final PhrGroup group = new PhrGroup(name.toString());
        group.setDescription("Dummy PHR Group_" + time);

        return group;
    }
}
