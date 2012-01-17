package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.util.Locale;

import javax.faces.context.FacesContext;
//http://stackoverflow.com/questions/4830588/jsf-locale-is-set-per-request-not-for-session
public class LocaleBean {

    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

}