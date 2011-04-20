/*
 * Project :iCardea
 * File : NotifyRestWS.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;

import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.DEFAULT_PCC_10_END_POINT;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.Processor;
import at.srfg.kmt.ehealth.phrs.util.Util;
import java.util.ArrayList;
import java.util.List;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.impl.DynamicUtil;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.BindingProvider;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCAR004030UVService;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.namespace.QName;

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
    private static final MedicationFactory MEDICATION_FACTORY = new MedicationFactory();
    private final List<Processor> processors;

    /**
     * Builds a <code>NotifyRestWS</code> instance.
     */
    public NotifyRestWS() {
        processors = new ArrayList<Processor>();
        processors.add(new MedicationPorcessor());
    }

    /**
     * GET based REST full web service used to trigger a PCC10 transaction.<br>
     * This web service can be access on :  
     * <JBOSS URI>/pcc10ws/restws/pcc10/notify?q=XXX
     * 
     * @param sender used to identify the sender.
     * @param q the query. Its syntax is user id - care provision code.
     * 
     * @return <code>javax.ws.rs.core.Response.Status.OK</code>, always.
     */
    @GET
    @Path("/notify")
    @Produces("application/json")
    public Response notify(@QueryParam("sender") String sender,
            @QueryParam("q") String q) {
        final Object[] forLog = Util.forLog(sender, q);
        LOGGER.debug("Tries to process input : {} from sender : {}", forLog);

        final int indexOf = q.indexOf("-");
        if (indexOf == -1) {
            LOGGER.error("Bad syntax for the request");
            return Response.status(Status.BAD_REQUEST).build();
        }

        final String userId = q.substring(0, indexOf);
        LOGGER.debug("User id : {}", userId);
        String provisionCode = q.substring(indexOf + 1, q.length());
        LOGGER.debug("Provision Code  : {}", provisionCode);

        // this is just a hot fox for the review, it only sends a default file
        // according wiht an paramter.
        if ("pcc09".equals(sender)) {
            final FileBasedProcessor processor = new FileBasedProcessor();
            processor.process(q);
            final Set<Exception> exceptions = processor.getExceptions();
            logExceptions(exceptions);
            final Response result = processor.getResult();
            return result;
        }

        Response result = Response.status(Status.NOT_FOUND).build();
        for (Processor processor : processors) {
            final boolean canProcess = processor.canProcess(q);
            if (canProcess) {
                processor.process(q);
                result = (Response) processor.getResult();
                final Set<Exception> exceptions = processor.getExceptions();
                logExceptions(exceptions);
                break;
            }
        }

        // process(provisionCode);

        return result;
    }

    private void logExceptions(Set<Exception> exceptions) {
        for (Exception exception : exceptions) {
            LOGGER.error(exception.getMessage(), exception);
        }
    }

    private void process(String code) {
        LOGGER.debug("Tries to use {}", code);
        if ("COBSCAT".equals(code)) {
            final QUPCIN043200UV01 medication = solveMedication();

            try {
                toWriteInTemp(medication, "medication");
            } catch (Exception exception) {
                LOGGER.debug("Can not create a log for medication");
            }

            sendPCC10(medication, DEFAULT_PCC_10_END_POINT);

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

        MEDICATION_FACTORY.setMedication(allMedications);
        final QUPCIN043200UV01 build;
        try {
            build = MEDICATION_FACTORY.build();
        } catch (PCC10BuildException buildException) {
            LOGGER.error(buildException.getMessage(), buildException);
            return null;
        }

        return build;
    }

    private void toWriteInTemp(Object toMarshal, String name) throws JAXBException {
        final String tempDir = System.getProperty("java.io.tmpdir");
        final JAXBContext context =
                JAXBContext.newInstance(org.hl7.v3.MCCIIN000002UV01.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File destiantion =
                new File(tempDir + File.separatorChar + name + ".xml");
        marshaller.marshal(toMarshal, destiantion);
        LOGGER.debug("The " + name + " was persisted on : " + destiantion.getAbsolutePath());
    }

    private void sendPCC10(QUPCIN043200UV01 request, String endpoint) {

        LOGGER.debug("Tries to send the request {} on the end point {}",
                new Object[]{request, endpoint});

        //final QUPCAR004030UVService service = new QUPCAR004030UVService();
        final QUPCAR004030UVService service;
        try {
            service = getQUPCAR004040UVService();
        } catch (MalformedURLException exception) {
            LOGGER.error("The Proxy for the end {} point can not be build.", endpoint);
            LOGGER.error(exception.getMessage(), exception);
            return;
        }

        // here I obtain the service (proxy).
        final QUPCAR004030UVPortType portType = service.getQUPCAR004030UVPort();
        final DefaultPCC10RequestFactory requestFactory =
                new DefaultPCC10RequestFactory();

        // I set the end point for the 
        setEndPointURI(portType, endpoint);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(request);
        LOGGER.debug("Acknoledge from endpoint {} is {} ", new Object[]{endpoint, ack});
    }

    /**
     * JBoss specific way to customize the WSDL client (end point address).
     * 
     * @param portType the client to be customized.
     */
    private void setEndPointURI(QUPCAR004030UVPortType portType, String endpoint) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
    }

    static QUPCAR004030UVService getQUPCAR004040UVService() throws MalformedURLException {

        final QName qName = new QName("urn:hl7-org:v3", "QUPC_AR004040UV_Service");
        final URL url = NotifyRestWS.class.getClassLoader().getResource("wsdl/QUPC_AR004040UV_Service.wsdl");
//        final URL url = new URL("file:/Volumes/Data/lab0/iiiiiCardea/phrs/ehkmt-integration/phrs-modules/pcc09WS/src/main/assembly/QUPC_AR004040UV_Service.wsdl");
        final QUPCAR004030UVService result = new QUPCAR004030UVService(url, qName);
        return result;
    }

    private QUPCIN043200UV01 getFromFile(String fileName) {
        final QUPCIN043200UV01 qupcIN043200UV01;
        try {
            qupcIN043200UV01 =
                    QueryFactory.buildQUPCIN043200UV01(fileName);

        } catch (JAXBException exception) {
            LOGGER.error(exception.getMessage(), exception);
            return null;
        }

        return qupcIN043200UV01;
    }
}
