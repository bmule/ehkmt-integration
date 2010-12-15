package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.Group;
import java.util.Set;


/**
 * Used to define the groups under a certain action can run.
 *
 * @author mihai
 */
public interface RunAsGroup {

    /**
     * Returns the groups under a certain action can run.
     *
     * @return the groups under a certain action can run.
     */
    Set<Group> getGroups();
}
