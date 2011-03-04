/*
 * Project :iCardea
 * File : JsonFilePersistor.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.apache.commons.io.FileUtils.readFileToString;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;



/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class DynaBeanJsonUtil {
    
    /**
     * Don't let anybody to instantiate this class.
     */
    private DynaBeanJsonUtil() {
        // UNIMPLEMENTED
    }

    public static String toJSONStrig(DynaBean bean) {
        final JSONObject jsonObject = JSONObject.fromObject(bean);
        return jsonObject.toString();
    }

    public static void toJSONFile(DynaBean bean, File file) throws IOException {
        final JSONObject jsonObject = JSONObject.fromObject(bean);
        final FileWriter writer = new FileWriter(file);
        final String toWrite = jsonObject.toString();
        writer.write(toWrite);
    }

    public static void toJSONFile(DynaBean bean, String file) throws IOException {
        toJSONFile(bean, new File(file));
    }

    public static DynaBean fromJSONString(String jsonString) {
        final JSONObject jsonObject = JSONObject.fromObject(jsonString);
        final DynaBean result = (DynaBean) JSONObject.toBean(jsonObject);
        return result;
    }
    
    public static DynaBean fromJSONFile(DynaBean bean, File file) 
            throws IOException {
        final String toString = readFileToString(file);
        final DynaBean result = fromJSONString(toString);
        return result;
    }
}
