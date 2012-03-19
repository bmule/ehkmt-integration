package at.srfg.kmt.ehealth.phrs.model.baseform



import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity
import at.srfg.kmt.ehealth.phrs.Constants
/**
 * Alternative means to write multiple records from a form
 *
 */

@Entity
public class ObsRecord extends BasePhrsModel{

    public final static String CODE_SYSTOLIC = Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE
    public final static String CODE_IDATOLIC = Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE

    public final static String CODE_BODY_WEIGHT = Constants.ICARDEA_INSTANCE_BODY_WEIGHT
    public final static String CODE_BODY_HEIGHT = Constants.ICARDEA_INSTANCE_BODY_HEIGHT

    public final static String UNITS_MM_HG = Constants.MM_HG
    public final static String UNITS_KILOGRAM = Constants.KILOGRAM
    public final static String UNITS_CENTIMETER = Constants.CENTIMETER


	//use code and type from BasePhrsModel
    //code = Interop code
    //type =
    //beginDate - effective time

    //Use origin



	String value
    String units
    
    Map<String,String> attrs

	
	public ObsRecord(){
		super();
        attrs = new HashMap<String,String>()
        
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_ACTION_EXECUTED_EXPLICIT
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
    /**
     *
     * @param transactionId  - to group form elements from one transaction
     * @param phrsClass
     * @param interopCodeUri
     * @param value
     * @param units
     */
    public ObsRecord(String transactionId,String phrsClass, String interopCodeUri, String value, String units){
        this();
        this.transactionId = transactionId
        this.type=phrsClass
        this.code=interopCodeUri
        this.value=value
        this.units=units

    }
    /**
     * The transactionId is created automatically
     * @param phrsClass
     * @param interopCodeUri
     * @param value
     * @param units
     */
    public ObsRecord(String phrsClass, String interopCodeUri, String value, String units){
        this();
        transactionId=UUID.randomUUID().toString()
        this.type=phrsClass
        this.code=interopCodeUri
        this.value=value
        this.units=units

    }
	
}
