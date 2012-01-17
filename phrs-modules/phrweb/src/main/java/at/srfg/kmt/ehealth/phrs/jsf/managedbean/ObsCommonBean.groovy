package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.annotation.PostConstruct
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import javax.faces.context.FacesContext

import org.primefaces.model.chart.CartesianChartModel

import at.srfg.kmt.ehealth.phrs.jsf.utils.HealthyCharts
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils


// collector observable lists or maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html


@ManagedBean(name="codlBean")
@RequestScoped
public class ObsCommonBean extends FaceBaseBean  {

	Map vitalsMap
	//map trick that contains models setup via properties file etc
	Map vitalsModelMap

	List bodyWeightModel
	List bloodPressureModel
	List problemModel
	List physicalActivityModel

	CartesianChartModel chartBodyWeight
	CartesianChartModel chartBloodPressure

	public ObsCommonBean() {
		super();//required!!

		// setPermittedActions performed by super class

		//System.out.println(this.getClass().toString()+' constructor');

		//assign domain class and create selected item
		/*
		 TODO replace and use more fine grained CRUD objects
		 or... set parameter in view object to select the appropriate object
		 domainClazz = ProfileActivityDailyLiving.class
		 setSelected(domainClazz.newInstance())	
		 initVocabularies(domainClazz,getLanguage())
		 */

		//TODO
		//String resourceType = showResourceTypeAttribute()
		//resourceType = resourceType ? resourceType :
		//initResources(resourceType)

		vitalsMap = loadVitalsMap()
		initObservations()

	}

	public void initObservations(){
		try {
			if(userService){
				bodyWeightModel 	= userService.getResources(ObsVitalsBodyWeight.class)
				chartBodyWeight 	= HealthyCharts.createChartBodyWeight(bodyWeightModel)

				bloodPressureModel = userService.getResources(ObsVitalsBloodPressure.class)
				chartBloodPressure 	= HealthyCharts.createChartBloodPressure(bloodPressureModel)
			}
			
			if( ! bodyWeightModel) bodyWeightModel= []
			if( ! bloodPressureModel) bloodPressureModel= []
			
			if( ! chartBodyWeight) chartBodyWeight=new CartesianChartModel()
			if( ! chartBloodPressure) chartBloodPressure = new CartesianChartModel()
			
		} catch (Exception e){
			println('init Obs codl'+e)
		}

	}
	public CartesianChartModel getChart01(){
		return chartBodyWeight
	}
	public CartesianChartModel getChart02(){
		return chartBloodPressure
	}
	public  CartesianChartModel getTestBp(){
		return HealthyCharts.testBloodPressureChart()
	}

	public  CartesianChartModel getTestBw(){
		return HealthyCharts.testBodyWeightChart()
	}

	//TODO
	public String showResourceTypeAttribute(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map requestMap = context.getExternalContext().getRequestParameterMap()
		println('requestMap='+ requestMap)
		return requestMap && requestMap.containsKey('infotype') ?
		requestMap.get('infotype') : 'overview'

	}
	//TODO
	private void initResources(String resourceType){
		switch (resourceType){
			case  'overview':
				domainClazz = new Object()
				setSelected(domainClazz.newInstance())
				break

			default :
				break
		}
	}
	/**
	 * Might this be called after the subclass's PostContruct or before ??
	 */
	@PostConstruct
	public void init(){
		//System.out.println('Postcontruct  subclass'+this.getClass().toString()+' uidThisView='+uidThisView+' locale='+FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	/*
	 @Override
	 public void loadModelMain(){
	 }
	 */
	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		//this.setAllowCreate(false);
		//this.setAllowDelete(false);
	}

	/**
	 * Called for a new form, local changes. Also for testing
	 * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
	 */
	@Override
	public void modifyNewResource(){
		super.modifyNewResource();

	}


	/*
	 vitalsMap.bw
	 vitalsMap.bw_bmi
	 vitalsMap.bw_date
	 vitalsMap.bh
	 vitalsMap.bp_systolic
	 vitalsMap.bp_diastolic
	 vitalsMap.bp_heartrate
	 vitalsMap.bp_date
	 */
	public Map loadVitalsMap(){
		Map map = [:]
		if(userService && userService.getHealthProfileFilterUri()){
			map = HealthyUtils.getCurrentVitals(userService.getHealthProfileFilterUri())
		}

		if(!map) map = [:]
		return map
	}



}

