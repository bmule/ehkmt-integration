/*
 * Project :iCardea
 * File : UserManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.UserManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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

    private static final String ANONYMUS_NAME = "Anonymus";

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
     * <code>userExist</code> for the removed user will cause return false. 
     * If the user was not registered (in a previous action) 
     * then this method has no effect.
     * 
     * @param user the user to remove, it can not be null.
     * @return the removed user.
     * @throws NullPointerException if the user argument is null.
     * @see #removeUsers(java.util.Set) 
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

        // mihai :
        // only managed entities can be removed/manipulated
        // so I need to add it in to the context (merge) before
        // I remove it.
        final PhrUser mergeResult = entityManager.merge(user);
        entityManager.remove(mergeResult);

        return user;
    }

    /**
     * Removes all the registered users after this method call the 
     * <code>areAnyUsersRegistered</code> for the removed user will cause 
     * return false. 
     */
    @Override
    public void removeAllUsers() {
        // FIXME : this is not the most efficent way to remove thigs, it can
        // also be done in a singular bulk operation. 
        final Query allUsersQuery = entityManager.createNamedQuery("getAllUsers");
        // FIXME : for a lot of userd do pagging
        final List<PhrUser> allUsers = allUsersQuery.getResultList();

        for (PhrUser user : allUsers) {
            entityManager.remove(user);
        }
    }

    /**
     * Proves if the underlying persistence contains a given user.
     * More precisely this test proves if the underlying persistence
     * layer contains an user with the same name and family name with
     * the specified user.
     *
     * @param user the group which the existence is to be tested.
     * @return true if the underlying persistence contains a given
     * user, false other wise.
     * @throws NullPointerException if the user argument is null.
     */
    @Override
    public boolean userExist(PhrUser user) {

        // FIXME : consider the login name like unique identifier.
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

    /**
     * Returns the anonymus user instance. 
     * There is only one anonymus user instance, repetative call for this method
     * will produce the same instance.
     * 
     * @return the anonymus user. 
     */
    @Override
    public PhrUser getAnonymusUser() {
        final Query anonymusQuery =
                entityManager.createNamedQuery("findAnonymusUser");
        PhrUser result;
        try {
            result = (PhrUser) anonymusQuery.getSingleResult();
        } catch (NoResultException noResultException) {
            result = new PhrUser(ANONYMUS_NAME, ANONYMUS_NAME);
            result.setIsAnonymous(true);
            entityManager.persist(result);
        } catch (NonUniqueResultException nonUniqueResultException)  {
            final UserException userException =
                    new UserException("More Anonymus user deteceted, data is incosistent.");
            throw userException;
        }
        
        return result;
    }

    /**
     * Removes all the users from the given set. If the user set contains
     * unregistered users then they will be ignored.
     * 
     * @param users the users to remove, it can not be null or empty set.
     * @throws NullPointerException if the users argument is null.
     * @throws IllegalArgumentException if the users argument is an empty set.
     */
    @Override
    public void removeUsers(Set<PhrUser> users) {

        if (users == null) {
            final NullPointerException nullException =
                    new NullPointerException("The users argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (users.isEmpty()) {
            final IllegalArgumentException illegalException =
                    new IllegalArgumentException("The users set can not be empty can not be null.");
            logger.error(illegalException.getMessage(), illegalException);
            throw illegalException;
        }

        logger.debug("Tries to removes users [{}].", users);
        for (PhrUser user : users) {
            removeUser(user);
        }
        logger.debug("Users [{}] was removed,", users);
    }

    /**
     * Returns all the users where the name match exactly a certain name.
     * If there are no matchers for the given name then this method will
     * return null.
     * 
     * @param name the name to match.
     * @return all the users where the name match exactly a certain name.
     * @throws NullPointerException if the name argument is null.
     */
    @Override
    public Set<PhrUser> getUsersForName(String name) {

        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query usersQuery =
                entityManager.createNamedQuery("findUserForName");
        usersQuery.setParameter("name", name);

        final List usersList = usersQuery.getResultList();
        final Set<PhrUser> result = new HashSet<PhrUser>();
        result.addAll(usersList);
        return result;
    }

    /**
     * Returns a set that contains all the PHRS users with the name matching
     * a given JPSQ like patten.<br>
     * The namePattern is a string literal or a string-valued
     * input parameter in which an underscore (_) stands for any single
     * character, a percent (%) character stands for any sequence of
     * characters (including the empty sequence), and all other characters
     * stand for themselves.
     *
     * @param namePattern the name pattern to search, it can not be null.
     * @return a set that contains all the PHRS users with the name matching
     * a given JPSQ like patten.
     * @throws NullPointerException if the name argument is null.
     */
    @Override
    public Set<PhrUser> getUsersForNamePattern(String namePattern) {

        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final Query usersQuery =
                entityManager.createNamedQuery("findUserForNamePattern");
        usersQuery.setParameter("namePattern", namePattern);

        final List usersList = usersQuery.getResultList();
        final Set<PhrUser> result = new HashSet<PhrUser>();
        result.addAll(usersList);
        return result;

    }

    @Override
    public Set<PhrUser> getUsersForNamesPattern(String namePattern, String famillyNamePattern) {


        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (famillyNamePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The famillyNamePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final Query usersQuery =
                entityManager.createNamedQuery("findUserForNameAndFamilyPattern");
        usersQuery.setParameter("namePattern", namePattern);
        usersQuery.setParameter("familyNamePattern", famillyNamePattern);

        final List usersList = usersQuery.getResultList();
        final Set<PhrUser> result = new HashSet<PhrUser>();
        result.addAll(usersList);
        return result;
    }

    /**
     * Returns all the registered users packed in to a set.
     * For a big amount of users this method may take a while.
     * 
     * @return all the registered users packed in to a set.
     */
    @Override
    public Set<PhrUser> getAllUsers() {
        final Query query = entityManager.createNamedQuery("getAllUsers");
        // FIXME : for a lot of userd do pagging
        final List resultList = query.getResultList();
        final Set<PhrUser> result = new HashSet<PhrUser>();
        result.addAll(resultList);

        return result;
    }

    /**
     * Proves if there is any registered users.
     * 
     * @return true if there is any registered users.
     */
    @Override
    public boolean areAnyUsersRegistered() {
        final Query query = entityManager.createNamedQuery("getAllUsers");
        final List resultList = query.getResultList();
        final boolean result = resultList.isEmpty();
        return result;
    }
}
