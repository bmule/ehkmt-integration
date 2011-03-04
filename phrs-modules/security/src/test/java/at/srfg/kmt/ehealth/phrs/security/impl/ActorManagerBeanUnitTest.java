/*
 * Project :iCardea
 * File : ActorManagerBeanUnitTest.java
 * Date : Dec 24, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrActor;
import static at.srfg.kmt.ehealth.phrs.security.impl.ModelFactory.createPhrActors;

import java.util.Set;
import at.srfg.kmt.ehealth.phrs.security.api.ActorManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.util.Util;
import java.net.MalformedURLException;
import java.util.HashSet;
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
 * @see ActorManagerBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class ActorManagerBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ActorManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(ActorManagerBeanUnitTest.class);

    /**
     * The <code>ActorManager</code> instance to test.
     */
    @EJB
    private ActorManager actorManager;

    /**
     * Builds a <code>ActorManagerBeanUnitTest</code> instance.
     */
    public ActorManagerBeanUnitTest() {
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
        ejbJar.addPackage(ActorManagerBeanUnitTest.class.getPackage());

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
     * Adds a PHRS actor (by using the <code>addActor</code>
     * method) and proves if this operation was successfully.
     * The actor existence is proved with the <code>actorExist</code>
     * method.
     *
     * @see ActorManager#addActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     * @see ActorManager#actorExist(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test
    public void testAddActor() {
        addActor();
    }

    /**
     * Builds a new <code>PhrActor</code>, register it and proves if the
     * register operation was right done.
     *
     * @return the new created actor.
     * @see ActorManager#addActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     * @see ActorManager#actorExist(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    private PhrActor addActor() {
        final PhrActor actor = createPhrActor();
        final boolean addActorResponse = actorManager.addActor(actor);
        assertTrue("The addActor response must be true.", addActorResponse);

        final String name = actor.getName();
        final Set<PhrActor> managedActors = actorManager.getActorsForName(name);
        final PhrActor managedActor = managedActors.iterator().next();


        final boolean actorExistResponse = actorManager.actorExist(managedActor);
        assertTrue("The actorExist must be true after add actor.", actorExistResponse);

        return actor;
    }

    /**
     * Adds an already existing <code>PhrActor</code>.
     *
     * @see ActorManager#addActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     * @see ActorManager#actorExist(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test
    public void testAddActorForExistActor() {
        final PhrActor actor = addActor();
        final boolean addActorResponse = actorManager.addActor(actor);

        assertFalse("The addActor response for existing actor must be false.",
                addActorResponse);
    }

    /**
     * Adds a null <code>PhrActor</code> and proves if an
     * <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see ActorManager#addActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor)
     */
    @Test(expected = EJBException.class)
    public void testAddActorWithNullActor() {
        final PhrActor actor = null;
        try {
            actorManager.addActor(actor);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the <code>ActorManager.removeActor(actor)</code> method with a
     * valid actor. More precisely this method registers a actor, remove 
     * the actor and prove if the remove was successfully, the actor 
     * existence is proved with the :
     * <code>ActorManager.getActorsForName(actor)</code> method.
     * 
     * @see ActorManager#removeActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     * @see ActorManager#actorExist(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test
    public void testRemoveActor() {
        final PhrActor actor = addActor();
        final PhrActor removeActor = actorManager.removeActor(actor);
        assertNotNull(removeActor);
        final String name = actor.getName();

        final Set<PhrActor> actorsForName = actorManager.getActorsForName(name);
        assertEquals(0, actorsForName.size());

        final Set<PhrActor> afterRemoveActor = actorManager.getActorsForName(name);
        assertTrue(afterRemoveActor.isEmpty());
    }

    /**
     * Tests the <code>ActorManager.removeActor(actor)</code> method with a \
     * null actor and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see ActorManager#removeActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test(expected = EJBException.class)
    public void testRemoveActorWithNullActor() {
        final PhrActor actor = null;
        try {
            actorManager.addActor(actor);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the <code>ActorManager.removeActor(actor)</code> method with a
     * unregister actor.
     * 
     * @see ActorManager#removeActor(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Test
    public void testRemoveActorWithWrongActor() {
        final PhrActor actor = createPhrActor();
        final PhrActor removeActor = actorManager.removeActor(actor);
        assertNull(removeActor);
    }

    /**
     * Tests the <code>ActorManager.removeAllActors()</code> method.
     * More precisely this method add some an actor, call the removeAllActors
     * method and after this proves if there are any existing actors, the test
     * done with the <code>ActorManager.removeAllActors()</code> and the result
     * set is proved if it is empty.
     * 
     * @see ActorManager#removeAllActors() 
     * @see ActorManager#getAllActors() 
     * @see ActorManager#areAnyActorsRegistered() 
     */
    @Test
    public void testRemoveAllActors() {
        final PhrActor actor = addActor();

        actorManager.removeAllActors();

        final Set<PhrActor> allActors = actorManager.getAllActors();
        final boolean noAcotrs = allActors.isEmpty();
        assertTrue("No actors are expected after the removeAllActors method call.",
                noAcotrs);
        final boolean areAnyActorsRegistered =
                actorManager.areAnyActorsRegistered();
        assertFalse("No actors are expected after the removeAllActors method call.",
                areAnyActorsRegistered);
    }

    /**
     * Tests the <code>ActorManager.getActorsForName(name)</code> method.
     * More precisely this test add a new actor, retreive it
     * using the <code>ActorManager.getActorsForName(name)</code> method
     * an prove it if this is correct.
     * 
     * @see ActorManager#getActorsForName(java.lang.String) 
     */
    @Test
    public void testGetActorsForName() {
        final PhrActor actor = addActor();
        final String name = actor.getName();
        final Set<PhrActor> actorsForNameResult = actorManager.getActorsForName(name);

        final int actorCount = actorsForNameResult.size();
        assertEquals(1, actorCount);

        final PhrActor newActor = actorsForNameResult.iterator().next();
        assertEquals(actor, newActor);
    }

    /**
     * Tests the <code>ActorManager.getActorsForName(name)</code> method
     * with a wrong name.
     * More precisely this test add/remove a new actor,and tries to retreive it
     * using the <code>ActorManager.getActorsForName(name)</code> method
     * with a name that does not match with any actors an prove it if the result
     * is correct. 
     * 
     * @see ActorManager#getActorsForName(java.lang.String) 
     */
    @Test
    public void testGetActorsForNameWithWrongName() {
        final PhrActor actor = addActor();
        final String name = actor.getName();
        final Set<PhrActor> actorsForNameResult =
                actorManager.getActorsForName(name + "XXXXX");

        final boolean noActors = actorsForNameResult.isEmpty();
        assertTrue(noActors);
    }

    /**
     * Tests the <code>ActorManager.getActorsForName(name)</code> method.
     * More precisely this test add/remove a new actor,retreive it
     * using the <code>ActorManager.getActorsForName(name)</code> method
     * an prove it if this is correct. 
     * 
     * @see ActorManager#getActorsForNamePattern(java.lang.String) (java.lang.String) 
     */
    @Test
    public void testGetActorsForNamesPattern() {
        final PhrActor actor = addActor();
        final String name = actor.getName();
        final Set<PhrActor> actorsForNameResult =
                actorManager.getActorsForNamePattern(name + "%");

        final int actorCount = actorsForNameResult.size();

        assertEquals(1, actorCount);

        final PhrActor newActor = actorsForNameResult.iterator().next();
        assertEquals(actor, newActor);
    }

    /**
     * Tests the <code>ActorManager.getActorsForNamePattern(name)</code> method 
     * with a null argument and proves if an <code>EJBException</code> occurs
     * (caused by a <code>NullPointerException</code>).
     * 
     * @see ActorManager#getActorsForNamePattern(java.lang.String) (java.lang.String) 
     */
    @Test(expected = EJBException.class)
    public void testGetActorsForNamePatternWithNullName() {
        try {
            actorManager.getActorsForNamePattern(null);
        } catch (EJBException exception) {
            final Throwable cause = exception.getCause();
            final boolean isNullPointer = cause instanceof NullPointerException;
            assertTrue("NullPointerException expected.", isNullPointer);
            throw exception;
        }
    }

    /**
     * Tests the individual anonymous actor. More precisely this test retreives
     * two time an anonymous actor and prove if the resulted are the same.
     * 
     * @see ActorManager#getAnonymusActor() 
     */
    @Test
    public void testGetAnonymusActor() {
        final PhrActor ActorActor = actorManager.getAnonymusActor();
        assertNotNull(ActorActor);

        final PhrActor newAnonymusActor = actorManager.getAnonymusActor();
        assertEquals(ActorActor, newAnonymusActor);
    }

    /**
     * Registers more actors and prove if all the actors was properly
     * registered. The actor presence is proved by comparing the registered
     * actors amount before and after the addActors  method call.
     * This test proves the : actorManager.addActors(actors) method.
     * 
     * @see ActorManager#addActors(java.util.Set) 
     */
    @Test
    public void testAddActors() {
        addActors(10);
    }

    private Set<PhrActor> addActors(int actorsCount) {
        final Set<PhrActor> actors = createPhrActors(actorsCount);

        final Set<PhrActor> allActorsBefore = actorManager.getAllActors();
        final int allActorsBeforeCount = allActorsBefore.size();

        actorManager.addActors(actors);

        final boolean areAnyActorsRegistered =
                actorManager.areAnyActorsRegistered();
        assertTrue(areAnyActorsRegistered);

        final Set<PhrActor> allActors = actorManager.getAllActors();
        final int allActorsAfterCount = allActors.size();

        // the actor count before remove was allActorsBeforeCount, from this 
        // count actorsCout was new added, this is the reason why
        // allActorsAfterCount = allActorsBeforeCount + actorsCout.
        assertEquals(allActorsBeforeCount + actorsCount, allActorsAfterCount);

        return actors;
    }

    /**
     * Registers more actors, remove them and prove if all the actors was
     * properly registered. The actor presence is proved by comparing the 
     * registered actors amount before and after the addActors  method call.
     * This test proves the : actorManager.removeActors(actors) method.
     * 
     * @see ActorManager#addActors(java.util.Set) 
     * @see ActorManager#removeActors(java.util.Set) 
     */
    @Test
    public void testRemoveActors() {
        final int actorsCout = 10;
        final Set<PhrActor> addedActors = addActors(actorsCout);
        final Set<PhrActor> allActorsBefore = actorManager.getAllActors();
        final int allActorsBeforeCount = allActorsBefore.size();

        final Set<PhrActor> toRemove = new HashSet<PhrActor>();
        for (PhrActor actor : addedActors) {
            final String name = actor.getName();
            final Set<PhrActor> actorsForName =
                    actorManager.getActorsForName(name);
            final PhrActor next = actorsForName.iterator().next();
            toRemove.add(next);
        }

        actorManager.removeActors(toRemove);
        final Set<PhrActor> allActors = actorManager.getAllActors();
        // the actor count before remove was allActorsBeforeCount, from this 
        // count actorsCout was new added and removed, this is the reason why
        // allActorsAfterCount = allActorsBeforeCount - actorsCout.
        assertEquals(allActorsBeforeCount - actorsCout, allActors.size());
    }
}
