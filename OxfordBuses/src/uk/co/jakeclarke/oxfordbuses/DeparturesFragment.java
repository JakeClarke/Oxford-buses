package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.DeparturesProvider.Departures.Bus;
import uk.co.jakeclarke.oxfordbuses.DeparturesProvider.DeparturesUpdateListener;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeparturesFragment extends Fragment {

	private Stop stop;
	private DeparturesProvider departuresProvider;
	private TextView stopName, naptan;
	private ListView departuresList;
	private ArrayAdapter<Bus> departuresAdapter;

	private final DeparturesUpdateListener departuresUpdateListener = new DeparturesUpdateListener() {

		@Override
		void onUpdate(DeparturesProvider departuresProvider) {
			departuresAdapter.clear();
			for (Bus b : departuresProvider.getDepartures().buses) {
				departuresAdapter.add(b);
			}
		}

		@Override
		void onError(DeparturesProvider stopMapManager) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.departureslist, container, false);

		// quick hack to fix orientation change crashes. This
		// fragment doesn't get used anyway.
		if (stop == null)
			return v;

		this.stopName = (TextView) v.findViewById(R.id.d_stopname);
		this.stopName.setText(stop.Name);
		this.naptan = (TextView) v.findViewById(R.id.d_naptancode);
		this.naptan.setText(stop.Naptan);
		this.departuresList = (ListView) v.findViewById(R.id.d_list);

		this.departuresAdapter = new ArrayAdapter<Bus>(this.getActivity()
				.getApplicationContext(), R.layout.departuresitem,
				R.id.destination) {
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
		this.departuresList.setAdapter(departuresAdapter);

		this.departuresProvider = new DeparturesProvider(this.getActivity()
				.getApplicationContext(), stop);

		this.departuresProvider
				.setDeparturesUpdateListener(this.departuresUpdateListener);

		return v;
	}

	@Override
	public void onResume() {
		this.departuresProvider.startUpdate();
		super.onResume();
	}

	@Override
	public void onPause() {
		this.departuresProvider.stopUpdates();
		super.onPause();
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

}
