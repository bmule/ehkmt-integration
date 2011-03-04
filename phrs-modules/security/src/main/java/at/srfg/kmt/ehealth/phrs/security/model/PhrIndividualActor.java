/*
 * Project :iCardea
 * File : PhrSocialActor.java
 * Date : Feb 8, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Defines a individual actor (e.g. a person is a individual actor).
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@Entity
@DiscriminatorValue("PhrIndividualActor")
public class PhrIndividualActor extends PhrActor {

    /**
     * 
     */
    public PhrIndividualActor() {
        // UNIMPLEMENTD
    }

    public PhrIndividualActor(String name) {
        super(name);
    }
}
