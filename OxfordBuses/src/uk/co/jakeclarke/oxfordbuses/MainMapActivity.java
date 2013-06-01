package uk.co.jakeclarke.oxfordbuses;

import java.util.HashMap;

import uk.co.jakeclarke.oxfordbuses.StopListFragment.SelectionListener;
import uk.co.jakeclarke.oxfordbuses.StopMapManager.Stop;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainMapActivity extends FragmentActivity {

	private SupportMapFragment mapFragment;
	private StopListFragment stopList;
	private GoogleMap map;
	private HashMap<Marker, Stop> stopLookup = new HashMap<Marker, Stop>();
	// bit of a hack there has to be a better way of doing this.
	private HashMap<Stop, Marker> markerLookup = new HashMap<Stop, Marker>();
	private StopMapManager stopManager;
	private boolean hasDoublePanel = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.map);
		this.mapFragment = (SupportMapFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.map);

		this.stopList = (StopListFragment) this.getSupportFragmentManager()
				.findFragmentById(R.id.stoplist);

		if (this.stopList != null) {
			this.hasDoublePanel = true;
		}

	}

	protected void onStart() {
		super.onStart();
		this.map = this.mapFragment.getMap();
		this.map.setMyLocationEnabled(true);

		if (this.map != null) {
			this.stopManager = new StopMapManager(this);
			this.stopManager.updateStops();
		}

		this.stopManager.setListener(new StopMapManager.StopUpdateListener() {

			@Override
			void onUpdate(StopMapManager stopMapManager) {
				stopLookup.clear();

				Stop[] stops = stopMapManager.getStops();

				if (hasDoublePanel) {
					stopList.setStops(stops);
				}

				for (int i = 0; i < stops.length; i++) {
					// this does nothing.
					Stop s = stops[i];

					Marker m = map.addMarker(new MarkerOptions()
							.position(s.latlong).title(s.Name)
							.snippet(s.Naptan));
					stopLookup.put(m, s);
					markerLookup.put(s, m);
				}

			}

			@Override
			void onError(StopMapManager stopMapManager) {
				// TODO Auto-generated method stub

			}

		});

		if (this.hasDoublePanel) {
			this.stopList.setSelectionListener(new SelectionListener() {

				@Override
				void onSelection(Stop selection) {
					markerLookup.get(selection).showInfoWindow();
					map.animateCamera(CameraUpdateFactory
							.newLatLng(selection.latlong));
				}

			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);
		if (this.hasDoublePanel) {
			menu.removeItem(R.id.listbutton);
		}

		return true;
	}

}
