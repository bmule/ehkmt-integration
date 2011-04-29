/*
 * Project :iCardea
 * File : PCC10DroneRunner.java
 * Encoding : UTF-8
 * Date : Apr 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10.ui;


/**
 * Starts and display the PCC09 (swing based) drone.</br>
 * This class can not be extend.
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class PCC09DroneRunner {
    
    
    /**
     * Don't let anybody to instantiate this class.
     */
    private PCC09DroneRunner() {
        // UNIMPLELEMTED.
    }
    
    /**
     * Initialize and shows the  PCC10 (swing based) drone.
     * 
     */
    public static void start() {
        final PCCDroneFrame droneFrame = new PCCDroneFrame("PCC 09 Drone", 1985);
        droneFrame.show();
    }
}
