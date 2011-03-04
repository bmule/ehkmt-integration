package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import java.util.Set;


/**
 * Used to define the groups under a certain action can run.
 *
 * @author mihai
 */
public interface RunAsGroup extends RunAction {

    /**
     * Returns the groups under a certain action can run.
     *
     * @return the groups under a certain action can run.
     */
    Set<PhrGroup> getGroups();
}
