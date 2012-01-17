package at.srfg.kmt.ehealth.phrs.jsf.support;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
/*
 * http://blogs.oracle.com/jtb/entry/poor_man_s_jsf_navigation
 * The phase listener gets the request, and checks for a parameter. The parameter is the path to the view ID you want to visit. For example: /faces/someview.xhtml.
 *  With Facelets, these goto URLs end up looking funny, 
 *  because the real view ID is in the URL also,
 * /faces/home.xhtml?viewId=/faces/other.xhtml
 * Not nice, but it works. Other solutions I've seen try to get fancy by keeping a mapping a view+action result=new view. That's cleaner, and it's easy to do once you understand what's going on above. 
 */
public class RedirectPhaseListener implements PhaseListener {

    public RedirectPhaseListener() {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void afterPhase(PhaseEvent phaseEvent) {
    }

    public void beforePhase(PhaseEvent phaseEvent) {
        FacesContext ctx = phaseEvent.getFacesContext();
        HttpServletRequest request =
                (HttpServletRequest) ctx.getExternalContext().getRequest();

        String viewId = request.getParameter("viewId");

        if (viewId != null) {
            UIViewRoot page = ctx.getApplication().getViewHandler().createView(ctx, viewId);
            ctx.setViewRoot(page);
            ctx.renderResponse();

        }
    }
} 
