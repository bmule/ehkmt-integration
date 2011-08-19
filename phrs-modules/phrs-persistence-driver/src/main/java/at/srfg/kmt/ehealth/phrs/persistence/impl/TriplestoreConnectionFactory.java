/*
 * Project :iCardea
 * File : ConnectionFactory.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl;


import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import java.net.URL;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Builds a connection to a triplestore.
 * 
 * TODO: this class must be moved in the driver!! 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class TriplestoreConnectionFactory {
    
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SesameTriplestore.class);


    /**
     * The configuration file name.
     */
    public static final String CONFIG_FILE = "generic_triplestore.xml";

    /**
     * The unique instance for this factory.
     */
    private static final TriplestoreConnectionFactory THIS;
    static {
        try {
            THIS = new TriplestoreConnectionFactory();
        } catch (GenericRepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }


    private final XMLConfiguration configuration;

    private final GenericTriplestore triplestore;

    private TriplestoreConnectionFactory() throws GenericRepositoryException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(CONFIG_FILE);
        if (resource == null) {
            configuration = null;
        } else {
            try {
                configuration =
                        new XMLConfiguration(CONFIG_FILE);
            } catch (ConfigurationException e) {
                LOGGER.warn(e.getMessage(), e);
                final GenericRepositoryException exception =
                        new GenericRepositoryException(e);
                throw exception;
            }
        }

        // TODO : The triplestore type will be according with the configuration  
        triplestore = new SesameTriplestore();
        
        final List<String> filesToLoad = 
                configuration.getList("postconstruct.loadfiles");
        System.out.println("Load files : " + filesToLoad);
        LOGGER.debug("Load files : " + filesToLoad);

        for (String fileToLoad : filesToLoad) {
            
            final LoadRdfPostConstruct loadRdf = new LoadRdfPostConstruct(fileToLoad);
            ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdf);
        }
        
        ((GenericTriplestoreLifecycle) triplestore).init();
    }
    
    /**
     * Returns the unique instance for this factory.
     * 
     * @return the unique instance for this factory.
     */
    public static TriplestoreConnectionFactory getInstance() {
        return THIS;
    }

    /**
     * Builds a connection to the triplestore.
     * 
     * @return a connection to the triplestore.
     */
    public synchronized GenericTriplestore getTriplestore() {
        return triplestore;
    }
}
