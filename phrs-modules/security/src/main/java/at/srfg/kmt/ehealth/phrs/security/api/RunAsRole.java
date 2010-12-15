package at.srfg.kmt.ehealth.phrs.security.api;

import java.util.Set;
import org.osgi.service.useradmin.Role;

/**
 * Used to define the roles under a certain action can run.
 *
 * @author mihai
 */
public interface RunAsRole {

    /**
     * Returns the roles under a certain action can run.
     *
     * @return the roles under a certain action can run.
     */
    Set<Role> getRoles();
}
