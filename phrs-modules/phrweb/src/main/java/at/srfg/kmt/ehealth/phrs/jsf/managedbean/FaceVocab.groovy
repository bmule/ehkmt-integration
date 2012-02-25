package at.srfg.kmt.ehealth.phrs.jsf.managedbean

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService
import javax.faces.context.FacesContext

/**
 *
 * Includes UserServices and vocabulary helper methods to support child beans
 * Vocabulary lookup for labels can check vocabulary services or I18 files.
 *
 */
class FaceVocab implements Serializable {


    public Locale locale
    public String language

    /**
     * Application wide bean?
     */
    public FaceVocab() {

        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        language = locale ? locale.getLanguage() : 'en'
    }

    public Locale getLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    public String getLanguage() {
        try {
            return getLocale().getLanguage()
        } catch (Exception e) {

        }
        return 'en'
    }

    /**
     * Should get language from locale
     * @param String
     * @return
     */
    public String getTermLabel(String termId) {
        return VocabularyService.getTermLabel(termId, language)
    }


    public ModelLabelValue getTerm(String termId) {
        return VocabularyService.getTerm(termId, language)
    }


}
