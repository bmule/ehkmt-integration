/*
 * Project :iCardea
 * File : RoleManagerBeanUnitTest.java
 * Date : Dec 24, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrRole;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.Fetcher;
import static org.junit.Assert.*;

import at.srfg.kmt.ehealth.phrs.util.Util;
import at.srfg.kmt.ehealth.phrs.security.api.RoleManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.Set;

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
 * Integration test for the RoleManagerBean.
 * More precisely this test build and deploy a test ejb jar that
 * contains the RoleManagerBean and proves its functionality together
 * with other components in a running container. <br>
 * Note : all test (the methods annotated with 'Test') run in the 
 * same transaction, each test run in a transaction.
 * 
 * @author Mihai
 * @see RoleManagerBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class RoleManagerBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.RoleManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(RoleManagerBeanUnitTest.class);

    /**
     * The <code>RoleManager</code> instance to test.
     */
    @EJB
    private RoleManager roleManager;

    /**
     * Builds a <code>RoleManagerBeanUnitTest</code> instance.
     */
    public RoleManagerBeanUnitTest() {
        // UNIMPLEMENTED
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
    public static Archive<?> createDeployment() throws MalformedURLException {
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.security.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.security.impl and
        // at.srfg.kmt.ehealth.phrs.security are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure
        ejbJar.addPackage(RoleManagerBeanUnitTest.class.getPackage());

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
     * Adds a PHRS role (by using the <code>addRole</code>
     * method) and proves if this operation was successfully.
     * The role existence is proved with the <code>roleExist</code>
     * method.
     *
     * @see RoleManager#addRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test
    public void testAddRole() {
        addRole();
    }

    /**
     * Builds a new <code>PhrRole</code>, register it and proves if the
     * register operation was right done.
     *
     * @return the new created role.
     */
    private PhrRole addRole() {
        final PhrRole role = createPhrRole();
        final boolean addRoleResponse = roleManager.addRole(role);
        assertTrue("The addRole response must be true.", addRoleResponse);

        final boolean roleExistResponse = roleManager.roleExist(role);
        assertTrue("The addRole roleExist must be true.", roleExistResponse);

        return role;
    }

    /**
     * Adds an already existing <code>PhrRole</code>.
     *
     * @see RoleManager#addRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test
    public void testAddRoleForExistRole() {
        final PhrRole role = addRole();
        final boolean addRoleResponse = roleManager.addRole(role);

        assertFalse("The addRole response for existing role must be false.",
                addRoleResponse);
    }

    /**
     * Adds a null <code>PhrRole</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     *
     * @see RoleManager#addRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test(expected = EJBException.class)
    public void testAddRoleWithNullRole() {
        final PhrRole role = null;
        try {
            roleManager.addRole(role);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Removes an registered <code>PhrRole</code> and proves if the remove
     * operation was successfully.
     *
     * @see RoleManager#removeRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test
    public void testRemoveRole() {
        final PhrRole role = addRole();
        final PhrRole removeRoleResponse = roleManager.removeRole(role);
        assertNotNull("removeRole response can not be null", removeRoleResponse);
        final boolean roleExist = roleManager.roleExist(role);
        assertFalse("The removed role can not exist after the removeRole.", roleExist);

        final String name = role.getName();
        final String name1 = removeRoleResponse.getName();
        assertEquals(name, name1);
    }

    /**
     * Removes an registered <code>PhrRole</code> and proves if the remove
     * operation was successfully.
     *
     * @see RoleManager#removeRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test
    public void testRemoveRoleForUndegisterRole() {
        final String newName = "Other Name_" + new Date().getTime();
        final PhrRole role = new PhrRole(newName);
        final boolean roleExist = roleManager.roleExist(role);
        assertFalse(roleExist);


        final PhrRole removeRole = roleManager.removeRole(role);

        final boolean groupExistAfterRemove = roleManager.roleExist(removeRole);
        assertFalse(groupExistAfterRemove);
    }

    /**
     * Removes a null <code>PhrRole</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see RoleManager#removeRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    @Test(expected = EJBException.class)
    public void testRemoveRoleWithNullRole() {
        final PhrRole role = null;
        try {
            roleManager.removeRole(role);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the RoleManager.getRoleForName(java.lang.String) method.
     * More precisely this test registers a test, retrive it and prove if
     * is the correct one.
     *
     * @see RoleManager#getRoleForName(java.lang.String)
     */
    @Test
    public void testGetRoleForName() {
        final PhrRole role = addRole();
        final String expected = role.getName();
        final PhrRole roleForNameResponse = roleManager.getRoleForName(expected);

        assertNotNull(roleForNameResponse);
        final String name = roleForNameResponse.getName();
        assertEquals(expected, name);
    }

    /**
     * Tries to access "unfetched" elements and fails with an exception.
     * More precisely this test adds(registers) a role and tries to access the
     * actors associated to the role, the actors are lazy initialized relations
     * and this action will raises an exception.
     */
    @Test(expected = RuntimeException.class)
    public void testGetGroupWithoutFetch() {
        final PhrRole role = addRole();
        final String name = role.getName();

        final PhrRole groupForName = roleManager.getRoleForName(name);
        final Set<PhrActor> members = groupForName.getPhrActors();

        try {
            // here I try to get an unfetched 
            members.size();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Tests the RoleManager.getRoleForName(java.lang.String) method with a
     * wrong name - a name that is does not math with any registered roles.
     */
    @Test
    public void testGetRoleForWrongName() {
        final PhrRole role = addRole();
        final String expected = role.getName();
        final PhrRole roleForNameResponse =
                roleManager.getRoleForName(expected + "XXXX");

        assertNull(roleForNameResponse);
    }

    /**
     * Tests the RoleManager.getRoleForName(java.lang.String) method with
     * a null argument. A EJBException caused by a NullPointerException raises.
     *
     * @see RoleManager#getRoleForName(java.lang.String)
     */
    @Test(expected = EJBException.class)
    public void testGetRoleForNullName() {
        final PhrRole role = addRole();
        try {
            roleManager.getRoleForName(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the RoleManager.getRolesForNamePattern(java.lang.String) method.
     * More precisely this test registers a role, retrive it and prove if
     * is the correct one.
     *
     * @see RoleManager#getRolesForNamePattern(java.lang.String)
     */
    @Test
    public void testRolesForNamePattern() {

        final PhrRole role = addRole();
        final String name = role.getName();
        final Set<PhrRole> response =
                roleManager.getRolesForNamePattern(name + "%");
        // I expect only one role with the name starting with the 'name'
        final int expected = 1;
        final int size = response.size();
        assertEquals(expected, size);
    }

    //@Test
    public void testRolesForNamePatternWithWrongName() {
        final PhrRole role = addRole();
        final String name = role.getName();
        final Set<PhrRole> response =
                roleManager.getRolesForNamePattern(name + "XXX" + "%");
        // there is no role with this name
        final int expected = 0;
        final int size = response.size();
        assertEquals(expected, size);
    }

    /**
     * Tests the RoleManager.getRolesForNamePattern(java.lang.String) 
     * method with a null argument. A EJBException caused
     * by a NullPointerException raises.
     *
     * @see RoleManager#getRolesForNamePattern(java.lang.String)
     */
    @Test(expected = EJBException.class)
    public void testRolesForNamePatternForNullName() {
        try {
            roleManager.getRolesForNamePattern(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Registers role and after this it removes all the roles and proves
     * if the new added role is removed. The role existence is prove with
     * the roleExist method and with the removeAllRoles method (this method
     * returns a empty string).
     *
     * @see RoleManager#removeRole(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     * @see RoleManager#getAllRoles()
     */
    @Test
    public void testRemoveAllRoles() {
        final PhrRole role = addRole();
        roleManager.removeAllRoles();
        final boolean roleExistResponse = roleManager.roleExist(role);
        assertFalse("No more Roles expected after a removeAllRoles methodf call.",
                roleExistResponse);

        final Set<PhrRole> allRoles = roleManager.getAllRoles();
        final boolean isEmpty = allRoles.isEmpty();
        assertTrue("No more roles expected after a removeAllRoles method call.",
                isEmpty);
    }

    /**
     * Assigns a actor to a role and prove if the operation was proper done.
     * This test tests the :
     * RoleManager.assignActorToRole(actor, role) method.
     * 
     * @see RoleManager#assignActorToRole(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrRole) 
     */
    @Test
    public void testAssignActorToRole() {
        final PhrRole role = addRole();
        final PhrActor actor = createPhrActor();

        roleManager.assignActorToRole(actor, role);
        final String name = role.getName();
        final Fetcher<PhrRole> fetcher = FetcherFactory.getRoleFetcher();
        final PhrRole roleForName = roleManager.getRoleForName(name, fetcher);
        final Set<PhrActor> actors = roleForName.getPhrActors();
        final int size = actors.size();

        // I excpect only one actor.
        assertEquals(1, size);
        final String expectdName = actor.getName();

        final PhrActor getActor = actors.iterator().next();
        final String getActorName = getActor.getName();

        assertEquals(expectdName, getActorName);
    }

    /**
     * Uses the  RoleManager.assignActorToRole(actor, role) with a null role
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see RoleManager#assignActorToRole(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrRole) 
     */
    @Test(expected = EJBException.class)
    public void testAssignActorToRoleWithNullRole() {
        final PhrRole role = null;
        final PhrActor actor = createPhrActor();

        try {
            roleManager.assignActorToRole(actor, role);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the RoleManager.removeActorFromRole(actor, role) method.
     * More precisely this method adds a new actor to a role, remove it
     * and prove it if the remove is proper done.
     * 
     * @see RoleManager#removeActorFromRole(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrRole) 
     */
    @Test
    public void testRemoveActorFromRole() {
        final PhrRole role = addRole();
        final PhrActor actor = createPhrActor();
        
        roleManager.assignActorToRole(actor, role);
        
        final String name = role.getName();
        final Fetcher<PhrRole> fetcher = FetcherFactory.getRoleFetcher();
        final PhrRole managedRole = roleManager.getRoleForName(name, fetcher);
        final Set<PhrActor> phrActors = managedRole.getPhrActors();
        // I expect one one actor
        assertEquals(1, phrActors.size());
        assertFalse(managedRole.isRoleEmpty());
        
        final PhrActor managedActor =
                managedRole.getPhrActors().iterator().next();
        
        roleManager.removeActorFromRole(managedActor, managedRole);
        final PhrRole managedRoleAfterRemove =
                roleManager.getRoleForName(name, fetcher);
        
        assertTrue(managedRoleAfterRemove.isRoleEmpty());
    }

    /**
     * Uses the  RoleManager.removeActorFromRole(actor, role) with a null role
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see RoleManager#removeActorFromRole(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrRole) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveActorFromRoleNullRole() {
        final PhrRole role = null;
        final PhrActor actor = createPhrActor();

        try {
            roleManager.removeActorFromRole(actor, role);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }

    }

    /**
     * Uses the  RoleManager.removeActorFromRole(actor, role) with a null actoractor
     * and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see RoleManager#removeActorFromRole(at.srfg.kmt.ehealth.phrs.security.model.PhrActor, at.srfg.kmt.ehealth.phrs.security.model.PhrRole) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveActorFromRoleNullActor() {
        final PhrRole role = createPhrRole();
        final PhrActor actor = null;

        try {
            roleManager.removeActorFromRole(actor, role);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }
    
}
