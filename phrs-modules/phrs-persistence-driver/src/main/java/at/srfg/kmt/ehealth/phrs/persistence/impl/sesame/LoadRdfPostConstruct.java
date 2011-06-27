/*
 * Project :iCardea
 * File : LoadRdfPostConstruct.java
 * Encoding : UTF-8
 * Date : Jun 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import java.io.InputStream;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import at.srfg.kmt.ehealth.phrs.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class LoadRdfPostConstruct implements Runnable {

    RepositoryConnection connection;

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct</code>..
     */
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(LoadRdfPostConstruct.class);

    @Override
    public void run() {
        final InputStream resourceAsStream =
                getClass().getClassLoader().getResourceAsStream("startup.rdf");
        if (resourceAsStream != null) {
            return;
        }
        
        try {
            connection.add(resourceAsStream, Constants.ICARDEA_NS, RDFFormat.RDFXML);
            connection.commit();
            LOGGER.debug("is empty? " + connection.isEmpty());

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
