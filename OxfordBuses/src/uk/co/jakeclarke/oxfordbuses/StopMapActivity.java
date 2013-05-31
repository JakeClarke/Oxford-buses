/**
 * This activity shows a map of all the stops and their locations.
 */
package uk.co.jakeclarke.oxfordbuses;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.mapoverlays.StopItemizedOverlay;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * @author Jake
 *
 */
public class StopMapActivity extends MapActivity {

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	MapController mc;
	MyLocationOverlay myLocation;
	MapView mapView;
	
	private SharedPreferences prefs;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        Maps.ParseMaps(getAssets());
        
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
        StopItemizedOverlay stopOverlay = new StopItemizedOverlay(stopIcon, StopMapActivity.this);

        mapOverlays.add(myLocation);
        mapOverlays.add(stopOverlay);
        
        if (stopOverlay.size() == 0) {
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
    
    private void search() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View dialogLayout = factory.inflate(R.layout.naptandialog, null);
		builder.setView(dialogLayout);
		builder.setTitle("Manual stop lookup");
		builder.setPositiveButton("Lookup", new OnClickListener()
		
		{

			@Override
			public void onClick(DialogInterface d, int which) {
				EditText naptanEt = (EditText)dialogLayout.findViewById(R.id.NaptanEditText);
				Intent i = new Intent(Intent.ACTION_VIEW, 
						OxontimeUtils.getTimesUri(naptanEt.getText().toString(), StopMapActivity.this));
				StopMapActivity.this.startActivity(i);
			}
			
		});
		builder.create().show();
    }
    
    @Override
    public boolean onSearchRequested() {
    	search();
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mapmenu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i = null;
    	switch(item.getItemId())
    	{
    	case R.id.listbutton:
    		i = new Intent(StopMapActivity.this, ListStopsActivity.class);
	        startActivity(i);
	        return true;
    	case R.id.mylocation:
    		GeoPoint g = myLocation.getMyLocation();
    		
    		if (g != null) {
				Toast.makeText(this, "Going to location...", Toast.LENGTH_LONG).show();
				mc.animateTo(g);
				mc.setZoom(16);
			}
    		else {
    			Toast.makeText(this, "Location not available.", Toast.LENGTH_LONG).show();
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
    
    private void promptBuildDB()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Database refresh required");
    	builder.setMessage("This is the first time this app has been run and will need to build the stop database. \n" +
    			"This will take a couple of minutes on a mobile connection. This only needs to take place once.");
    	builder.setCancelable(false);
    	builder.setPositiveButton("Build now", new OnClickListener()
    	{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(StopMapActivity.this, GetStopsActivity.class);
		        startActivity(i);
				StopMapActivity.this.finish();
			}
    		
    	});
    	
    	builder.setNegativeButton("Build later", new OnClickListener()
    	{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				StopMapActivity.this.finish();
			}
    		
    	});
    	
    	builder.create().show();
    	
    	
    }

}
