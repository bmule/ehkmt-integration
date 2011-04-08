/*
 * Project :iCardea
 * File : Pcc9QueryFactory.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.bind.JAXBException;
import org.hl7.v3.QUPCIN043100UV01;


/**
 * Used to build PCC09 queries based on several kinds of input.<br>
 * All the queries builded with this class are test purposed.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class Pcc9QueryFactory {

    private final static String FILE = "pcc09query.properties";

    /**
     * Builds a PCC09 query based on a properties file.
     * The name for the properties file is defined with the <code>FILE</code> 
     * constants.
     * 
     * @see #FILE
     */
    static QUPCIN043100UV01 buildQuery() throws IOException, JAXBException {
        final ClassLoader classLoader = Pcc9QueryFactory.class.getClassLoader();
        final InputStream stream = classLoader.getResourceAsStream(FILE);
        if (stream == null) {
            final String msg = String.format("The file [%s] can not be found in classpath.", FILE);
            throw new FileNotFoundException(msg);
        }

        final Properties properties = new Properties();
        properties.load(stream);

        final String careProvisionCode =
                getProperty(properties, "careProvisionCode");
        final String careProvisionReason =
                getProperty(properties, "careProvisionReason");
        final String careRecordTimePeriodBegin =
                getProperty(properties, "careRecordTimePeriodBegin");
        final String careRecordTimePeriodEnd =
                getProperty(properties, "careRecordTimePeriodEnd");
        final String clinicalStatementTimePeriodBegin =
                getProperty(properties, "clinicalStatementTimePeriodBegin");
        final String clinicalStatementTimePeriodEnd =
                getProperty(properties, "clinicalStatementTimePeriodEnd");
        final String includeCarePlanAttachment =
                getProperty(properties, "includeCarePlanAttachment");
        final String maximumHistoryStatements =
                getProperty(properties, "maximumHistoryStatements");
        final String patientAdministrativeGender =
                getProperty(properties, "patientAdministrativeGender");
        final String patientBirthTime = 
                getProperty(properties, "patientBirthTime");
        final String patientID = getProperty(properties, "patientID");
        final String patientName = getProperty(properties, "patientName");
        final String patientSurname = getProperty(properties, "patientSurname");
        
        final QUPCIN043100UV01 query = 
                QueryFactory.buildQUPCIN043100UV01(careProvisionCode, 
                careProvisionReason, 
                careRecordTimePeriodBegin, 
                careRecordTimePeriodEnd, 
                clinicalStatementTimePeriodBegin, 
                clinicalStatementTimePeriodEnd, 
                includeCarePlanAttachment, 
                maximumHistoryStatements, 
                patientAdministrativeGender, 
                patientBirthTime, 
                patientID, 
                patientName, 
                patientSurname);

        return query;
    }

    private static String getProperty(Properties properties, String propertyName) {

        final String result = properties.getProperty(propertyName);
        if (result == null) {
            final String msg =
                    String.format("The property with name [%s] can not be found", propertyName);
            throw new NullPointerException(msg);
        }

        return result;

    }
}
