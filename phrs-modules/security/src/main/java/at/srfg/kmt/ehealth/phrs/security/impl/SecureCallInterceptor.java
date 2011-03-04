/*
 * Project :iCardea
 * File : SecureCallInterceptor.java
 * Date : Jan 17, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import static at.srfg.kmt.ehealth.phrs.security.impl.RunAsConstants.GROUPS;
import static at.srfg.kmt.ehealth.phrs.security.impl.RunAsConstants.ROLES;
import static at.srfg.kmt.ehealth.phrs.security.impl.RunAsConstants.ACTION;
import at.srfg.kmt.ehealth.phrs.util.Util;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.lang.reflect.Method;
import java.util.Set;

import java.util.Map;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
public class SecureCallInterceptor {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.SecureCallInterceptor</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(SecureCallInterceptor.class);

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        final Map<String, Object> contextData = context.getContextData();
        final Set<PhrGroup> groups =
                (Set<PhrGroup>) contextData.get(GROUPS.toString());
        final Set<PhrRole> roles =
                (Set<PhrRole>) contextData.get(ROLES.toString());
  
        final PhrAction action = (PhrAction) contextData.get(ACTION.toString());
        
        final Method method = context.getMethod();
        
        final Object[] forLog = Util.forLog(method.getName(), action, groups, roles);
        
        logger.debug("Intercept method call [{}] coresponding to PHR Action [{}] for groups [{}] and roles [{}]", forLog);

        return context.proceed();
    }
}
