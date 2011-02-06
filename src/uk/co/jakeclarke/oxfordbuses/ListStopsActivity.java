package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.handlers.ListStopsListener;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListStopsActivity extends ListActivity
{
	private List<Stop> stopArray;
	private StopProvider sp;
	private ListStopsListener listener;
	private ListView listView;
	private Stop selectedStop;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		listener = new ListStopsListener(this);
		
		updateStopsArray();
		setListAdapter(new ArrayAdapter<Stop>(this, R.layout.stoplistitem, stopArray));
		listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(listener);

		listView.setOnItemLongClickListener(listener);
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        	// Set the appropriated options
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

	private void updateStopsArray()
	{
		if(stopArray == null)
		{
			stopArray = new ArrayList<Stop>();
		}
		stopArray.clear();
		sp = new StopProvider(this);
		// generate the array
		stopArray = sp.getAllStops();
	}

	public StopProvider getSp()
	{
		return sp;
	}

	public Stop getSelectedStop()
	{
		return selectedStop;
	}

	public void setSelectedStop(Stop selectedStop)
	{
		this.selectedStop = selectedStop;
	}
}