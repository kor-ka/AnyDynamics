package ru.kor_inc.andy;


import android.app.*;
import android.content.*;
import android.database.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class SpinnerActivity extends Activity {
DbTool db = new DbTool();

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spinner);
		
		Intent intentTable = getIntent();
		String currentTable = intentTable.getStringExtra("currentTable");
		
		final Cursor c = db.getCursorWithGroupBy(currentTable, this, "telo");
		final int teloColIndex = c.getColumnIndex("telo");
		startManagingCursor(c);
		
		ListView listViewSpinnerActivity = (ListView) findViewById(R.id.listViewspinnerActivity);
		
		String[] from = new String[] {"telo"};
	    int[] to = new int[] {R.id.dataLayoutSpinnerTxt};
		
	
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.datalayoutspinner, c, from, to);
		listViewSpinnerActivity.setAdapter(adapter);
		
		final Context ctx = this;
		//push test
		listViewSpinnerActivity.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent();
				c.moveToPosition(position);
			    intent.putExtra("text", c.getString(teloColIndex));
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
	}

	

}
