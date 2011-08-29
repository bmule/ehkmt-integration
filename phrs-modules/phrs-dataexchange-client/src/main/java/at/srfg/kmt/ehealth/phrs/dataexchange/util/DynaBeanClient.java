/*
 * Project :iCardea
 * File : DynaBeanUtil.java
 * Encoding : UTF-8
 * Date : Aug 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;

import at.srfg.kmt.ehealth.phrs.dataexchange.client.SchemeClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

/**
 *
 * @author Mihai
 */
public final class DynaBeanClient {

    private final GenericTriplestore triplestore;
    private final SchemeClient schemeClient;

    public DynaBeanClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
        schemeClient = new SchemeClient(triplestore);
    }

    public DynaBean getDynaBean(String resourceURI)
            throws IllegalAccessException, InstantiationException, TripleException {

        final Iterable<Triple> forSubject = triplestore.getForSubject(resourceURI);
        Map<String, List<String>> properties =
                new HashMap<String, List<String>>();

        for (Triple triple : forSubject) {
            final String predicate = triple.getPredicate();
            final String value = triple.getValue();
            
            List<String> field = properties.get(predicate); 
            if (field == null) {
                field = new ArrayList<String>();
                properties.put(predicate, field);
            }
            field.add(value);
        }
        
        
        final List<DynaProperty> properites = new ArrayList<DynaProperty>();
        for (Map.Entry<String, List<String>> field : properties.entrySet()) {
            final String key = field.getKey();
            final List<String> value = field.getValue();

            final boolean isList = value.size() > 1;
            final DynaProperty dynaProperty = isList 
                    ? new DynaProperty(key, ArrayList.class) 
                    : new DynaProperty(key); 
        } 


        DynaProperty[] fileds = new DynaProperty[properites.size()];
        final BasicDynaClass result = new BasicDynaClass("", null, fileds);

        

        return result.newInstance();
    }
}
