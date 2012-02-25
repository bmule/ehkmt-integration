package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.jsf.utils.HealthyCharts
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyHeight
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import org.primefaces.model.chart.CartesianChartModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ManagedBean(name="odlbwBean")
@RequestScoped
public class ObsBodyWeightBean extends FaceBaseBean  {
    private final static Logger LOGGER = LoggerFactory.getLogger(ObsBodyWeightBean.class);

	CartesianChartModel chartModel

	public ObsBodyWeightBean() {
		super();//required!!
		// setPermittedActions performed by super class

		domainClazz = ObsVitalsBodyWeight.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())
		try {
			loadModelMain()

		} catch (Exception e){
			println('ObsBodyWeightBean loadModelMain Exception '+e)
		}
		
		try{
			initChartModel()
		} catch (Exception e){
			println('ObsBodyWeightBean initChartModel Exception '+e)
		}
		
	}
	public CartesianChartModel getChart01(){
		if(chartModel) return chartModel
		else return new CartesianChartModel()
	}
	public void initChartModel(){
		try{
			chartModel = HealthyCharts.createChartBodyWeight(this.getModelMain())
			
		} catch (Exception e){
			println('initChartModel Exception '+e)
		}
		if( ! chartModel) chartModel = new CartesianChartModel()
	}

	public CartesianChartModel getTestBw(){
		return HealthyCharts.testBodyWeightChart()
	}
	CartesianChartModel temp = new CartesianChartModel()
	public CartesianChartModel getTestNone(){
		return temp
	}

	@Override
	public void setPermittedActions(){
		super.setPermittedActions();

		setAllowDelete(false)
		setAllowCreate(true)
		
		setModify(AuthorizationService.MODIFY_YES)
		
	}

	/**
	 * Called for a new form, local changes. Also for testing
	 * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
	 */

	@Override
	public void modifyNewResource(){
		super.modifyNewResource();
		selected.beginDate =new Date()
		Double bheight = currentBodyHeight()
		if(!bheight) bheight = 0d
		if(selected) selected.bodyHeight= bheight

	}

	public Double currentBodyHeight(){
		Double bh=0d
		try{
			/*if(userService && userService.getHealthProfileFilterUri()){
				def temp= HealthyUtils.getCurrentBodyHeight(userService.getHealthProfileFilterUri())
				return temp
			}*/
			ObsVitalsBodyHeight bheightObj =userService.getResourceSingle(ObsVitalsBodyHeight.class, false)
			if(bheightObj){
				bh = bheightObj.getBodyHeight()
			}
		} catch (Exception e){
			LOGGER.error('getCurrentBodyHeight Exception '+e)
		}
		return bh
	}

	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()
		try{
			if(selected){
				ObsVitalsBodyHeight bheightObj =userService.getResourceSingle(ObsVitalsBodyHeight.class, true)
				if(selected.bodyHeight && selected.bodyHeight > 0 ){
					boolean update=false
					
					if( ! bheightObj.getBodyHeight() ) {
						update = true
					} else if(selected.bodyHeight && selected.bodyHeight > 0){
						
						if( bheightObj.getBodyHeight() !=  selected.bodyHeight){
							update = true
						}
					}
					if(update){
						bheightObj.setBodyHeight(selected.bodyHeight)
						userService.crudSaveResource(bheightObj)
					}
				}
			}
		} catch (Exception e){
			println('storeModifyFirst bodyHeightException '+e)
		}
		try{
			if(selected){
				//store BMI
				Double val = HealthyUtils.computeBMIMetric(selected.bodyWeight, selected.bodyHeight)
				if(!val) val= 0d
				selected.bmi= val
			}
		} catch (Exception e){
			println('storeModifyFirst Exception '+e)
		}
	}
}

