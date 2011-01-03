/*
 * Project :iCardea
 * File : JNDIClinet.java
 * Date : Dec 22, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;



/**
 *
 * @author Mihai
 */
public class JNDIClinet {

    //public static final String LocalJNDIName = GroupManager.class.getSimpleName() + "/local";

    public static void main(String... args) throws NamingException {
        System.out.println("JNDIClinet start.");
        String name = "GroupManagerBean/remote";

        // java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory,
        // java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
        Properties p = new Properties();
        p.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        p.put("java.naming.provider.url", "jnp://localhost:1099");
        p.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");

        InitialContext ctx = new InitialContext(p);
        final Object lookup = ctx.lookup(name);
        System.out.println("Lookup : " +  lookup);
        System.out.println("JNDIClinet ends.");

    }
}
