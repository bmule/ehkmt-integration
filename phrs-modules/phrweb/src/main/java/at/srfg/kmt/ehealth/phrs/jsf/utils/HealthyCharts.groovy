package at.srfg.kmt.ehealth.phrs.jsf.utils

import java.util.Date
import java.util.List

import org.joda.time.DateTime
import org.primefaces.model.chart.CartesianChartModel

import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils

class HealthyCharts {


	public static  CartesianChartModel testBloodPressureChart(){
		List<ObsVitalsBloodPressure> list = []
		ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure()
		
		bp1.systolic = 120
		bp1.diastolic = 80
		bp1.heartRate = 67
		bp1.beginDate= new Date()
		list.add(bp1)
		
		
		DateTime dateTime = new DateTime(bp1.beginDate.getTime())
		dateTime.minusDays(4)
		
		ObsVitalsBloodPressure bp2 = new ObsVitalsBloodPressure()
		bp2.systolic = 180
		bp2.diastolic = 94
		bp2.heartRate = 88
		
		dateTime.minusDays(1)
		bp2.beginDate= new Date(dateTime.getMillis())
		list.add(bp2)
	
		
		ObsVitalsBloodPressure bp3 = new ObsVitalsBloodPressure()
		bp3.systolic = 200
		bp3.diastolic = 94
		bp3.heartRate = 96
		
		dateTime.minusDays(6)
		bp3.beginDate= new Date(dateTime.getMillis())
		list.add(bp3)
		
		CartesianChartModel model = createChartBloodPressure(list)
		return model
	}

	public static  CartesianChartModel testBodyWeightChart(){
		List<ObsVitalsBodyWeight> list = []
		ObsVitalsBodyWeight bp1 = new ObsVitalsBodyWeight()
		
		bp1.bodyWeight = 87
		bp1.bodyHeight = 175
		bp1.bmi = 26.2d
		bp1.beginDate= new Date()
		SeriesItem series = new SeriesItem(bp1.beginDate.toString())
	
		
		list.add(bp1)
		
		
		DateTime dateTime = new DateTime(bp1.beginDate.getTime())
		dateTime.minusDays(4)
		
		ObsVitalsBodyWeight bp2 = new ObsVitalsBodyWeight()
		bp2.bodyWeight = 92
		bp2.bodyHeight = 175
		bp2.bmi = 32.2d
		dateTime.minusDays(1)
		bp2.beginDate= new Date(dateTime.getMillis())
		
		list.add(bp2)
	
		
		ObsVitalsBodyWeight bp3 = new ObsVitalsBodyWeight()
		bp3.bodyWeight = 123
		bp3.bodyHeight = 175
		bp3.bmi = 31.3d
		
		dateTime.minusDays(6)
		bp3.beginDate= new Date(dateTime.getMillis())
		list.add(bp3)
		
		//	CartesianChartModel model = createCartChartModel(['Body Weight','Diastoic','Heart Rate'],list)
		CartesianChartModel model = createChartBodyWeight(list)
		return model
	}
	
	public static CartesianChartModel createCartChartModel( List<String> seriesLabels, 
		List<SeriesItem> items){

		CartesianChartModel cartesianModel = new CartesianChartModel();
		
		if(items && seriesLabels){
			for (int i:seriesLabels.size() - 1){
				ChartSeriesExt chart = new ChartSeriesExt();
				chart.setLabel(seriesLabels.get(i));

				//TODO createSeries formatter... sort by date, fix dates and format them
				int counter=-1

				items.each() { SeriesItem item ->
					try{
						counter++
						//Category (x axis)
						String category		= item.category
						for(int col:item.columnValues.size()-1){
							chart.set(category, item.getColumnValues().get(col))//category, value
						}
					} catch (Exception e){
						println(''+e)
					}
				}

				cartesianModel.addSeries(chart);
			}
		}
		return cartesianModel
	}

	public static CartesianChartModel createChartBloodPressure(List<ObsVitalsBloodPressure> list){
		CartesianChartModel cartesianModel = new CartesianChartModel();

		ChartSeriesExt chart_1 = new ChartSeriesExt();
		ChartSeriesExt chart_2 = new ChartSeriesExt();
		ChartSeriesExt chart_3 = new ChartSeriesExt();

		chart_1.setLabel("Blood Pressure (mmg/ml) Systolic");
		chart_2.setLabel("Blood Pressure (mmg/ml) Diastolic");
		chart_3.setLabel("Heart Rate (bpm)");

		//List listBw = getUserService().
		//chart.set("2004", 120);

		if(list){
			//TODO createSeries formatter... sort by date, fix dates and format them
			int counter=-1
			list.each() { ObsVitalsBloodPressure item ->
				try{
					counter++

					//Category (x axis)
					def theDate 		= item.getBeginDate() ? item.getBeginDate() : item.getCreateDate()
					String category		= HealthyUtils.formatDate(theDate,'','yyyy-MM') //"${formatDate(theDate,'','yyyy-MM')} note Gstring needs 'def' category "$counter _ $formatDate"

					// 1st line
					Double value_1 		= item.getSystolic() ? item.getSystolic() : 0d
					
					chart_1.set(category, value_1)//category, value

					// 2nd line
					Double value_2 	  = item.getDiastolic() ? item.getDiastolic() : 0d
					chart_2.set(category, value_2)//category, value

					// 3rd line
					Double value_3 	  = item.getHeartRate() ? item.getHeartRate() : 0d
					chart_3.set(category, value_3)//category, value
				

				} catch (Exception e){
					println(''+e)
				}
			}

		}
		
		cartesianModel.addSeries(chart_1);
		cartesianModel.addSeries(chart_2);
		cartesianModel.addSeries(chart_3);
		
		/*
	titleX="Time"  titleY="Body Weight(kg)
		 */
		return cartesianModel
	}

	public static ChartSeriesExt fillChartTime(Date startDate,int daysBack){

		DateTime start = new DateTime(startDate.getTime());
		DateTime  previousDay
		ChartSeriesExt chart = new ChartSeriesExt();
		int counter=-1
		for(int i:daysBack){
			counter++
			previousDay = start.minusDays(1)
			String category		= "$counter" //Gstring needs 'def'
			chart.setLabel(category)
			chart.setId(counter)
		}
		return chart

	}

	public static void fillChartTime(ChartSeriesExt series,int daysBack, def timeValue,String label){

		DateTime start = new DateTime();
		DateTime  end
		ChartSeriesExt chart = new ChartSeriesExt();
		int counter=-1


		for(int i:daysBack){
			counter++
			end = start.minusDays(1)
			String category		= "$counter" //Gstring needs 'def'
			chart.setLabel(category)
			chart.setId(counter)
		}

	}

	public static CartesianChartModel createChartBodyWeight(List<ObsVitalsBodyWeight> list){
		CartesianChartModel cartesianModel = new CartesianChartModel();

		ChartSeriesExt chart_1 = new ChartSeriesExt();
		ChartSeriesExt chart_2 = new ChartSeriesExt();

		chart_1.setLabel("Body Weight (kg)");
		//chart_2.setLabel("BMI");

		//List listBw = getUserService().
		//chart.set("2004", 120);


		if(list){
			//TODO createSeries formatter... sort by date, fix dates and format them
			int counter=-1
			list.each() { ObsVitalsBodyWeight item ->
				try{
					counter++
					//Category (x axis)
					def theDate 		= item.getBeginDate() ? item.getBeginDate() : item.getCreateDate()
					String category		= HealthyUtils.formatDate(theDate,'','yyyy-MM') //"${formatDate(theDate,'','yyyy-MM')} note Gstring needs 'def' category "$counter _ $formatDate"

					// 1st line
					Double value_1 		= item.getBodyWeight() ? item.getBodyWeight() : 0d
					chart_1.set(category, value_1)//category, value

					// 2nd line
					//Double value_2 	  = item.getBmi() ? item.getBmi() : 0d
					//chart_2.set(category, value_2)//category, value

				} catch (Exception e){
					println('error'+e)
				}
			}

		}

		cartesianModel.addSeries(chart_1);
		//cartesianModel.addSeries(chart_2);
		
		return cartesianModel
	}

}
