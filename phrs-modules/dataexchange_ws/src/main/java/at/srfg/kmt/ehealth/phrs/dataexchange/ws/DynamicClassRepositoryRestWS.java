/*
 * Project :iCardea
 * File : DynamicClassRepositoryRestWS.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import at.srfg.kmt.ehealth.phrs.dataexchange.model.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelClassFactory;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.Status;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides web services for the <code>DynamicClassRepositoryRestWS</code>.
 * </br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>/dataexchange_ws/dynamic_class_repository/getClass -  used obtain 
 * a Dynamic Class like JSON object,the class is located after its  unique URI.
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynamicBeanRepository
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

    /**
     * GET based web service used to obtain Dynamic Class like JSON object, 
     * the class is located after its  unique UR. </br>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/dynamic_class_repository/getClass
     * 
     * @return a JSON array that contains the required class (like JSON object).
     * If there are no matches for the given URI then a :
     * <code>javax.ws.rs.core.Response.Status.NO_CONTENT</code> is returned. 
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>
     */
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

    /**
     * GET based web service used to obtain load all the PHRS default classes.</br>
     * This classes are :
     * <ul>
     * <li> Body Weight Class
     * <li> Blood Pleasure class
     * <li> Body Weight
     * <li> Medication
     * </ul>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/dynamic_class_repository/loadDefaultClasses
     * 
     * @return if the operation was successfully :
     * <code>javax.ws.rs.core.Response.Status.OK</code> is returned.  <br>
     * If the backend can not process the query from any reason then this 
     * method returns a :
     * <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code>
     */
    @GET
    @Path("/loadDefaultClasses")
    @Produces("application/json")
    public Response loadDefaultClasses() {

        final DynamicClassRepository classRepository;
        try {
            classRepository = JBossJNDILookup.lookupLocal(DynamicClassRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        // ActivityOfDailyLiving
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> activityOfDailyModelMap =
                ModelClassFactory.createBodyWeighModelMap();
        final DynamicClass activityOfDailyDynamicClass =
                ModelFactory.buildDynamicClass(Constants.ACTIVITY_OF_DAILY_LIVING_CLASS_NAME,
                Constants.ACTIVITY_OF_DAILY_LIVING_CLASS_URI, activityOfDailyModelMap);
        registerClass(classRepository, activityOfDailyDynamicClass);

        // ActivityItem
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> activityItemModelMap =
                ModelClassFactory.createBodyWeighModelMap();
        final DynamicClass activityItemDynamicClass =
                ModelFactory.buildDynamicClass(Constants.ACTIVITY_ITEM_CLASS_NAME,
                Constants.ACTIVITY_ITEM_CLASS_URI, activityItemModelMap);
        registerClass(classRepository, activityItemDynamicClass);

        // ActivityLevel
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> activityLevelModelMap =
                ModelClassFactory.createBodyWeighModelMap();
        final DynamicClass activityLevelDynamicClass =
                ModelFactory.buildDynamicClass(Constants.ACTIVITY_LEVEL_CLASS_NAME,
                Constants.ACTIVITY_LEVEL_CLASS_URI, activityLevelModelMap);
        registerClass(classRepository, activityLevelDynamicClass);

        // BloodPressure
                final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> bloodPreasureModelMap =
                ModelClassFactory.createBodyWeighModelMap();
        final DynamicClass bloodPreasureDynamicClass =
                ModelFactory.buildDynamicClass(Constants.BLOOD_PREASURE_CLASS_NAME,
                Constants.BLOOD_PREASURE_CLASS_URI, bloodPreasureModelMap);
        registerClass(classRepository, bloodPreasureDynamicClass);

        // BodyWeight
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> bodyWeighModelMap =
                ModelClassFactory.createBodyWeighModelMap();
        final DynamicClass bodyWeighDynamicClass =
                ModelFactory.buildDynamicClass(Constants.BODY_WEIGHT_CLASS_NAME,
                Constants.BODY_WEIGHT_CLASS_URI, bodyWeighModelMap);
        registerClass(classRepository, bodyWeighDynamicClass);

        // medication
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> medicationModelMap =
                ModelClassFactory.createMedicationModelMap();
        final DynamicClass medicationDynamicClass =
                ModelFactory.buildDynamicClass(Constants.MEDICATION_CLASS_NAME,
                Constants.MEDICATION_CLASS_URI, medicationModelMap);
        registerClass(classRepository, medicationDynamicClass);

        return Response.status(Status.OK).build();
    }

    private void registerClass(DynamicClassRepository classRepository,
            DynamicClass dynamicClass) {
        final String uri = dynamicClass.getUri();
        final String name = dynamicClass.getName();
        final boolean bodyWeighClassExists =
                classRepository.exits(uri);

        String [] toLog = {uri, name};
        if (!bodyWeighClassExists) {
            classRepository.persist(dynamicClass);
            LOGGER.debug("Persist the class with this URI [{}] and name [{}].", toLog);
        } else {
            LOGGER.debug("The class with this URI [{}] and Name [{}] was already registered.", toLog);
        }
    }
}
