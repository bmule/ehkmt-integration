/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : PCC09InputProcessorBean.java 
 * Encoding : UTF-8
 * Date : Apr 11, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09.impl;

import at.srfg.kmt.ehealth.phrs.pcc09.api.PCC09InputProcessor;
import at.srfg.kmt.ehealth.phrs.pcc09.api.PCC09InputProcessorException;



/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class PCC09InputProcessorBean implements PCC09InputProcessor {

    @Override
    public void process(String careProvisionCode, String careProvisionReason, String careRecordTimePeriodBegin, String careRecordTimePeriodEnd, String clinicalStatementTimePeriodBegin, String clinicalStatementTimePeriodEnd, String includeCarePlanAttachment, String maximumHistoryStatements, String patientAdministrativeGender, String patientBirthTime, String patientID, String patientName, String patientSurname) throws PCC09InputProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
