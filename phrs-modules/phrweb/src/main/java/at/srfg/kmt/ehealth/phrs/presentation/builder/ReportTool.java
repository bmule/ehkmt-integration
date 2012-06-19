package at.srfg.kmt.ehealth.phrs.presentation.builder;

import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class ReportTool {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(ReportTool.class);
    protected ResourceBundle resourceBundle;

    public ReportTool(){
        resourceBundle = ReportTool.resourceBundle();
    }
    public ReportTool(ResourceBundle resourceBundle){

        if(resourceBundle!=null)
            this.resourceBundle = resourceBundle;
        else
            this.resourceBundle = ReportTool.resourceBundle();
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Provides a simple means to create formatted output
     * @param properties ModelLabelValue: id and value must not be null.  id should be an i18 known code.  Type=i18 will cause a label lookup lookup for i18 label in vocab list or 118 lists
     *                   Value might already be a combination of two or more fields and the key refers to a group
     * @param templateId  provides output according to a template
     *                    csv, default (html), properties (prop=value, ...)
     * @return
     */
    public  String formatTextToString(List<ModelLabelValue> properties,String templateId){
        Object object=null;
        StringBuffer sb = new StringBuffer();
        //simple html break. Velocity will do better
        String delimiter="<br/>";
        templateId = templateId!=null ? templateId :"default";

        if("csv".equals(templateId)) {
            delimiter=",";
        }
        //check for I18 codes type=i18
        for(ModelLabelValue lv:properties){
            if(lv!=null && lv.getId()!=null && lv.getValue()!=null){
                if("properties".equals(templateId)) {
                    sb.append(lv.getId());
                    sb.append(" = ");
                }
                sb.append(lv.getValue());
                if(sb.length()>1){
                    sb.append(delimiter);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Wraps resource bundle
     * Look up resource bundle, return default label or key name
     * @param key
     * @param defaultLabel
     * @return
     */
    public String getLabel(String key,String defaultLabel){
        String label=null;
        if(key!=null && !key.isEmpty())   {

            try {
                label = resourceBundle.getString(key);
            } catch (Exception e) {
                label = defaultLabel;
            }

        }
        if(label==null)   {
            if(defaultLabel!=null)    label=defaultLabel;
            else label=key;
        }
        return label;
    }

    /**
     * Wraps resource bundle
     * @param key
     * @param defaultKeyname    Use key name if label not found
     * @return
     */
    public String getLabel(String key,boolean defaultKeyname){
        String label=null;
        if(key!=null && !key.isEmpty())   {

            try {
                label = resourceBundle.getString(key);
            } catch (Exception e) {
            }

        }
        if(label==null && defaultKeyname) label=key;
        return label;
    }
    /**
     * Using JSF context to determine locale
     * @return
     */
    public static ResourceBundle resourceBundle() {
        Locale locale=null;
        ResourceBundle resourceBundle;
        if(FacesContext.getCurrentInstance()!=null){
            locale=FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        }
        if(locale!=null)
            resourceBundle= ResourceBundle.getBundle("Messagelabels",locale);
        else
            resourceBundle = ResourceBundle.getBundle("Messagelabels");

        return resourceBundle;
    }


    /**
     *
     * @param locale  null default locale
     * @return
     */
    public static ResourceBundle resourceBundle(Locale locale) {

        ResourceBundle resourceBundle;

        if(locale!=null)
            resourceBundle = ResourceBundle.getBundle("Messagelabels",locale);
        else
            resourceBundle = ResourceBundle.getBundle("Messagelabels");

        return resourceBundle;
    }
}
