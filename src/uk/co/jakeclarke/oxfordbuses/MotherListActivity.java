package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.adapters.FavouriteStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import android.app.ListActivity;

public class MotherListActivity extends ListActivity
{
	public static final int GET_ALL_STOPS = 0;
	public static final int GET_FAVOURITE_STOPS = 1;
	
	protected List<Stop> stopArray;
	protected StopProvider sp;
	protected Stop selectedStop;

	/**
	 * Populate the list of Stops
	 * @param int - action Takes the value MotherListActivity.GET_ALL_STOPS or MotherListActivity.GET_FAVOURITE_STOPS
	 */
	public void updateStopsArray(int action)
	{
		// If the list is null, instanciate an empty one
		if(stopArray == null)
		{
			stopArray = new ArrayList<Stop>();
		}
		stopArray.clear();
		sp = new StopProvider(this);
		// generate the list
		switch(action)
		{
			case GET_ALL_STOPS:
				stopArray = sp.getAllStops();
				break;
			case GET_FAVOURITE_STOPS:
				stopArray = sp.getAllFavourites();
				setListAdapter(new FavouriteStopAdapter(this, stopArray));
				break;
		}
	}

	/**
	 * Get the Stop provider of the Activity
	 * @return StopProvider
	 */
	public StopProvider getSp()
	{
		return sp;
	}

	/**
	 * Get the Stop selected in the list
	 * @return Stop
	 */
	public Stop getSelectedStop()
	{
		return selectedStop;
	}

	/**
	 * Set the Stop selected in the list
	 * @param Stop selectedStop
	 */
	public void setSelectedStop(Stop selectedStop)
	{
		this.selectedStop = selectedStop;
	}
}
