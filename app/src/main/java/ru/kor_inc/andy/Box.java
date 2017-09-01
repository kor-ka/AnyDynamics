package ru.kor_inc.andy;



// experimen
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;


import android.app.*;
import android.content.*;
import android.database.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import android.widget.AdapterView.*;
import au.com.bytecode.opencsv.CSVWriter;
import ru.kor_inc.andytwo.R;

public class Box extends Activity{
DbTool db = new DbTool();


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_box);
		
		Intent intent = getIntent();
		final String currentTable = intent.getStringExtra("currentTable");
		
		Cursor c = db.getCursor(currentTable, this);
		startManagingCursor(c);
		ListView listViewBox = (ListView) findViewById(R.id.listViewBox);
		
		registerForContextMenu(listViewBox);
		
		String[] from = new String[] { "date", "time", "numeric", "telo", "chId" };
	    int[] to = new int[] { R.id.dataLayoutDate, R.id.dataLayoutTime, R.id.dataLayoutNumeric, R.id.dataLayoutTelo, R.id.dataLayoutChid };

		getActionBar().setDisplayHomeAsUpEnabled(true);
	
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.datalayout, c, from, to);
		listViewBox.setAdapter(adapter);
		
		final Context ctx = this;
		//push test
		listViewBox.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent(ctx, DataEdit.class);
				intent.putExtra("position", position);
				intent.putExtra("currentTable", currentTable);
				startActivityForResult(intent, 1);
			}
		});
	

	}
	
	
	
	protected void onActivityResult(int rc, int reqv, Intent intent){
		this.onCreate(null);	
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
		      ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, 1, 0, "Удалить");
		  }
	
	public boolean onContextItemSelected(MenuItem item) {
		Intent intent = getIntent();
		final String currentTable = intent.getStringExtra("currentTable");
		Cursor c = db.getCursor(currentTable, this);
	    if (item.getItemId() == 1) {
	      // получаем из пункта контекстного меню данные по пункту списка 
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      // извлекаем id записи и удаляем соответствующую запись в БД
	      db.delRec(currentTable, acmi.id,this);
	      // обновляем курсор
	      c.requery();
	      //Обновляем activity после удаления элемента
	      this.onCreate(null);
	      return true;
	    }
	    return super.onContextItemSelected(item);
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.boxmenu, menu);
		menu.add(0,1,1,"Удалить все к хренам");
		menu.add(0,2,2,"Экспортировать");
		
		return super.onCreateOptionsMenu(menu);
		
	}
	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent = getIntent();
		final String currentTable = intent.getStringExtra("currentTable");
		switch (item.getItemId()){
			case 1:
				db.clear(currentTable, this);
				this.onCreate(null);
				Toast.makeText(this, "Ты убил их! Изверг!", Toast.LENGTH_SHORT).show();
			break;
			case 2:
				new ExportDatabaseCSVTask().execute("");
			break;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
			break;
			case R.id.chart:
				Intent intentChart = new Intent(this, ChartActivity.class);
				intentChart.putExtra("currentTable", currentTable);
				this.startActivity(intentChart);
			break;
			case R.id.filter:
				Intent intentFilter = new Intent(this, ChartActivity.class);
				intentFilter.putExtra("currentTable", currentTable);
				this.startActivity(intentFilter);
			break;
		}
		return true;
	}
	
	public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
	    private final ProgressDialog dialog = new ProgressDialog(Box.this);

	    @Override
	    protected void onPreExecute() {
	        this.dialog.setMessage("Exporting database...");
	        this.dialog.show();
	    }

	    protected Boolean doInBackground(final String... args) {
	    	
	    	Intent intent = getIntent();
			final String currentTable = intent.getStringExtra("currentTable");
			
	        File dbFile = getDatabasePath(Box.this.getFilesDir().getPath()+"databases/firstAnyDynamicDataTable.db");
	        System.out.println(dbFile);  // displays the data base path in your logcat 
	         File exportDir = new File(Environment.getExternalStorageDirectory(), "AnDyExport");

	        if (!exportDir.exists()) { exportDir.mkdirs(); }

	        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		    String date = sdf.format(new Date(System.currentTimeMillis()));
		    
	        File file = new File(exportDir, "andyexport"+date+".csv");
	        try {
	            file.createNewFile();
	            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
	            Cursor curCSV = db.getCursor(currentTable, Box.this);
	            csvWrite.writeNext(curCSV.getColumnNames());
	            while(curCSV.moveToNext()) {
	                String arrStr[] ={curCSV.getString(0),curCSV.getString(1),curCSV.getString(2), curCSV.getString(3),curCSV.getString(4)};
	                csvWrite.writeNext(arrStr);
	            }
	            csvWrite.close();
	            curCSV.close();
	            return true;
	        } catch(SQLException sqlEx) {
	            Log.e("Box", sqlEx.getMessage(), sqlEx);
	            return false;
	        } catch (IOException e) {
	            Log.e("Box", e.getMessage(), e);
	            return false;
	        }
	    }

	    protected void onPostExecute(final Boolean success) {
	        if (this.dialog.isShowing()) { this.dialog.dismiss(); }
	        if (success) {
	            Toast.makeText(Box.this, "Ищи на SD в /AnDyExport :)", Toast.LENGTH_LONG).show();
	        } else {
	            Toast.makeText(Box.this, "Что-то с экспортом не так пошло...", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
}
