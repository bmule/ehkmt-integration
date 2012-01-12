/*
 * Project :iCardea
 * File : SecondDisptacher.java
 * Encoding : UTF-8
 * Date : Jan 12, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class SocketListener {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SocketListener</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SocketListener.class);

    private class Producer implements Runnable {

        private ServerSocket serverSocket;

        private final BlockingQueue queue;

        private Producer(ServerSocket socket, BlockingQueue queue) {
            this.serverSocket = socket;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (!serverSocket.isClosed()) {
                try {
                    final Socket accepted = serverSocket.accept();
                    final ObjectInputStream inputStream =
                            new ObjectInputStream(accepted.getInputStream());
                    final HashMap readObject = (HashMap) inputStream.readObject();
                    queue.put(readObject);
                } catch (Exception exception) {
                    LOGGER.error(exception.getMessage(), exception);
                }
            }
        }
    }

    private class Consumer implements Runnable {

        private final BlockingQueue queue;

        private Consumer(BlockingQueue q) {
            queue = q;
        }

        public void run() {
            try {
                while (true) {
                    consume(queue.take());
                }
            } catch (InterruptedException ex) {
            }
        }

        private void consume(Object toConsume) {
            LOGGER.debug("Tries to consume {}" + toConsume);
            new PCC10Task((Map) toConsume).run();
            
        }
        

    }

    public void start() throws IOException {
        BlockingQueue q = new ArrayBlockingQueue(5);
        Producer p = new Producer(new ServerSocket(5578, 10), q);
        Consumer c = new Consumer(q);
        new Thread(p).start();
        new Thread(c).start();

    }
}
