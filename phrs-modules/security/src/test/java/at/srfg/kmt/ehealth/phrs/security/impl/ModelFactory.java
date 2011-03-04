/*
 * Project :iCardea
 * File : ModelFactory.java
 * Date : Jan 12, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to create model instances (defiend in the 
 * <code>at.srfg.kmt.ehealth.phrs.mosdel</code>). <br>
 * This class has only test purposes.
 * 
 * @author Mihai
 */
class ModelFactory {

    /**
     * Don't let anybody to instantiate this class.
     */
    private ModelFactory() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>PhrGroup</code> with a certain name and description.
     * The <code>PhrGroup</code> name is formed from the 'Group_' prefix
     * followed by the actual time in milliseconds.
     *
     * @return a <code>PhrGroup</code> with a certain name and description.
     */
    static PhrGroup createPhrGroup() {
        final StringBuffer name = new StringBuffer();
        name.append("Group_");
        final long time = new Date().getTime();
        name.append(time);

        final PhrGroup group = new PhrGroup(name.toString());
        group.setDescription("Dummy PHR Group_" + time);

        return group;
    }

    /**
     * Builds a <code>PhrActor</code> with a certain name and description.
     * The <code>PhrActor</code> name is formed from the 'Name_' prefix
     * followed by the actual time in milliseconds.
     *
     * @return a <code>PhrActor</code> with a certain name and description.
     */
    static PhrActor createPhrActor() {
        final StringBuffer name = new StringBuffer();
        name.append("Name_");
        final long time = new Date().getTime();
        name.append(time);


        final PhrActor result = new PhrActor(name.toString());
        final StringBuffer description = new StringBuffer();
        description.append("Description_");
        description.append(time);
        
        result.setDescription(description.toString());

        return result;
    }
    
    /**
     * Builds a set of set that contains a lot of distinct
     * <code>PhrActor</code> instances, each instance is build with the 
     * <code>createPhrActor</code> method.
     * 
     * @param count the number of the distinct instances for the result set.
     * @return a set of set that contains a lot of distinct
     * <code>PhrActor</code> instances.
     * @see #createPhrActor() 
     */
    static Set<PhrActor> createPhrActors(int count) {
        
        final Set<PhrActor> result = new HashSet<PhrActor>();
        for (int i = 0;i < count; i++) {
            final PhrActor actor = createPhrActor();
            final String name = actor.getName();
            actor.setName(name + "__" + i);
            result.add(actor);
            
            final String description = actor.getDescription();
            actor.setDescription(description + "__" + i);
            result.add(actor);
        }
        
        return result;
    }

    /**
     * Builds a <code>PhrRole</code> with a certain name and description.
     * The <code>PhrRole</code> name is formed from the 'Role_' prefix
     * followed by the actual time in milliseconds.
     *
     * @return a <code>PhrRole</code> with a certain name and description.
     */
    static PhrRole createPhrRole() {
        final StringBuffer name = new StringBuffer();
        name.append("Role_");
        final long time = new Date().getTime();
        name.append(time);

        final PhrRole role = new PhrRole(name.toString());
        role.setDescription("Dummy PHR Role_" + time);

        return role;
    }
}
