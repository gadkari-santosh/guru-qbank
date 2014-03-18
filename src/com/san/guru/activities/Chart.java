package com.san.guru.activities;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;

public class Chart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] titles = new String[] { "CORRECT", "WRONG" };
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 });
        values.add(new double[] { 50, 30, 60, 100, 90, 10, 0, 65, 75.7, 90, 16, 14 });
        int[] colors = new int[] { Color.RED, Color.GREEN };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "Monthly sales in the last 2 years", "", "", 0.5, 12.5, 0, 100, Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
      //  renderer.setXLabels(12);
//        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false,false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        View view = ChartFactory.getBarChartView(this, buildBarDataset(titles, values), renderer, Type.STACKED); //Type.STACKED
        view.setBackgroundColor(Color.BLACK);
        setContentView(view);
    }

    protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
            int seriesLength = v.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(v[k]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setPanEnabled(true, true);
        renderer.setZoomEnabled(false,false);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        
        renderer.setXLabels(0);
        renderer.setYLabels(0);
        
        for (int i=0; i<12; i++) {
        	renderer.addXTextLabel(i, "santosh" +i);
        }
        
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setZoomEnabled(false);
        renderer.setExternalZoomEnabled(false);
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false,false);
        renderer.setShowLegend(false);
        renderer.setShowGrid(false);
        renderer.setShowAxes(false);
        
        renderer.setMarginsColor(Color.parseColor("#FBFBFC"));
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0,Color.BLACK);
        renderer.setMargins(new int[] {30, 70, 10, 0});
 
        
        renderer.setOrientation(Orientation.VERTICAL);
        
        renderer.setYLabelsAngle(90f);
		renderer.setBarSpacing(0.5);
		
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(10);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(100);
    }
}
