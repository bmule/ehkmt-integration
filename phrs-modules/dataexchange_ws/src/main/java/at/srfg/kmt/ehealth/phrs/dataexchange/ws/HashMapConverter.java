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
import java.util.Iterator;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.Converter;


/**
 * Transforms a JSON like object in to a Map. Each JOSN property name is used 
 * like key and the property.
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

            final Map result = new HashMap();
            final String content = (String) o;
            
            if (content.trim().isEmpty()) {
                return result;
            }
            
            final JSONObject json = JSONObject.fromObject(o);
            if (json.isEmpty()) {
                return result;
            }
            
            for (final Iterator keys = json.keys(); keys.hasNext();) {
                final String key =  keys.next().toString();
                final Object value = json.get(key);
                result.put(key, value);
            }

            return result;
        }

        return null;

    }

    private String removeQuotes(String in) {
        final StringBuilder result = new StringBuilder(in);
        if (in.startsWith("\"")) {
            result.deleteCharAt(0);
        }

        if (in.endsWith("\"")) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }
}
