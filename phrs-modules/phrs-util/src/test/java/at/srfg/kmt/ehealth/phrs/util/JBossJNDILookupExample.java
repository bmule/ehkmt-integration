/*
 * Project :iCardea
 * File : JBossJNDILookupExample.java
 * Date : Jan 3, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 * This class is just an example - it has only didactical purposes.
 *
 * @author Mihai
 */
public class JBossJNDILookupExample {

    public void myMethod() {
        try {

            // here I do an local look up for a bean named MyServiceBean and use it.
            // the bean must be localted in the phrs ear named :
            // phrs-ear-0.1-SNAPSHOT (see the JBossENCBuilder.EAR_NAME)
            final MyService lookupLocal =
                    JBossJNDILookup.lookupLocal(MyService.class);
            lookupLocal.doStuff(10);
        } catch (NamingException ex) {
            Logger.getLogger(JBossJNDILookupExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void myMethod1() {
        // builds an ENC JNDI name for a bean named MyServiceBean local scoped
        // and it is packed in a ear named :
        // phrs-ear-0.1-SNAPSHOT (see the JBossENCBuilder.EAR_NAME)
        final String name = JBossENCBuilder.getENCNameLocal(MyService.class);

        try {
            // lookup and use the bean
            final MyService lookupLocal = JBossJNDILookup.lookup(name);
            lookupLocal.doStuff(10);
        } catch (NamingException ex) {
            Logger.getLogger(JBossJNDILookupExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
