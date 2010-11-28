package uk.co.jakeclarke.oxfordbuses;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class ListFavouriteStopsActivity extends ListActivity {

	SimpleCursorAdapter adapter;
	StopProvider sp;
	
	public ListFavouriteStopsActivity() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sp = new StopProvider(this);
		sp.open();
		Cursor c = sp.getAllFavourites();
		adapter = new SimpleCursorAdapter(this, R.layout.favouritestop, c, new String[]{"naptancode", "stopName"}, new int[] {R.id.favstopnaptan, R.id.favstopname} );
		this.setListAdapter(adapter);
		
		
	}
	
	

}
