/*
 * Project :iCardea
 * File : TestBean.java
 * Date : Dec 26, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.util;

import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * Simple implementation for the defined business.
 *
 * @author Mihai
 */
@Stateless
@Local(MyService.class)
public class MyServiceBean implements MyService {

    /**
     * Returns an incremented with one value for the <code>from</code> argument.
     *
     * @param from the argument.
     * @return an incremented with one value for the <code>from</code> argument.
     */
    @Override
    public int doStuff(int from) {
        return from + 1;
    }
}
