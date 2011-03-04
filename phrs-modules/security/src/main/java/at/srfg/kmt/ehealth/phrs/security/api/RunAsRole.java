package at.srfg.kmt.ehealth.phrs.security.api;


import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.Set;


/**
 * Used to define the roles under a certain action can run.
 *
 * @author mihai
 */
public interface RunAsRole extends RunAction {

    /**
     * Returns the roles under a certain action can run.
     *
     * @return the roles under a certain action can run.
     */
    Set<PhrRole> getRoles();
}
