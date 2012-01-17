package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.io.Serializable;
import java.util.Map;

import org.primefaces.model.chart.PieChartModel;

public class PieChartModelExt extends PieChartModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8855326418387121809L;

	public PieChartModelExt() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PieChartModelExt(Map<String, Number> data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

}
