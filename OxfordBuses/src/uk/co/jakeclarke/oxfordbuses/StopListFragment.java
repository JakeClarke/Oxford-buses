package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.StopMapManager.Stop;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class StopListFragment extends Fragment {

	private ListView list;
	private EditText search;
	private Stop[] stops;
	private ArrayAdapter<Stop> stopListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.stoplist, container, false);
		this.list = (ListView) v.findViewById(R.id.stoplist);
		this.list.setAdapter(this.stopListAdapter);

		this.search = (EditText) v.findViewById(R.id.searchbox);
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

		this.list.setAdapter(stopListAdapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (selectionListener != null)
					selectionListener.onSelection(((Stop) arg0
							.getItemAtPosition(position)));

			}
		});

		return v;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		stopListAdapter = new ArrayAdapter<Stop>(activity,
				R.layout.favouritestop, R.id.stopname) {

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

	}

	public void setStops(Stop[] stops) {
		this.stops = stops;
		this.updateAdapter();
	}

	private void updateAdapter() {
		this.stopListAdapter.clear();
		for (Stop s : this.stops) {
			if (this.search.getText().equals("")
					|| s.Name.toUpperCase().contains(
							this.search.getText().toString().toUpperCase())
					|| s.Naptan.contains(this.search.getText())) {
				this.stopListAdapter.add(s);
			}
		}

		this.stopListAdapter.notifyDataSetChanged();
	}

	SelectionListener selectionListener;

	public void setSelectionListener(SelectionListener listener) {
		this.selectionListener = listener;
	}

	public static abstract class SelectionListener {
		abstract void onSelection(Stop selection);
	}
}
