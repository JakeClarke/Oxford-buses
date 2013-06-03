package uk.co.jakeclarke.oxfordbuses;

import java.util.HashMap;

import uk.co.jakeclarke.oxfordbuses.StopListFragment.SelectionListener;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
	private StopsProvider stopManager;
	private boolean hasDoublePanel = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.map);
		this.mapFragment = (SupportMapFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.map);

		if (this.findViewById(R.id.listframe) != null) {
			this.hasDoublePanel = true;

			this.stopList = new StopListFragment();

			FragmentTransaction transaction = MainMapActivity.this
					.getSupportFragmentManager().beginTransaction();

			transaction.add(R.id.listframe, this.stopList);
			transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
			transaction.commit();
		}

		setUpMapIfNeeded();

	}

	protected void onStart() {
		super.onStart();

		setUpMapIfNeeded();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			this.stopManager.updateStops();
			return true;
		} else if (item.getItemId() == R.id.listbutton) {
			Intent i = new Intent(this, StopListActivity.class);
			this.startActivity(i);
			return true;
		}
		return false;
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			this.map = this.mapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.
				this.map.setMyLocationEnabled(true);

				this.stopManager = new StopsProvider(this,
						new StopsProvider.StopUpdateListener() {

							@Override
							void onUpdate(StopsProvider stopMapManager) {
								stopLookup.clear();

								Stop[] stops = stopMapManager.getStops();

								if (hasDoublePanel) {
									stopList.setStops(stops);
								}

								for (int i = 0; i < stops.length; i++) {
									// this does nothing.
									Stop s = stops[i];

									Marker m = map
											.addMarker(new MarkerOptions()
													.position(s.latlong)
													.title(s.Name)
													.snippet(s.Naptan));
									stopLookup.put(m, s);
									markerLookup.put(s, m);
								}

							}

							@Override
							void onError(StopsProvider stopMapManager) {
								// TODO Auto-generated method stub

							}

						});

				// setup our listeners.

				if (this.hasDoublePanel) { // tablet actions.
					this.stopList.setSelectionListener(new SelectionListener() {

						@Override
						void onSelection(Stop selection) {
							markerLookup.get(selection).showInfoWindow();
							map.animateCamera(CameraUpdateFactory
									.newLatLng(selection.latlong));
						}

					});

					this.map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

						@Override
						public void onInfoWindowClick(Marker marker) {
							DeparturesFragment departures = new DeparturesFragment();
							departures.setStop(stopLookup.get(marker));
							FragmentTransaction transaction = MainMapActivity.this
									.getSupportFragmentManager()
									.beginTransaction();

							transaction.replace(R.id.listframe, departures);
							transaction
									.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							transaction.addToBackStack(null);
							transaction.commit();
						}

					});

				} else {
					// phone action.
					this.map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

						@Override
						public void onInfoWindowClick(Marker marker) {
							Intent i = new Intent(MainMapActivity.this,
									DeparturesActivity.class);

							i.putExtra(DeparturesActivity.KEY_STOP,
									stopLookup.get(marker));
							MainMapActivity.this.startActivity(i);
						}
					});

				}
			}
		}

	}

}
