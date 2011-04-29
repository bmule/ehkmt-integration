/*
 * Project :iCardea
 * File : DroneClient.java
 * Encoding : UTF-8
 * Date : Apr 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.ANY;
import org.hl7.v3.CD;
import org.hl7.v3.COCTMT050000UVPatient;
import org.hl7.v3.COCTMT050000UVPerson;
import org.hl7.v3.II;
import org.hl7.v3.IVLTS;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.PQ;
import org.hl7.v3.QUPCIN043200UV01;
import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01ControlActProcess;
import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01Subject1;
import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01Subject5;
import org.hl7.v3.REPCMT004000UV01PertinentInformation5;
import org.hl7.v3.REPCMT004000UV01RecordTarget;


/**
 * Use to send messages to the PCC 10 Drone.
 * 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
final class PCC10DroneAgent {

    private final static int PORT = 1984;

    private final static String HOST = "localhost";

    private final static SimpleDateFormat MESSAGE_DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss");

    private final static SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    static void send(QUPCIN043200UV01 qupcin043200uv01) {
        //send("MESSAGE : " + qupcin043200uv01, PORT, HOST);
        process(qupcin043200uv01);
    }

    private static void send(String msg, int port, String host) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            final OutputStream outputStream = socket.getOutputStream();
            final PrintWriter printWriter =
                    new PrintWriter(outputStream, true);
            printWriter.println(msg);
            printWriter.close();

        } catch (IOException e) {
            final String ioMsg = String.format("The logging drone is not "
                    + "active, the next exception (%s) it is not relevant.",
                    e.getClass().getSimpleName());
            System.out.println(ioMsg);
//            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException exception) {
                    final String ioMsg = String.format("The logging drone is not "
                            + "active, the next exception (%s) it is not relevant.",
                            exception.getClass().getSimpleName());
                    System.out.println(ioMsg);
//                    exception.printStackTrace();
                }
            }
        }
    }

    private static void process(QUPCIN043200UV01 query) {
        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();
        final List<QUPCIN043200UV01MFMIMT700712UV01Subject1> subjects =
                controlActProcess.getSubject();

        // FIXME : conrol if you have more or none elements
        final QUPCIN043200UV01MFMIMT700712UV01Subject1 subject = subjects.get(0);
        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                subject.getRegistrationEvent().getSubject2();
        final JAXBElement<REPCMT004000UV01RecordTarget> recordTarget_je =
                subject2.getCareProvisionEvent().getRecordTarget();
        final REPCMT004000UV01RecordTarget recordTarget = recordTarget_je.getValue();
        final JAXBElement<COCTMT050000UVPatient> patient_je = recordTarget.getPatient();
        final COCTMT050000UVPatient patient = patient_je.getValue();

        final String patientMsg = toString(patient);
        send(patientMsg, PORT, HOST);
        send(" ", PORT, HOST);

        final List<REPCMT004000UV01PertinentInformation5> pertinentInformation3 =
                subject2.getCareProvisionEvent().getPertinentInformation3();
        process(pertinentInformation3);
    }

    private static String toString(COCTMT050000UVPatient patient) {
        final StringBuilder result = new StringBuilder();
        final JAXBElement<COCTMT050000UVPerson> patientPerson_je =
                patient.getPatientPerson();
        final COCTMT050000UVPerson patientPerson = patientPerson_je.getValue();
//        final PN name = patientPerson.getName().get(0);
//        String patientName = ((EnGiven) ((JAXBElement) name.getContent().get(0)).getValue()).getRepresentation().toString();
//        String patientSurname = ((EnFamily) ((JAXBElement) name.getContent().get(1)).getValue()).getRepresentation().value();

        result.append("Patient : Andreas Schmidt");

        return result.toString();
    }

    private static void process(List<REPCMT004000UV01PertinentInformation5> informations) {
        for (REPCMT004000UV01PertinentInformation5 information5 : informations) {
            process(information5);
        }
    }

    private static void process(REPCMT004000UV01PertinentInformation5 information) {
        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration =
                information.getSubstanceAdministration();


        final boolean isMedication = substanceAdministration != null;
        if (isMedication) {
            processMedication(information);
        } else {
            processObservation(information);
            send(" ", PORT, HOST);
            send(" ", PORT, HOST);
        }
    }

    private static void processObservation(REPCMT004000UV01PertinentInformation5 information) {
        send("Observation with the following paramters : ", PORT, HOST);
        final JAXBElement<POCDMT000040Observation> observation_je =
                information.getObservation();
        final POCDMT000040Observation observation = observation_je.getValue();

        if (isVitalSign(observation)) {
            send("This Observation is a (HL7 V3) Vital Sign.", PORT, HOST);
        }

        if (isProblem(observation)) {
            send("This Observation is a (HL7 V3) Problem.", PORT, HOST);
        }



        final CD code = observation.getCode();
        send("Code : " + toString(code), PORT, HOST);

        final IVLTS effectiveTime = observation.getEffectiveTime();
        send("Date : " + toString(effectiveTime), PORT, HOST);

        final List<ANY> values = observation.getValue();
        for (ANY any : values) {
            if (any instanceof CD) {
                send("Value : " + toString((CD) any), PORT, HOST);
            } else if (any instanceof PQ) {
                send("Quantity : " + toString((PQ) any), PORT, HOST);
            }
        }
    }

    private static String toString(PQ pq) {
        final StringBuilder result = new StringBuilder();
        result.append(pq.getValue());
        result.append(" ");
        result.append(pq.getUnit());

        return result.toString();
    }

    private static String toString(CD cd) {
        final StringBuilder result = new StringBuilder();
        result.append(cd.getDisplayName());
        result.append("(");
        result.append(cd.getCodeSystem());
        result.append(":");
        result.append(cd.getCode());
        result.append(")");
        return result.toString();
    }

    private static String toString(IVLTS ivlts) {
        final StringBuilder result = new StringBuilder();
        final String value = ivlts.getValue();

        Date fromMessage;
        try {
            fromMessage = MESSAGE_DATE_FORMAT.parse(value);
        } catch (ParseException exception) {
            fromMessage = new Date();
        }

        final String format = DATE_FORMAT.format(fromMessage);

        result.append(format);
        return result.toString();
    }

    private static void processMedication(REPCMT004000UV01PertinentInformation5 information) {
    }

    private static boolean isVitalSign(POCDMT000040Observation information) {
        final List<II> templateIDs = information.getTemplateId();
        final Set<String> mustBe = new HashSet<String>();
        mustBe.add("1.3.6.1.4.1.19376.1.5.3.1.4.13");
        mustBe.add("2.16.840.1.113883.10.20.1.31");
        mustBe.add("1.3.6.1.4.1.19376.1.5.3.1.4.13.2");

        final Set<String> toTest = new HashSet<String>();
        for (II ii : templateIDs) {

            final String extension = ii.getExtension();
            toTest.add(extension);
        }

        return toTest.containsAll(mustBe);
    }

    private static boolean isProblem(POCDMT000040Observation information) {
        final List<II> templateIDs = information.getTemplateId();
        final Set<String> mustBe = new HashSet<String>();
        mustBe.add("2.16.840.1.113883.10.20.1.28");
        mustBe.add("1.3.6.1.4.1.19376.1.5.3.1.4.5");

        final Set<String> toTest = new HashSet<String>();
        for (II ii : templateIDs) {
            final String extension = ii.getExtension();
            toTest.add(extension);
        }

        return toTest.containsAll(mustBe);
    }
}
