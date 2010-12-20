/*
 * Project :iCardea
 * File : RunAsInterceptor.java
 * Date : Dec 15, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;


import static at.srfg.kmt.ehealth.phrs.security.impl.RunAsConstants.GROUPS;
import static at.srfg.kmt.ehealth.phrs.security.impl.RunAsConstants.ROLES;
import at.srfg.kmt.ehealth.phrs.security.api.RunAs;
import at.srfg.kmt.ehealth.phrs.security.api.RunAsGroup;
import at.srfg.kmt.ehealth.phrs.security.api.RunAsRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Mihai
 */
public class RunAsInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        final Object target = context.getTarget();
        final RunAs annotation =
                    target.getClass().getAnnotation(RunAs.class);
        boolean isRunAs = annotation != null;
        if (!isRunAs) {
            return context.proceed();
        }

        // FIXME : instance of is expensive, prove using the class
        final boolean isRunAsGroup = target instanceof RunAsGroup;
        final Set<PhrGroup> groups = isRunAsGroup
                ? ((RunAsGroup) target).getGroups()
                : new HashSet<PhrGroup>();

        // FIXME : instance of is expensive, prove using the class
        final boolean isAsRole = target instanceof RunAsRole;
        final Set<PhrRole> roles = isAsRole
                ? ((RunAsRole) target).getRoles()
                : new HashSet<PhrRole>();

        final Map<String, Object> contextData = context.getContextData();
        if (!groups.isEmpty()) {
            contextData.put(GROUPS.toString(), groups);
        }
        if (!roles.isEmpty()) {
            contextData.put(ROLES.toString(), roles);
        }

        return context.proceed();
    }
}
