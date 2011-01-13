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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        final String name = user.getName();
        final String familyName = user.getFamilyName();
        final PhrUser oldUser = getUserForExactNameAndFamily(name, familyName);
        if (oldUser == null) {
            entityManager.persist(user);
            logger.debug("User [{}] was addd to the PHRS system.", user);
            return true;
        }

        entityManager.merge(user);
        logger.debug("Uroup [{}] was upadted.", user);
        return false;
    }

    /**
     * Returns a users that match the given URI or null if there are no 
     * users that match the specified URI. 
     * 
     * @param uri the URI fro the user to be search.
     * @return a users that match the given URI or null if there are no 
     * users that match the specified URI. 
     */
    private PhrUser getUserForURI(String uri) {
        final Query uriQuery = entityManager.createNamedQuery("findUserForURI");
        uriQuery.setParameter("uri", uri);
        try {
            final PhrUser result = (PhrUser) uriQuery.getSingleResult();
            return result;
        } catch (NoResultException exception) {
            logger.debug("No User fro uri : {}", uri);
        }

        return null;
    }

    /**
     * Returns a user that exactly match the specified name and
     * the family name or null if there are no user that match match
     * the specified name and the family name.
     * 
     * @param name the name to search.
     * @param family the name to search.
     * @return a user that exactly match the specified name and
     * the family name or null for no matches.
     */
    private PhrUser getUserForExactNameAndFamily(String name, String family) {
        final Query query = entityManager.createNamedQuery("findUserForNameAndFamilyName");
        query.setParameter("name", name);
        query.setParameter("familyName", family);

        try {
            final PhrUser result = (PhrUser) query.getSingleResult();
            return result;
        } catch (NoResultException exception) {
            logger.debug("No User with name [{}] and family name [{}]", name, family);
        }

        return null;
    }

    /**
     * Registers a users.
     * 
     * @param users the users to add, it can not be null or empty set
     * otherwise an exception raises.
     * @throws NullPointerException if the users argument is null.
     * @throws IllegalArgumentException if the users argument is an empty set.
     * @see #addUser(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Override
    public void addUsers(Set<PhrUser> users) {
        if (users == null) {
            final NullPointerException nullException =
                    new NullPointerException("The users argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (users.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The users can not be an empty exception.");
            logger.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        for (PhrUser user : users) {
            addUser(user);
        }
    }

    /**
     * Unregisters a given user, after this method call the 
     * <code>userExist</code> method call returns null.  
     * 
     * @param user the user to remove, it can not be null.
     * @return the removed user.
     * @throws NullPointerException if the user argument is null.
     * @see #userExist(at.srfg.kmt.ehealth.phrs.security.model.PhrUser) 
     */
    @Override
    public PhrUser removeUser(PhrUser user) {
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The users argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        // I know is wird I merge before I delete but I must be sure that
        // the user is in context.
        entityManager.merge(user);
        entityManager.remove(user);
        
        return user;
    }

    @Override
    public void removeAllUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Proves if the underlying persistence contains a given user.
     *
     * @param user the group which the existence is to be tested.
     * @return true if the underlying persistence contains a given
     * user, false other wise.
     * @throws NullPointerException if the user argument is null.
     */
    @Override
    public boolean userExist(PhrUser user) {

        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final String name = user.getName();
        final String familyName = user.getFamilyName();
        final PhrUser oldUser = getUserForExactNameAndFamily(name, familyName);
        final boolean result = oldUser != null;

        return result;
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
