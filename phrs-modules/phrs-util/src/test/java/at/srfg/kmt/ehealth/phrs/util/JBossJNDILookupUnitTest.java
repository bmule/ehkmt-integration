/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.util;


import java.net.MalformedURLException;
import javax.naming.Context;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.shrinkwrap.api.ShrinkWrap;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * This test suite is used to test the <code>JBossJNDILookup</code>
 * functionality.<br>
 * This test builds a ejb archive deploy it in to a runnig container and after
 * this use JNDI lookups to find the bean.
 * This test runs in the same JVM like the container (application server). <br>
 * The JNDI lookup requires a proper configuration for the
 * <code>InitialContext</code>; this can be done in two ways with a properties
 * file named jndi.properties placed in the classpath or with a programmatically
 * fill <code>java.util.Properties</code> instance. This test uses both
 * approaches. <br>
 * <b>Note : </b> this tests requires a running  container.
 *
 * @author mihai
 * @see JBossJNDILookup.
 */
@RunWith(Arquillian.class)
//@Run(RunModeType.IN_CONTAINER)
public class JBossJNDILookupUnitTest {

    /**
     * Builds a <code>JBossJNDILookupUnitTest</code> instance.
     */
    public JBossJNDILookupUnitTest() {
        // UNIMPLEMENETD
    }

    /**
     * Builds a <code>JavaArchive</code> which contains the <code>MyService</code>
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
        final JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class, "test.jar");
        ejbJar.addClasses(MyService.class, MyServiceBean.class);
        return ejbJar;
    }

    /**
     * This test proves the <code>JBossJNDILookup.lookup(java.lang.String, java.util.Hashtable)</code>
     * method for given JNDI configuration :
     * <ul>
     * <li> java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
     * <li> java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
     * <li> java.naming.provider.url=localhost:1099
     * </ul>
     * More precisely this test does a JNDI lookup for the <code>MyServiceBean</code>
     * use it and prove if the usage is correct.
     *
     * @throws NamingException underlying JNDI does not contains the default
     * context.
     * @see JBossJNDILookup#lookup(java.lang.String, java.util.Hashtable)
     */
    //@Test
    public void testLookupWithProgProperties() throws NamingException {
        final Properties cnxConfig = new Properties();

        cnxConfig.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        cnxConfig.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        cnxConfig.put("java.naming.provider.url", "localhost:1099");

        final int explected = 10;
        final MyService service = 
                JBossJNDILookup.lookup("phrs-ear-0.1-SNAPSHOT/GroupManagerBean/local", cnxConfig);
        final long result = service.doStuff(explected);

        Assert.assertEquals(explected + 1, result);
    }

    /**
     * This test proves the <code>JBossJNDILookup.lookup(java.lang.String)</code>
     * method for the default JNDI configuration, this configuration is placed
     * in to a file names jndi.properties placed in to the classpath,
     * this file contains:
     * <ul>
     * <li> java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
     * <li> java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
     * <li> java.naming.provider.url=localhost:1099
     * </ul>
     * More precisely this test does a JNDI lookup for the <code>MyServiceBean</code>
     * use it and prove if the usage is correct.
     *
     * @throws NamingException underlying JNDI does not contains the needed bean.
     * context.
     * @see JBossJNDILookup#lookup(java.lang.String, java.util.Hashtable)
     */
    @Test
    public void testLookupWithDefaultProperties() throws NamingException {
        final int explected = 10;

        final Context initialContext = new InitialContext();

        final MyService service =
                (MyService) initialContext.lookup("test/MyServiceBean/local");

        final long result = service.doStuff(explected);

        Assert.assertEquals(explected + 1, result);
    }

    /**
     * This test proves the <code>JBossJNDILookup.lookup(java.lang.String)</code>
     * method for the default JNDI configuration, this configuration is placed
     * in to a file names jndi.properties placed in to the classpath,
     * this file contains:
     * <ul>
     * <li> java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
     * <li> java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
     * <li> java.naming.provider.url=localhost:1099
     * </ul>
     * More precisely this test does a JNDI lookup for the <code>MyServiceBean</code>
     * use it and prove if the usage is correct. The ENC name is build using the
     * <code>JBossENCBuilder.getENCNameLocal</code> method.
     *
     *
     * @throws NamingException underlying JNDI does not contains the needed bean.
     * @see JBossJNDILookup#lookup(java.lang.String, java.util.Hashtable)
     */
    @Test
    public void testLookupWithString() throws NamingException {
        final int explected = 10;
        final String name = JBossENCBuilder.getENCNameLocal("test", MyService.class);

        final MyService service =
                JBossJNDILookup.lookup("test/MyServiceBean/local");
        final long result = service.doStuff(explected);

        Assert.assertEquals(explected + 1, result);
    }

    /**
     * Does a lookup using a wrong name, a NamingException occurs.
     *
     *
     * @throws NamingException this test proves this exception.
     * @see JBossJNDILookup#lookup(java.lang.String)
     */
    @Test(expected = NamingException.class)
    public void testLookupWithWrongName() throws NamingException {
        JBossJNDILookup.lookup("XXX");
    }

    /**
     * Does a lookup using a null name, a NullPointerException occurs.
     *
     * @throws NullPointerException this test proves this exception.
     * @see JBossJNDILookup#lookup(java.lang.String)
     */
    @Test(expected = NullPointerException.class)
    public void testLookupWithNullName() throws NamingException {
        JBossJNDILookup.lookup(null);
    }
}
