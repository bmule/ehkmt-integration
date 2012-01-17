package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import at.srfg.kmt.ehealth.phrs.model.baseform.ObsActivityPhysical
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService


@ManagedBean(name="odlact")
@RequestScoped
public class ObsActivityBean extends FaceBaseBean  {
// collector observable lists or maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html
	//CartesianChartModel chartModel
	
	
	public ObsActivityBean() {
		super();//required!!
		// setPermittedActions performed by super class

		domainClazz = ObsActivityPhysical.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())

		try {
			loadModelMain()

		} catch (Exception e){
			println('ObsActivityBean loadModelMain Exception '+e)
		}
		/*
		try{
			initChartModel()
		} catch (Exception e){
			println('ObsActivityBean initChartModel Exception '+e)
		}*/
		
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
		//this.setAllowCreate(false);
		this.setAllowDelete(false);
		setModify(AuthorizationService.MODIFY_YES)
	
		setAllowCreate(true)
		//no need for row view button
		setAllowView(true)
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
			selected.status='activityItem_isActiveStatusTrue'
		
		}

	}


	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()

		if(selected){

		}
	}
}

