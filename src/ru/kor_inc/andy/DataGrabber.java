package ru.kor_inc.andy;

//check
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.sql.*;
import java.text.*;
import android.content.SharedPreferences.Editor;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DataGrabber extends Activity implements OnClickListener{
EditText dateField;
EditText timeField;
EditText numericField;
EditText textField;
Button btnSave;
Button buttonSpinner;
DbTool dbWr = new DbTool();
boolean isItStartedFirstTime = true;
private static final String TAG = "kor_ka Log";
private DrawerLayout mDrawerLayout;
private ListView mDrawerList;
private ActionBarDrawerToggle mDrawerToggle;
private String[] tables;
SharedPreferences prefCurrentTable;
String currentTable;
String tabelLabel;

@Override
protected void onStart(){
	super.onStart();
	if(isItStartedFirstTime){
		
		Log.d(TAG, "888    d8P  .d88888b. 8888888b.         888    d8P        d8888 ");
		Log.d(TAG, "888   d8P  d88P' 'Y88b888   Y88b        888   d8P        d88888 ");
		Log.d(TAG, "888  d8P   888     888888    888        888  d8P        d88P888 ");
		Log.d(TAG, "888d88K    888     888888   d88P        888d88K        d88P 888 ");
		Log.d(TAG, "8888888b   888     8888888888P'  888888 8888888b      d88P  888 ");
		Log.d(TAG, "888  Y88b  888     888888 T88b          888  Y88b    d88P   888 ");
		Log.d(TAG, "888   Y88b Y88b. .d88P888  T88b         888   Y88b  d8888888888 ");
		Log.d(TAG, "888    Y88b 'Y88888P' 888   T88b        888    Y88bd88P     888 ");
		isItStartedFirstTime = false;
	}

}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prefCurrentTable = getPreferences(MODE_PRIVATE);
		Editor ed=prefCurrentTable.edit();
		ed.putString("firstAnyDynamicDataTable", "AnyDynamics|Расход");
		ed.putString("secondAnyDynamicDataTable", "AnyDynamics|Приход");
		ed.commit();
		currentTable = prefCurrentTable.getString("currentTable","firstAnyDynamicDataTable");
		
		getActionBar().setTitle(prefCurrentTable.getString(currentTable, ""));
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_grabber);
		
		dateField = (EditText) findViewById(R.id.dateField);
		timeField = (EditText) findViewById(R.id.timeField);
		numericField = (EditText) findViewById(R.id.numericField);
		textField = (EditText) findViewById(R.id.textField);
		btnSave = (Button) findViewById(R.id.btnSave);
		buttonSpinner = (Button) findViewById(R.id.buttonSpinner);
		
		btnSave.setOnClickListener(this);
		buttonSpinner.setOnClickListener(this);
		
		btnSave.getBackground().setColorFilter(0xff66cc66 ,PorterDuff.Mode.MULTIPLY);
		
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
		String time = stf.format(new Date(System.currentTimeMillis()));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date d=new Date(System.currentTimeMillis());
		d.setDate(d.getDate()+1);
	    String date = sdf.format(d);
		timeField.setSaveEnabled(false);
	    dateField.setText(date);
	    timeField.setText(time);
		
		//drower...
		tables = new String[]{"Расход","Приход"};
	
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, tables));
		
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	    // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

		
		
		
        
		
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
			this,                  /* host Activity */
			mDrawerLayout,         /* DrawerLayout object */
			R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
			R.string.drawer_open,  /* "open drawer" description for accessibility */
			R.string.drawer_close  /* "close drawer" description for accessibility */
		) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.datagrabbermenu, menu);
		return true;
	}
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    
	public boolean onOptionsItemSelected(MenuItem item){
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		switch (item.getItemId()){
			
			case R.id.boxlist:
				Intent intent = new Intent(this, Box.class);
				intent.putExtra("currentTable", currentTable);
				this.startActivity(intent);

			break;
			case R.id.chart:
				Intent intentChart = new Intent(this, ChartActivity.class);
				intentChart.putExtra("currentTable", currentTable);
				this.startActivity(intentChart);
			break;
		}
		return true;
	}
    
	private void selectItem(int position) {
        Editor ed=prefCurrentTable.edit();
		switch (position){
		case 0:
			currentTable = "firstAnyDynamicDataTable";				
				ed.putString("currentTable", currentTable);
			ed.commit();
			getActionBar().setTitle("AnyDynamics|Расход");
		break;
		
		case 1:
			currentTable = "secondAnyDynamicDataTable";
			ed.putString("currentTable",currentTable);
			ed.commit();
			getActionBar().setTitle("AnyDynamics|Приход");
		break;
		}
		
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnSave:
				dbWr.WriteToSql(currentTable,timeField.getText().toString(), dateField.getText().toString(), numericField.getText().toString(), textField.getText().toString(), this);
				dbWr.DataToLog(timeField.getText().toString(), dateField.getText().toString(), numericField.getText().toString(), textField.getText().toString());
				
				Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
				SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
				String time = stf.format(new Date(System.currentTimeMillis()));
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			    String date = sdf.format(new Date(System.currentTimeMillis()));
				
			    dateField.setText(date);
			    timeField.setText(time);
			    numericField.setText("");
				textField.setText("");
			break;
			
			case R.id.buttonSpinner:
				Intent intent = new Intent(this, SpinnerActivity.class);
				intent.putExtra("currentTable", currentTable);
				this.startActivityForResult(intent, 1);
			break;
		}
	}
	 @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (data == null) {return;}
	    String text = data.getStringExtra("text");
	    textField.setText(text);
	  }
}
