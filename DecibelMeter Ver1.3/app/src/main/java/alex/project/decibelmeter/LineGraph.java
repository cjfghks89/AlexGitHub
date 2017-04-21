package alex.project.decibelmeter;

/**
 * Created by Alex on 2017-03-12.
 */

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class LineGraph {

    private GraphicalView view;

    private TimeSeries dataset = new TimeSeries("Decibel level");
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    public XYSeriesRenderer renderer = new XYSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();


    public LineGraph() {
        //Add single dataset to multiple dataset
        mDataset.addSeries(dataset);

        //customization time for line 1
        //renderer.setColor(Color.BLACK);
        renderer.setColor(Color.rgb(239, 61, 75));
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(false);
        renderer.setChartValuesTextSize(20);
        //renderer.setDisplayChartValues(true);
        renderer.setDisplayChartValuesDistance(1);
        renderer.setDisplayBoundingPoints(true);
        renderer.setGradientEnabled(true);





        //Enable Zoom

        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("0.25초당 측정된 소음량(dB)");
        mRenderer.setAxisTitleTextSize(20);
        mRenderer.setXLabels(5);
        mRenderer.setYLabels(10);
        mRenderer.setLabelsTextSize(20);
        //mRenderer.setYTitle("데시벨 레벨");
        mRenderer.setShowGridX(true);
        mRenderer.setGridColor(Color.GRAY);



        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);



        //Add single renderer to multiple renderer
        mRenderer.addSeriesRenderer(renderer);
    }

    public GraphicalView getView(Context context) {
        view = ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }

    public void addNewPoints(Point p) {
        dataset.add(p.getX(), p.getY());
    }
}
