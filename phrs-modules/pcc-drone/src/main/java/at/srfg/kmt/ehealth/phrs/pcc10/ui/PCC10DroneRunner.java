/*
 * Project :iCardea
 * File : PCC10DroneRunner.java
 * Encoding : UTF-8
 * Date : Apr 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10.ui;


/**
 * Starts and display the PCC10 (swing based) drone.</br>
 * This class can not be extend.
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class PCC10DroneRunner {
    
    
    /**
     * Don't let anybody to instantiate this class.
     */
    private PCC10DroneRunner() {
        // UNIMPLELEMTED.
    }
    
    /**
     * Initialize and shows the  PCC10 (swing based) drone.
     * 
     */
    public static void start() {
        final PCCDroneFrame droneFrame = new PCCDroneFrame("PCC 10 Drone", 1984);
        droneFrame.show();
    }
}
