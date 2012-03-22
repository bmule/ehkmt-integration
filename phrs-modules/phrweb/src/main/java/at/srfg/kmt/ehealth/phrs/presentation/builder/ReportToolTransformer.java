package at.srfg.kmt.ehealth.phrs.presentation.builder;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorPhrItem;
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsRecord;
import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue;
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyEnhancer;

import java.util.ArrayList;
import java.util.List;

public class ReportToolTransformer {
    private ReportTool reportTool;

    public ReportToolTransformer() {
        reportTool = new ReportTool();
    }

    /**
     * @param med
     * @return
     */
    public List<MonitorPhrItem> tranformResource(List phrResources) {
        List<MonitorPhrItem> list = new ArrayList<MonitorPhrItem>();

        if (phrResources != null && !phrResources.isEmpty()) {

            for (Object obj : phrResources) {

                MonitorPhrItem item = null;
                if (obj instanceof MedicationTreatment) {
                    item = toMonitorPhrItem((MedicationTreatment) obj);
                } else if(obj instanceof ObsRecord){
                   item= toMonitorPhrItem((ObsRecord) obj);
                }
                if (item != null) list.add(item);

            }


        }
        //  MonitorPhrItem item= new MonitorPhrItem();


        return list;
    }

    /**
     * Medication transformation
     *
     * @param resource
     * @return
     */
    public MonitorPhrItem toMonitorPhrItem(MedicationTreatment resource) {
        if(resource == null) return null;

        MonitorPhrItem item = new MonitorPhrItem();
        item.setResourceType(Constants.PHRS_MEDICATION_CLASS);
        //I18 code to lookup on UI
        //String labelCode=VocabularyEnhancer.determineLabelCode(item.getResourceType());
        item.setDescriptionLabelCode("resource_type_medication");

        item.setOwnerUri(resource.getOwnerUri());
        item.setPID(resource.getPID());

        item.setLabel(resource.getLabel());
        item.setStatus(resource.getStatus());
        item.setStatusStandard(resource.getStatusStandard());

        List<ModelLabelValue> props = new ArrayList<ModelLabelValue>();
        props.add(new ModelLabelValue("drugCode", resource.getProductCode()));
        //props.add(new ModelLabelValue("drugCode", resource.getProductCode()));

        if (resource.getTreatmentMatrix() != null) {
            if (resource.getTreatmentMatrix().getDosage() != null)
                props.add(new ModelLabelValue("dosage", resource.getTreatmentMatrix().getDosage().toString()));
            else
                props.add(new ModelLabelValue("dosage", "0"));

            if (resource.getTreatmentMatrix().getDosageQuantity() != null)
                props.add(new ModelLabelValue("dosageQuantity", resource.getTreatmentMatrix().getDosageQuantity()));
            else
                props.add(new ModelLabelValue("dosageQuantity", "0"));

            if (resource.getTreatmentMatrix().getDosageUnits() != null)
                props.add(new ModelLabelValue("dosageUnits", resource.getTreatmentMatrix().getDosageUnits()));
            else
                props.add(new ModelLabelValue("dosageUnits", ""));
        }

        item.setSummary(reportTool.formatTextToString(props, "default"));

        return item;
    }

    public static String determineLabelCode(String code) {
        String labelCode = null;

        return labelCode;
    }

    //item.setDescriptionLabelCode("phr_medication");
    public MonitorPhrItem toMonitorPhrItem(ObsRecord resource) {
        if(resource == null) return null;
        MonitorPhrItem item = new MonitorPhrItem();

        item.setDescriptionLabelCode(
                VocabularyEnhancer.determineLabelCode(resource.getCode()));

        item.setOwnerUri(resource.getOwnerUri());
        item.setPID(resource.getPID());

        item.setLabel(resource.getLabel());
        item.setStatus(resource.getStatus());
        item.setStatusStandard(resource.getStatusStandard());


        return item;
    }
}
