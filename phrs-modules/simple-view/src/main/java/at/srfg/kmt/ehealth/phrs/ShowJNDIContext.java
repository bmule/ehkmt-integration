/*
 * Project :iCardea
 * File : ShowJNDIContext.java
 * Date : Dec 21, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs;


import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

/**
 *
 * @author Mihai
 */
public class ShowJNDIContext {

    public static void show() {
        try {
            InitialContext ctx = new InitialContext();
            System.out.println("List all the names in the JNDI-Context:");
            showJndiContext(ctx, "", "");
            ctx.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static void showJndiContext(InitialContext ctx, String name, String space) {
        if (null == name) {
            name = "";
        }
        if (null == space) {
            space = "";
        }
        try {
            NamingEnumeration en = ctx.list(name);
            while (en != null && en.hasMoreElements()) {
                String delim = (null != name && 0 < name.length()) ? "/" : "";
                NameClassPair nc = (NameClassPair) en.next();
                System.out.println(space + name + delim + nc);
                if (40 > space.length()) {
                    showJndiContext(ctx, nc.getName(), "    " + space);
                }
            }
        } catch (javax.naming.NamingException ex) {
        }
    }
}
