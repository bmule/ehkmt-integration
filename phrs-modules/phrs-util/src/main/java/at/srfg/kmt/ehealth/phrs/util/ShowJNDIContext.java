/*
 * Project :iCardea
 * File : ShowJNDIContext.java
 * Date : Dec 21, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.util;


import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * It is used to print the all the existing ENC names from the JNDI registry
 * on the standard output. <br>
 * This class is not design to be extended.
 *
 * @author Mihai
 */
public final class ShowJNDIContext {

    /**
     * Don't let anybody to instantiate this class.
     */
    private ShowJNDIContext() {
        // UNIMPLEMENTED
    }

    public static void showAll(InitialContext ctx) throws NamingException {
        showJndiContext(ctx, "", "");
        ctx.close();
    }


    public static void showJndiContext(InitialContext ctx, String name,
            String space) throws NamingException {
        if (null == name) {
            name = "";
        }
        if (null == space) {
            space = "";
        }

        NamingEnumeration en = ctx.list(name);
        while (en != null && en.hasMoreElements()) {
            final String delim = (null != name && 0 < name.length()) ? "/" : "";
            final  NameClassPair nc = (NameClassPair) en.next();

            System.out.println(space + name + delim + nc);

            if (40 > space.length()) {
                showJndiContext(ctx, nc.getName(), "    " + space);
            }
        }

    }
}
