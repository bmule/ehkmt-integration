/*
 * Project :iCardea
 * File : PhrCommunity.java
 * Date : Feb 25, 2011
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
@DiscriminatorValue("PhrCommunity")
public class PhrCommunity extends PhrSocialActor {

    public PhrCommunity() {
    }

    public PhrCommunity(String name) {
        super(name);
    }
}
