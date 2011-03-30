/*
 * Project :iCardea
 * File : DymanicClassRepository.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.util.Set;
import javax.persistence.Query;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface DymanicClassRepository {

    DynamicClass create();

    DynamicClass create(String uri);

    DynamicClass create(String name, String uri);

    boolean persist(DynamicClass dynamicClass);

    boolean exits(String uri);

    boolean exits(DynamicClass dynamicClass);

    DynamicClass get(String uri);

    Set<DynamicClass> get(Query query);

    Set<DynamicClass> getForPrefix(String uri);

    DynamicClass remove(String uri);

    DynamicClass remove(DynamicClass dynamicClass);

}
