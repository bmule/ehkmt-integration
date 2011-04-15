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


import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.Converter;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class HashMapConverter implements Converter {

    @Override
    public Object convert(Class type, Object o) {
        if (o == null) {
            return null;
        }

        
        if (o instanceof String && type == HashMap.class) {
            
            final StringBuffer strRepresentation = 
                    new StringBuffer(o.toString());
            strRepresentation.deleteCharAt(0);
            strRepresentation.deleteCharAt(strRepresentation.length() - 1);
            
            final Map result = new HashMap();
            if (strRepresentation.toString().isEmpty()) {
                return result;
            }
            
            for (StringTokenizer entries = new StringTokenizer(strRepresentation.toString(), ","); 
                    entries.hasMoreElements();) {
                final String entry = entries.nextToken();
                System.out.println("-->" + entry);
                final String[] keyAndValue = entry.split(":");
                result.put(keyAndValue[0], keyAndValue[1]);
            }

            return result;
        }

        return null;

    }
}
