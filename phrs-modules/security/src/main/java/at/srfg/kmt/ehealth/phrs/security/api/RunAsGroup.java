package at.srfg.kmt.ehealth.phrs.security.api;

import java.util.Set;
import org.osgi.service.useradmin.Group;

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
