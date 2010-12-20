package tr.com.srdc.icardea.consenteditor.controller;

public interface ConsentManagerInterface {
	boolean validate(String healthcareActorID, /* such as ROLECODE:DOCTOR, ROLECODE:NURSE,etc. */ 
			String patientID,
			String resource /* such as RESOURCECODE:MEDICATION, RESOURCECODE:PROBLEM,etc. */,
			String action /* such as READ, WRITE, UPDATE */);
}
