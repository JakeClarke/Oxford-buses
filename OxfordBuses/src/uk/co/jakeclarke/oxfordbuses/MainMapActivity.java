package uk.co.jakeclarke.oxfordbuses;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;

public class MainMapActivity extends FragmentActivity {
	
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.map);
		this.mapFragment = (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map);
	
		
	}
	
	protected void onStart() {
		super.onStart();
		this.map = this.mapFragment.getMap();
		this.map.setMyLocationEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mapmenu, menu);
	    return true;
	}
	
}
