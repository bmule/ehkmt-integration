package at.srfg.kmt.ehealth.phrs;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to load all the default settings
 * Static Constants are loaded to make available to the JSF application scope
 */
public class DefaultLoader implements ServletContextListener {

	
		
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.DefaultLoader</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DefaultLoader.class);

    /**
     * Loads all the default users when the application contexty starts.
     *
     * @param servletContextEvent the servlet context.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	
    	LOGGER.info("DefaultLoader ConstantMapper.ID_CONSTANT_MAPPER START");
    	
    	//System.out.println("DefaultLoader ConstantMapper.ID_CONSTANT_MAPPER START");
    	
    	try {
    		ServletContext sc = servletContextEvent.getServletContext();
        	sc.setAttribute(ConstantMapper.ID_CONSTANT_MAPPER, ConstantMapper.getNameToValueMap());
        } catch (Exception e) {
        	LOGGER.debug("DefaultLoader ConstantMapper.ID_CONSTANT_MAPPER error"+e);
        }
        //System.out.println("DefaultLoader ConstantMapper.ID_CONSTANT_MAPPER END");
        LOGGER.info("DefaultLoader ConstantMapper.ID_CONSTANT_MAPPER END");



    }

    /*
     * Builds a set of default users and registers them.

  
    private void loadDefUsers() throws ParseException {
        final Patient mopu = new Auser("test1");

*/

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // UNIMPLEMENETD
    }


    
}

