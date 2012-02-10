/*
 * Project :iCardea
 * File : DroneUI.java
 * Encoding : UTF-8
 * Date : Jan 29, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Drone UI able to send trigger PCC 10 messages, the messages are based to the
 * already persisted information.
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
     *
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
        final JFrame frame = new JFrame(code + "-Drone");
        final JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        final Configuration configuration = getConfiguration();
        final JPanel configurationPanel = getConfigurationPanel(configuration);
        result.add(configurationPanel);
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
                    // the care provision code can be also obtained from the reposiotry.
//                    final String code =
//                            (String) request.get("http://www.icardea.at/phrs/hl7V3#careProcisionCode");
                    final String wsAdress =
                            (String) request.get(Constants.HL7V3_REPLY_ADRESS);
                    final String id =
                            (String) request.get(Constants.PHRS_ACTOR_PROTOCOL_ID);

                    final Map<String, String> properties = new HashMap<String, String>();
                    properties.put("patientId", id);
                    // I am not sure if I need the names and the id, for the 
                    // moment I ignore the id. Its value will be ignored.
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

    private Configuration getConfiguration() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("generic_triplestore.xml");
        try {
            Configuration configuration =
                    new XMLConfiguration(resource);
            return configuration;
        } catch (ConfigurationException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    private JPanel getConfigurationPanel(Configuration configuration) {
        final JPanel result = new JPanel();
        result.setBorder(BorderFactory.createTitledBorder("Configuration"));
        result.setLayout(new GridLayout(0, 2));
        final String loadedFiles = configuration.getString("postconstruct.loadfiles");
        final String fileDump = configuration.getString("memorysail.filedump");
        final String repURI = configuration.getString("remote.uri");
        final String repID = configuration.getString("remote.repository-id");

        result.add(new JLabel("Loaded RDF files : "));
        result.add(new JLabel(loadedFiles));

        if (fileDump != null) {
            result.add(new JLabel("Repository type : "));
            result.add(new JLabel("Memory - File Based"));
            
            result.add(new JLabel("File Dump : "));
            result.add(new JLabel(fileDump));
        }

        if (repURI != null && repID != null) {
            result.add(new JLabel("Repository type : "));
            result.add(new JLabel("Remote HTTP"));
            
            result.add(new JLabel("Reposiotry URI : "));
            result.add(new JLabel(repURI));
            
            result.add(new JLabel("Reposiotry ID : "));
            result.add(new JLabel(repID));
        }

        return result;
    }
}
