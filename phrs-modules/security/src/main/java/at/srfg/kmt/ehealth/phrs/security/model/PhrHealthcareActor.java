/*
 * Project :iCardea
 * File : PhrSocialActor.java
 * Date : Feb 8, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;

import javax.persistence.DiscriminatorValue;

/**
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@DiscriminatorValue("PhrHealthcareActor")
public class PhrHealthcareActor extends PhrIndividualActor {

    public PhrHealthcareActor() {
    }

    public PhrHealthcareActor(String name) {
        super(name);
    }
}
