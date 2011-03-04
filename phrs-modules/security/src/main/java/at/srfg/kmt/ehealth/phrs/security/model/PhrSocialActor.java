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
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@Entity
@DiscriminatorValue("PhrSocialActor")
public class PhrSocialActor extends PhrActor {

    public PhrSocialActor() {
    }

    public PhrSocialActor(String name) {
        super(name);
    }
}
