/*
 * Project :iCardea
 * File : PCCTask.java
 * Encoding : UTF-8
 * Date : Jan 18, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.util.Map;



/**
 * Defines a <a
 * href="http://wiki.ihe.net/index.php?title=PCC-10">PCC10</a> task - such a 
 * build a message or send a message for a given set of properties.
 *
 * @author m1s 
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public interface PCCTask {

    /**
     * True if this properties map can be consumed.
     * 
     * @param properties the map of properties to be checked.
     * @return true if this PCCtask can be consumed.
     */
    boolean canConsume(Map properties);
    
    /**
     * Consumes a set of properties.
     * 
     * @param properties the properties to be consumed.
     * @throws ConsumeException by any consume related exception.
     */
    void consume(Map properties) throws ConsumeException;

    /**
     * If this methods returns false then the task chain must be stop after the
     * <code>consume</code> method call ends. This method make sense only if
     * there more task are used together in to a chain of tasks.
     * 
     * @return if false then then the task chain must be stop after the
     * <code>consume</code> method call ends.
     */
    boolean continueToNext();
}
