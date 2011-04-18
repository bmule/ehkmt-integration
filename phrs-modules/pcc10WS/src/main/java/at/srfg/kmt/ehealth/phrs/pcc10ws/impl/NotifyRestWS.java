/*
 * Project :iCardea
 * File : NotifyRestWS.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.impl.DynamicUtil;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This REST web service is call when a PCC10 transaction is required.
</br>
 * This class exposes :
 * <ul>
 * <li> <JBOSS URI>/pcc10ws/restws/pcc10/notify?q=XXX - used to 
 * notify that a cerytain PCC09 was processed.
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Path("/restws/pcc10")
public class NotifyRestWS {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.NotifyRestWS</code>.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyRestWS.class);
    
    private static final MedicationFactory medicationFactory = new MedicationFactory();

    /**
     * GET based REST full web service used to trigger a PCC10 transaction.<br>
     * This web service can be access on :  
     * <JBOSS URI>/pcc10ws/restws/pcc10/notify?q=XXX
     * 
     * @param q JSON object that contains information about the PCC09 request.
     * @return <code>javax.ws.rs.core.Response.Status.OK</code>, always.
     */
    @GET
    @Path("/notify")
    @Produces("application/json")
    public Response notify(@QueryParam("q") String q) {
        LOGGER.debug("Tries to process input : {}", q);

        final int indexOf = q.indexOf("-");
        if (indexOf == -1) {
            return Response.status(Status.OK).build();
        }

        final String userId = q.substring(0, indexOf);
        LOGGER.debug("User id : {}", userId);
        String provisionCode = q.substring(indexOf + 1, q.length());
        LOGGER.debug("Provision Code  : {}", provisionCode);
        //process(provisionCode);

        return Response.status(Status.OK).build();
    }

    private void process(String code) {
        LOGGER.debug("Tries to ");

        if ("COBSCAT".equals(code)) {
            solveMedication();
            return;
        }

        if ("MEDCCAT".equals(code)) {

            return;
        }

        if ("CONDLIST".equals(code)) {
            return;
        }

        if ("PROBLIST".equals(code)) {
            return;
        }

        if ("INTOLIST".equals(code)) {
            return;
        }

        if ("MEDLIST".equals(code)) {
            return;
        }
    }
    
        private QUPCIN043200UV01 solveMedication() {
        final DynamicBeanRepository beanRepository;
        
        try {
            beanRepository = JBossJNDILookup.lookupLocal(DynamicBeanRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return null;
        }

        // FIXME : use the constats here
        final String medURI =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.Medication";
        final Set<DynaBean> allMedications;
        try {
            allMedications = beanRepository.getAllForClass(medURI);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return null;
        }
        
        LOGGER.debug("Try to generate medications for :");
        for (DynaBean medication : allMedications) {
            LOGGER.debug(DynamicUtil.toString(medication));
        }
        
        medicationFactory.setMedication(allMedications);
        final QUPCIN043200UV01 build;
        try {
            build = medicationFactory.build();
        } catch (PCC10BuildException buildException) {
            LOGGER.error(buildException.getMessage(), buildException);
            return null;
        }
        
        return build;
    }
}
