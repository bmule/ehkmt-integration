/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs;


import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is just an example - this class has only didactical purposes. <br>
 * To use this example use the following URL :
 * http://localhost:8080/simple-view/TestServlet?q=XXX where 
 * XXX is : 
 * <ul>
 * <li> addGroup - to add a group.
 * <li> removeAllGroups - removes all registered groups.
 * </ul>
 *
 * @author Mihai
 */
public class TestServlet extends HttpServlet {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.TestServlet</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TestServlet.class);

    /**
     * Runs on every HTTP GET method.
     *
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        final String q = request.getParameter("q");
        if (q == null || q.isEmpty()) {
            logger.debug("No q paramater set.");
            writeToResponse(response, "No q parameter.");
            return;
        }

        if ("addGroup".equalsIgnoreCase(q)) {
            addGroup(response);
            return;
        }
        
        if ("removeAllGroups".equalsIgnoreCase(q)) {
            removeAllGroups(response);
            return;
        }

        writeToResponse(response, "Parameter [" + q + "] not supported.");
    }

    private void writeToResponse(HttpServletResponse response, String msg) throws IOException {

        final PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>  " + msg + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    private void addGroup(HttpServletResponse response) throws IOException {
        final GroupManager groupManager;
        try {
            groupManager = JBossJNDILookup.lookupLocal(GroupManager.class);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            writeToResponse(response, "FAILURE ! - see the log");
            return;
        }

        final PhrGroup group = new PhrGroup("mihais");
        groupManager.addGroup(group);
        logger.debug("The Group [#0] was persited.", group);
        writeToResponse(response, group.toString());
    }

    private void removeAllGroups(HttpServletResponse response) throws IOException {
        final GroupManager groupManager;
        try {
            groupManager = JBossJNDILookup.lookupLocal(GroupManager.class);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            writeToResponse(response, "FAILURE ! - see the log");
            return;
        }
        
        groupManager.removeAllGroups();
    }

    /** 
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Test Servlet";
    }
}
