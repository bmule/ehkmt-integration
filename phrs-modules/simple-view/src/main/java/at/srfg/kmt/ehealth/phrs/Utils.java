/*
 * Project :iCardea
 * File : Utils.java
 * Encoding : UTF-8
 * Date : Mar 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class Utils {

    static void writeToResponseH1(HttpServletResponse response, String msg)
            throws IOException {

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
    
    static void writeToResponse(HttpServletResponse response, String msg)
            throws IOException {

        final PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println(msg);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
    static void println(String msg) {
        final StringBuffer result = new StringBuffer();
        result.append(msg);
        result.append("</br>");
        
        return;
        
    }
    
    
}
