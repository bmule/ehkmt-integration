/*
 * Project :iCardea
 * File : RoleManagerBeanUnitTest.java
 * Date : Dec 24, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import java.util.Set;
import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrUser;
import at.srfg.kmt.ehealth.phrs.security.api.UserManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import at.srfg.kmt.ehealth.phrs.util.Util;
import java.net.MalformedURLException;
import javax.ejb.EJB;
import javax.ejb.EJBException;
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
 * Integration test for the GroupManagerBean.
 * More precisely this test build and deploy a test ejb jar that
 * contains the GroupManagerBean and proves its functionality together
 * with other components in a running container. <br>
 * Note : all test (the methods annotated with 'Test') run in the 
 * same transaction, each test run in a transaction.
 * 
 * @author Mihai
 * @see UserManagerBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class UserManagerBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.UserManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UserManagerBeanUnitTest.class);

    /**
     * The <code>UserManager</code> instance to test.
     */
    @EJB
    private UserManager userManager;

    /**
     * Builds a <code>UserManagerBeanUnitTest</code> instance.
     */
    public UserManagerBeanUnitTest() {
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
    public static JavaArchive createDeployment() throws MalformedURLException {
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.security.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.security.impl and
        // at.srfg.kmt.ehealth.phrs.security are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure
        ejbJar.addPackage(UserManagerBeanUnitTest.class.getPackage());

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
     * Adds a PHRS role (by using the <code>addUser</code>
     * method) and proves if this operation was successfully.
     * The role existence is proved with the <code>userExist</code>
     * method.
     *
     * @see UserManager#addUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     * @see UserManager#userExist(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test
    public void testAddUser() {
        addUser();
    }

    /**
     * Builds a new <code>PhrUser</code>, register it and proves if the
     * register operation was right done.
     *
     * @return the new created user.
     */
    private PhrUser addUser() {
        final PhrUser user = createPhrUser();
        final boolean addUserResponse = userManager.addUser(user);
        assertTrue("The addUser response must be true.", addUserResponse);

        final boolean userExistResponse = userManager.userExist(user);
        assertTrue("The userExist must be true after add user.", userExistResponse);

        return user;
    }

    /**
     * Adds an already existing <code>PhrUser</code>.
     *
     * @see UserManager#addUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     * @see UserManager#userExist(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test
    public void testAddUserForExistUser() {
        final PhrUser user = addUser();
        final boolean addUserResponse = userManager.addUser(user);

        assertFalse("The addUser response for existing user must be false.",
                addUserResponse);
    }

    /**
     * Adds a null <code>PhrUser</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see UserManager#addUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test(expected = EJBException.class)
    public void testAddUserWithNullUser() {
        final PhrUser user = null;
        try {
            userManager.addUser(user);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the <code>UserManager.removeUser(user)</code> method with a \
     * valid user. More precisely this method registers a user, remove 
     * users and prove if the remove was successfully, the user 
     * existence is proved with the <code>UserManager.userExist(user)</code>.
     * 
     * @see UserManager#removeUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     * @see UserManager#userExist(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test
    public void testRemoveUser() {
        final PhrUser user = addUser();

        userManager.removeUser(user);
        final boolean userExist = userManager.userExist(user);
        assertFalse(userExist);
    }

    /**
     * Tests the <code>UserManager.removeUser(user)</code> method with a \
     * null user and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see UserManager#removeUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveUserWithNullUser() {
        final PhrUser user = null;
        try {
            userManager.removeUser(user);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the <code>UserManager.removeAllUsers()</code> method.
     * More precisely this method add some an user, call the removeAll method
     * and after this proves if there are any existing users, the test
     * done with the <code>UserManager.getAllUsers()</code> and the result set 
     * is proved if it is empty.
     * 
     * @see UserManager#removeAllUsers() 
     * @see UserManager#getAllUsers() 
     * @see UserManager#userExist(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Test
    public void testRemoveAllUsers() {
        final PhrUser user = addUser();

        userManager.removeAllUsers();
        final Set<PhrUser> allUsers = userManager.getAllUsers();
        final boolean noUsers = allUsers.isEmpty();
        assertTrue("No users are expected after the removeAllUsers method call.", noUsers);
        final boolean userExist = userManager.userExist(user);
        assertFalse("The user can not exists after the removeAllUsers method call.", userExist);
    }

    /**
     * Tests the <code>UserManager.getUsersForName(name)</code> method.
     * More precisely this test add/remove a new user,retreive it
     * using the <code>UserManager.getUsersForName(name)</code> method
     * an prove it if this is correct. 
     * 
     * @see UserManager#getUsersForName(java.lang.String) 
     */
    @Test
    public void testGetUsersForName() {
        final PhrUser user = addUser();
        final String name = user.getName();
        final Set<PhrUser> usersForNameResult = userManager.getUsersForName(name);

        final int userCount = usersForNameResult.size();

        assertEquals(1, userCount);

        final PhrUser newUser = usersForNameResult.iterator().next();
        final String newUserName = newUser.getName();
        assertEquals(user.getName(), newUserName);

        final String newUserFamilyName = newUser.getFamilyName();
        assertEquals(user.getFamilyName(), newUserFamilyName);
    }

    /**
     * Tests the <code>UserManager.getUsersForName(name)</code> method
     * with a wrong name.
     * More precisely this test add/remove a new user,and tries to retreive it
     * using the <code>UserManager.getUsersForName(name)</code> method
     * with a name that does not match with any users an prove it if the result
     * is correct. 
     * 
     * @see UserManager#getUsersForName(java.lang.String) 
     */
    @Test
    public void testGetUsersForNameWithWrongName() {
        final PhrUser user = addUser();
        final String name = user.getName();
        final Set<PhrUser> usersForNameResult =
                userManager.getUsersForName(name + "XXXXX");

        final boolean noUsers = usersForNameResult.isEmpty();
        assertTrue(noUsers);
    }

    /**
     * Tests the <code>UserManager.getUsersForName(name)</code> method with a 
     * null argument and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see UserManager#getUsersForName(java.lang.String) 
     */
    @Test(expected = EJBException.class)
    public void testGetUsersForNameWithNullName() {
        final PhrUser user = addUser();
        try {
            userManager.getUsersForName(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the <code>UserManager.getUsersForNamePattern(name)</code> method.
     * More precisely this test add/remove a new user,retreive it
     * using the <code>UserManager.getUsersForNamePattern(name)</code> method
     * an prove it if this is correct. 
     */
    @Test
    public void testGetUsersForNamesPattern() {
        final PhrUser user = addUser();
        final String name = user.getName();
        final Set<PhrUser> usersForNameResult =
                userManager.getUsersForNamePattern(name + "%");

        final int userCount = usersForNameResult.size();

        assertEquals(1, userCount);

        final PhrUser newUser = usersForNameResult.iterator().next();
        final String newUserName = newUser.getName();
        assertEquals(user.getName(), newUserName);

        final String newUserFamilyName = newUser.getFamilyName();
        assertEquals(user.getFamilyName(), newUserFamilyName);
    }

    /**
     * Tests the <code>UserManager.getUsersForNamePattern(name)</code> method 
     * with a null argument and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see UserManager#getUsersForName(java.lang.String) 
     */
    @Test(expected = EJBException.class)
    public void testGetUsersForNamePatternWithNullName() {
        final PhrUser user = addUser();
        try {
            userManager.getUsersForNamePattern(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    @Test
    public void testGetUsersForNameAndFamilyPattern() {
        final PhrUser user = addUser();
        final String name = user.getName();
        final String nameFamily = user.getFamilyName();

        final Set<PhrUser> usersForNameResult =
                userManager.getUsersForNamesPattern(name + "%", nameFamily + "%");

        final int userCount = usersForNameResult.size();
        assertEquals(1, userCount);

        final PhrUser newUser = usersForNameResult.iterator().next();
        final String newUserName = newUser.getName();
        assertEquals(user.getName(), newUserName);

        final String newUserFamilyName = newUser.getFamilyName();
        assertEquals(user.getFamilyName(), newUserFamilyName);
    }

    @Test
    public void testGetUsersForNameAndFamilyPatternWithOherFamily() {
        final PhrUser user = addUser();
        final String name = user.getName();
        final String nameFamily = user.getFamilyName();

        final Set<PhrUser> usersForNameResult =
                userManager.getUsersForNamesPattern(name + "%", nameFamily + "XXXX%");

        final boolean noUsers = usersForNameResult.isEmpty();
        assertTrue(noUsers);
    }

    @Test(expected = EJBException.class)
    public void testGetUsersForNameAndFamilyPatternWithNull() {
        final String name = null;
        final String nameFamily = null;
        try {
            userManager.getUsersForNamesPattern(name, nameFamily);

        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    @Test
    public void testGetAnonymusUser() {
        final PhrUser anonymusUser = userManager.getAnonymusUser();
        assertNotNull(anonymusUser);
        
        final PhrUser newAnonymusUser = userManager.getAnonymusUser();
        assertEquals(anonymusUser.getName(), newAnonymusUser.getName());
        assertEquals(anonymusUser.getFamilyName(), newAnonymusUser.getFamilyName());
    }
}
