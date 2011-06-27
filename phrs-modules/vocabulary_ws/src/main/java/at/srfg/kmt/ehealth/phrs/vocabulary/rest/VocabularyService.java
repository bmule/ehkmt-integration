/*
 * Project :iCardea
 * File : VocabularyService.java
 * Encoding : UTF-8
 * Date : May 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.rest;


//import at.srfg.kmt.ehealth.phrs.vocabulary.api.VocabularyLoader;
import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
/**
 * Provides web services for the <code>VocabularyLoader</code> and
 * <code>ControlledItemRepository</code>. </br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>//phrs/vocabulary/load - used to 
 * load all the default vocabulary terms.
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/get - used to get 
 * all the controlled vocabulary terms that have a certain code system. 
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/getForTag - 
 * used to get all the controlled vocabulary terms that have a certain tag.
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/getForPrefLabel - 
 * used to get all the controlled vocabulary where the prefered label match 
 * exact a given String.
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/getForPrefLabelPrefix - 
 * used to get all the controlled vocabulary where the prefered label starts 
 * with a given String.
 * </ul>
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@ApplicationScoped
@Path("/phrs/vocabulary")
@Produces({"application/json"})
public class VocabularyService {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.VocabularyService</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VocabularyService.class);
    
//    @Inject
//    private VocabularyLoader vocabularyLoader;
    
    
    //@Inject
    //private VocabularyLoader vocabularyLoader;

    /**
     * GET based web service used to load all the default vocabulary terms.
     * 
     * @return <code>javax.ws.rs.core.Response.Status.OK</code> if the operation 
     * is successfully.
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>.
     */
    @GET
    @Path("/load")
    public Response load() {
//        System.out.println("--> " + voabularyLoader);
        System.out.println("--> ");
        System.out.println("--> ");
        System.out.println("--> ");
        System.out.println("--> ");
        
        return Response.status(Status.OK).build();
    }
}
