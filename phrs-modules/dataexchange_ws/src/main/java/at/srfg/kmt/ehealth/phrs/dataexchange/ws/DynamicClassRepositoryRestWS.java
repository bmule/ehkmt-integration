/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.impl.DynamicUtil;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.Status;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mihai
 */
@Path("/dynamic_class_repository")
public class DynamicClassRepositoryRestWS {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DynamicClassRepositoryRestWS</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynamicClassRepositoryRestWS.class);
    
    
    @GET
    @Path("/getClass")
    @Produces("application/json")
    public Response getClass(@QueryParam("class_uri") String classUri) {
        
        if (classUri == null && classUri.isEmpty()) {
            LOGGER.error("This classUri  [{}] is not valid", classUri);
            return Response.status(Status.BAD_REQUEST).build();
        }

        final DynamicClassRepository classRepository;
        try {
            classRepository = JBossJNDILookup.lookupLocal(DynamicClassRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final boolean classExits = classRepository.exits(classUri.toString());
        if (!classExits) {
            LOGGER.error("The class with URI [{}] is not register in the repository.", classUri);
            return Response.status(Status.NO_CONTENT).build();
        }
        
        final DynamicClass clazz = classRepository.get(classUri);
        final JSONObject result = new JSONObject();

        final String name = clazz.getName();
        result.put("name", name);
        
        final String class_uri = clazz.getUri();
        result.put("class_uri", class_uri);
        
        // FIXME : add all the properties 
        
        
         
         return Response.ok(result.toString()).build();
     }
}
