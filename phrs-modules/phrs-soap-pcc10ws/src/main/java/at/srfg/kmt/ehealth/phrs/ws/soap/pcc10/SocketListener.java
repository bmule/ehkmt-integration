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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Listen a specified port where serialized
 * <code>java.util.Map</code> instances can be send, this map of properties are
 * consumed according whit the system needs. The process and the receive for the
 * map of properties is done in separate thread. <br/> More precisely this class
 * receives a serialized map of parameters via a socket connection on a given
 * port and after this if the map of parameters contains all the required
 * information then it pass to a message builder utility class able to build and
 * send a PCC10 message to a given end-point. The logic related for responsible
 * for PCC10 message (build and send) are contain in other class. </br> This
 * class is not designed to be extended.
 *
 * @author Miahi
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SocketListener {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SocketListener</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SocketListener.class);

    /**
     * Holds all the <code>PCCTask</code> used to process the incoming map of
     * properties.
     */
    private final Set<PCCTask> tasks;
    
    /**
     * Builds a
     * <code>SocketListener</code> instance.
     */
    SocketListener() {
        tasks = new HashSet<PCCTask>();
        tasks.add(new MedicationTask());
        tasks.add(new ProblemEntryTask());
        tasks.add(new VitalSignTask());
        
        LOGGER.debug("The actual set of tasks is : {} ", tasks);
    }

    /**
     * It listens an given socket and it push the information from the sockets
     * in to a given Blocking Queue.
     */
    private class Producer implements Runnable {

        /**
         * The Socket able to receive the map of parameter.
         */
        private final ServerSocket serverSocket;

        /**
         * The Blocking Queue where the received map of properties are pushed.
         */
        private final BlockingQueue queue;

        /**
         * Don't let anybody to instantiate this class.
         *
         * @param socket the socket to be listen, it can not be null.
         * @param queue the Blocking Queue where the received map of parameter
         * is pushed, it can not be null.
         * @throws NullPointerException if the
         * <code>socket</code> and
         * <code>queue</code> arguments are null.
         * @throws IllegalArgumentException if the
         * <code>socket</code> argument represents a closed socket.
         */
        private Producer(ServerSocket socket, BlockingQueue queue) {

            if (socket == null) {
                final NullPointerException exception =
                        new NullPointerException("The socket argument can not be null.");
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            }

            if (socket.isClosed()) {
                final IllegalArgumentException exception =
                        new IllegalArgumentException("The specified socket is close.");
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            }

            if (queue == null) {
                final NullPointerException exception =
                        new NullPointerException("The queue argument can not be null.");
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            }

            this.serverSocket = socket;
            this.queue = queue;
        }

        /**
         * Listen the socket and push all the received map of parameters in to
         * the Blocking Queue.
         */
        @Override
        public void run() {
            while (!serverSocket.isClosed()) {
                try {
                    final Socket accepted = serverSocket.accept();
                    final ObjectInputStream inputStream =
                            new ObjectInputStream(accepted.getInputStream());
                    final HashMap readObject = (HashMap) inputStream.readObject();
                    queue.put(readObject);
                    inputStream.close();
                } catch (Exception exception) {
                    LOGGER.error(exception.getMessage(), exception);
                }
            }
        }
    }

    /**
     * It listens an Blocking Queue, pulls all the new inserted elements and use
     * the
     * <code>PCC10Task</code> to build and send a PCC10 message.
     */
    private class Consumer implements Runnable {

        /**
         * The Blocking Queue where the received map of properties are pushed.
         */
        private final BlockingQueue queue;

        /**
         * Don't let anybody to instantiate this class.
         *
         * @param queue the Blocking Queue where the received map of parameter
         * is pushed, it can not be null.
         * @throws NullPointerException if the
         * <code>queue</code> arguments are null.
         */
        private Consumer(BlockingQueue queue) {

            if (queue == null) {
                final NullPointerException exception =
                        new NullPointerException("The queue argument can not be null.");
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            }

            this.queue = queue;
        }

        public void run() {
            try {
                while (true) {
                    consume(queue.take());
                }
            } catch (InterruptedException exception) {
                LOGGER.error(exception.getMessage(), exception);
            }
        }

        private void consume(Object toConsume) {
            LOGGER.debug("Tries to consume {}" + toConsume);
            final boolean isMap = toConsume instanceof Map;

            if (!isMap) {
                LOGGER.warn("The received toConsume argument is not a java.util.Map, it can not be consummed.");
                return;
            }

//            final PCC10Task task = new PCC10Task((Map) toConsume);
//            task.run();
            
            for (PCCTask task : tasks) {
                if (task.canConsume((Map) toConsume)) {
                    try {
                        task.consume((Map) toConsume);
                    } catch (ConsumeException exception) {
                        LOGGER.warn("Failure by running task");
                        LOGGER.warn(exception.getMessage(), exception);
                    }
                }
            }
        }
    }

    /**
     * Starts this socket listener on a given port.
     *
     * @param port the port to be listen, it can not be smaller then 1024.
     * @throws IOException by any IO related error (most of the time related
     * with socket).
     * @throws IllegalArgumentException if the <code>port</code> argument is
     * smaller 1024.
     */
    public void start(int port) throws IOException {

        if (port <= 1024) {
            final IllegalArgumentException exception =
                    new IllegalArgumentException("The port argumetn can not be msaller than 1024");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final BlockingQueue q = new ArrayBlockingQueue(5);
        final Producer p = new Producer(new ServerSocket(port, 10), q);
        final Consumer c = new Consumer(q);
        final Thread producer = new Thread(p);
        producer.start();
        final Thread consumer = new Thread(c);
        consumer.start();
        LOGGER.info("SocketListener started on localhost:" + port);
    }
}
