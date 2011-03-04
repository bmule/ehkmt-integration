/*
 * Project :iCardea
 * File : RunAction.java
 * Date : Dev 17, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;

/**
 * Used to define the action to run.
 * 
 * @author mradules
 */
public interface RunAction {
    
    /**
     * The action to run.
     * @return 
     */
    PhrAction getAction();
}
