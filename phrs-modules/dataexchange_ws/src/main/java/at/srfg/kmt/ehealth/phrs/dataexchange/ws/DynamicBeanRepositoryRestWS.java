/*
 * Project :iCardea
 * File : DynamicBeanRepositoryRestWS.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.impl.DynamicUtil;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory;
import at.srfg.kmt.ehealth.phrs.datamodel.impl.ModelClassFactory;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.jboss.cache.config.Dynamic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides web services for the <code>DynamicBeanRepository</code>. </br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/persist used to persist a 
 * <li> <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/getAllForClass
 * <li> <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/getLastForClass
 * </ul>
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynamicBeanRepository
 */
@Path("/dynamic_bean_repository")
public class DynamicBeanRepositoryRestWS {

    public static final String CLASS_URI_PROPERTY = "class_uri";

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DynamicBeanRepositoryRestWS</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynamicBeanRepositoryRestWS.class);

    /**
     * POST based web service used to persist a given dynamic bean JSON 
     * serialized. </br>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/persist.
     * 
     * @return <code>javax.ws.rs.core.Response.Status.OK</code> if the operation 
     * is successfully.
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>.
     * If the dyna bean JSON representation (the <code>dynaBean</code> argument)
     * does not contains a  property named "class_uri" that contains the URI for
     * an existent class then this method returns :
     * <code>javax.ws.rs.core.Response.Status.BAD_REQUEST</code>.
     */
    @POST
    @Path("/persist")
    @Produces("application/json")
    public Response persist(@FormParam("dynaBean") String dynaBean) {

        if (dynaBean == null && dynaBean.isEmpty()) {
            LOGGER.error("This dynabean JSON representation [{}] is not valid", dynaBean);
            return Response.status(Status.BAD_REQUEST).build();
        }

        LOGGER.debug("Tries to persists {}", dynaBean);
        final JSONObject json;
        try {
            json = JSONObject.fromObject(dynaBean);
        } catch (RuntimeException rte) {
            LOGGER.error("This dynabean JSON representation [{}] is not valid", dynaBean);
            LOGGER.error(rte.getMessage(), rte);
            return Response.status(Status.BAD_REQUEST).build();
        }

        final String classURI = (String) json.get(CLASS_URI_PROPERTY);
        if (classURI == null) {
            LOGGER.error("This dynabean JSON representation [{}] does not have a class_uri property", dynaBean);
            return Response.status(Status.BAD_REQUEST).build();
        }

        final DynamicClassRepository classRepository;
        try {
            classRepository = JBossJNDILookup.lookupLocal(DynamicClassRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final boolean classExits = classRepository.exits(classURI.trim());
        if (!classExits) {
            LOGGER.error("The class with URI [{}] is not register in the repository.", classURI);
            return Response.status(Status.BAD_REQUEST).build();
        }

        final DynamicBeanRepository beanRepository;
        try {
            beanRepository = JBossJNDILookup.lookupLocal(DynamicBeanRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final DynamicClass dynamicClass = classRepository.get(classURI.trim());
        final DynaBean newDynaBean;
        try {
            newDynaBean = DynamicUtil.getNewInstance(dynamicClass);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        final DynaBean jsonBean = DynamicUtil.fromJSONString(dynaBean);

        // cpoy all the json dyna mean in the new bean
        final DynaProperty[] dynaProperties =
                jsonBean.getDynaClass().getDynaProperties();

        for (DynaProperty dynaProperty : dynaProperties) {
            final String name = dynaProperty.getName();
            if (ModelClassFactory.isBodyWeighProperty(name)) {
                final Object contnet = jsonBean.get(name);
                newDynaBean.set(name, contnet);
            }
        }

        LOGGER.debug("Tries to persist bean : {}", DynamicUtil.toString(newDynaBean));
        beanRepository.add(newDynaBean);
        LOGGER.debug("The bean [{}] was succefully persisted.", DynamicUtil.toString(newDynaBean));
        // FIXME : return the uRI for the peristed bean
        return Response.status(Status.OK).build();
    }
    
    

    /**
     * GET based web service used to obtain all version for a bean, the
     * bean is identified after its (unique) class URI. </br>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/getAllForClass
     * 
     * @return a JSON array that contains all beans versions.
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>.
     */
    @GET
    @Path("/getAllForClass")
    @Produces("application/json")
    public Response getAllForClass(@QueryParam("class_uri") String classUri) {
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

        final DynamicBeanRepository beanRepository;
        try {
            beanRepository = JBossJNDILookup.lookupLocal(DynamicBeanRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final Set<DynaBean> forClass;
        try {
            forClass = beanRepository.getAllForClass(classUri);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final JSONArray array = new JSONArray();
        for (DynaBean dynaBean : forClass) {
            final String json = DynamicUtil.toString(dynaBean);
            array.add(json);
        }

        LOGGER.debug("Return beans {}.", array);
        return Response.ok(array.toString()).build();
    }

    /**
     * GET based web service used to obtain the last version for a bean, the
     * bean is identified after its (unique) class URI. </br>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/dynamic_bean_repository/getLastForClass
     * 
     * @return a JSON string that contains the last last version for a bean.
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>.
     */
    @GET
    @Path("/getLastForClass")
    @Produces("application/json")
    public Response getLastForClass(@QueryParam("class_uri") String classUri) {
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

        final DynamicBeanRepository beanRepository;
        try {
            beanRepository = JBossJNDILookup.lookupLocal(DynamicBeanRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final DynaBean forClass;
        try {
            forClass = beanRepository.getForClass(classUri);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final String jsonBean = DynamicUtil.toString(forClass);
        LOGGER.debug("The bean {} is return.", jsonBean);

        return Response.ok(jsonBean).build();
    }
}
