/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import java.util.Set;
import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrGroup;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrUser;

import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
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
    @Test(expected = EJBException.class)
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
     * Assigns a user to a group and prove if the operation was proper done.
     * This test tests the :
     * GroupManager.assignUserToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, 
     * at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) method.
     * 
     * @see GroupManager#assignUserToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test
    public void testAssignUserToGroup() {
        final PhrGroup group = addGroup();
        final PhrUser user = createPhrUser();
        groupManager.assignUserToGroup(user, group);

        final String name = group.getName();
        final PhrGroup groupForName = groupManager.getGroupForName(name);
        final Set<PhrUser> users = groupForName.getPhrUsers();
        final int size = users.size();

        // I excpect only one user.
        assertEquals(1, size);
        final String expectdName = user.getName();
        final String expectdFamilyName = user.getFamilyName();

        final PhrUser getUser = users.iterator().next();

        final String getUserName = getUser.getName();
        final String getUserFamilyName = getUser.getFamilyName();

        assertEquals(expectdName, getUserName);
        assertEquals(expectdFamilyName, getUserFamilyName);
    }

    /**
     * Uses the  GroupManager.assignUserToGroup(user, group) with a null user
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#assignUserToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testAssignUserToGroupWithNullUser() {
        final PhrGroup group = addGroup();
        final PhrUser user = null;
        try {
            groupManager.assignUserToGroup(user, group);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Uses the  GroupManager.assignUserToGroup(user, group) with a null group
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#assignUserToGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testAssignUserToGroupWithNullGroup() {
        final PhrGroup group = null;
        final PhrUser user = createPhrUser();
        try {
            groupManager.assignUserToGroup(user, group);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the GroupManager.removeUserFromGroup(user, group) method.
     * More precisely this method adds a new user to a group, remove it
     * and prove it if the remove is proper done.
     * 
     * @see GroupManager#removeUserFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test
    public void testRemoveUserFromGroup() {
        final PhrGroup group = addGroup();
        final PhrUser user = createPhrUser();
        groupManager.assignUserToGroup(user, group);
        
        // I retrive the group from the peristence layer,
        // this group has the user set it.
        final String groupName = group.getName();
        final PhrGroup persistedGroup =
                groupManager.getGroupForName(groupName);
        final Set<PhrUser> phrUsers = persistedGroup.getPhrUsers();
        final int userCount = phrUsers.size();
        
        // there is only one user registered on the previous statement
        assertEquals(1, userCount);
        
        // this is the registered user instance, it differ from the 'user' one 
        // created with the createPhrUser because it is retreived from the
        // persistence layer. The 'user' exist only in memory heap.
        final PhrUser involvedUser = phrUsers.iterator().next();
        groupManager.removeUserFromGroup(involvedUser, persistedGroup);

        final String name = group.getName();
        final PhrGroup groupForName = groupManager.getGroupForName(name);
        final Set<PhrUser> users = groupForName.getPhrUsers();

        assertTrue(users.isEmpty());
    }

    /**
     * Uses the  GroupManager.removeUserFromGroup(user, group) with a null group
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#removeUserFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveUserFromGroupNullGroup() {
        final PhrGroup group = null;
        final PhrUser user = createPhrUser();

        groupManager.removeUserFromGroup(user, group);
    }

    /**
     * Uses the  GroupManager.removeUserFromGroup(user, group) with a null user
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see GroupManager#removeUserFromGroup(at.srfg.kmt.ehealth.phrs.security.model.PhrUser, at.srfg.kmt.ehealth.phrs.security.model.PhrGroup) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveUserFromGroupNullUser() {
        final PhrGroup group = createPhrGroup();
        final PhrUser user = null;

        groupManager.removeUserFromGroup(user, group);
    }

}
