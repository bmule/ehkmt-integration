/*
 * Project :iCardea
 * File : PCC10DroneRunnerExample.java
 * Encoding : UTF-8
 * Date : Apr 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10.ui;


/**
 * Shows how to start the PCC10 (Swing based drone). </br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.pcc10.ui.PCC10DroneRunnerExample -Dexec.classpathScope=test</br>
 * Take care the doesn not compile the classes.
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class PCC10DroneRunnerExample {
    
    /**
     * Run this class from the command line.
     * 
     * @param args the command line arguments. No arguments are required.
     */
    public static void main(String ... args) {
        PCC10DroneRunner.start();
    }
}
