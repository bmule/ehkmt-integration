package at.srfg.kmt.ehealth.phrs.model.baseform;

import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportTool;

/**
 * 
 * Used for reports to assemble content
 * Model and functions to organize data
 * 
 */
public class MonitorPhrItem {

    private String ownerUri;
    private String PID;
    private String status;
    private String statusStandard;
    private String code;

    private String action;
    private String actionOutcome;

    private String label;
    /**
     * Constants.PHRS_MEDICATION_CLASS, etc
     */
    private String resourceType;

    private String summary;

    private ReportTool reportTool;

    public MonitorPhrItem(){
       reportTool = new ReportTool();

    }

    public String getOwnerUri() {
        return ownerUri;
    }

    public void setOwnerUri(String ownerUri) {
        this.ownerUri = ownerUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatusStandard() {
        return statusStandard;
    }

    public void setStatusStandard(String statusStandard) {
        this.statusStandard = statusStandard;
    }

    public ReportTool getReportTool() {
        return reportTool;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionOutcome() {
        return actionOutcome;
    }

    public void setActionOutcome(String actionOutcome) {
        this.actionOutcome = actionOutcome;
    }
}
