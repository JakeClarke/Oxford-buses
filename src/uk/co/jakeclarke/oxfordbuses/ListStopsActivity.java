package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.handlers.ListStopsListener;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity listing all the bus stops stored in the database
 *
 */
public class ListStopsActivity extends MotherListActivity
{
	private ListStopsListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		listener = new ListStopsListener(this);
		
		// Populate the list of Stops
		updateStopsArray(GET_ALL_STOPS);
		
		// Set a new adapter with item renderer and give the list of Stops
		setListAdapter(new ArrayAdapter<Stop>(this, R.layout.stoplistitem, stopArray));
		ListView listView = getListView();
		
		// Allow the user to filter bus stops by text (keyboard, virtual or not)
		listView.setTextFilterEnabled(true);

		// Add action listeners
		listView.setOnItemClickListener(listener);
		listView.setOnItemLongClickListener(listener);
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        	// Display the dialog that asks the user if yes or not he wants to
        	//add the selected bus Stop to its favourites
	        case Constants.LISTSTOP_DIALOG:
	    		return new AlertDialog.Builder(this)
	    			.setTitle(getString(R.string.liststopsdialogs_favourite_stop))
	    			.setMessage(getString(R.string.liststopsdialogs_add_stop_favourite))
	    			.setCancelable(true)
    				.setPositiveButton(getString(R.string.liststopsdialogs_yes), listener)
	    			.setNegativeButton(getString(R.string.liststopsdialogs_no), listener)
	    			.create();
        }
        return null;
    }
}