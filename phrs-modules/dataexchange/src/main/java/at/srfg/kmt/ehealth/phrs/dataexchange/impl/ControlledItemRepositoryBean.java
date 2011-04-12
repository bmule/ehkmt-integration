/*
 * Project :iCardea
 * File : ControlledItemRepositoryBean.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItemTag;
import java.util.HashSet;
import java.util.List;
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
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(ControlledItemRepository.class)
public class ControlledItemRepositoryBean implements ControlledItemRepository {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ControlledItemRepositoryBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ControlledItemRepositoryBean.class);
    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    @Override
    public boolean add(ControlledItem item) {
        if (item == null) {
            final NullPointerException nullException =
                    new NullPointerException("The item argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final boolean contains = entityManager.contains(item);

        if (contains) {
            entityManager.merge(item);
            return false;
        }

        final Long id = item.getId();
        if (id == null) {
            entityManager.persist(item);
            return true;
        }

        final ControlledItem find = entityManager.find(ControlledItem.class, id);
        if (find == null) {
            entityManager.persist(item);
            return true;
        }

        entityManager.merge(item);
        return false;
    }

    @Override
    public boolean contains(ControlledItem item) {
        final boolean contains = entityManager.contains(item);

        if (contains) {
            return true;
        }

        final Long id = item.getId();
        if (id == null) {
            return false;
        }

        final ControlledItem find = entityManager.find(ControlledItem.class, id);

        return find == null;
    }

    @Override
    public boolean contains(String codeSystem, String code) {
        final ControlledItem result =
                getByCodeSystemAndCode(codeSystem, code);
        return result != null;
    }

    @Override
    public Set<ControlledItem> get(Query query) {
        final List resultList = query.getResultList();

        final Set<ControlledItem> results = new HashSet<ControlledItem>();
        results.addAll(resultList);

        return results;
    }

    @Override
    public Set<ControlledItem> getByCodeSystem(String codeSystem) {
        final Query query =
                entityManager.createNamedQuery("selectControlledItemByCodeSystem");
        query.setParameter("code_system", codeSystem);
        final List resultList = query.getResultList();

        final Set<ControlledItem> results = new HashSet<ControlledItem>(resultList.size());
        results.addAll(resultList);

        return results;
    }

    @Override
    public final ControlledItem getByCodeSystemAndCode(String codeSystem, String code) {
        final Query query =
                entityManager.createNamedQuery("selectControlledItemCodeSystemAndCode");
        query.setParameter("code_system", codeSystem);
        query.setParameter("code", code);
        try {
            final ControlledItem result = (ControlledItem) query.getSingleResult();
            return result;
        } catch (NoResultException exception) {
            LOGGER.debug("No item with the code system {} and code {}");
            LOGGER.debug(exception.getMessage(), exception);
        }

        return null;
    }

    @Override
    public Set<ControlledItem> getByPrefLabel(String prefLabel) {
        
        if (prefLabel == null) {
            final NullPointerException nullException = 
                    new NullPointerException("The prefLabelArgumetn can not be null or empty");
            LOGGER.error(prefLabel);
            throw nullException;
        }
        
        final Query query =
                entityManager.createNamedQuery("selectControlledItemByPrefLabel");
        query.setParameter("pref_label", prefLabel);

        final List resultList = query.getResultList();
        final Set<ControlledItem> result =
                new HashSet<ControlledItem>(resultList.size());
        result.addAll(resultList);

        return result;
    }

    @Override
    public Set<ControlledItem> getByPrefLabelPrefix(String prefLabelPrefix) {
        
        if (prefLabelPrefix == null) {
            final NullPointerException nullException = 
                    new NullPointerException("The prefLabelPrefix can not be null or empty");
            LOGGER.error(prefLabelPrefix);
            throw nullException;
        }
        
        final StringBuffer patern = new StringBuffer();
        patern.append("%");
        patern.append(prefLabelPrefix);
        
        final Query query =
                entityManager.createNamedQuery("selectControlledItemByPrefLabel");
        query.setParameter("pref_label_prefix", patern.toString());

        final List resultList = query.getResultList();
        final Set<ControlledItem> result =
                new HashSet<ControlledItem>(resultList.size());
        result.addAll(resultList);

        return result;
    }

    @Override
    public ControlledItem remove(ControlledItem item) {

        if (item == null) {
            final NullPointerException nullException =
                    new NullPointerException("The item argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final boolean contains = entityManager.contains(item);

        if (contains) {
            entityManager.remove(item);
            return item;
        }

        final Long id = item.getId();
        if (id == null) {
            return null;
        }

        final ControlledItem find = entityManager.find(ControlledItem.class, id);
        if (find == null) {
            return null;
        }

        entityManager.remove(item);
        return find;
    }

    @Override
    public void update(ControlledItem item) {
        if (item == null) {
            final NullPointerException nullException =
                    new NullPointerException("The item argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final boolean contains = entityManager.contains(item);

        if (contains) {
            entityManager.merge(item);
            return;
        }

        final Long id = item.getId();
        if (id == null) {
            return;
        }

        final ControlledItem merge = entityManager.merge(item);
    }

    @Override
    public final Set<ControlledItem> getByTag(ControlledItem tag) {

        if (tag == null) {
            final NullPointerException nullException =
                    new NullPointerException("The tag argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Long id = tag.getId();
        if (id == null) {
            return new HashSet<ControlledItem>();
        }
        final ControlledItem findItem =
                entityManager.find(ControlledItem.class, id);

        if (findItem == null) {
            return new HashSet<ControlledItem>();
        }

        final Query query =
                entityManager.createNamedQuery("selectControlledItemTagByTagging");
        query.setParameter("tagging", tag);

        final List resultList = query.getResultList();
        final Set<ControlledItem> result =
                new HashSet<ControlledItem>(resultList.size());
        result.addAll(resultList);

        return result;
    }

    @Override
    public final Set<ControlledItem> getTags(ControlledItem item) {

        if (item == null) {
            final NullPointerException nullException =
                    new NullPointerException("The item argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Long id = item.getId();
        if (id == null) {
            return new HashSet<ControlledItem>();
        }
        final ControlledItem findItem =
                entityManager.find(ControlledItem.class, id);

        final Query query =
                entityManager.createNamedQuery("selectControlledItemTagByTagged");
        query.setParameter("tagged", findItem);

        final List resultList = query.getResultList();
        final Set<ControlledItem> result =
                new HashSet<ControlledItem>(resultList.size());
        result.addAll(resultList);

        return result;
    }

    @Override
    public void tag(ControlledItem tagged, ControlledItem tagging) {

        if (tagged == null && tagging == null) {
            final NullPointerException nullException =
                    new NullPointerException("The tagged or tagging can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (!tagExists(tagged, tagging)) {
            final ControlledItemTag tag = new ControlledItemTag(tagged, tagging);
            entityManager.persist(tag);
        } else {
            LOGGER.warn("Tag relation already exists.");
        }

    }

    /**
     * Proves if a tagging relation exist already or not.
     * 
     * @param tagged the tagged part for a tag relation. This is the item that 
     * get tagged.
     * @param tagging the tagging part for a tag relation. This is the tag
     * applied to the item.
     * @return true if the tag relation defined with the both arguments 
     * (<code>tagged</code> and <code>tagging</code>) exists.
     * @throws NonUniqueResultException if the underlying persistence layer
     * contains inconsistent data. A tag relation can not appear two times for 
     * the same participants (<code>tagged</code> and <code>tagging</code>).
     */
    private boolean tagExists(ControlledItem tagged, ControlledItem tagging) {
        final Query query =
                entityManager.createNamedQuery("selectControlledItemTagByTaggedAndTagging");
        query.setParameter("tagged", tagged);
        query.setParameter("tagging", tagging);

        try {
            query.getSingleResult();
        } catch (NoResultException exception) {
            return false;
        }

        return true;
    }

    @Override
    public void removeAllTags(ControlledItem tagged) {
        final Set<ControlledItem> tags = getTags(tagged);
        for (ControlledItem tag : tags) {
            entityManager.remove(tag);
        }
    }

    @Override
    public void untag(ControlledItem tagged, ControlledItem tagging) {
        final Query query =
                entityManager.createNamedQuery("selectControlledItemTagByTaggedAndTagging");
        query.setParameter("tagged", tagged);
        query.setParameter("tagging", tagging);

        final List<ControlledItemTag> resultList = query.getResultList();
        for (ControlledItemTag tag : resultList) {
            entityManager.remove(tag);
        }
    }

    /**
     * Proves the existence for a certain tag relation.
     * The relation is defined by two participants the "tagged" and "tagging".
     * The tagged is the item that get tagged.
     * The tagging is the tag.
     * 
     * @param tagged the tagged item, it can not be null.
     * @param tagging the tag, it can not be null.
     * @return true if the tag relation defined with the tagged and tagging
     * elements exists.
     */
    @Override
    public boolean tagExist(ControlledItem tagged, ControlledItem tagging) {

        if (tagged == null) {
            final NullPointerException nullException =
                    new NullPointerException("The tagged argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (tagging == null) {
            final NullPointerException nullException =
                    new NullPointerException("The tagging argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query =
                entityManager.createNamedQuery("countTagByTaggedAndTagging");
        query.setParameter("tagged", tagged);
        query.setParameter("tagging", tagging);

        final Long tagsCount = (Long) query.getSingleResult();

        return tagsCount.intValue() > 0;

    }
}
