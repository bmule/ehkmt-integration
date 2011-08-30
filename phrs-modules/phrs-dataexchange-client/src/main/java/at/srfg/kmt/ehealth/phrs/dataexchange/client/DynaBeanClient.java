/*
 * Project :iCardea
 * File : DynaBeanUtil.java
 * Encoding : UTF-8
 * Date : Aug 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 * It is able to transform a resource in to a Dynamic Bean.
 * @author Mihai
 */
public final class DynaBeanClient {

    private final GenericTriplestore triplestore;
    private final SchemeClient schemeClient;

    public DynaBeanClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
        schemeClient = new SchemeClient(triplestore);
    }

    private DynaClass getDynaClass(String resourceURI)
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
            properites.add(dynaProperty);
        }
        DynaProperty[] fileds = new DynaProperty[properites.size()];
        properites.toArray(fileds);
        final BasicDynaClass result = new BasicDynaClass(resourceURI, null, fileds);

        return result;
    }

    public DynaBean getDynaBean(String resourceURI)
            throws IllegalAccessException, InstantiationException, TripleException {
        final DynaClass clazz = (DynaClass) getDynaClass(resourceURI);
        final DynaBean newInstance = clazz.newInstance();

        final Iterable<Triple> forSubject = triplestore.getForSubject(resourceURI);
        for (Triple triple : forSubject) {
            final String predicate = triple.getPredicate();
            final String value = triple.getValue();

            if (schemeClient.isPropertyResource(predicate)) {
                final DynaBean dynaBean = getDynaBean(value);
                newInstance.set(predicate, dynaBean);
            } else {
                final DynaProperty dynaProperty =
                        newInstance.getDynaClass().getDynaProperty(predicate);
                final boolean isList = dynaProperty.getType().equals(ArrayList.class);
                if (!isList) {
                    newInstance.set(predicate, value);
                } else {
                    List props = (List) newInstance.get(predicate);
                    if (props == null) {
                        props = new ArrayList<String>();
                        newInstance.set(predicate, props);
                    }
                    props.add(value);
                }
            }

        }

        return newInstance;
    }
}
