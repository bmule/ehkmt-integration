/*
 * Project :iCardea
 * File : VocabularyServlet.java
 * Encoding : UTF-8
 * Date : Mar 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs;


import static at.srfg.kmt.ehealth.phrs.Utils.writeToResponseH1;
import static at.srfg.kmt.ehealth.phrs.Utils.writeToResponse;
import static at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup.lookupLocal;
import static at.srfg.kmt.ehealth.phrs.dataexchange.api.Constants.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.VocabularyLoader;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is just an example - this class has only didactical purposes. <br>
 * To use this example use the following URL :
 * http://localhost:8080/simple-view/VocabularyServlet?q=XXX where 
 * XXX is : 
 * 
 * <ul>
 * <li> loadVocabularyItems - loads the default vocabulary items.
 * <li> listSymptoms - list all the symptoms. All the symptoms are all the 
 * vocabulary items tagged with the "symptom".
 * <li> listDAO - list all the activities of daily living. All the activities of
 * daily living are all the vocabulary items tagged with "DAO 
 * (Daily Activity Observation)".
 * <li> listRisks - list all the risks factors. All the risks factors living are
 * all the vocabulary items tagged with "risk".
 * <li> listVitalSigns - list all the risks factors. All the risks factors living are
 * all the vocabulary items tagged with "Vital signs".
 * </ul>
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class VocabularyServlet extends HttpServlet {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.VocabularyServlet</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VocabularyServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final String q = request.getParameter("q");

        if (q == null || q.isEmpty()) {
            LOGGER.debug("No q paramater set.");
            writeToResponseH1(response, "No q parameter.");
            return;
        }

        if ("loadVocabularyItems".equalsIgnoreCase(q)) {
            loadVocabularyItems(response);
            return;
        }

        if ("listSymptoms".equalsIgnoreCase(q)) {
            listSymptoms(response);
            return;
        }
        
        if ("listVitalSigns".equalsIgnoreCase(q)) {
            listVitalSigns(response);
            return;
        }
        
        if ("listDAO".equalsIgnoreCase(q)) {
            listDAO(response);
            return;
        }
        
        if ("listRisks".equalsIgnoreCase(q)) {
            listRisks(response);
            return;
        }

        writeToResponseH1(response, "Parameter [" + q + "] not supported.");
    }

    private void loadVocabularyItems(HttpServletResponse response) throws IOException {
        final VocabularyLoader loader;
        try {
            loader = lookupLocal(VocabularyLoader.class);
            loader.load();
            writeToResponseH1(response, "Default vocabulary was succesfully loaded.");
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            writeToResponseH1(response, "FAILURE ! - see the log");
            return;
        }
    }

    private void listSymptoms(HttpServletResponse response) throws IOException {
        final String symptomCode = "C1457887";
        list(response, symptomCode);
    }
    
    private void listVitalSigns(HttpServletResponse response) throws IOException {
        final String vitalSignsCode = "C0518766";
        list(response, vitalSignsCode);
    }
    
    private void listDAO(HttpServletResponse response) throws IOException {
        final String vitalSignsCode = "C1269688";
        list(response, vitalSignsCode);
    }
    
    private void listRisks(HttpServletResponse response) throws IOException {
        final String risksCode = "C0332167";
        list(response, risksCode);
    }

    private void list(HttpServletResponse response, String tagCode) throws IOException {

        final ControlledItemRepository repository;
        try {
            repository = lookupLocal(ControlledItemRepository.class);
            final ControlledItem item =
                    repository.getByCodeSystemAndCode(SNOMED, tagCode);
            final String msg = "Tag " + item + "</br>";
            final Set<ControlledItem> byTag = repository.getByTag(item);
            final StringBuffer output = new StringBuffer();
            for (ControlledItem symptom : byTag) {
                output.append(symptom);
                output.append("</br>");
            }
            writeToResponse(response, output.toString());

        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            writeToResponseH1(response, "FAILURE ! - see the log");
            return;
        }
    }
}
