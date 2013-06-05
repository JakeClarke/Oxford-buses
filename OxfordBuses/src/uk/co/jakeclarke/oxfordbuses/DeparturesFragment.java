package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.DeparturesProvider.Departures.Bus;
import uk.co.jakeclarke.oxfordbuses.DeparturesProvider.DeparturesUpdateListener;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DeparturesFragment extends ListFragment {

	private Stop stop;
	private DeparturesProvider departuresProvider;
	private TextView stopName, naptan;
	private ArrayAdapter<Bus> departuresAdapter;
	private ImageButton favbutton;
	private boolean isFavourite = false;
	private StopsProvider stopsProvider;
	private TextView emptyListText;

	private boolean isProviderInitialised = false;

	private final DeparturesUpdateListener departuresUpdateListener = new DeparturesUpdateListener() {

		@Override
		void onUpdate(DeparturesProvider departuresProvider) {
			departuresAdapter.clear();
			for (Bus b : departuresProvider.getDepartures().buses) {
				departuresAdapter.add(b);
			}
			DeparturesFragment.this.emptyListText
					.setText(R.string.getting_departures_none);
		}

		@Override
		void onError(DeparturesProvider stopMapManager) {
			DeparturesFragment.this.emptyListText
					.setText(R.string.getting_departures_err);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.stopsProvider = new StopsProvider(this.getActivity());

		this.departuresAdapter = new ArrayAdapter<Bus>(this.getActivity(),
				R.layout.departuresitem, R.id.destination) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				Bus b = this.getItem(position);
				TextView serviceName = (TextView) v
						.findViewById(R.id.destination);
				serviceName.setText(b.destination);

				TextView serviceNum = (TextView) v.findViewById(R.id.service);
				serviceNum.setText(b.service);

				TextView time = (TextView) v.findViewById(R.id.time);
				time.setText(b.time);

				return v;
			}
		};

		// Annoyingly if you create a fragment from within a layout this gets
		// called before it has a chance to set the stop.
		if (stop != null) {
			initProvider();
		} // otherwise we init the provider when the stop is set.
		isProviderInitialised = true;
	}

	/**
	 * 
	 */
	private void initProvider() {
		this.departuresProvider = new DeparturesProvider(this.getActivity()
				.getApplicationContext(), stop);
		this.departuresProvider
				.setDeparturesUpdateListener(this.departuresUpdateListener);

		this.departuresProvider.startUpdate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.departureslist, container, false);

		this.stopName = (TextView) v.findViewById(R.id.d_stopname);

		this.naptan = (TextView) v.findViewById(R.id.d_naptancode);

		this.setListAdapter(departuresAdapter);

		this.favbutton = (ImageButton) v.findViewById(R.id.favourite);

		this.favbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DeparturesFragment.this.isFavourite) {
					stopsProvider.removeFavourite(stop);

					DeparturesFragment.this.isFavourite = false;
				} else {
					stopsProvider.addFavourite(stop);

					DeparturesFragment.this.isFavourite = true;
				}

				DeparturesFragment.this.updateFavIcon();
			}
		});

		this.emptyListText = (TextView) v.findViewById(android.R.id.empty);
		this.emptyListText.setText(R.string.getting_departures);

		if (this.stop != null) {
			updateUI();
		}

		return v;
	}

	/**
	 * 
	 */
	private void updateUI() {
		this.stopName.setText(stop.Name);
		this.naptan.setText(stop.Naptan);

		this.isFavourite = this.stopsProvider.isFavouriteStop(stop);
		this.updateFavIcon();
	}

	@Override
	public void onPause() {
		this.departuresProvider.stopUpdates();
		super.onPause();
	}

	public void setStop(Stop stop) {
		this.stop = stop;
		// This can be called before anything else has be which would cause the
		// following calls to fail.
		if (isProviderInitialised) {
			// Stop the previous provider before creating a new one.
			if (this.departuresProvider != null) {
				this.departuresProvider.stopUpdates();
			}
			initProvider();
			updateUI();
		}
	}

	private void updateFavIcon() {
		this.favbutton
				.setImageResource((this.isFavourite) ? android.R.drawable.star_big_on
						: android.R.drawable.star_big_off);
	}

}
