/*
 * Project :iCardea
 * File : GenericTriplestoreDebuger.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;

import java.util.List;



/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public interface GenericTriplestoreExporter {

    String export(String subject, String predicate, String value);

    String export(String subject, String predicate);
    
    String export(String subject);

    String export(List<PathElement> path);
}
