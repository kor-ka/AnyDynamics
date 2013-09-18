package ru.kor_inc.andy;

import android.app.*;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.iguanaui.controls.*;
import com.iguanaui.controls.axes.*;
import com.iguanaui.controls.valuecategory.*;
import com.iguanaui.graphics.*;
import java.text.*;
import java.util.*;

public class ChartActivity extends Activity
{
DbTool db = new DbTool();
Button brik1;
Button brik2;
Button brik3;
Button brik4;
Button brik5;
Button brikOther;

Button bntChartLabel1;
Button bntChartLabel2;
Button bntChartLabel3;
Button bntChartLabel4;
Button bntChartLabel5;
Button bntChartLabelOther;

TextView textChartLabel1;
TextView textChartLabel2;
TextView textChartLabel3;
TextView textChartLabel4;
TextView textChartLabel5;
TextView textChartLabelOther;

//iguana...
private DataChart dataChart;
private List<String> categories=new ArrayList<String>();
private List<Float> column1=new ArrayList<Float>();
private List<Float> column2=new ArrayList<Float>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_chart);
		
		Intent intentTable = getIntent();
		String currentTable = intentTable.getStringExtra("currentTable");
		
		Button[] briks = new Button[]{
				brik1 = (Button) findViewById(R.id.brik1),
				brik2 = (Button) findViewById(R.id.brik2),
				brik3 = (Button) findViewById(R.id.brik3),
				brik4 = (Button) findViewById(R.id.brik4),
				brik5 = (Button) findViewById(R.id.brik5),
				brikOther = (Button) findViewById(R.id.brikOther)
		};
		
		Button[] labelsBtn = new Button[]{
				bntChartLabel1 = (Button) findViewById(R.id.bntChartLabel1),
				bntChartLabel2 = (Button) findViewById(R.id.bntChartLabel2),
				bntChartLabel3 = (Button) findViewById(R.id.bntChartLabel3),
				bntChartLabel4 = (Button) findViewById(R.id.bntChartLabel4),
				bntChartLabel5 = (Button) findViewById(R.id.bntChartLabel5),
				bntChartLabelOther = (Button) findViewById(R.id.bntChartLabelOther)
		};
		
		TextView[] labelsTxt = new TextView[]{
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabel1),
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabel2),
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabel3),
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabel4),
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabel5),
				textChartLabel1 = (TextView) findViewById(R.id.textChartLabelOther)
		};
		
		int[] labelWeights = new int[5];
						
		Cursor c = db.getCursorWithGroupByAndSum(currentTable, this, "telo", "numeric");
		
		if (c.moveToFirst()) {
        	int teloColIndex = c.getColumnIndex("telo");
			int numColIndex = c.getColumnIndex("numeric"); 
			int i = 0;
			int labelsSum=0;
			LinearLayout.LayoutParams llparamsBrik;
        	do { 
        		String telo = c.getString(teloColIndex);
        		int num = c.getInt(numColIndex);
    			Button brik = briks[i];
    			Button labelBtn = labelsBtn[i];
    			TextView labelTxt = labelsTxt[i];
				labelWeights[i] = num;
				llparamsBrik =  (LinearLayout.LayoutParams) brik.getLayoutParams();
				llparamsBrik.weight = num;
				labelTxt.setText(telo+":"+num);
				labelBtn.setVisibility(View.VISIBLE);
				labelsSum=labelsSum+num;
				i++;
        	} while (c.moveToNext() && i<=4);
			
		
			
			//выставляем название вес и процент пункту other
			int otherSum=0;
			if(c.getCount()>=6){
				do{
					int num = c.getInt(numColIndex);
					otherSum = otherSum + num;
				} while (c.moveToNext());
				Button brik = briks[5];
				Button labelBtn = labelsBtn[5];
    			TextView labelTxt = labelsTxt[5];
				labelsSum=labelsSum+otherSum;
				llparamsBrik =  (LinearLayout.LayoutParams) brik.getLayoutParams();
				llparamsBrik.weight = otherSum;
				double otherProsent = round(((double)otherSum)/((double)labelsSum)*100,2);
				labelTxt.setText("other:" + otherSum+"|"+otherProsent+"%");
				labelBtn.setVisibility(View.VISIBLE);
			}
			
			//добавим проценты для лейблов
			i=0;
			do{
				int labelWeight = labelWeights[i];
				TextView labelTxt = labelsTxt[i];
				double labelProsent= round(((double)labelWeight)/((double)labelsSum)*100,2);
				labelTxt.setText(labelTxt.getText().toString()+"|"+labelProsent+"%");
				i++;	
			}while(i<=4 && labelWeights[i]!=0);
			c.close();
        }else{
			c.close();
       }
	   
	   //iguana...
		createData();

        createChart();
//        createUI();

	}	
	
	 protected void onResume() {
		    super.onResume();
		    this.onCreate(null);
		  }
		  
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chartmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		Intent intentTable = getIntent();
		String currentTable = intentTable.getStringExtra("currentTable");
		switch (item.getItemId()){

			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				break;

			case R.id.boxlist:
				Intent intent = new Intent(this, Box.class);
				intent.putExtra("currentTable", currentTable);
				this.startActivity(intent);
				break;
		}
		return true;
	}
	
//iguana...
	private void createData()
	{
        Random random=new Random();
        float value1=25.0f;
        float value2=25.0f;

        for (int i=0; i < 5000; ++i)
		{
        	value1 += (random.nextFloat() - 0.5f);
        	value2 += (random.nextFloat() - 0.5f);

        	categories.add(Integer.toString(i));
        	column1.add(value1);
        	column2.add(value2);
        }		
	}

	private void updateData()
	{
        Random random=new Random();
        float value1=25.0f;
        float value2=25.0f;

        for (int i=0; i < categories.size(); ++i)
		{
        	value1 += (random.nextFloat() - 0.5f);
        	value2 += (random.nextFloat() - 0.5f);

        	column1.set(i, value1);
        	column2.set(i, value2);
        }
	}

	private void createChart()
	{
		dataChart = (DataChart)findViewById(R.id.dataChart);	// get the empty chart view from the activity layout
        dataChart.setHorizontalZoomEnabled(true);			// allow horizontal zooming
        dataChart.setVerticalZoomEnabled(false);			// don't allow vertical zooming

        // set up an x axis

        CategoryXAxis categoryAxis=new CategoryXAxis(); 
        categoryAxis.setDataSource(categories);				// tell the axis about the data table
        categoryAxis.setLabelFormatter(new CategoryAxis.LabelFormatter() {
				public String format(CategoryAxis axis, Object item)
				{
					return (String)item;						// return the axis item as a string
				}
			});
        dataChart.scales().add(categoryAxis);				// all axes must be added to the chart's scales collection

        // set up a y axis

        NumericYAxis valueAxis=new NumericYAxis();
        valueAxis.setMinimumValue(0.0f);					// the random data look much nicer with a fixed axis range
        valueAxis.setMaximumValue(50.0f);					// the random data look much nicer with a fixed axis range
        valueAxis.setLabelFormatter(new NumericAxis.LabelFormatter() {
				public String format(NumericAxis axis, float item, int precision)
				{
					if (precision != numberFormat.getMinimumFractionDigits())
					{
						numberFormat.setMinimumFractionDigits(precision);	// set the formatting precision
						numberFormat.setMaximumFractionDigits(precision);	// set the formatting precision
					}

					return numberFormat.format(item);						// return item as a string
				}

				final NumberFormat numberFormat=NumberFormat.getInstance();	// numeric formatter for axis values
			});
        dataChart.scales().add(valueAxis);					// all axes must be added to the chart's scales collection

        {
	        ValueCategorySeries series=new LineSeries();	
	        series.setCategoryAxis(categoryAxis);			// tell the series its x axis
	        series.setValueAxis(valueAxis);					// tell the series its y axis
	        series.setValueMember("");						// tell the series the data rows are the values
	        series.setDataSource(column1);					// tell the series the data table

			Brush seriesColor = new SolidColorBrush(Color.rgb(204,66,204));
			series.setBrush(seriesColor);
	        dataChart.series().add(series);					// add the series to the chart
        }

        {
	        ValueCategorySeries series=new LineSeries();	
	        series.setCategoryAxis(categoryAxis);			// tell the series its x axis
	        series.setValueAxis(valueAxis);					// tell the series its y axis
	        series.setValueMember("");						// tell the series the data rows are the values
	        series.setDataSource(column2);					// tell the series the data table
			Brush seriesColor = new SolidColorBrush(Color.rgb(99,204,99));
			series.setBrush(seriesColor);
	        dataChart.series().add(series);					// add the series to the chart
		
	   }
	}
/*	private void createUI() 
	{
        Button updateButton=(Button)findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0)
				{
					updateData();

					for (Series series: dataChart.series())
					{
						series.notifyDataReset();
					}
				}
			});
	}
*/

}
