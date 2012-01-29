/*
 * Project :iCardea
 * File : DroneUI.java
 * Encoding : UTF-8
 * Date : Jan 29, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Drone UI able to send trigger PCC 10 messages, the messages are based to
 * the already persisted information. 
 * 
 * @author Miahi
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class DroneUI {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.DroneUI</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DroneUI.class);

    private final String code;

    private final PHRSRequestClient requestClient;

    private final DynaBeanClient beanClient;

    public static void main(String... args) {
        new DroneUI(args[0]).show();
    }

    /**
     * Don't let anybody to instantiate this class.
     * @param code 
     */
    private DroneUI(String code) {
        this.code = code;
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();
        requestClient = new PHRSRequestClient(triplestore);
        beanClient = new DynaBeanClient(triplestore);
    }

    private void show() {
        final JFrame frame = new JFrame();
        final JPanel result = new JPanel();
        final JButton button = new JButton(new DroneUI.SendMessageAction(code));
        result.add(button);
        frame.getContentPane().add(result);
        frame.pack();
        frame.setVisible(true);
    }

    private final class SendMessageAction extends AbstractAction {

        public SendMessageAction(String string) {
            super(string);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                final Iterable<String> resources = requestClient.getAllPHRSRequests();
                for (String resource : resources) {
                    final DynaBean request = beanClient.getDynaBean(resource);
                    final String code =
                            (String) request.get("http://www.icardea.at/phrs/hl7V3#careProcisionCode");
                    final String wsAdress =
                            (String) request.get("http://www.icardea.at/phrs/hl7V3#wsReplyAddress");
                    final String id =
                            (String) request.get("http://www.icardea.at/phrs/actor#protocolId");

                    final Map<String, String> properties = new HashMap<String, String>();
                    properties.put("patientId", id);
                    properties.put("patientNames", "patientNames");
                    properties.put("careProvisionCode", code);
                    properties.put("responseEndpointURI", wsAdress);

                    notify("localhost", 5578, properties);

                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        private void notify(String host, int port, Map<String, String> params) {
            LOGGER.debug("Tries to dispach this properties {}.", params);
            try {
                final Socket socket = new Socket(host, port);
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(params);
            } catch (Exception e) {
                LOGGER.error("Prameters : {} can not be dispathed.", params);
                LOGGER.error(e.getMessage(), e);
            }

            LOGGER.debug("The {} was distpatched", params);
        }
    }
}
