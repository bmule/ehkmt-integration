package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileRisk
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ManagedBean(name="riskBean")
@RequestScoped
public class ProfileRiskfactorBean extends FaceBaseBean  {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProfileRiskfactorBean.class);


	//public String buttonId;

	/**
	 * Sets the domainClass from the subclass
	 * Creates a new 'selected' emtpy resource
	 * TODO handle this in PostConstruct but depends on whether we trust this. Some issues with older Tomcats and other containers
	 * if they run the super class Postconstruct method at all...
	 */
	public ProfileRiskfactorBean() {
		super();//required!!
		// setPermittedActions performed by super clas
		//System.out.println(this.getClass().toString()+' contructor');

		//assign domain class and create selected item
		domainClazz = ProfileRisk.class
		selected = domainClazz.newInstance()
		initVocabularies(domainClazz,getLanguage())

		//println('constructor selectedItem '+selectedItem ? 'not null' : 'null')
		//println('constructor selectedItem code ='+selectedItem && selectedItem.code ? 'not null' : 'null')

		loadModelMain()
	}


	@Override
	public void store(){
		//TODO do we allow user to delete a risk or reset to inactive?? The default is PhrsConstants.SELECTION_NO_ANSWER
		if(selected) {
			selected.status='riskfactor_isActiveStatus'
			//<h:inputHidden id='riskBean.selected.status' value='riskfactor_isActiveStatus'/>
		}
		super.store()
	}
	@Override
	public void loadModelMain(){
		if(getUserService()){
			internalModelList = getUserService().getResources(getDomainClazz());
			//this UI shows all possibilities from the vocabulary...including those objects the user actually saved.
			internalModelList = fillModelUnselectedData(internalModelList,getDomainClazz(), PhrsConstants.TAG_RISK_FACTORS)
		}
	}
	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		this.setAllowCreate(false);
		this.setAllowDelete(false);
		//no need for row view button
		setAllowView(false)//removes view row button

	}


	/**
	 * Called for a new form, local changes. Also for testing
	 * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
	 */
	@Override
	public void modifyNewResource(){
		super.modifyNewResource();


	}




	Collection check1

	public Collection getCheck1(){
		println('getcheck1')
		check1= ['a', 'b', 'c']
		return check1;

	}
	public void setCheck1(Collection check1){
		println('setcheck1')
		this.check1 = check1
	}
	public Collection getCheckSelect(){
		return ['a', 'b', 'xxx']
	}
	public Collection getCheckVocab(){
		//Collection lvs1 = vocabMap.TAG_RISK_SMOKING_TYPES
		Collection lvs = vocabMap.get('TAG_RISK_SMOKING_TYPES')
	
		Collection out= []
		lvs.each(){lv ->
			out.add(lv.id)
		}
	
		return out
	}

}

