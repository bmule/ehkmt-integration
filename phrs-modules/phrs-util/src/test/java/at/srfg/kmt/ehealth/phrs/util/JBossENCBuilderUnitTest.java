/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.util;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the <code>JBossENCBuilder</code> class functionality.
 *
 * @author Mihai
 * @see JBossENCBuilder
 */
public class JBossENCBuilderUnitTest {

    /**
     * Builds a <code>JBossENCBuilderUnitTest</code> instance.
     */
    public JBossENCBuilderUnitTest() {
        // UNIMPLEMENTD
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.Class)</code>
     * method with expected (right arguments).
     *
     * @see JBossENCBuilder#getENCNameLocal(java.lang.Class)
     */
    @Test
    public void testGetENCNameLocalForClass() {
        final String expected =
                JBossENCBuilder.EAR_NAME + "/MyServiceBean/"
                + JBossENCBuilder.LOCAL_SCOPE;
        final String result =
                JBossENCBuilder.getENCNameLocal(MyServiceBean.class);
        assertEquals("Wrong enc local name", result, expected);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.Class)</code>
     * method functionality for a null argument - a NullPointerException raises.
     *
     * @see JBossENCBuilder#getENCNameLocal(java.lang.Class)
     * @throws NullPointerException this methods test this exception.
     */
    @Test(expected = NullPointerException.class)
    public void testGetENCNameLocalForClassWithNullType() {
        final Class clazz = null;
        JBossENCBuilder.getENCNameLocal(clazz);
    }

    /**
     * Test the JBossENCBuilder.getENCNameRemote(java.lang.Class)
     * method for a argument from type Class.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.Class)
     */
    @Test
    public void testGetENCNameRemoteForClass() {
        final String expected =
                JBossENCBuilder.EAR_NAME + "/MyServiceBean/"
                + JBossENCBuilder.REMOTE_SCOPE;
        final String result =
                JBossENCBuilder.getENCNameRemote(MyServiceBean.class);
        assertEquals("Wrong enc remote name", result, expected);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.Class)</code>
     * method functionality for a null argument - a NullPointerException raises.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.Class)
     * @throws NullPointerException this methods test this exception.
     */
    @Test(expected = NullPointerException.class)
    public void testGetENCNameRemoteForClassWithNullType() {
        final Class clazz = null;
        JBossENCBuilder.getENCNameRemote(clazz);
    }

    /**
     * Test the JBossENCBuilder.getENCNameRemote(java.lang.Class)
     * method for a argument from type Interface.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.Class)
     */
    @Test
    public void testGetENCNameLocalForInterface() {
        final String expected =
                JBossENCBuilder.EAR_NAME + "/MyServiceBean/"
                + JBossENCBuilder.LOCAL_SCOPE;
        final String result =
                JBossENCBuilder.getENCNameLocal(MyService.class);
        assertEquals("Wrong enc local name", result, expected);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.Class)</code>
     * method functionality.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.Class)
     */
    @Test
    public void testGetENCNameRemoteForInterface() {
        final String expected =
                JBossENCBuilder.EAR_NAME + "/MyServiceBean/"
                + JBossENCBuilder.REMOTE_SCOPE;
        final String result =
                JBossENCBuilder.getENCNameRemote(MyService.class);
        assertEquals("Wrong enc remote name", result, expected);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.String)</code>
     * method functionality for right arguments.
     *
     * @see JBossENCBuilder#getENCNameLocal(java.lang.String)
     */
    @Test
    public void testGetENCLocalNameForString() {
        final String className = "MyBean";
        final String expected =
                JBossENCBuilder.EAR_NAME + "/" + className
                + "/" + JBossENCBuilder.LOCAL_SCOPE;
        final String name = JBossENCBuilder.getENCNameLocal(className);
        assertEquals(expected, name);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.String)</code>
     * method functionality with a null argument -  a NullPointerException
     * raises.
     *
     * @throws NullPointerException this test proves this exception.
     * @see JBossENCBuilder#getENCNameLocal(java.lang.String)     *
     */
    @Test(expected = NullPointerException.class)
    public void testGetENCLocalNameForNullString() {
        final String className = null;
        JBossENCBuilder.getENCNameLocal(className);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.String)</code>
     * method functionality for right arguments.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.String)
     */
    @Test
    public void testGetENCRemoteNameForString() {
        final String className = "MyBean";
        final String expected =
                JBossENCBuilder.EAR_NAME + "/" + className
                + "/" + JBossENCBuilder.REMOTE_SCOPE;
        final String name = JBossENCBuilder.getENCNameRemote(className);
        assertEquals(expected, name);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.String)</code>
     * method functionality with a null argument -  a NullPointerException
     * raises.
     *
     * @throws NullPointerException this test proves this exception.
     * @see JBossENCBuilder#getENCNameRemote(java.lang.String)
     */
    @Test(expected = NullPointerException.class)
    public void testGetENCRemteNameForNullString() {
        final String className = null;
        JBossENCBuilder.getENCNameRemote(className);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.String)</code>
     * method functionality for right arguments.
     *
     * @see JBossENCBuilder#getENCNameLocal(java.lang.String, java.lang.String)
     */
    @Test
    public void testGetENCLocalNameForEanAndClass() {
        final String className = "MyBean";
        final String earName = "my_ear";
        final String expected = earName + "/" + className
                + "/" + JBossENCBuilder.LOCAL_SCOPE;
        final String name = JBossENCBuilder.getENCNameLocal(earName, className);
        assertEquals(expected, name);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.String)</code>
     * method functionality for right arguments.
     *
     * @see JBossENCBuilder#getENCNameRemote(java.lang.String, java.lang.String)
     */
    @Test
    public void testGetENCRemoteNameForEanAndClass() {
        final String className = "MyBean";
        final String earName = "my_ear";
        final String expected = earName + "/" + className
                + "/" + JBossENCBuilder.REMOTE_SCOPE;
        final String name = JBossENCBuilder.getENCNameRemote(earName, className);
        assertEquals(expected, name);
    }

    /**
     * Tests the <code>JBossENCBuilder.getENCNameLocal(java.lang.String)</code>
     * method functionality for null arguments  -  a NullPointerException
     * raises.
     *
     * @throws NullPointerException this test proves this exception.
     * @see JBossENCBuilder#getENCNameLocal(java.lang.String, java.lang.String)
     */
    @Test(expected=NullPointerException.class)
    public void testGetENCLocalNameForNullEanAndNullClass() {
        final String className = null;
        final String earName = null;
        JBossENCBuilder.getENCNameLocal(earName, className);
    }
    
    /**
     * Tests the <code>JBossENCBuilder.getENCNameRemote(java.lang.String)</code>
     * method functionality for null arguments  -  a NullPointerException
     * raises.
     *
     * @throws NullPointerException this test proves this exception.
     * @see JBossENCBuilder#getENCNameRemote(java.lang.String, java.lang.String)
     */
    @Test(expected=NullPointerException.class)
    public void testGetENCRemoteNameForNullEanAndNullClass() {
        final String className = null;
        final String earName = null;
        JBossENCBuilder.getENCNameRemote(earName, className);
    }
}
