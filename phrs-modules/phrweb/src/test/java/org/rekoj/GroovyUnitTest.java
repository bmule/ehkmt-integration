/*
 * Project :iCardea
 * File : GroovyUnitTest.java
 * Encoding : UTF-8
 * Date : Dec 20, 2011
 * User : Mihai Radulescu
 */
package org.rekoj;


import junit.framework.Assert;
import org.junit.Test;


/**
 * Tests the Groovy / java interaction. <br/>
 * To execute <b>only</b> this test use the follow maven command :
 * <pre>
 * mvn clean compile test -Dtest=GroovyUnitTest
 * </pre>
 * 
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class GroovyUnitTest {

    /**
     * Builds a <code>GroovyUnitTest</code> instance.
     */
    public GroovyUnitTest() {
        // UNIMPLEMENTED
    }
    
    /**
     * Invoke a java class method together with groovy instance and prove the
     * result is the expected one.
     */
    @Test
    public void amazingComplexTest() {
        final Helper helper = new Helper();
        final String helpResult = helper.help(new Example());
        final String expectedResponse = "Hey ho, let's go";
        // See the Helpler class for more details. From here originate 
        // the expectedResponse
        Assert.assertEquals(expectedResponse, helpResult);
    }
}
