/*
 * Project :iCardea
 * File : MyServiceBean.java
 * Date : Jan 17, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.RunAs;
import at.srfg.kmt.ehealth.phrs.security.api.RunAsGroup;
import at.srfg.kmt.ehealth.phrs.security.api.RunAsRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrAction;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implements a simple service. <br>
 * This bean is used to show how a certain action can be accomplished
 * like the member from a certain group with a certain role. <br>
 * The action, groups and roles are set programmatically in the
 * <code>init</code> method. The container will intercept the business method 
 * (<code>doStuff</code>) call and attached the action, groups and roles to it.
 * In this way the business method (<code>doStuff</code>) is enriched with 
 * new information, and this information is run-time interchangeable. <br>
 * 
 * This class is just an example - it has only didactical purposes.
 * 
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@Stateless
@Local(MyService.class)
@RunAs
@Interceptors({RunAsInterceptor.class, SecureCallInterceptor.class})
public class MyServiceBean implements MyService, RunAsRole, RunAsGroup {

    /**
     * The involved action.
     */
    private PhrAction action;

    /**
     * The roles for the action.
     */
    private Set<PhrRole> roles;
    
    /**
     * The group for the action.
     */
    private Set<PhrGroup> groups;

    /**
     * Acts like a constructor.
     */
    @PostConstruct
    public void init() {
        action = new PhrAction();
        action.setName("Dummy action");

        roles = new HashSet<PhrRole>();
        final PhrRole phrRole = new PhrRole("test_role");
        roles.add(phrRole);
        
        groups = new HashSet<PhrGroup>();
        final PhrGroup phrGroup = new PhrGroup("test_group");
        groups.add(phrGroup);
    }

    /**
     * Returns the value on the <code>i</code> argument increment with 10.
     * 
     * @param i the value to implement
     * @return the value on the <code>i</code> argument increment with 10.
     */
    @Override
    public int doStuff(int i) {
        return i + 7;
    }

    @Override
    public Set<PhrRole> getRoles() {
        return roles;
    }

    @Override
    public Set<PhrGroup> getGroups() {
        return groups;
    }

    @Override
    public PhrAction getAction() {
        return action;
    }
}
