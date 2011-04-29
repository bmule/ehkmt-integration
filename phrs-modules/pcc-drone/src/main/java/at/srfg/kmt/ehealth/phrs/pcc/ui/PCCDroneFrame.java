/*
 * Project :iCardea
 * File : PCCDroneFrame.java
 * Encoding : UTF-8
 * Date : Apr 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc.ui;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
final class PCCDroneFrame {

    /**
     * The port to listen on.
     */
    private final int port;

    private final JFrame frame;

    private final JList messagesList;

    PCCDroneFrame(String title, int port) {
        this.port = port;
        frame = new JFrame();
        frame.setTitle(title);
        final DefaultListModel defaultListModel = new DefaultListModel();
        messagesList = new JList();
        messagesList.setModel(defaultListModel);
    }

    void show() {
        final JPanel mainPanel = buildMainPanel();
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setVisible(true);
        
        logMessage("Drone waits for messages.");

        final UpdateMessagesTarget target;
        try {
            target = new UpdateMessagesTarget();
            Thread thread = new Thread(target);
            thread.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private JPanel buildMainPanel() {
        final JPanel resut = new JPanel(new BorderLayout());
        resut.add(new JScrollPane(messagesList), BorderLayout.CENTER);

        return resut;
    }

    private void logMessage(String msg) {
        final DefaultListModel model = (DefaultListModel) messagesList.getModel();
        model.addElement(msg);
    }

    private final class ReloadAction extends AbstractAction {

        public ReloadAction(String string) {
            super(string);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private final class UpdateMessagesTarget implements Runnable {

        private ServerSocket serverSocket;

        private UpdateMessagesTarget() throws IOException {
            serverSocket = new ServerSocket(port);
        }

        @Override
        public void run() {

            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    final InputStream inputStream =
                            client.getInputStream();
                    final InputStreamReader reader =
                            new InputStreamReader(inputStream);

                    final BufferedReader in = new BufferedReader(reader);

                    final String msg = in.readLine();
                    if (msg != null || !msg.isEmpty()) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                final ListModel model = messagesList.getModel();
                                ((DefaultListModel) model).addElement(msg);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
