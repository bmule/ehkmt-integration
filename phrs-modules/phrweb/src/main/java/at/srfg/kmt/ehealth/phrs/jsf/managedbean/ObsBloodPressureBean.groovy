package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.jsf.utils.HealthyCharts
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import org.primefaces.model.chart.CartesianChartModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ManagedBean(name="odlbpBean")
@RequestScoped
public class ObsBloodPressureBean extends FaceBaseBean  {
    private final static Logger LOGGER = LoggerFactory.getLogger(ObsBloodPressureBean.class);

	CartesianChartModel chartModel

	public CartesianChartModel getChart02(){
		if(chartModel) return chartModel
		else return new CartesianChartModel()
	}
	public ObsBloodPressureBean() {
		super();//required!!
		// setPermittedActions performed by super class

		domainClazz = ObsVitalsBloodPressure.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())

		try {
			loadModelMain()

		} catch (Exception e){
			println('ObsBloodPressureBean loadModelMain Exception '+e)
		}

		try{
			initChartModel()
		} catch (Exception e){
			println('ObsBloodPressureBean initChartModel Exception '+e)
		}
	
	}

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
	}

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
		}

	}


	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()

		if(selected){

		}
	}
}

