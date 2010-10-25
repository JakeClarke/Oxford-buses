package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListStopsActivity extends ListActivity {
	
	ArrayList<Stop> StopArray;
	StopProvider sp;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  StopArray = new ArrayList<Stop>();
	  updateStopsArray();
	  setListAdapter(new ArrayAdapter<Stop>(this, R.layout.stoplistitem,StopArray));
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		          Toast.LENGTH_SHORT).show();
		      
		      Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.oxontime.com/pip/stop.asp?naptan=" 
		    		  + ListStopsActivity.this.StopArray.get(position).naptancode + "&textonly=1"));
		      startActivity(i);
		    }
		  });
	}


	private void updateStopsArray() {
		StopArray.clear();
		sp = new StopProvider(this);
		sp.open();
		// generate the array
		Cursor stopCursor = sp.getAllStops();
		int numberOfrows = sp.NumberOfRows();
		for (int i = 0; i < numberOfrows; i++)
		{
			Stop tempStop = new Stop(stopCursor);
			// order should be:
			//KEY_NAPTAN, KEY_COORDS, KEY_STOPNAME,KEY_STOPBEARING, KEY_PARENTMAP
			StopArray.add(tempStop);
			stopCursor.moveToNext();
				
		}
		stopCursor.close();
		sp.close();
	}
	
	
	
}
