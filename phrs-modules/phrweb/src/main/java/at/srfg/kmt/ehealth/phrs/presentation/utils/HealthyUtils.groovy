package at.srfg.kmt.ehealth.phrs.presentation.utils

import java.io.Serializable
import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient

class HealthyUtils implements Serializable{

	public static final String DATE_PATTERN_LABEL_YEAR_MONTH_DAY ='yyyy-MM-dd'
        public static final String DATE_PATTERN_LABEL_YEAR_MONTH_DAY_TIME="yyyy-MM-dd HH:mm";
	/**
	 * Uses pattern yyyy-MM-dd
	 * @param dateTime
	 * @param defaultLabel
	 * @return
	 */
	public static String formatDate(DateTime dateTime,String defaultLabel){
		return formatTimeDate(dateTime,defaultLabel,DATE_PATTERN_LABEL_YEAR_MONTH_DAY)
	}
	/**
	*
	* @param date
	* @param defaultLabel
	* @param pattern 	e.g. yyyy-MM, yyyy-MM-dd ...
	* @return
	*/
   public  static String formatDate(Date date,String defaultLabel,String pattern){
	   if(!date) return defaultLabel
	   return formatTimeDate( new DateTime(date.getTime()),defaultLabel,pattern)
   }
	/**
	 * 
	 * @param dateTime
	 * @param defaultLabel
	 * @param pattern 	e.g. yyyy-MM, yyyy-MM-dd ...
	 * @return
	 */
	public static String formatTimeDate(DateTime dateTime,String defaultLabel,String pattern){
		if(!dateTime) return defaultLabel
		if(!pattern) pattern='yyyyMMdd'
		DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern)
		String formattedTime = dateTime.toString(dtf);
		return formattedTime;
	}

	/**
	* Uses pattern yyyy-MM-dd
	* @param date
	* @param defaultLabel
	* @return
	*/
   public  static String formatDate(Date date,String defaultLabel){
	   return formatDate(date,defaultLabel,DATE_PATTERN_LABEL_YEAR_MONTH_DAY)
   }
	public static Double computeBMI(Double weight, Double height, String measurementUnit) {

		computeBMIMetric(weight,height)
	}

	public static Double computeBMIMetric(Double weight, Double height) {
		Double bmi=0d;

		try {
			if(height && height > 0  && weight && weight > 0){
			} else {
				return 0d;
			}

			Float theHeight = new Double(height);
			if(height < 3){
				//meter
				//then they are using meters  not centimenters?
			} else {
				theHeight = theHeight / 100d;
			}

			bmi= weight / (theHeight * theHeight);
			bmi = bmi > 0 ? bmi.round(1) : bmi
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmi;
	}
	/**
	 * Find the current body weight
	 * @param theOwnerUri
	 * @return
	 */
	public static Double getCurrentBodyHeight(ObsVitalsBodyWeight bweight){

		if(bweight){
			Double bh  = bweight && bweight.bodyHeight   ?  bweight.bodyHeight: null
			if(bh) {
				return bh
			}
		}
		return new Double(0d)
	}
	/**
	 * 
	 * @param theOwnerUri
	 * @return
	 */
	public static Double getCurrentBodyHeight(String theOwnerUri){
		Double bh = new Double(0d)
		try{
			//only one field is retrieved into object bodyHeight !!!
			ObsVitalsBodyWeight bw= getCurrentBodyWeightFromAllEntries(theOwnerUri)

			bh  = getCurrentBodyHeight(bw)

		} catch (Exception e){
			println('getCurrentBodyHeight Exception '+e)
		}

		return bh
	}
	/**
	 * Look at recent, then next oldest BW record for height. When we need to look all old records
	 * @param theOwnerUri
	 * @return
	 */
	public static ObsVitalsBodyWeight getCurrentBodyWeightFromAllEntries(String theOwnerUri){
		ObsVitalsBodyWeight bweight_entry=null
		try{
			com.google.code.morphia.Datastore ds = PhrsStoreClient.getInstance().getPhrsDatastore()

			//.field('ownerUri').equal(theOwnerUri)

			//only one field is retrieved into object bodyHeight !!!
			List list = ds.createQuery(ObsVitalsBodyWeight.class).filter('ownerUri', theOwnerUri).order('-createDate').asList()
			if(list){
				list.each(){ ObsVitalsBodyWeight entry ->
					if(!bweight_entry && entry && entry.bodyHeight && entry.bodyHeight > 0d){
						bweight_entry = entry
					}
				}
			}

		} catch (Exception e){
			println('Exception '+e)
		}
		return bweight_entry
	}
	/**
	 * Look for last entry, not from profile
	 *  * @deprecated
	 * @param theOwnerUri
	 * @return
	 */
	public static ObsVitalsBodyWeight getCurrentWeightObjectFromAllEntries(String theOwnerUri){
		try{
			com.google.code.morphia.Datastore ds = PhrsStoreClient.getInstance().getPhrsDatastore()

			ObsVitalsBodyWeight entry = ds.createQuery(ObsVitalsBodyWeight.class).filter('ownerUri', theOwnerUri).order('-createDate').get();

			return entry

		} catch (Exception e){
			println('Exception '+e)
		}
		return null
	}
	/**
	 HealthyUtils.getCurrentVitals(theOwnerUri)
	 vitalsMap.bw
	 vitalsMap.bw_bmi
	 vitalsMap.bw_date
	 vitalsMap.bh
	 vitalsMap.bp_systolic
	 vitalsMap.bp_diastolic
	 vitalsMap.bp_heartrate
	 vitalsMap.bp_date
	 * 
	 * @param theOwnerUri
	 * @return
	 */
	public static Map<String,String> getCurrentVitals(String theOwnerUri){
		Map map = [:]
		try {

			ObsVitalsBloodPressure bpressure 	= getCurrentBloodPressureObjectFromAllEntries(theOwnerUri)
			ObsVitalsBodyWeight bweight 		= getCurrentBodyWeightFromAllEntries(theOwnerUri)
			
			ObsVitalsBodyWeight bhObject 		= getCurrentBodyWeightFromAllEntries(theOwnerUri)
			
			Double bh= bhObject ? bhObject.getBodyHeight() : 0d
			Double height=null
			if(bh) height = bh
			else if(bweight) height = bweight.getBodyHeight()

			if(bweight && bweight.getBodyWeight()) 		map.put('bw',bweight.getBodyWeight().toString())
			else  map.put('bw','0')

			if(bweight && bweight.getBmi()) 			map.put('bw_bmi',bweight.getBmi().toString())
			else map.put('bw_bmi','0')

			if(height) 									map.put('bh',height.toString())
			else map.put('bh','0')

			if(bweight && bweight.getBeginDate()) 	{	
				map.put('bw_date',formatDate(bweight.getBeginDate(),''))
			} else map.put('bw_date','')

			if(bpressure && bpressure.getSystolic()) 	map.put('bp_systolic',bpressure.getSystolic().toString())
			else map.put('bp_systolic','0')

			if(bpressure && bpressure.getDiastolic()) 	map.put('bp_diastolic',bpressure.getDiastolic().toString())
			else map.put('bp_diastolic','0')
			
			if(bpressure && bpressure.getHeartRate()) 	map.put('bp_heartrate',bpressure.getHeartRate().toString())
			else map.put('bp_heartrate','0')

			if(bpressure && bpressure.getBeginDate()) {
				map.put('bp_date',HealthyUtils.formatDate(bpressure.getBeginDate(),''))
			}
			else map.put('bp_date','')

		} catch (Exception e) {
			println('error'+e)
		}
		return map
	}

	public static ObsVitalsBloodPressure getCurrentBloodPressureObjectFromAllEntries(String theOwnerUri){
		try{
			com.google.code.morphia.Datastore ds = PhrsStoreClient.getInstance().getPhrsDatastore()

			//.field('ownerUri').equal(theOwnerUri)

			ObsVitalsBloodPressure entry = ds.createQuery(ObsVitalsBloodPressure.class).filter('ownerUri', theOwnerUri).order('-createDate').get();
			return entry

		} catch (Exception e){
			println('getCurrentBloodPressureObjectFromAllEntries Exception '+e)
		}
		return null
	}

	public static def getCurrentResourceObjectFromAllEntries(String theOwnerUri,Class domainClazz){
		try{
			com.google.code.morphia.Datastore ds = PhrsStoreClient.getInstance().getPhrsDatastore()
			def entry= ds.createQuery(domainClazz).filter('ownerUri', theOwnerUri).order('-createDate').get();
			return entry
		} catch (Exception e){
			println('Exception '+e)
		}
		return null
	}
	//http://www.zparacha.com/
	public static String lastChars(String inputString,
	int subStringLength){
		int length = inputString.length();
		if(length <= subStringLength){
			return inputString;
		}
		int startIndex = length-subStringLength;
		return inputString.substring(startIndex);
	}
}
