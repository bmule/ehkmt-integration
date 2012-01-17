package at.srfg.kmt.ehealth.phrs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * To support JSF
 * http://java_rzanner.blogspot.com/2006/09/usage-of-constant-values
 * -in-jsp-code.html
 * 
 */
/*
 * put the Map into an application scoped attribute using a
 * ServletContextListener:
 * 
 * ... public void contextInitialized(ServletContextEvent event) {
 * ServletContext sc = event.getServletContext();
 * sc.setAttribute(ConstantMapper.ID_CONSTANT_MAPPER,
 * Constants.getNameToValueMap()); } ...
 * 
 * 
 * This way I could access all my constants in the JSPs via JSP expression
 * language:
 * 
 * ... <c:if test="${globalView.linieTab eq ConstantsMap.TAB_DETAILS}"> ...
 * 
 * 
 * Since the expression language allows you to reference mapped values via
 * Map['key'] or Map.key, I could use the "dot" syntax to mimic a normal static
 * access to the Constants classes fields.
 * 
 * In JSFs expression language you additionally have to use the prefix
 * "applicationScope.", since the normal VariableResolver implementations search
 * for a managed bean by default - which my Map clearly isn't...
 * 
 * <h:outputLabel value="#{applicationScope.ConstantsMap.USER_ID_GUEST}">
 * 
 * 
 * ConstantsMap.XXX or applicationScope.ConstantsMap.XXX
 */
public final class ConstantMapper {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConstantMapper.class);
	/** ID for reference from the ServletContext. */
	public static final String ID_CONSTANT_MAPPER = "ConstantsMap";

	// other constants follow, e. g. for the example below:
	public static final String TAB_DETAILS = "tabDetails";

	/** "Cache" holding all public static fields by it's field name */
	private static Map nameToValueMap = createNameToValueMap();

	/**
	 * Puts all public static fields via introspection into the resulting Map.
	 * Uses the name of the field as key to reference it's in the Map.
	 * 
	 * @return a Map of field names to field values of all public static fields
	 *         of this class
	 */
	private static Map createNameToValueMap() {
		// System.out.println("ConstantMapper createNameToValueMap START");
		// LOGGER.info("ConstantMapper createNameToValueMap START");
		Map result = new HashMap();
		try {

			Field[] publicFields = PhrsConstants.class.getFields();

			for (int i = 0; i < publicFields.length; i++) {
				Field field = publicFields[i];
				String name = field.getName();
				try {
					result.put(name, field.get(null));
				} catch (Exception e) {
					System.err
							.println("Initialization of Constants class failed!");
					e.printStackTrace(System.err);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("ConstantMapper createNameToValueMap START");
			LOGGER.error("ConstantMapper createNameToValueMap", e);
		}
		/*
		 * if(result != null){
		 * //System.out.println("ConstantMapper createNameToValueMap END, results="
		 * +result.size());
		 * LOGGER.info("ConstantMapper createNameToValueMap END results="
		 * +result.size()); } else { System.out.println(
		 * "ConstantMapper createNameToValueMap END, results= NULL this is an error"
		 * ); LOGGER.info(
		 * "ConstantMapper createNameToValueMap END,  results= NULL this is an error"
		 * ); }
		 */

		return result;
	}

	/**
	 * Gets the Map of all public static fields. The field name is used as key
	 * for the value of the field itself.
	 * 
	 * @return the Map of all public static fields
	 */
	public static Map getNameToValueMap() {
		return nameToValueMap;
	}
}
