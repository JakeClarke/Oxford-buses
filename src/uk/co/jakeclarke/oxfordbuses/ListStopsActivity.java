package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
		      Stop SelectedStop = (Stop)parent.getAdapter().getItem(position);
		      Intent i = new Intent(Intent.ACTION_VIEW, 
		    		  OxontimeUtils.getTimesUri(SelectedStop.naptancode, ListStopsActivity.this));
		      startActivity(i);
		    }
		  });
	  
	  lv.setOnItemLongClickListener(new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			final Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
			AlertDialog.Builder builder = new AlertDialog.Builder(ListStopsActivity.this);
			builder.setTitle("Favourite stop?");
			builder.setMessage("Would you like to add this stop to your favourites?");
			builder.setCancelable(true);
			builder.setPositiveButton("Yes", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					sp.open();
					sp.insertFavourite(selectedStop.stopName, selectedStop.naptancode);
					sp.close();
					Toast.makeText(ListStopsActivity.this, "Added to favourites", Toast.LENGTH_LONG).show();
					
				}
				
			});
			builder.setNegativeButton("No", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
				
			
			});
			builder.create().show();
			
			return true;
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
