package at.srfg.kmt.ehealth.phrs.authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Provide a filter that will check and register a remote login from 
 * a portal or application under cross context setup 
 * AND integrate also with an OpenId authentication. 
 */

public class SimpleAuthenticationFilter implements Filter {
	  private FilterConfig config;

	  public void doFilter(ServletRequest req, ServletResponse resp,
	      FilterChain chain) throws IOException, ServletException {
		  
	    if (((HttpServletRequest) req).getSession().getAttribute(
	        AuthenticationBean.AUTH_KEY) == null) {
	      ((HttpServletResponse) resp).sendRedirect("../login_restricted.jsf");
	    } else {
	      chain.doFilter(req, resp);
	    }
	  }

	  public void init(FilterConfig config) throws ServletException {
	    this.config = config;
	  }

	  public void destroy() {
	    config = null;
	  }
	}

