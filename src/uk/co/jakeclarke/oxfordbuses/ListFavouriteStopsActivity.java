package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.adapters.FavouriteStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.handlers.ListFavouriteStopsListener;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ListFavouriteStopsActivity extends ListActivity
{
	private List<Stop> stopArray;
	private StopProvider sp;
	private ListFavouriteStopsListener listener;
	private Stop selectedStop;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setTitle(getString(R.string.favourite_title));
		
		listener = new ListFavouriteStopsListener(this);

		updateStopsArray();
		ListView lv = this.getListView();
		lv.setOnItemClickListener(listener);
		lv.setOnItemLongClickListener(listener);
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
        ListFavouriteStopsListener listener = new ListFavouriteStopsListener(this, id);
        switch (id)
        {
        	// Set the appropriated options
	        case Constants.LISTFAVOURITESTOP_FAVOURITE:
	    		return new AlertDialog.Builder(this)
	    			.setTitle(getString(R.string.favouritedialogs_remove_stop))
	    			.setMessage(getString(R.string.favouritedialogs_remove_stop_from_favourite, selectedStop.getStopName()))
	    			.setCancelable(true)
	    			.setPositiveButton(getString(R.string.favouritedialogs_yes), listener)
	    			.setNegativeButton(getString(R.string.favouritedialogs_no), listener)
	    			.create();

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

	public void updateStopsArray()
	{
		if(stopArray == null)
		{
			stopArray = new ArrayList<Stop>();
		}
		stopArray.clear();
		sp = new StopProvider(this);
		// generate the array
		stopArray = sp.getAllFavourites();
		setListAdapter(new FavouriteStopAdapter(this, stopArray));
	}

	public Stop getSelectedStop()
	{
		return selectedStop;
	}

	public void setSelectedStop(Stop selectedStop)
	{
		this.selectedStop = selectedStop;
	}

	public StopProvider getSp()
	{
		return sp;
	}
}