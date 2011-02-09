package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.handlers.ListFavouriteStopsListener;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Activity listing the favourite bus stops stored in the database
 *
 */
public class ListFavouriteStopsActivity extends MotherListActivity
{
	private ListFavouriteStopsListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		listener = new ListFavouriteStopsListener(this);

		// Populate the list of Stops
		updateStopsArray(GET_FAVOURITE_STOPS);
		
		ListView listView = this.getListView();

		// Add action listeners
		listView.setOnItemClickListener(listener);
		listView.setOnItemLongClickListener(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listfavouritestops_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.addstop:
				this.showDialog(Constants.LISTFAVOURITESTOP_OPTION);
				return true;
	
			default:
				return false;
		}
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	// Instanciante a new listener to handle user actions on the ListFavouriteStopsActivity
    	// The type of the listener to handle dialogs actions depend on the dialog Id
        ListFavouriteStopsListener listener = new ListFavouriteStopsListener(this, id);

        // Create a different dialog depending of the action (determined by the Id given when the 'showDialog' was being called)
        switch (id)
        {
    		// Display the dialog dedicated to ask the user if he wants to remove this bus Stop
        	// fro his favourites
	        case Constants.LISTFAVOURITESTOP_FAVOURITE:
	    		return new AlertDialog.Builder(this)
	    			.setTitle(getString(R.string.favouritedialogs_remove_stop))
	    			.setMessage(getString(R.string.favouritedialogs_remove_stop_from_favourite, selectedStop.getStopName()))
	    			.setCancelable(true)
	    			.setPositiveButton(getString(R.string.favouritedialogs_yes), listener)
	    			.setNegativeButton(getString(R.string.favouritedialogs_no), listener)
	    			.create();

        	// Display the dialog dedicated to create a new favourite bus Stop
	        case Constants.LISTFAVOURITESTOP_OPTION:
				LayoutInflater inflater = this.getLayoutInflater();
				View layout = inflater.inflate(R.layout.addfavouritedialog, (ViewGroup) findViewById(R.id.layout_root));
				return new Builder(this)
					.setView(layout)
					.setPositiveButton(getString(R.string.favouritedialogs_add_stop), listener)
					.create();
        }
        return null;
    }
}