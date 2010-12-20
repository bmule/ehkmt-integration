/*
 * Project :iCardea
 * File : TestBean.java
 * Date : Dec 17, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;

import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.Test;

/**
 *
 * @author Mihai
 */

public class TestBean {

    @Test
    public void testMy() throws NamingException {
        System.out.println("------");
        System.out.println("------");
        try {
            dd();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("------");
        System.out.println("------");
        System.out.println("------");
    }

    private void dd() throws NamingException {
                // jndi.customer.ejb=java:/comp/env/ejb/CustomerEntityBean
// jndi.process.ejb=java:/comp/env/ejb/ProcessCustomerSessionBean

        final String beanName = "/java:/comp/env/PermissionManagerBean";
        final InitialContext initial = new InitialContext();
        System.out.println("initial -->" + initial);
        final Object result = initial.lookup(beanName);
//        System.out.println("---> " + result);
    }

}
