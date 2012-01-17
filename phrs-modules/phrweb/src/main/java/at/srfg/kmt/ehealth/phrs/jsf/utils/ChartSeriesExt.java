package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.io.Serializable;

import org.primefaces.component.chart.series.ChartSeries;

/**
 * 
 * Address the serialization exception when charts are  the h:form element is on the same page or wrapping the chart
 *
 */
public class ChartSeriesExt extends ChartSeries implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4457279907113268118L;

	public ChartSeriesExt() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChartSeriesExt(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

}
