/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs;


import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup;
import javax.naming.InitialContext;
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
 *
 * @author mradules
 */
public class TestServlet extends HttpServlet {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.TestServlet</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TestServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
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

    private void writeToResponse(HttpServletResponse response, String msg) throws IOException {


        try {
            final InitialContext ctx = new InitialContext();
            final Object lookup = ctx.lookup("java:comp/env/");
            logger.info(">>>" + lookup);
            logger.info(">>>" + ctx.getEnvironment());

        } catch (Exception exception) {
            exception.printStackTrace();
        }



        PrintWriter out = response.getWriter();
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
