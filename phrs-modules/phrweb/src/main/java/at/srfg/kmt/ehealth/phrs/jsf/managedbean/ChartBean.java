package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.chart.CartesianChartModel;

import at.srfg.kmt.ehealth.phrs.jsf.utils.ChartSeriesExt;
import at.srfg.kmt.ehealth.phrs.jsf.utils.PieChartModelExt;

@ManagedBean(name="chartBean")
@RequestScoped
public class ChartBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3080767410022005385L;

	private CartesianChartModel cartesianModel;

   private PieChartModelExt pieModel;

	public ChartBean() {
        createCartesianModel();
        createPieModel();
	}

    public CartesianChartModel getCartesianModel() {
        return cartesianModel;
    }

    public PieChartModelExt getPieModel() {
        return pieModel;
    }

    private void createCartesianModel() {
        cartesianModel = new CartesianChartModel();

        ChartSeriesExt boys = new ChartSeriesExt();
        
        boys.setLabel("Boys");

        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);

        ChartSeriesExt girls = new ChartSeriesExt();
        girls.setLabel("Girls");

        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);

        cartesianModel.addSeries(boys);
        cartesianModel.addSeries(girls);
    }

    private void createPieModel() {
        pieModel = new PieChartModelExt();

        pieModel.set("Brand 1", 540);
        pieModel.set("Brand 2", 325);
        pieModel.set("Brand 3", 702);
        pieModel.set("Brand 4", 421);
    }
    
}
                    
                    
