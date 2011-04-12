/*
 * Project :iCardea
 * File : ControlledItemRepositoryRestWS.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.VocabularyLoader;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides web services for the <code>VocabularyLoader</code> and
 * <code>ControlledItemRepository</code>. </br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/load - used to 
 * load all the default vocabulary terms.
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/get - used to get 
 * all the controlled vocabulary terms that have a certain code system. 
 * <li> <JBOSS URI>/dataexchange_ws/controlled_item_repository/getForTag - 
 * used to get all the controlled vocabulary terms that have a certain tag.
 * </ul>
 *
 * @author Mihai
 */
@Path("/controlled_item_repository")
public class ControlledItemRepositoryRestWS {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ControlledItemRepositoryRestWS</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ControlledItemRepositoryRestWS.class);

    /**
     * Builds a <code>ControlledItemRepositoryRestWS</code> instance.
     */
    public ControlledItemRepositoryRestWS() {
        // UNIMPLEMENTD
    }

    /**
     * GET based web service used to load all the default vocabulary terms.
     * 
     * @return <code>javax.ws.rs.core.Response.Status.OK</code> if the operation 
     * is successfully or <code>javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR</code> 
     * by any kind of error.
     */
    @GET
    @Path("/load")
    @Produces("application/json")
    public Response load() {
        final VocabularyLoader vocabularyLoader;
        try {
            vocabularyLoader = JBossJNDILookup.lookupLocal(VocabularyLoader.class);
            vocabularyLoader.load();
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Status.OK).build();
    }

    /**
     * GET based web service used to  obtain all the controlled vocabulary
     * items with a certain code system and code. 
     * This web service accepts only one argument, this argument can have 
     * two different forms :
     * <ul>
     * <li> 'code system code : code' - searches for the item with a certain 
     * code/code system. This kind of queries must return a singular result.
     * <li> code system code - searches for all the items with a given 
     * code systems
     * </ul> 
     * </br>
     * This web service returns an array of JSON objects where each element 
     * follows the following syntax :
     * <pre>
     * {"code":"XXX",
     *  "codeSystem":"XXX",
     *  "prefLabel":"XXX"}
     * </pre>
     * If there are no matching elements for the given query then this
     * web service returns an empty array.<br>
     * This web service can be access on :  
     * <JBOSS URI>/dataexchange_ws/controlled_item_repository/get
     * 
     * @return returns an array of JSON objects that match the given criteria.
     */
    @GET
    @Produces("application/json")
    @Path("/get")
    public Response getForCodeAndCodeSystem(@QueryParam("q") String q) {
        final int indexOf = q.indexOf(":");
        final String code;
        final String codeSystem;
        if (indexOf == -1) {
            codeSystem = q.trim();
            code = null;
        } else {
            codeSystem = q.substring(0, indexOf).trim();
            code = q.substring(indexOf + 1).trim();
        }

        final ControlledItemRepository itemRepository;
        try {
            itemRepository = JBossJNDILookup.lookupLocal(ControlledItemRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final Set<ControlledItem> items = new HashSet<ControlledItem>();
        if (code == null) {
            final Set<ControlledItem> byCodeSystem =
                    itemRepository.getByCodeSystem(codeSystem);
            items.addAll(byCodeSystem);
        } else {
            final ControlledItem byCodeSystemAndCode =
                    itemRepository.getByCodeSystemAndCode(codeSystem, code);
            if (byCodeSystemAndCode != null) {
                items.add(byCodeSystemAndCode);
            }
        }

        final JSONArray jsonArray = new JSONArray();
        for (ControlledItem item : items) {
            final JSONObject json = get(item);
            jsonArray.add(json);
        }

        return Response.ok(jsonArray.toString()).build();
    }

    /**
     * GET based web service used to  obtain all the controlled vocabulary
     * items tagged with a certain tag. </br>
     * This web service accepts only one argument and this is must follow this 
     * syntax : code system code : code. </br>
     * This web service returns an array of JSON objects where each element 
     * follows the following syntax :
     * <pre>
     * {"code":"XXX",
     *  "codeSystem":"XXX",
     *  "prefLabel":"XXX"}
     * </pre>
     * If there are no matching elements for the given query then this
     * web service returns an empty array.<br>
     * This web service can be access on : 
     * <JBOSS URI>/dataexchange_ws/controlled_item_repository/getForTag
     * 
     * @return returns an array of JSON objects that match the given criteria.
     */
    @GET
    @Produces("application/json")
    @Path("/getForTag")
    public Response getForTag(@QueryParam("q") String q) {

        final int indexOf = q.indexOf(":");
        if (indexOf == -1) {
            final String msg =
                    String.format("The query [%s] can not be processed.", q);
            LOGGER.error(msg);
            Response.ok(Status.BAD_REQUEST).build();
        }

        final String codeSystem = q.substring(0, indexOf).trim();
        final String code = q.substring(indexOf + 1).trim();

        final ControlledItemRepository itemRepository;
        try {
            itemRepository = JBossJNDILookup.lookupLocal(ControlledItemRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final ControlledItem tag =
                itemRepository.getByCodeSystemAndCode(codeSystem, code);
        if (tag == null) {
            final String msg =
                    String.format("No matching tags with code system [%s] and code [%s] found.", codeSystem, code);
            LOGGER.error(msg);

            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        final Set<ControlledItem> itemsByTag = itemRepository.getByTag(tag);
        final JSONArray jsonArray = new JSONArray();
        for (ControlledItem item : itemsByTag) {
            final JSONObject json = get(item);
            jsonArray.add(json);
        }

        return Response.ok(jsonArray.toString()).build();
    }

    private JSONObject get(ControlledItem item) {
        final JSONObject result = new JSONObject();
        final String code = item.getCode();
        result.put("code", code);
        final String codeSystem = item.getCodeSystem();
        result.put("codeSystem", codeSystem);
        final String prefLabel = item.getPrefLabel();
        result.put("prefLabel", prefLabel);


        return result;
    }
}
