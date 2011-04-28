/*
 * Project :iCardea
 * File : ProblemProcessor.java 
 * Encoding : UTF-8
 * Date : Apr 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.DEFAULT_PCC_10_END_POINT;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.Processor;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynaClassException;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicPropertyTypeException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.Response.Status;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class DailyLivingObsProcessor implements Processor<Response> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DailyLivingObsProcessor</code>.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyLivingObsProcessor.class);

    private static final DailyLivingFactroy OBS_FACTORY = new DailyLivingFactroy();

    private Set<Exception> exceptions;

    private Response result;

    /**
     * Builds a <code>ProblemProcessor</code> instance.
     */
    DailyLivingObsProcessor() {
        result = Response.status(Status.NOT_MODIFIED).build();
        exceptions = new HashSet<Exception>();
    }

    @Override
    public boolean canProcess(Object input) {
        if (input == null) {
            LOGGER.warn("Null input can not be processed.");
            return false;
        }

        if (!(input instanceof String)) {
            LOGGER.warn("Non String input can not be processed.");
            return false;
        }

        final String in = input.toString().trim();
        final int indexOf = in.indexOf("-");
        if (indexOf == -1) {
            LOGGER.warn("This input [{}] has the wrong syntax", in);
            return false;
        }

        final boolean isMedList = in.endsWith("RISKLIST");
        return isMedList;
    }

    @Override
    public Set<Exception> getExceptions() {
        return exceptions;
    }

    @Override
    public Response getResult() {
        return result;
    }

    @Override
    public boolean process(Object input) {
        final QUPCIN043200UV01 vitalSigns;
        try {
            vitalSigns = buildProblems();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            exceptions.add(exception);
            return false;
        }

        QUPCAR004030UVServiceUtil.sendPCC10(vitalSigns, DEFAULT_PCC_10_END_POINT);

        try {
            QUPCAR004030UVServiceUtil.toWriteInTemp(vitalSigns, "daily-observtions");
        } catch (Exception exception) {
            // I dont care !
        }

        result = Response.status(Status.OK).build();
        return exceptions.isEmpty();

    }

    private QUPCIN043200UV01 buildProblems() throws
            PCC10BuildException, DynamicPropertyTypeException, DynaClassException {
        final DynamicBeanRepository beanRepository;

        try {
            beanRepository = JBossJNDILookup.lookupLocal(DynamicBeanRepository.class);
        } catch (NamingException namingException) {
            LOGGER.error(namingException.getMessage(), namingException);
            return null;
        }

        // FIXME : use the constats here
        final String obsURI =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityOfDailyLiving";
        final Set<DynaBean> allObs = beanRepository.getAllForClass(obsURI);
        OBS_FACTORY.setObservations(allObs);

        final QUPCIN043200UV01 build = OBS_FACTORY.build();
        return build;
    }
}
