package ru.kor_inc.andy;

import android.annotation.*;
import android.app.*;
import android.app.DatePickerDialog.*;
import android.app.TimePickerDialog.*;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import android.view.View.OnClickListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DataEdit extends Activity implements OnClickListener{
	EditText dateField;
	EditText timeField;
	EditText numericField;
	EditText textField;
	String date;
	String time;
	String numeric;
	String telo;
	String id;
	ContentValues cv = new ContentValues();
	Button btnSave;
	Button buttonSpinner;
	DbTool db = new DbTool();
	int DIALOG_DATE = 1;
	int DIALOG_TIME = 2;
	boolean isItStartedFirstTime = true;
	private static final String TAG = "kor_ka Log";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_data_grabber_dialog);
		
		Intent intentTable = getIntent();
		String currentTable = intentTable.getStringExtra("currentTable");
		
		boolean isItStartedFirstTime = true;
		dateField = (EditText) findViewById(R.id.dateField);
		timeField = (EditText) findViewById(R.id.timeField);
		numericField = (EditText) findViewById(R.id.numericField);
		textField = (EditText) findViewById(R.id.textField);
		btnSave = (Button) findViewById(R.id.btnSave);
		buttonSpinner = (Button) findViewById(R.id.buttonSpinner);
		
		btnSave.setOnClickListener(this);
		buttonSpinner.setOnClickListener(this);
		dateField.setOnClickListener(this);
		timeField.setOnClickListener(this);

		btnSave.getBackground().setColorFilter(0xff66cc66 ,PorterDuff.Mode.MULTIPLY);
		
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 1);
		Cursor c = db.getCursor(currentTable, this);
		c.moveToPosition(position);
		
		int dateColIndex = c.getColumnIndex("date");
		int timeColIndex = c.getColumnIndex("time");
		int numColIndex = c.getColumnIndex("numeric");
		int teloColIndex = c.getColumnIndex("telo");
		date = c.getString(dateColIndex);
		time = c.getString(timeColIndex);
		numeric = c.getString(numColIndex);
		telo = c.getString(teloColIndex);
		
		dateField.setText(date);
		timeField.setText(time);
		numericField.setText(numeric);
		textField.setText(telo);
	}
	
	@Override
	public void onClick(View v) {
		Intent intentTable = getIntent();
		String currentTable = intentTable.getStringExtra("currentTable");
		switch (v.getId())
		{
			case R.id.btnSave:
				cv.put("date", dateField.getText().toString());
				cv.put("time", timeField.getText().toString());
				cv.put("numeric", numericField.getText().toString());
				cv.put("telo", textField.getText().toString());
			
				Intent intent = getIntent();
				int position = intent.getIntExtra("position", 1);
				Cursor c = db.getCursor(currentTable, this);
				c.moveToPosition(position);
		
				int idColIndex = c.getColumnIndex("_id");
				id = c.getString(idColIndex);
				
				db.update(currentTable, this, id, cv);
			
				Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
				finish();
				break;
				
				case R.id.buttonSpinner:
					Intent intentSpinner = new Intent(this, SpinnerActivity.class);
					intentSpinner.putExtra("currentTable", currentTable);
					this.startActivityForResult(intentSpinner, 1);
				break;
				
				case R.id.dateField:
					showDialog(DIALOG_DATE);
				break;
				
				case R.id.timeField:
					showDialog(DIALOG_TIME);
				break;
		}
		
		
	}

	protected Dialog onCreateDialog(int id){ 
		Integer myDay =Integer.parseInt(dateField.getText().toString().substring(0, 2));
		int myIntDay= myDay.intValue();
		Integer myMonth =Integer.parseInt(dateField.getText().toString().substring(3, 5));
		int myIntMonth= myMonth.intValue();
		Integer myYear =Integer.parseInt(dateField.getText().toString().substring(6, 10));
		int myIntYear= myYear.intValue();
		
		Integer myHour =Integer.parseInt(timeField.getText().toString().substring(0, 2));
		int myIntHour= myHour.intValue();
		Integer myMinute =Integer.parseInt(timeField.getText().toString().substring(3, 5));
		int myIntMinute= myMinute.intValue();
		
		if (id == DIALOG_DATE)
		{ 
			DatePickerDialog dpd = new DatePickerDialog(this, myCallBack, myIntYear, myIntMonth - 1, myIntDay);
			return dpd;
		}else if(id==DIALOG_TIME){
			TimePickerDialog tpd = new TimePickerDialog(this, myTimeCallBack, myIntHour, myIntMinute, true); 
			return tpd;
		}
		return super.onCreateDialog(id);
	}
	OnDateSetListener myCallBack = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int month, int day) { 
		month++;
			String dayPrint = new String(""+day);
			if(dayPrint.length()<2){
			dayPrint="0"+dayPrint;
			}
			String monthPrint = new String(""+month);
			if(monthPrint.length()<2){
				monthPrint="0"+monthPrint;
			}	
		dateField.setText(dayPrint+"-"+monthPrint+"-"+year);
		}
		
	};
	
	OnTimeSetListener myTimeCallBack = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hour, int minute)
		{
			String hourPrint = new String(""+hour);
			if(hourPrint.length()<2){
				hourPrint="0"+hourPrint;
			}
			String minutePrint = new String(""+minute);
			if(minutePrint.length()<2){
				minutePrint="0"+minutePrint;
			}
			timeField.setText(hourPrint+":"+minutePrint);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {return;}
	    String text = data.getStringExtra("text");
	    textField.setText(text);
	}
	
}
