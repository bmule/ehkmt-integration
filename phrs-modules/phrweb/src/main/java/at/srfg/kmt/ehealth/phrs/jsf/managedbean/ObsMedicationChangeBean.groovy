package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.util.List

import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService


@ManagedBean(name="odlmed")
@RequestScoped
public class ObsMedicationChangeBean extends FaceBaseBean  {
    // collector observable lists or maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html
    //CartesianChartModel chartModel
    String infoType

    public ObsMedicationChangeBean() {
        super();//required!!
        // setPermittedActions performed by super class
        infoType = findTypeParam()
        domainClazz =   MedicationTreatment.class
        setSelected(domainClazz.newInstance())

        initVocabularies(domainClazz,getLanguage())

        try {
            loadModelMain()
        } catch (Exception e){
            println('ObsMedicationChangeBean loadModelMain Exception '+e)
        }
        /*
        try{
        initChartModel()
        } catch (Exception e){
        println('ObsActivityBean initChartModel Exception '+e)
        }*/

    }
    //		 modelMain= getLoadAllHistory()

    //problem with coding of status...could get interop code PhrsConstants.STATUS_COMPLETE etc
    @Override
    public void loadModelMain(){
        /**
         * Import new interop messages as new domain objects
         */
        try {
            importInteropMessages(Constants.PHRS_MEDICATION_CLASS)
        } catch (Exception e){
            LOGGER.error('Medication building list importInteropMessages',e)
        }
        /**
         * load model
         */
        super.loadModelMain()
        //get parameter
        if( ! infoType) infoType = findTypeParam()

        switch(infoType){
            case 'inactive':
                internalModelList = filterResultsByStatus('medicationSummary_medicationStatus_false',true,internalModelList)
            //medicationSummary_medicationStatus_false
            break
            //
            //let history default to ALL records inactive and active
            //case 'history':
            //internalModelList=crudReadHistory()
            //break
            case 'active':
                internalModelList = filterResultsByStatus('medicationSummary_medicationStatus_true',true,internalModelList)
			
            break
            default:
            //No filter,show inactive and active internalModelList = internalModelList                          
            //medicationSummary_medicationStatus_true PhrsConstants.STATUS_COMPLETE
            break

        }
    }

    /**
     * Setup request attribute 'view' that controls view widget rendering
     * and the list model e.g. active list of meds, inactive list, history of changes
     * @return
     */
    public String findTypeParam(){

        String temp =  findRequestParam('view')
        switch(temp){
            case 'inactive':

            break

            case 'active':

            break

            case 'history':

            break

            default:
            temp = 'active'
            break

        }
        return temp
    }
    /*
    public void initChartModel(){
    try{
    chartModel = HealthyCharts.createChartBloodPressure(this.getModelMain())
    } catch (Exception e){
    println('initChartModel Exception '+e)
    }
    if( ! chartModel) chartModel = new CartesianChartModel()
    }
    public  CartesianChartModel getTestBp(){
    return HealthyCharts.testBloodPressureChart()
    }*/

    @Override
    public void setPermittedActions(){
        super.setPermittedActions();
        //TODO Issue: fired by super constructor before known in domain controller!
        infoType = findTypeParam()
        switch(infoType){
            case 'inactive':
            this.setAllowDelete(false);
            setModify(AuthorizationService.MODIFY_YES)

            setAllowCreate(false)
            setAllowView(true)
            break
            case 'active':
            this.setAllowDelete(false);
            setModify(AuthorizationService.MODIFY_YES)

            setAllowCreate(true)
            setAllowView(true)
            break
            case 'history':
            this.setAllowDelete(false);
            setModify(AuthorizationService.MODIFY_NO)

            setAllowCreate(false)
            setAllowView(true)
            break

            default:
            //default is active
            this.setAllowDelete(false);
            setModify(AuthorizationService.MODIFY_YES)

            setAllowCreate(true)
            setAllowView(true)

            break

        }

    }

    /**
     * Called for a new form, local changes. Also for testing
     * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
     */
    @Override
    public void modifyNewResource(){
        super.modifyNewResource();
        if(selected){
            selected.beginDate =new Date()
            selected.status='medicationSummary_medicationStatus_true'
            selected.treatmentMatrix.dosageUnits=Constants.TABLET
            selected.treatmentMatrix.dosageInterval='http://www.icardea.at/phrs/instances/EveryDay'
            selected.treatmentMatrix.dosageTimeOfDay='http://www.icardea.at/phrs/instances/NotSpecified'
            selected.reasonCode='http://www.icardea.at/phrs/instances/NoSpecialTreatment'
        }

    }


    @Override
    public void storeModifyFirst(){
        super.storeModifyFirst()

        if(selected){

        }
    }
        
    @Override
    public void formEdit(){
        super.formEdit();
        if(selected && selected.treatmentMatrix && selected.treatmentMatrix.dosageUnits){
            //MedicationTreatment domain = (MedicationTreatment)selected
            //fix existing test data
            if( selected.treatmentMatrix.dosageUnits == 'http://www.icardea.at/phrs/instances/pills'){
                
                selected.treatmentMatrix.dosageUnits=Constants.TABLET
            }
        }
    }
}

