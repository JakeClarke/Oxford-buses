/**
 * This activity shows a map of all the stops and their locations.
 */
package uk.co.jakeclarke.oxfordbuses;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.adapters.RegularStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.handlers.StopMapListener;
import uk.co.jakeclarke.oxfordbuses.mapoverlays.StopItemizedOverlay;
import uk.co.jakeclarke.oxfordbuses.providers.Maps;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * MAIN Activity displaying Bus Stops on a Google Map
 *
 */
public class StopMapActivity extends MapActivity
{
	private MapController mc;
	private MyLocationOverlay myLocation;
	private MapView mapView;
	private SharedPreferences prefs;
	
	// Node, list of stops being selected by a Tap on the Map
	private List<Stop> currentNodeStops;

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Maps.parseMaps(getAssets());

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		myLocation = new MyLocationOverlay(this, mapView);
		myLocation.enableMyLocation();
		myLocation.enableCompass();

		// get MapController that helps to set/get location, zoom etc.
		mc = mapView.getController();
		// set the location over oxford
		GeoPoint p = new GeoPoint((int)(51.752434 * 1000000),(int)(-1.262226* 1000000));
		mc.animateTo(p);
		mc.setZoom(14);

		// add the stops to the map
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable stopIcon = this.getResources().getDrawable(R.drawable.stopicon);
		StopItemizedOverlay stopOverlay = new StopItemizedOverlay(stopIcon, this);

		mapOverlays.add(stopOverlay);
		mapOverlays.add(myLocation);

		// If there is no stop on the Map, prompt the user to build it and populate the databse
		if (stopOverlay.size() == 0)
		{
			promptBuildDB();
		}
	}

	@Override
	protected void onPause ()
	{
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume ()
	{
		myLocation.enableCompass();
		myLocation.enableMyLocation();

		mapView.setSatellite(prefs.getBoolean("satellite", false));

		super.onResume();
	}

	@Override
	protected void onStop()
	{
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		super.onStop();
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	// Instanciante a new listener to handle user actions on the StopMapActivity
    	// The type of the listener to handle dialogs actions depend on the dialog Id
        StopMapListener listener = new StopMapListener(this, id);
        
        // Create a different dialog depending of the action (determined by the Id given when the 'showDialog' was being called)
        switch (id)
        {
        	// Display the dialog dedicated for Search action
	        case Constants.STOPMAP_DIALOG_SEARCH:
	    		LayoutInflater factory = LayoutInflater.from(this);
	    		final View dialogLayout = factory.inflate(R.layout.naptandialog, null);
	            
	    		return new AlertDialog.Builder(this)
	    			.setView(dialogLayout)
	    			.setTitle(getString(R.string.naptandialog_manual_stop_lookup))
	    			.setPositiveButton(getString(R.string.naptandialog_lookup), listener)
		    		.create();

        	// Display the dialog dedicated to ask the user if he wants to build the database
	        case Constants.STOPMAP_DIALOG_PROMPT:
	    		return new AlertDialog.Builder(this)
	    			.setTitle(getString(R.string.stopmapdialogs_database_refresh_required))
	    			.setMessage(getString(R.string.stopmapdialogs_first_launch_message))
	    			.setCancelable(false)
	    			.setPositiveButton(getString(R.string.stopmapdialogs_build_now), listener)
		    		.setNegativeButton(getString(R.string.stopmapdialogs_build_later), listener)
		    		.create();

        	// Display the dialog dedicated for a Tap on a bus sotp Node
	        case Constants.STOPMAP_DIALOG_TAP:
	    		return new AlertDialog.Builder(this)
	    			.setTitle(getString(R.string.stopitemizedoverlay_dialogs_pick_stop))
	    			.setNegativeButton(getString(R.string.stopitemizedoverlay_dialogs_close), listener)
		    		.setAdapter(new RegularStopAdapter(this, currentNodeStops), listener)
		    		.create();
        }
        return null;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	switch (id)
    	{
    		// Re-populate the dialog with the list of stop of the "Tapped" bus stop Node
			case Constants.STOPMAP_DIALOG_TAP:
				((AlertDialog)dialog).getListView().setAdapter(new RegularStopAdapter(this, currentNodeStops));
				break;
		}
    }

	@Override
	public boolean onSearchRequested()
	{
		// Show the Search dialog
		this.showDialog(Constants.STOPMAP_DIALOG_SEARCH);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stopmap_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i = null;
		switch(item.getItemId())
		{
			case R.id.listbutton:
				i = new Intent(this, ListStopsActivity.class);
				startActivity(i);
				return true;
	
			case R.id.mylocation:
				GeoPoint g = myLocation.getMyLocation();
				if (g != null)
				{
					Toast.makeText(this, getString(R.string.stopmap_going_to_location), Toast.LENGTH_LONG).show();
					mc.animateTo(g);
					mc.setZoom(16);
				}
				else
				{
					Toast.makeText(this, getString(R.string.stopmap_location_not_available), Toast.LENGTH_LONG).show();
				}
				return true;
	
			case R.id.favouritestops:
				i = new Intent(this, ListFavouriteStopsActivity.class);
				this.startActivity(i);
				return true;
	
			case R.id.menu_settings:
				i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				return true;
	
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Prompt the user to build the bus stop database and populate the databse
	 * 
	 */
	private void promptBuildDB()
	{
		this.showDialog(Constants.STOPMAP_DIALOG_PROMPT);
	}

	/**
	 * Set the selected bus stop Node.
	 * 
	 * @param List<Stop> currentNodeStops
	 */
	public void setCurrentNodeStops(List<Stop> currentNodeStops)
	{
		this.currentNodeStops = currentNodeStops;
	}
}