/*
 * Project :iCardea
 * File : PCC09DroneAgent.java
 * Encoding : UTF-8
 * Date : Apr 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import org.hl7.v3.CD;
import org.hl7.v3.II;
import org.hl7.v3.MCCIMT000100UV01Sender;
import org.hl7.v3.QUPCIN043100UV01;
import org.hl7.v3.QUPCIN043100UV01QUQIMT020001UV01ControlActProcess;
import org.hl7.v3.QUPCMT040300UV01CareProvisionCode;
import org.hl7.v3.QUPCMT040300UV01ParameterList;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
final class PCC09DroneAgent {

    private final static int PORT = 1985;

    private final static String HOST = "localhost";

    static void send(QUPCIN043100UV01 query) {

        final MCCIMT000100UV01Sender sender = query.getSender();
        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final List<QUPCMT040300UV01ParameterList> parameterList =
                controlActProcess.getQueryByParameter().getValue().getParameterList();
       
        for (QUPCMT040300UV01ParameterList param : parameterList) {
            process(param);
        }
    }

    private static void process(QUPCMT040300UV01ParameterList parameter) {
        final II patientIdIntanceId = parameter.getPatientId().getValue();
        
        send("Patient id : " + toString(patientIdIntanceId), PORT, HOST);

        final QUPCMT040300UV01CareProvisionCode provisionCode =
                parameter.getCareProvisionCode().getValue();

        // obtains the Care Provision Code (it can be 0..1)
        final CD provCodeConceptDescriptor = provisionCode.getValue();
        final String provCodeMsg = String.format("Care Provision Code : %s",
                toString(provCodeConceptDescriptor));
        send(provCodeMsg, PORT, HOST);
    }

    private static String toString(CD cd) {
        final StringBuffer result = new StringBuffer();

        if (cd.getCode() != null) {
            result.append("Code : ");
            result.append(cd.getCode());
            result.append(", ");
        }

        if (cd.getCodeSystem() != null) {
            result.append("CodeSystem : ");
            result.append(cd.getCodeSystem());
            result.append(", ");
        }


        if (cd.getCodeSystemName() != null) {
            result.append("CodeSystemName : ");
            result.append(cd.getCodeSystemName());
            result.append(", ");
        }

        if (cd.getCodeSystemVersion() != null) {
            result.append("CodeSystemVersion : ");
            result.append(cd.getCodeSystemVersion());
            result.append(", ");
        }

        int length = result.length();
        if (length > 2) {
            result.delete(length - 2, length - 1);
        }

        return result.toString();
    }

    private static String toString(II ii) {
        final StringBuffer result = new StringBuffer();
        final String extension = ii.getExtension();

        if (extension != null) {
            result.append("Extension (UUID) : ");
            result.append(extension);
        }

        return result.toString();
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
}
