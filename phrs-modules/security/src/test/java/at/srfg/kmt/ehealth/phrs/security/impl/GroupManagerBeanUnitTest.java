/*
 * Project :iCardea
 * File : GroupManagerBeanUnitTest.java
 * Date : Dec 24, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.Fetcher;
import java.util.Set;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrGroup;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrActor;

import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;

import at.srfg.kmt.ehealth.phrs.util.Util;
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
 * with other components in a running container. <br>
 * Note : all test (the methods annotated with 'Test') run in the 
 * same transaction, each test run in a transaction.
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
    public static Archive<?> createDeployment() throws MalformedURLException {
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

        final Package utilsPackage = Util.class.getPackage();
        ejbJar.addPackage(utilsPackage);

        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final EnterpriseArchive ear =
                ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
        ear.addModule(ejbJar);


        final String earStructure = ear.toString(true);
        logger.debug("EAR jar structure on deploy is :");
        logger.debug(earStructure);

        final String ejbStructure = ejbJar.toString(true);
        logger.debug("EJB jar structure on deploy is :");
        logger.debug(ejbStructure);
        
        return ear;
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
     * @see GroupManager#addGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
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
    //@Test
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
    //@Test
    public void testRemoveGroupForUndegisterGroup() {
        final String newName = "Other Name_" + new Date().getTime();
        final PhrGroup group = new PhrGroup(newName);
        final boolean groupExist = groupManager.groupExist(group);
        assertFalse(groupExist);

        final PhrGroup removeGroup = groupManager.removeGroup(group);

        final boolean groupExistAfterRemove = groupManager.groupExist(removeGroup);
        assertFalse(groupExistAfterRemove);
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
     * Tests the GroupManager.getGroupForName(java.lang.String) method.
     * More precisely this test registers a test, retrive it and prove if
     * is the correct one.
     *
     *
     * @see GroupManager#getGroupForName(java.lang.String)
     */
    @Test
    public void testGetGroupForName() {
        final PhrGroup group = addGroup();
        final String expected = group.getName();
        final PhrGroup groupForNameResponse = groupManager.getGroupForName(expected);

        assertNotNull(groupForNameResponse);
        final String name = groupForNameResponse.getName();
        assertEquals(expected, name);
    }

    /**
     * Tests the GroupManager.getGroupForName(java.lang.String) method with a
     * wrong name - a name that is does not math with any registered groups.
     */
    @Test
    public void testGetGroupForWrongName() {
        final PhrGroup group = addGroup();
        final String expected = group.getName();
        final PhrGroup groupForNameResponse = groupManager.getGroupForName(expected + "XXXX");

        assertNull(groupForNameResponse);
    }

    /**
     * Tests the GroupManager.getGroupForName(java.lang.String) method with
     * a null argument. A EJBException caused by a NullPointerException raises.
     *
     * @see GroupManager#getGroupForName(java.lang.String)
     */
    @Test(expected = EJBException.class)
    public void testGetGroupForNullName() {
        final PhrGroup group = addGroup();
        try {
            groupManager.getGroupForName(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the GroupManager.getGroupsForNamePattern(java.lang.String) method.
     * More precisely this test registers a test, retrive it and prove if
     * is the correct one.
     *
     * @see GroupManager#getGroupsForNamePattern(java.lang.String)
     */
    @Test
    public void testGroupsForNamePattern() {

        final PhrGroup group = addGroup();
        final String name = group.getName();
        final Set<PhrGroup> response =
                groupManager.getGroupsForNamePattern(name + "%");
        // I expect only one group with the name starting with the 'name'
        final int expected = 1;
        final int size = response.size();
        assertEquals(expected, size);
    }

    @Test
    public void testGroupsForNamePatternWithWrongName() {
        final PhrGroup group = addGroup();
        final String name = group.getName();
        final Set<PhrGroup> response =
                groupManager.getGroupsForNamePattern(name + "XXX" + "%");
        // there is no group with this name
        final int expected = 0;
        final int size = response.size();
        assertEquals(expected, size);
    }

    /**
     * Tests the GroupManager.getGroupsForNamePattern(java.lang.String) 
     * method with a null argument. A EJBException caused
     * by a NullPointerException raises.
     *
     * @see GroupManager#getGroupsForNamePattern(java.lang.String)
     */
    @Test(expected = EJBException.class)
    public void testGroupsForNamePatternForNullName() {
        try {
            groupManager.getGroupsForNamePattern(null);
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
     * Assigns a actor to a group and prove if the operation was proper done.
     * This test tests the :
     * GroupManager.addActorToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup, 
     * at.srfg.kmt.ehealth.phrs.security.model.PhrActor) method.
     * 
     * @see GroupManager#addActorToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup, at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test
    public void testAssignActorToGroup() {

        // I use the fetcher to ensure that all the Group relations are fetched.
        final Fetcher<PhrGroup> fetcher = FetcherFactory.getGroupFetcher();

        final PhrGroup group = addGroup();
        final PhrActor actor = createPhrActor();
        groupManager.addActorToGroup(group, actor);

        final String name = group.getName();
        final PhrGroup groupForName = groupManager.getGroupForName(name, fetcher);
        final Set<PhrActor> actors = groupForName.getMembers();
        final int size = actors.size();

        // I excpect only one actor.
        assertEquals(1, size);
        final String expectdName = actor.getName();

        final PhrActor getActor = actors.iterator().next();

        final String getActorName = getActor.getName();

        assertEquals(expectdName, getActorName);

        assertTrue(!groupForName.isGroupEmpty());
    }

    /**
     * Tries to access "unfetched" elements and fails with an exception.
     * More precisely this test adds(registers) a group and tries to access the
     * actors associated to the group, the actors are lazy initialized relations
     * and this action will raises an exception.
     */
    @Test(expected = RuntimeException.class)
    public void testGetGroupWithoutFetch() {
        final PhrGroup group = addGroup();
        final String name = group.getName();

        final PhrGroup groupForName = groupManager.getGroupForName(name);
        final Set<PhrActor> members = groupForName.getMembers();

        try {
            // here I try to get an unfetched 
            members.size();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Uses the  GroupManager.assignActorToGroup(actor, group) with a null actor
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#addActorToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup, at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test(expected = EJBException.class)
    public void testAssignActorToGroupWithNullActor() {
        final PhrGroup group = addGroup();
        final PhrActor actor = null;
        try {
            groupManager.addActorToGroup(group, actor);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Uses the  GroupManager.removeActorFromGroup(actor, group) with a null group
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#removeActorFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveActorFromGroupNullGroup() {
        final PhrGroup group = null;
        final PhrActor actor = createPhrActor();

        groupManager.removeActorFromGroup(group, actor);
    }

    /**
     * Uses the  GroupManager.removeActorFromGroup(actor, group) with a null actor
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#removeUserFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveActorFromGroupNullActor() {
        final PhrGroup group = createPhrGroup();
        final PhrActor actor = null;

        try {
            groupManager.removeActorFromGroup(group, actor);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Adds an actor remove it and prove it if the action was right done.
     * This test test the removeActorFromGroup(managedGroup, actor) method.
     * 
     * @see GroupManager#removeActorFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup, at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     * @see PhrGroup#isGroupEmpty() 
     * @see PhrGroup#getMembers() 
     */
    @Test
    public void testRemoveActorFromGroup() {
        final PhrGroup group = addGroup();
        final PhrActor actor = createPhrActor();
        groupManager.addActorToGroup(group, actor);

        // I use the fetcher to ensure that all the Group relations are fetched.
        final Fetcher<PhrGroup> fetcher = FetcherFactory.getGroupFetcher();
        final String name = group.getName();
        final PhrGroup managedGroup = groupManager.getGroupForName(name, fetcher);
        final Set<PhrActor> managedGroupMembers = managedGroup.getMembers();

        // I expect only one member
        assertEquals(1, managedGroupMembers.size());
        assertTrue(managedGroupMembers.contains(actor));

        groupManager.removeActorFromGroup(managedGroup, actor);

        final PhrGroup emptyGroup = groupManager.getGroupForName(name, fetcher);
        assertTrue(emptyGroup.getMembers().isEmpty());
        assertTrue(emptyGroup.isGroupEmpty());
    }

    /**
     * Adds one group, removes all groups and removes all groups. 
     * This test test the removeAllGroups(managedGroup, actor) method.
     */
    @Test
    public void testRemoveAllActorFromGroup() {
        final PhrGroup group = addGroup();
        final PhrActor actor = createPhrActor();
        groupManager.addActorToGroup(group, actor);

        groupManager.removeAllGroups();

        final Set<PhrGroup> allGroups = groupManager.getAllGroups();
        assertTrue(allGroups.isEmpty());
    }
}
