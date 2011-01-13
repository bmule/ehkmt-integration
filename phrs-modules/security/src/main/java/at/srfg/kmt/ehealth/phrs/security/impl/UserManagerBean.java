/*
 * Project :iCardea
 * File : UserManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.UserManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateless bean local scoped used to manage and manipulate
 * <code>PhrUser</code> and related information.
 *
 * @author Mihai
 */
@Stateless
@Local(UserManager.class)
public class UserManagerBean implements UserManager {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.UserManagerBean</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UserManagerBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Registers a  user instance or update and existing one if 
     * the underlying persistence contains already a group with
     * the same name. The method returns true if the group is
     * added and false if the group is updated.
     *
     * @param group the group to add, it can not be null.
     * @return true if the group is added, false if the group is updated.
     * @throws NullPointerException if the <code>user</code> argument is null.
     */
    @Override
    public boolean addUser(PhrUser user) {
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The user argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }
        
        

        logger.debug("Tries to add user [{}]", user);
        entityManager.merge(user);
        logger.debug("Group [{}] was upadted.", user);
        return true;
    }
    
        @Override
    public boolean addUsers(Set<PhrUser> users) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    @Override
    public void removeAllUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhrUser removeUser(PhrUser PhrUser) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean userExist(PhrUser PhrUser) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public PhrUser getAnonymusUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhrUser removeUsers(Set<PhrUser> users) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<PhrUser> getUsersForName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<PhrUser> getUsersForNamePattern(String namePattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<PhrUser> getUsersForNamePattern(String namePattern, String famillyNamePattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
