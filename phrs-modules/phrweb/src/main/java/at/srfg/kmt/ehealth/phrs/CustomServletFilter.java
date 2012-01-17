package at.srfg.kmt.ehealth.phrs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
/*
<filter>
    <filter-name>theFilter</filter-name>
    <filter-class>org.demo.CustomServletFilter</filter-class>
    <init-param>
	<param-name>excludePatterns</param-name>
	<param-value>public/*</param-value>
    </init-param>
</filter>

<filter-mapping>
    <filter-name>theFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
 */
public class CustomServletFilter {
	private String excludePatterns;
	public void init(FilterConfig cfg) throws ServletException {
		this.excludePatterns = cfg.getInitParameter("excludePatterns");

	}

	public void doFilter(ServletRequest request,
			         ServletResponse response,
				 FilterChain chain) 
		throws IOException, ServletException {
		String url = request.toString();
		if (matchExcludePatterns(url)) {
		    chain.doFilter(request, response);
		    return;
		}
	
	}

	private boolean matchExcludePatterns(String url) {
		// TODO Auto-generated method stub
		return false;
	}
}
