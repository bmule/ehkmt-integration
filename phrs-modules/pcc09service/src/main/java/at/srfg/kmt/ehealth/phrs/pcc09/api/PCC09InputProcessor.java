/*
 * Project :iCardea
 * File : PCC09InputProcessor.java 
 * Encoding : UTF-8
 * Date : Apr 11, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface PCC09InputProcessor {
    
    void process(String careProvisionCode,
            String careProvisionReason,
            String careRecordTimePeriodBegin,
            String careRecordTimePeriodEnd,
            String clinicalStatementTimePeriodBegin,
            String clinicalStatementTimePeriodEnd,
            String includeCarePlanAttachment,
            String maximumHistoryStatements,
            String patientAdministrativeGender,
            String patientBirthTime,
            String patientID,
            String patientName,
            String patientSurname) throws PCC09InputProcessorException;
    
}
