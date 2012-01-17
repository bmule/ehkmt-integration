/*
 * Project :iCardea
 * File : RunSecondDispatcher.java
 * Encoding : UTF-8
 * Date : Jan 12, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.io.IOException;


/**
 * mvn exec:java
 * -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener
 * -Dexec.classpathScope=test
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class StartSocketListener {

    /**
     * Don't let anybody to instantiate this class.
     */
    private StartSocketListener() {
        // UNIMPLEMEENTD
    }

    public static void main(String... args) throws IOException {
        new SocketListener(args[0], args[1]).start();
    }
}
