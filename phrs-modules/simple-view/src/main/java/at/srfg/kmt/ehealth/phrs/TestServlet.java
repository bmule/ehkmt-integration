/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs;


import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mradules
 */
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {



        try {
            search();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        


        response.setContentType("text/html;charset=UTF-8");
        writeToResponse(response);
    }

    private void writeToResponse(HttpServletResponse response) throws IOException {


        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>  " + "here" + "</h1>");
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

    private void search() throws NamingException {
        final Context context = new InitialContext();

        final GroupManager lookup = (GroupManager) context.lookup("java:comp/env/GroupManagerBean/local");
        System.out.println("-->" + lookup);



        // phrs-ear-0.1-SNAPSHOT/GroupManagerBean/local

    }

}
