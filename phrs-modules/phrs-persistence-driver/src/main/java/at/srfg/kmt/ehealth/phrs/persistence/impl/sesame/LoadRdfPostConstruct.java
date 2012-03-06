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
 * Used to load a specific RDF/XML file in to the underlying persistence layer. 
 *  
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class LoadRdfPostConstruct implements Runnable {

    /**
     * The Sesame specific connection.
     */
    RepositoryConnection connection;

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoadRdfPostConstruct.class);

    /**
     * The 
     */
    private final String fileName;

    public LoadRdfPostConstruct(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("The Filename can not be null.");
        }
        
        this.fileName = fileName;
    }

    @Override
    public void run() {
        final InputStream resourceAsStream =
                getClass().getClassLoader().getResourceAsStream(fileName);
        if (resourceAsStream == null) {
            LOGGER.warn("The initial data set can not be located. No resource with name " + fileName + " found in calsspath.");
            return;
        }
        
        try {
            final boolean isOpen = connection.isOpen();
            if (!isOpen) {
                LOGGER.error("The file {} can not be loaded because the connection is closed.", fileName);
                return;
            }
            
            connection.add(resourceAsStream, Constants.ICARDEA_NS, RDFFormat.RDFXML);
            connection.commit();
        } catch (Exception ex) {
            
            LOGGER.error("The file {} can not be loaded.", fileName);
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
