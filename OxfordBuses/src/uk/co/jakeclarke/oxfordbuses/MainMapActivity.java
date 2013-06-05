package uk.co.jakeclarke.oxfordbuses;

import java.util.HashMap;

import uk.co.jakeclarke.oxfordbuses.StopListFragment.SelectionListener;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import uk.co.jakeclarke.oxfordbuses.settings.SettingsActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

	private boolean wasRefreshRequested = false;

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
			if (!this.wasRefreshRequested) {
				this.stopManager.updateStops();
				this.wasRefreshRequested = true;
			} else {
				Toast.makeText(this, R.string.getting_stops_in_progress,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		} else if (item.getItemId() == R.id.listbutton) {
			Intent i = new Intent(this, StopListActivity.class);
			this.startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.favouritestops) {
			this.showFavourites();
			return true;
		} else if (item.getItemId() == R.id.menu_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			this.startActivity(i);
			return true;
		}
		return false;
	}

	/**
	 * Loads up the relevant departures.
	 * 
	 * @param s
	 */
	private void showDepartures(Stop s) {
		if (hasDoublePanel) {
			DeparturesFragment departures = new DeparturesFragment();
			departures.setStop(s);
			FragmentTransaction transaction = MainMapActivity.this
					.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.listframe, departures);
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// doesn't do anything for some reason.
			transaction.setBreadCrumbTitle(R.string.departures);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			Intent i = new Intent(this, DeparturesActivity.class);

			i.putExtra(DeparturesActivity.KEY_STOP, s);
			this.startActivity(i);
		}
	}

	private void showFavourites() {
		if (hasDoublePanel) {
			StopListFragment favourites = new StopListFragment() {
				@Override
				public View onCreateView(LayoutInflater inflater,
						ViewGroup container, Bundle savedInstanceState) {
					View v = super.onCreateView(inflater, container,
							savedInstanceState);

					// allows us to refresh the source when departures have
					// gone.
					this.setStops(stopManager.getFavouriteStops());
					return v;
				}
			};
			favourites.setSelectionListener(new SelectionListener() {

				@Override
				void onSelection(Stop selection) {
					tapListItem(selection);
				}

			});

			FragmentTransaction transaction = this.getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.listframe, favourites);
			transaction = transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.setBreadCrumbTitle(R.string.favourites)
					.addToBackStack(null);
			transaction.commit();
		} else {
			Intent i = new Intent(MainMapActivity.this,
					FavouriteStopsActivity.class);
			this.startActivity(i);
		}
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
								markerLookup.clear();

								Stop[] stops = stopMapManager.getStops();

								if (hasDoublePanel) {
									stopList.setStops(stops);
								}

								for (int i = 0; i < stops.length; i++) {
									Stop s = stops[i];

									Marker m = map
											.addMarker(new MarkerOptions()
													.position(s.latlong)
													.title(s.Name)
													.snippet(s.Naptan));
									stopLookup.put(m, s);
									markerLookup.put(s, m);
								}

								if (wasRefreshRequested) {
									Toast.makeText(MainMapActivity.this,
											R.string.getting_stops_complete,
											Toast.LENGTH_LONG).show();
									wasRefreshRequested = false;
								}

							}

							@Override
							void onError(StopsProvider stopMapManager) {
								Toast.makeText(MainMapActivity.this,
										R.string.getting_stops_err,
										Toast.LENGTH_LONG).show();

								wasRefreshRequested = false;
							}

						});

				// setup our listeners.

				if (this.hasDoublePanel) { // tablet actions.
					this.stopList.setSelectionListener(new SelectionListener() {

						@Override
						void onSelection(Stop selection) {
							tapListItem(selection);
						}

					});

				}

				this.map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {

						Stop s = stopLookup.get(marker);
						showDepartures(s);
					}

				});
			}
		}

	}

	/**
	 * The list tap item behaviour.
	 * 
	 * @param selection
	 */
	private void tapListItem(Stop selection) {
		Marker m = markerLookup.get(selection);
		if (!m.isInfoWindowShown()) { // one tap for zoom
			m.showInfoWindow();
			map.animateCamera(CameraUpdateFactory.newLatLng(selection.latlong));
		} else { // two taps for show departures.
			showDepartures(selection);
		}
	}

}
