package com.san.guru.activities;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.guru.R;

public class ResultActivity extends Activity{

		private XYMultipleSeriesDataset getTruitonBarDataset() {
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			final int nr = 14;
			Random r = new Random(100);
			ArrayList<String> legendTitles = new ArrayList<String>();
			legendTitles.add("Subjects");
			for (int i = 0; i < 1; i++) {
				CategorySeries series = new CategorySeries(legendTitles.get(i));
				for (int k = 0; k < nr; k++) {
					series.add(r.nextInt(100) % 100);
				}
				dataset.addSeries(series.toXYSeries());
			}
			return dataset;
		}

		@SuppressLint("ResourceAsColor")
		public XYMultipleSeriesRenderer getTruitonBarRenderer() {
			XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
			renderer.setAxisTitleTextSize(16);
			renderer.setChartTitleTextSize(20);
			renderer.setLabelsTextSize(15);
			renderer.setLegendTextSize(15);
			renderer.setMargins(new int[] { 30, 40, 15, 0 });
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(R.color.DeepSkyBlue);
			renderer.addSeriesRenderer(r);
			return renderer;
		}

		private void myChartSettings(XYMultipleSeriesRenderer renderer) {
			renderer.setChartTitle("Subject wise result");
            renderer.setMarginsColor(Color.parseColor("#FBFBFC"));
            renderer.setXLabelsColor(Color.BLACK);
            renderer.setYLabelsColor(0,Color.BLACK);
            renderer.setMargins(new int[] { 20, 20, 130, 20 }); // top, left, bottom or 
            
            renderer.setApplyBackgroundColor(true);
            renderer.setBackgroundColor(Color.parseColor("#FBFBFC"));
            renderer.setShowLegend(false);
            
            renderer.setXLabels(0);
            renderer.setYLabels(0);
            
            renderer.setLabelsColor(Color.BLACK);
			renderer.setXAxisMin(0);
			renderer.setXAxisMax(10);
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(100);
			renderer.setOrientation(Orientation.VERTICAL);
			renderer.addXTextLabel(1, "XML (20/100)");
			renderer.addXTextLabel(2, "Core Java (1/5)");
			renderer.addXTextLabel(3, "Spring (20/30)");
			renderer.addXTextLabel(4, "Hibernate (9/8)");
			renderer.addXTextLabel(5, "XML (7/67)");
			renderer.addXTextLabel(6, "Design Pattern (23/45)");
			renderer.addXTextLabel(7, "Software Engg. (12/23)");
			renderer.addXTextLabel(8, "Hibernate (33/66)");
			renderer.addXTextLabel(9, "XML (20/100)");
			renderer.addXTextLabel(10, "Core Java (1/5)");
			renderer.addXTextLabel(11, "Spring (20/30)");
			renderer.addXTextLabel(12, "Hibernate (9/8)");
			renderer.addXTextLabel(13, "XML (7/67)");
			renderer.addXTextLabel(14, "Design Pattern (23/45)");
			renderer.setYLabelsAngle(90f);
			renderer.setBarSpacing(0.5);
			
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(3);
			renderer.setLabelFormat(format);

			renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(20);
			renderer.getSeriesRendererAt(0).setChartValuesFormat(format);
			renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
			renderer.getSeriesRendererAt(0).setChartValuesSpacing(20);
			renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
		    renderer.setLabelsTextSize(20);
		}

	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.layout_result);
		
		DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int height = dimension.heightPixels;
        int width = dimension.widthPixels;
        
        TextView view = (TextView) findViewById(R.id.txtVwScore);
        TextView view1 = (TextView) findViewById(R.id.txtVwPace);
		
		LayoutParams layoutParams = view.getLayoutParams();
		LayoutParams layoutParams1 = view1.getLayoutParams();
		
		layoutParams.height = height - (int)(height * 0.85);
		layoutParams.width = width - (int)(width * 0.60);
		
		layoutParams1.height = height - (int)(height * 0.85);
		layoutParams1.width = width - (int)(width * 0.60);
		
		view.setText(Html.fromHtml("<b>Result</b><br><br> <font color='#254117'><b>98%</b> <br><small>58 out of 62</small>"));
		view1.setText(Html.fromHtml("<b>Pace</b><br><br> <font color='red'><b>5</b> <br><small> Ques. per min.</small>"));

		XYMultipleSeriesRenderer renderer = getTruitonBarRenderer();
		renderer.setXAxisMax((int)(height - (int)height * 0.5));
		renderer.setShowAxes(false);
		myChartSettings(renderer);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		
		GraphicalView barChartView = ChartFactory.getBarChartView(this, getTruitonBarDataset(), renderer,Type.STACKED);
		layout.addView(barChartView, new LayoutParams( width, (int)(height - (int)height * 0.5) ));
                
	}
	
}