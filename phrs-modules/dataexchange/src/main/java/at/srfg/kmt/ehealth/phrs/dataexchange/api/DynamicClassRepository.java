/*
 * Project :iCardea
 * File : DymanicClassRepository.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface DynamicClassRepository {

    /**
     * Builds a 
     * 
     * @return 
     */
    DynamicClass create();

    DynamicClass create(String uri);

    DynamicClass create(String name, String uri);

    boolean persist(DynamicClass dynamicClass);

    boolean exits(String uri);

    boolean exits(DynamicClass dynamicClass);

    DynamicClass get(String uri);

    /**
     * Returns all beans that match the given query by example.
     * The semantic for the <code>qbe</code> argument is 
     * defined in the implementation.
     * 
     * @param query the query by example parameter map.
     * @return all beans that match the given query.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     */
    List<DynamicClass> get(Map<String, ?> qbe);

    Set<DynamicClass> getForPrefix(String uri);

    DynamicClass remove(String uri);

    DynamicClass remove(DynamicClass dynamicClass);
}
