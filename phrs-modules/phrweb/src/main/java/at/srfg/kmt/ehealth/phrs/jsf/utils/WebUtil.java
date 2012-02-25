package at.srfg.kmt.ehealth.phrs.jsf.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class WebUtil {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(WebUtil.class);
    //    final HttpServletRequest request =
//            (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//    final String logoUrl = request.getContextPath() + "/servlet/fileServlet?id="
//            + generatingOrg.getLogo().getIdLazyFix();
//    Contexts.getEventContext().set("orgLogoUrl", logoUrl);

    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    public static String getContextPath(FacesContext context) {
        try {
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return getContextPath(request);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Relative path should include starting "/" otherwise it is inserted
     *
     * @param request
     * @param relativePath "/servlet/myServlet?id=", cannot be null
     * @return request.getContextPath() + "/servlet/myServlet?id=";
     */
    public static String getUrl(HttpServletRequest request, String relativePath) {
        String url = null;
        try {
            if (request != null && relativePath != null) {
                String path = relativePath.startsWith("/") ? relativePath : "/" + relativePath;

                url = request.getContextPath() + path;
                //   request.getContextPath() + "/servlet/fileServlet?id=";
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return url;
    }

    /**
     * Relative path should include starting "/"
     *
     * @param context
     * @param relativePath - cannot be null
     * @return
     */
    public static String getUrl(FacesContext context, String relativePath) {
        String url = null;
        try {
            if (context != null) {
                getUrl((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        relativePath);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return url;
    }

    public static String getUrlByFacesContext(String relativePath) {
        String url = null;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                getUrl((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                        relativePath);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return url;
    }
    public static void addFacesMessageSeverityInfo(String header,String message){
        WebUtil.addFacesMessage(FacesMessage.SEVERITY_INFO,header,message);
    }
    public static void addFacesMessageSeverityError(String header,String message){
        WebUtil.addFacesMessage(FacesMessage.SEVERITY_ERROR,header,message);
    }
    public static void addFacesMessageSeverityWarn(String header,String message){
        WebUtil.addFacesMessage(FacesMessage.SEVERITY_WARN,header,message);
    }
    public static void addFacesMessage(FacesMessage.Severity severity, String header,String message){
       
        if(FacesContext.getCurrentInstance()!=null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,header,message ));
        }
    }

}
