package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.adapters.RegularStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListStopsActivity extends ListActivity
{
	List<Stop> stopArray;
	StopProvider sp;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		updateStopsArray();
		setListAdapter(new RegularStopAdapter(this, stopArray));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
						Toast.LENGTH_SHORT).show();
				Stop SelectedStop = (Stop)parent.getAdapter().getItem(position);
				Intent i = new Intent(Intent.ACTION_VIEW, 
						OxontimeUtils.getTimesUri(SelectedStop.getNaptanCode(), ListStopsActivity.this));
				startActivity(i);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id)
			{
				final Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(ListStopsActivity.this);
				builder.setTitle(getString(R.string.liststopsdialogs_favourite_stop));
				builder.setMessage(getString(R.string.liststopsdialogs_add_stop_favourite));
				builder.setCancelable(true);
				builder.setPositiveButton(getString(R.string.liststopsdialogs_yes), new OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						sp.insertFavourite(selectedStop.getStopName(), selectedStop.getNaptanCode());
						Toast.makeText(ListStopsActivity.this, getString(R.string.liststopsdialogs_added), Toast.LENGTH_LONG).show();
					}
				});
				builder.setNegativeButton(getString(R.string.liststopsdialogs_no), new OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				builder.create().show();

				return true;
			}
		});
	}

	private void updateStopsArray()
	{
		if(stopArray == null)
		{
			stopArray = new ArrayList<Stop>();
		}
		stopArray.clear();
		sp = new StopProvider(this);
		// generate the array
		stopArray = sp.getAllStops();
	}
}