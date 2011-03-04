/*
 * Project :iCardea
 * File : PhrIndividualAnonymous.java
 * Date : Feb 8, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@Entity
@DiscriminatorValue("PhrIndividualAnonymous")
@NamedQueries({@NamedQuery(name="selectAnonymousIndividual", 
                query="SELECT a_actor FROM PhrIndividualAnonymous AS a_actor")})
public class PhrIndividualAnonymous extends PhrIndividualActor {

    public PhrIndividualAnonymous() {
    }

    public PhrIndividualAnonymous(String name) {
        super(name);
    }
}
