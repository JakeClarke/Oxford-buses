package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class StopListFragment extends ListFragment {

	private EditText search;
	private Stop[] stops = new Stop[0];
	private ArrayAdapter<Stop> stopListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.stoplist, container, false);

		this.search = (EditText) v.findViewById(R.id.d_stopname);
		this.search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				updateAdapter();

			}
		});

		this.setListAdapter(this.stopListAdapter);

		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Stop s = this.stopListAdapter.getItem(position);
		if (selectionListener != null)
			selectionListener.onSelection(s);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		stopListAdapter = new ArrayAdapter<Stop>(activity,
				R.layout.stoplistitem, R.id.stopname) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				Stop s = this.getItem(position);
				TextView tv = (TextView) v.findViewById(R.id.stopname);
				tv.setText(s.Name);
				tv = (TextView) v.findViewById(R.id.stopnaptan);
				tv.setText(s.Naptan);

				return v;
			}

		};

		this.updateAdapter();

	}

	public void setStops(Stop[] stops) {
		this.stops = stops;
		this.updateAdapter();
	}

	Object listLock = new Object();

	Handler h = new Handler();

	private void updateAdapter() {

		if (this.stopListAdapter == null)
			return;

		if (this.stops == null || this.stops.length == 0) {
			this.stopListAdapter.clear();
			return;
		}

		Runnable rs = new Runnable() {

			final ArrayList<Stop> newStops = new ArrayList<Stop>();
			// cache to provent cracshes.
			final String searchQ = (search != null) ? search.getText()
					.toString() : "";

			@Override
			public void run() {
				for (Stop s : stops) {
					if (searchQ.equals("")
							|| s.Name.toUpperCase().contains(
									searchQ.toUpperCase())
							|| s.Naptan.contains(searchQ)) {
						newStops.add(s);
					}
				}

				// post the change back the main thread.
				h.post(new Runnable() {

					@Override
					public void run() {
						stopListAdapter.clear();
						for (Stop s : newStops) {
							stopListAdapter.add(s);
						}
					}

				});

			}

		};
		new Thread(rs).start();

	}

	SelectionListener selectionListener;

	public void setSelectionListener(SelectionListener listener) {
		this.selectionListener = listener;
	}

	public static abstract class SelectionListener {
		abstract void onSelection(Stop selection);
	}
}
