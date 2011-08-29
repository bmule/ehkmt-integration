/*
 * Project :iCardea
 * File : DynaBeanUtil.java
 * Encoding : UTF-8
 * Date : Aug 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;

import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

/**
 *
 * @author Mihai
 */
public final class DynaBeanUtil {
    
    
    private DynaBeanUtil() {
        // UNIMPLEMENTED
    }
    
    public static DynaBean getDynaBean(Iterable<Triple> triples) throws IllegalAccessException, InstantiationException {
        
        final BasicDynaClass result = new BasicDynaClass();

        List<DynaProperty> properites = new ArrayList<DynaProperty>();
        
        for (Triple triple : triples) {
            final String predicate = triple.getPredicate();
            final String value = triple.getValue();
        }
        
        return result.newInstance();
    } 
    
}
