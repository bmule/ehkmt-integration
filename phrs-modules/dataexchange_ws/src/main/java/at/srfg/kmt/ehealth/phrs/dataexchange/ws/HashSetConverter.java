/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : HashMapConverter.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.Converter;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class HashSetConverter implements Converter {

    @Override
    public Object convert(Class type, Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof String && type == HashSet.class) {
            final Set result = new HashSet();
            
            for (StringTokenizer entries = new StringTokenizer(o.toString(), ","); 
                    entries.hasMoreElements();) {
                final String entry = entries.nextToken();
                result.add(entry);
            }

            return result;
        }

        return null;

    }
}
