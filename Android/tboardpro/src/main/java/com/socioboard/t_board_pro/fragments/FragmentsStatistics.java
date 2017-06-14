package com.socioboard.t_board_pro.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.socioboard.t_board_pro.util.EntityModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class FragmentsStatistics extends Fragment {

	View rootview;

	TboardproLocalData tboardproLocalData;

	ArrayList<EntityModel> arralistEntityModels;

	String timeTitle = "";

	View mChart;

	long maxLimit = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootview = inflater.inflate(R.layout.fragment_stats, container, false);

		tboardproLocalData = new TboardproLocalData(getActivity());

		arralistEntityModels = tboardproLocalData
				.getAllUsersEntity(MainSingleTon.currentUserModel.getUserid());

		// arralistEntityModels.clear();
		arralistEntityModels.add(0, new EntityModel(0, 0, 0, 0));
		// arralistEntityModels.add(new EntityModel(20, 30, 40, 50));
		// arralistEntityModels.add(new EntityModel(66, 30, 80, 150));
		// arralistEntityModels.add(new EntityModel(45, 30, 42, 180));

		openChart2();

		return rootview;
 	}

	private void openChart2() {

		long[] followers = new long[arralistEntityModels.size()];
		long[] followings = new long[arralistEntityModels.size()];
		long[] mutualFollowers = new long[arralistEntityModels.size()];
		long[] nonFollowers = new long[arralistEntityModels.size()];

		System.out.println("arralistEntityModels " + arralistEntityModels);

		for (int i = 0; i < arralistEntityModels.size(); i++) {

			followers[i] = arralistEntityModels.get(i).getFollowers();

			followings[i] = arralistEntityModels.get(i).getFollowings();

			mutualFollowers[i] = arralistEntityModels.get(i).getMutuals();

			nonFollowers[i] = arralistEntityModels.get(i).getNonfollwers();

			if (arralistEntityModels.get(i).getFollowers() > maxLimit) {

				maxLimit = arralistEntityModels.get(i).getFollowers();

			}
			
			if (arralistEntityModels.get(i).getFollowings() > maxLimit) {

				maxLimit = arralistEntityModels.get(i).getFollowings();

			}
			
			if (arralistEntityModels.get(i).getMutuals() > maxLimit) {

				maxLimit = arralistEntityModels.get(i).getMutuals();

			}
			
			if (arralistEntityModels.get(i).getNonfollwers() > maxLimit) {

				maxLimit = arralistEntityModels.get(i).getNonfollwers();

			}

		}

		maxLimit = maxLimit + 10;

		System.out.println("maxLimit = " + maxLimit);

		XYSeries xySeriesfollower = new XYSeries("Followers");
		XYSeries xySeriesfollowings = new XYSeries("Followings");
		XYSeries xySeriesmutualFollowers = new XYSeries("MutualFollowers");
		XYSeries xySeriesnonFollowers = new XYSeries("NonFollowers");

		// Adding data to Income and Expense Series
		for (int i = 0; i < arralistEntityModels.size(); i++) {

			xySeriesfollower.add(i, followers[i]);
			xySeriesfollowings.add(i, followings[i]);
			xySeriesmutualFollowers.add(i, mutualFollowers[i]);
			xySeriesnonFollowers.add(i, nonFollowers[i]);

		}

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		dataset.addSeries(xySeriesfollower);
		dataset.addSeries(xySeriesfollowings);
		dataset.addSeries(xySeriesmutualFollowers);
		dataset.addSeries(xySeriesnonFollowers);

		// Creating XYSeriesRenderer to customize incomeSeries
		XYSeriesRenderer rendererfollowers = new XYSeriesRenderer();
		rendererfollowers.setColor(Color.CYAN); // color of the graph set to
												// cyan
		rendererfollowers.setFillPoints(true);
		rendererfollowers.setLineWidth(2f);
		rendererfollowers.setDisplayChartValues(true);
		// setting chart value distance
		rendererfollowers.setDisplayChartValuesDistance(10);
		// setting line graph point style to circle
		rendererfollowers.setPointStyle(PointStyle.POINT);
		// setting stroke of the line chart to solid
		rendererfollowers.setStroke(BasicStroke.SOLID);

		// Creating XYSeriesRenderer to customize expenseSeries
		XYSeriesRenderer rendererFollowings = new XYSeriesRenderer();
		rendererFollowings.setColor(Color.GREEN);
		rendererFollowings.setFillPoints(true);
		rendererFollowings.setLineWidth(2f);
		rendererFollowings.setDisplayChartValues(true);
		// setting line graph point style to circle
		rendererFollowings.setPointStyle(PointStyle.POINT);
		// setting stroke of the line chart to solid
		rendererFollowings.setStroke(BasicStroke.SOLID);

		XYSeriesRenderer rendererMutualFollowers = new XYSeriesRenderer();

		rendererMutualFollowers.setColor(Color.YELLOW); // color of the graph
														// set to cyan
		rendererMutualFollowers.setFillPoints(true);
		rendererMutualFollowers.setLineWidth(2f);
		rendererMutualFollowers.setDisplayChartValues(true);
		// setting chart value distance
		rendererMutualFollowers.setDisplayChartValuesDistance(10);
		// setting line graph point style to circle
		rendererMutualFollowers.setPointStyle(PointStyle.POINT);
		// setting stroke of the line chart to solid
		rendererMutualFollowers.setStroke(BasicStroke.SOLID);

		// Creating XYSeriesRenderer to customize expenseSeries
		XYSeriesRenderer rendererNonFollowers = new XYSeriesRenderer();
		rendererNonFollowers.setColor(Color.RED);
		rendererNonFollowers.setFillPoints(true);
		rendererNonFollowers.setLineWidth(2f);
		rendererNonFollowers.setDisplayChartValues(true);
		// setting line graph point style to circle
		rendererNonFollowers.setPointStyle(PointStyle.POINT);
		// setting stroke of the line chart to solid
		rendererNonFollowers.setStroke(BasicStroke.SOLID);

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
		multiRenderer.setChartTitle("Followers Analysis");
		multiRenderer.setXTitle("Year 2015");
		multiRenderer.setYTitle("Followers List");

		/***
		 * Customizing graphs
		 */

		// setting text size of the title

		multiRenderer.setChartTitleTextSize(28);
		// setting text size of the axis title

		multiRenderer.setAxisTitleTextSize(20);
		// setting text size of the graph lable

		multiRenderer.setLabelsTextSize(12);
		// setting zoom buttons visiblity
		// multiRenderer.setZoomButtonsVisible(true);
		// setting pan enablity which uses graph to move on both axis
		// multiRenderer.setPanEnabled(false, false);
		// setting click false on graph

		multiRenderer.setClickEnabled(false);
		// setting zoom to false on both axis
		// multiRenderer.setZoomEnabled(true, true);
		// setting lines to display on y axis

		multiRenderer.setShowGridY(true);
		// setting lines to display on x axis

		multiRenderer.setShowGridX(true);
		// setting legend to fit the screen size

		multiRenderer.setFitLegend(true);
		// setting displaying line on grid

		multiRenderer.setShowGrid(true);
		// setting zoom to false

		multiRenderer.setZoomEnabled(false);
		// setting external zoom functions to false

		multiRenderer.setExternalZoomEnabled(false);
		// setting displaying lines on graph to be formatted(like using
		// graphics)

		multiRenderer.setAntialiasing(false);
		// setting to in scroll to false

		multiRenderer.setInScroll(false);
		// setting to set legend height of the graph

		multiRenderer.setLegendHeight(30);
		// setting x axis label align

		multiRenderer.setXLabelsAlign(Align.CENTER);
		// setting y axis label to align

		multiRenderer.setYLabelsAlign(Align.LEFT);
		// setting text style

		multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
		// setting no of values to display in y axis

		multiRenderer.setYLabels(10);
		// setting y axis max value, Since i'm using static values inside the
		// graph so i'm setting y max value to 4000.
		// if you use dynamic values then get the max y value and set here

		multiRenderer.setYAxisMax(maxLimit);
		// setting used to move the graph on xaxiz to .5 to the right
		// multiRenderer.setXAxisMin(0.5);
		// setting used to move the graph on xaxiz to .5 to the right

		multiRenderer.setXAxisMax(arralistEntityModels.size());
		// setting bar size or space between two bars

		multiRenderer.setBarSpacing(0.5);
		// Setting background color of the graph to transparent

		multiRenderer.setBackgroundColor(Color.BLACK);
		// Setting margin color of the graph to transparent

		multiRenderer.setMarginsColor(getResources().getColor(
				android.R.color.transparent));

		multiRenderer.setApplyBackgroundColor(true);

		multiRenderer.setScale(2f);

		// setting x axis point size
		multiRenderer.setPointSize(4f);

		// setting the margin size for the graph in the order top, left, bottom,
		// right

		multiRenderer.setMargins(new int[] { 30, 30, 30, 30 });

		DateFormat dateFormat = new SimpleDateFormat("MM/dd");

		for (int i = 0; i < arralistEntityModels.size(); i++) {

			Date date = new Date(arralistEntityModels.get(i).getMillis());

			System.out.println(dateFormat.format(date)); // 2014/08/06

			if (i == 0) {

				multiRenderer.addXTextLabel(i, "" + 0);

			} else {

				multiRenderer.addXTextLabel(i, dateFormat.format(date));

			}
		}

		// Adding incomeRenderer and expenseRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to
		// multipleRenderer
		// should be same

		multiRenderer.addSeriesRenderer(rendererfollowers);
		multiRenderer.addSeriesRenderer(rendererFollowings);
		multiRenderer.addSeriesRenderer(rendererMutualFollowers);
		multiRenderer.addSeriesRenderer(rendererNonFollowers);
		multiRenderer.setPanEnabled(false, false);

		multiRenderer.setYAxisMin(0);
		multiRenderer.setXAxisMin(0);

		// this part is used to display graph on the xml

		LinearLayout chartContainer = (LinearLayout) rootview
				.findViewById(R.id.chart);

		// remove any views before u paint the chart

		chartContainer.removeAllViews();

		// drawing bar chart

		System.out.println("dataset " + dataset.toString());

		System.out.println("multiRenderer " + multiRenderer.toString());

		mChart = ChartFactory.getLineChartView(
				FragmentsStatistics.this.getActivity(), dataset, multiRenderer);

		mChart.setClickable(false);

		// adding the view to the linearlayout

		chartContainer.addView(mChart);

		// Creating an intent to plot bar chart using dataset and
		// multipleRenderer

	}

}