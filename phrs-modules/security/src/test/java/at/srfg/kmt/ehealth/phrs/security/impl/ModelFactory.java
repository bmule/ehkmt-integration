/*
 * Project :iCardea
 * File : ModelFactory.java
 * Date : Jan 12, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Date;

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
     * Builds a <code>PhrUser</code> with a certain name and family name.
     * The <code>PhrUser</code> name is formed from the 'Name_' prefix
     * followed by the actual time in milliseconds.
     *
     * @return a <code>PhrUser</code> with a certain name and family.
     */
    static PhrUser createPhrUser() {
        final StringBuffer name = new StringBuffer();
        name.append("Name_");
        final long time = new Date().getTime();
        name.append(time);

        final StringBuffer famName = new StringBuffer();
        famName.append("Family_");
        name.append(time);

        final PhrUser user = new PhrUser(name.toString(), famName.toString());

        return user;
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
