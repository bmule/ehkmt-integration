package at.srfg.kmt.ehealth.phrs;


import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PhrSessionTimeoutFilter implements Filter {
    /**
     * When the session destroyed, MySessionListener will do necessary logout operations.
     * Later, at the first request of client, this filter will be fired and redirect
     * the user to the appropriate timeout page if the session is not valid.
     *
     * @author hturksoy
     */

    private static final Logger logger = LoggerFactory.getLogger(PhrSessionTimeoutFilter.class.getName());


    private String timeoutPage = "index.xhtml";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {

        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            // is session expire control required for this request?
            if (isSessionControlRequiredForThisResource(httpServletRequest)) {

                // is session invalid?
                if (isSessionInvalid(httpServletRequest)) {
                    //String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage();
                    logger.debug("session is invalid! redirecting to timeoutpage : /" + getTimeoutPage());
                     //redirect does not work
                    //httpServletResponse.sendRedirect(timeoutUrl);

                    RequestDispatcher rd = request.getRequestDispatcher("/"+getTimeoutPage());//"/xxx.jsp");
                    rd.forward(request,response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
   /*
 HttpSession session = request.getSession();
            if(session==null){
                RequestDispatcher rd = context.getRequestDispatcher("/demo/inner.jsp");
                rd.forward(request,response);
            }
    */
    /**
     * session shouldn't be checked for some pages. For example: for timeout page..
     * Since we're redirecting to timeout page from this filter,
     * if we don't disable session control for it, filter will again redirect to it
     * and this will be result with an infinite loop...
     */
    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
        String requestPath = httpServletRequest.getRequestURI();

        boolean controlRequired = !StringUtils.contains(requestPath, getTimeoutPage());

        return controlRequired;
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
        return sessionInValid;
    }

    public void destroy() {
    }

    public String getTimeoutPage() {
        return timeoutPage;
    }

    public void setTimeoutPage(String timeoutPage) {
        this.timeoutPage = timeoutPage;
    }
    /*
<filter>
 <filter-name>PhrSessionTimeoutFilter</filter-name>
 <filter-class>at.srfg.kmt.ehealth.phrs.PhrSessionTimeoutFilter</filter-class>
</filter>

<filter-mapping>
 <filter-name>PhrSessionTimeoutFilter</filter-name>
     <url-pattern>*.xhtml</url-pattern>
</filter-mapping>
    */

}
