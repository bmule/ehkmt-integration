/*
 * Project :iCardea
 * File : Util.java
 * Date : Jan 11, 2011
 * User : Mihai
 */


package at.srfg.kmt.ehealth.phrs.util;


/**
 * Contains a set of common purposed methods.
 * 
 * @author Mihai
 */
public class Util {


    /**
     * Don't let anyone to instantiate this class.
     */
    private Util() {
        // UNIMPLEMENTYED
    }
    
    /**
     * Used to transform an argument list in to a <code>java.lang.Array</code>.
     * This method can he useful when you try to build an argument array for
     * the logging framework, the next code snippet exemplify this : 
     * <pre>
     *     final Object[] toLog = Util.forLog(users, group);
     *     logger.debug("Tries to remove users [{}] from the group [{}].", toLog);
     * </pre>
     * 
     * @param arg the arguments, none or or many.
     * @return an array that contains all the specified arguments, the arguments
     * order remain the same.
     */
    public static Object [] forLog(Object ...arg) {
        if (arg == null) {
            return null;
        }
        
        return arg;
    }
}
