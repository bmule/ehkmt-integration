/*
 * Project :iCardea
 * File : PhrPropertyUtilUnitTest.java.
 * Date : Mar 3, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Used to prove the <code>PhrPropertyUtil</code> functionality.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see PhrPropertyUtil
 */
public class PhrPropertyUtilUnitTest {

    @Test
    public void testBuildName() {
        final String ns = "ns";
        final String name = "name";

        final String buildName = PhrPropertyUtil.buildName(ns, name);
        assertEquals("ns__name", buildName);
    }

    @Test
    public void testGetName() {
        final String fullName = "ns__name";
        final String name = PhrPropertyUtil.getName(fullName);
        assertEquals("name", name);
    }

    @Test
    public void testGetNamespace() {
        final String fullName = "ns__name";
        final String nameSpace = PhrPropertyUtil.getNamespace(fullName);
        assertEquals("ns", nameSpace);

    }
}
