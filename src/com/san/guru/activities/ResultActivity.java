package com.san.guru.activities;

import static com.san.guru.constant.AppContents.INTENT_DATA;
import static com.san.guru.constant.AppContents.INTENT_RESULT;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.util.DateTimeUtils;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;

public class ResultActivity extends Activity {

	private static final String STRING_FORMAT_PERCENT = "<b>Result</b><br><br> <font color='#254117'><b>%s</b> <br><small>%s out of %s</small>";

	private static final String STRING_FORMAT_PACE = "<b>Pace</b><br><br> <font color='#254117'><b>%s Q/Min</b> <br><small>Time:%s</small>";

	private TestResult result = null;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call() {
						Intent intent = new Intent(ResultActivity.this, ChooseSubjectActivity.class);
						ResultActivity.this.startActivity(intent);
						
						finish();
					}
				}, false);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values) {
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
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(20);
		renderer.setPanEnabled(true, true);
		renderer.setZoomEnabled(false, false);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}

		renderer.setXLabels(0);
		renderer.setYLabels(0);
		
		List<SubjectResult> subjectsResult = result.getSubjectResult();
		for (int i = 0; i < subjectsResult.size(); i++) {
			SubjectResult result = subjectsResult.get(i);
			
			renderer.addXTextLabel(i+1, String.format("%s (%s / %s)", 
										result.getName(), 
										result.getTotalCorrect(),
										result.getTotalQuestions()));
		}

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setLabelsColor(labelsColor);
		renderer.setZoomEnabled(false);
		renderer.setExternalZoomEnabled(true);
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false);
		renderer.setShowGrid(false);
		renderer.setShowAxes(false);

		renderer.setMarginsColor(Color.parseColor("#FBFBFC"));
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.WHITE);
		renderer.setMargins(new int[] {30, 70, 10, 0});


		renderer.setOrientation(Orientation.VERTICAL);

		renderer.setYLabelsAngle(90f);
		renderer.setBarSpacing(0.5);

		renderer.setLabelsColor(Color.BLACK);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		IntentData intentData = null;

		String totalTime = null;

		setContentView(R.layout.layout_result);

		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			intentData = (IntentData) intent.getExtras().get(INTENT_DATA);

			if (intentData != null) {
				result = (TestResult) intentData.getValue(INTENT_RESULT);
				totalTime = DateTimeUtils.getTimeString(result
						.getTotalTimeinSeconds());
			}
		}

		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		int height = dimension.heightPixels;
		int width = dimension.widthPixels;

		TextView view = (TextView) findViewById(R.id.txtVwScore);
		TextView view1 = (TextView) findViewById(R.id.txtVwPace);

		LayoutParams layoutParams = view.getLayoutParams();
		LayoutParams layoutParams1 = view1.getLayoutParams();

		layoutParams.height = height - (int) (height * 0.85);
		layoutParams.width = width - (int) (width * 0.60);

		layoutParams1.height = height - (int) (height * 0.85);
		layoutParams1.width = width - (int) (width * 0.60);

		view.setText(Html.fromHtml(String.format(STRING_FORMAT_PERCENT,
				result.getPercent(), result.getTotalCorrect(),
				result.getTotalQuestions())));
		view1.setText(Html.fromHtml(String.format(STRING_FORMAT_PACE,
				result.getPace(), totalTime)));

		// XYMultipleSeriesRenderer renderer = getTruitonBarRenderer();
		// renderer.setXAxisMax((int)(height - (int)height * 0.5));
		// renderer.setShowAxes(false);
		// myChartSettings(renderer);

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);

		String[] titles = new String[] { "CORRECT", "WRONG" };
		List<double[]> values = new ArrayList<double[]>();
		//values.add(new double[] { 100, 100, 100, 100, 100, 100, 100, 100, 100,	100, 100, 100 });
		//values.add(new double[] { 50, 30, 60, 100, 90, 10, 0, 65, 75.7, 90, 16,	14 });
		
		List<SubjectResult> subjectResult = result.getSubjectResult();
		double[] X = new double[subjectResult.size()];
		double[] Y = new double[subjectResult.size()];
		
		for (int i =0 ; i<subjectResult.size();i++) {
			Y[i] = subjectResult.get(i).getPercent();
		}
		for (int i =0 ; i<subjectResult.size();i++) {
			X[i] = 100;
		}
		
		values.add(X); values.add(Y);
		
		int[] colors = new int[] { Color.parseColor("#FE0000"),	Color.parseColor("#6E8B3D") };
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "Subject wise result", "", "", 0.5, 12.5, 0,	100, Color.GRAY, Color.LTGRAY);
		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		// renderer.setXLabels(12);
		// renderer.setYLabels(10);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanEnabled(true, false);
		renderer.setZoomEnabled(false, false);
		renderer.setZoomRate(1f);
		renderer.setBarSpacing(0.5f);

		GraphicalView barChartView = ChartFactory.getBarChartView(this,
				buildBarDataset(titles, values), renderer, Type.STACKED);
		layout.addView(barChartView, new LayoutParams(width,
				(int) (height - (int) height * 0.5)));

		Button quitTestButton = (Button) findViewById(R.id.butQuitTest);
		quitTestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						v.getContext());

				// set title
				alertDialogBuilder.setTitle("Exit");

				// set dialog message
				alertDialogBuilder
						.setMessage("Do you really want to exit?")
						.setCancelable(false)
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Do nothing.
									}
								})
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										finish();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
	}

}