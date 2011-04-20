/*
 * Project :iCardea
 * File : FileBasedProcessor.java 
 * Encoding : UTF-8
 * Date : Apr 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;

import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.DEFAULT_PCC_10_END_POINT;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.Processor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class FileBasedProcessor implements Processor<Response> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.NotifyRestWS</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FileBasedProcessor.class);
    private final Map<String, Set<String>> fileNameMapping;
    private Response result;
    private Set<Exception> exceptions;

    /**
     * Builds a <code>FileBasedProcessor</code> instance.
     */
    public FileBasedProcessor() {
        result = Response.status(Status.NOT_MODIFIED).build();
        exceptions = new HashSet<Exception>();
        fileNameMapping = new HashMap<String, Set<String>>();
        initFileMaping();
        LOGGER.debug("The message - file mapping is : ", fileNameMapping);
    }

    private void initFileMaping() {
        final Set<String> cobscatFiles = new HashSet<String>();
        cobscatFiles.add("PCC-10-Input-Scenario-VitalSign.xml");
        fileNameMapping.put("COBSCAT", cobscatFiles);

        final Set<String> medlistFiles = new HashSet<String>();
        medlistFiles.add("PCC-10-Input-Scenario-Medications.xml");
        fileNameMapping.put("MEDLIST", medlistFiles);

        final Set<String> medccatFiles = new HashSet<String>();
        medccatFiles.add("PCC-10-Input-Scenario-Problems.xml");
        medccatFiles.add("PCC-10-Input-Scenario-Problems2.xml");
        medccatFiles.add("PCC-10-Input-Scenario-DailyLiving.xml");
        fileNameMapping.put("MEDCCAT", medccatFiles);
    }

    @Override
    public boolean canProcess(Object input) {
        if (input == null) {
            return false;
        }

        if (!(input instanceof String)) {
            return false;
        }

        final String inString = input.toString();
        final int indexOf = inString.indexOf("-");

        return indexOf > 0;
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
        // just to be sure
        final boolean canProcess = canProcess(input);
        if (!canProcess) {
            throw new IllegalArgumentException("Can not process input :" + input);
        }

        final String in = input.toString();
        final int indexOf = in.indexOf("-");

        final String userId = in.substring(0, indexOf);
        LOGGER.debug("User id : {}", userId);
        String provisionCode = in.substring(indexOf + 1, in.length());
        LOGGER.debug("Provision Code  : {}", provisionCode);

        final Set<String> files = fileNameMapping.get(provisionCode);
        LOGGER.debug("Process files : {}", files);
        if (files == null) {
            result = Response.status(Status.NOT_MODIFIED).build();
            return true;
        }
        
        for (String fileName : files) {

            LOGGER.debug("Tries to unmarshal the file : " + fileName);
            final QUPCIN043200UV01 fromFile;
            try {
                fromFile =
                        QUPCAR004030UVServiceUtil.buildQUPCIN043200UV01(fileName);
            } catch (JAXBException jaxbe) {
                LOGGER.error(jaxbe.getMessage(), jaxbe);
                result = Response.status(Status.INTERNAL_SERVER_ERROR).build();
                return false;
            }
            LOGGER.debug("File {} was succesfully unmarshaled", fileName);

            QUPCAR004030UVServiceUtil.sendPCC10(fromFile, DEFAULT_PCC_10_END_POINT);

            try {
                QUPCAR004030UVServiceUtil.toWriteInTemp(fromFile, fileName);
            } catch (JAXBException jaxbe) {
                LOGGER.error(jaxbe.getMessage(), jaxbe);
            }
        }


        result = Response.status(Status.OK).build();
        return true;
    }
}
