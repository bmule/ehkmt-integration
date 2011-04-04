/*
 * Project :iCardea
 * File : VocabularyLoaderBean.java
 * Encoding : UTF-8
 * Date : Mar 24, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.VocabularyLoader;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to load the default SMOMED-CT items and the related tags 
 * (for the SMOMED-CT items). The load process is based on two properties files
 * located in the classpath. The names for the files are defined with the 
 * <code>SNOMEND_FILE</code> and the <code>SNOMEND_TAG_FILE</code> constants. <br>
 * The <code>SNOMEND_FILE</code> properties file syntax is :
 * SNOMED-CT (unique) id = SNOMED-CT display name. The '=' can be replaced by
 * the one or more space or tab characters. <br>
 * The <code>SNOMEND_TAG_FILE</code> properties file syntax is :
 * SNOMED-CT id for the item to tag = SNOMED-CT id for the tag. 
 * The '=' can be replaced by the one or more space or tab characters. <br>
 * Consider that the <code>SNOMEND_FILE</code> contains : 
 * <pre>
 * 19019007    Symptom
 * 29857009    Chest pain 
 * </pre>
 * and the the <code>SNOMEND_TAG_FILE</code> contains : 
 * <pre>
 * 29857009    19019007
 * </pre>
 * 
 * If this two files are processed then the repository will contain two
 * controlled items named "Symptom" and "Chest pain", and according with the tag
 * relations defined in the <code>SNOMEND_TAG_FILE</code> the "Chest pain" item
 * will be tagged with "Symptom" item.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(VocabularyLoader.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class VocabularyLoaderBean implements VocabularyLoader {

    /**
     * The name for the properties file that contains the controlled items.
     */
    private String SNOMEND_FILE = "phrs.snomed-ct.properties";
    /**
     * The name for the properties file that contains the controlled items tag 
     * relations.
     */
    private String SNOMEND_TAG_FILE = "phrs.snomed-ct-tags.properties";
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.VocabularyLoaderBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VocabularyLoaderBean.class);
    /**
     * Used to persist the controlled items.
     */
    @EJB
    private ControlledItemRepository controlledItemRepository;
    @Resource
    private UserTransaction transaction;

    /**
     * Loads and tags all the items defined in the items and tags files.
     * See the class comments for more informations.
     */
    @Override
    public void load() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final InputStream itemInput =
                classLoader.getResourceAsStream(SNOMEND_FILE);
        final Properties itemProperties = new Properties();
        if (itemInput == null) {
            final String msg =
                    String.format("The file %s ca not be located in the classpath", SNOMEND_FILE);
            LOGGER.warn(msg);
        } else {
            try {
                itemProperties.load(itemInput);
            } catch (IOException ex) {
                LOGGER.warn(ex.getMessage(), ex);
                throw new EJBException(ex);
            }
        }
        try {
            processItems(itemProperties);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        final InputStream tagInput =
                classLoader.getResourceAsStream(SNOMEND_TAG_FILE);
        final Properties tagProperties = new Properties();
        if (tagInput == null) {
            final String msg =
                    String.format("The file %s ca not be located in the classpath", SNOMEND_TAG_FILE);
            LOGGER.warn(msg);
        } else {
            try {
                tagProperties.load(tagInput);
            } catch (IOException ex) {
                LOGGER.warn(ex.getMessage(), ex);
                throw new EJBException(ex);
            }
        }
        try {
            tagItems(tagProperties);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Creates and persists all the items encapsulated in the given 
     * <code>Properties</code> instance. <br>
     * Each property entry must contains like key the SNOMED-CT unique id and 
     * like value the display text (e.g. key=19019007, value=Symptom).
     * 
     * @param properties it contains all the items to persists, it can not be null.
     * @throws  NullPointerException if the <code>properties</code> argument is
     * null.
     */
    private void processItems(Properties properties) throws RollbackException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException {

        if (properties == null) {
            final NullPointerException nullException =
                    new NullPointerException("The properties argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
        }

        transaction.begin();
        for (Map.Entry entry : properties.entrySet()) {
            final String code = (String) entry.getKey();
            final String label = (String) entry.getValue();

            if (label == null) {
                final String msg =
                        String.format("The dispaly name for code %s can not be null.", code);
                final NullPointerException nullException =
                        new NullPointerException(msg);
                LOGGER.error(msg, nullException);
                throw nullException;
            }

            final ControlledItem controlledItem =
                    new ControlledItem(Constants.SNOMED, "SNOMED", code.trim(), label.trim());
            controlledItemRepository.add(controlledItem);
        }
        transaction.commit();
    }

    /**
     * Associates tags on controlled items for a given <code>Properties</code>
     * instance. <br>
     * Each property entry must contains like key the SNOMED-CT id for the
     * item to tag and the value is the SNOMED-CT id for for the tagged item.
     * 
     * @param properties it contains the tagging relations, it can not be null.
     * @throws  NullPointerException if the <code>properties</code> argument is
     * null.
     */
    private void tagItems(Properties properties) throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException, NotSupportedException {

        if (properties == null) {
            final NullPointerException nullException =
                    new NullPointerException("The properties argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
        }

        transaction.begin();
        for (Map.Entry entry : properties.entrySet()) {
            final String codeItem = (String) entry.getKey();
            String codeTag = (String) entry.getValue();
            if (codeTag == null) {
                final String msg =
                        String.format("The tag code property for tagged item %s can not be null.", codeItem);
                final NullPointerException nullException =
                        new NullPointerException(msg);
                LOGGER.error(msg, nullException);
            }
            // just to be sure that all the spaces are out.
            codeTag = codeTag.trim();
            

            final ControlledItem item =
                    controlledItemRepository.getByCodeSystemAndCode(Constants.SNOMED, codeItem);
            if (item == null) {
                LOGGER.warn("No item with code {} was found in the repository. "
                        + "Tagging operation fails becuase the tagged item is not pressent.");
            }

            final ControlledItem tag =
                    controlledItemRepository.getByCodeSystemAndCode(Constants.SNOMED, codeTag);
            if (tag == null) {
                LOGGER.warn("No item with code {} was found in the repository. "
                        + "Tagging operation fails becuase the tag item is not pressent.");
            }

            if (item != null && tag != null) {
                controlledItemRepository.tag(item, tag);
            }

        }
        transaction.commit();
    }
    
    /**
     * Transforms a string list ',' tokenised in to a Set of strings.
     * 
     * @param tags the 
     * @return 
     */
    private Set<String> getTags(String tags) {
        final Set<String> result = new HashSet<String>();
        for (StringTokenizer st = new StringTokenizer(tags, ","); st.hasMoreTokens(); ) {
            final String tagCode = st.nextElement().toString().trim();
            result.add(tagCode);
        }
        
        return result;
        
    }
}
