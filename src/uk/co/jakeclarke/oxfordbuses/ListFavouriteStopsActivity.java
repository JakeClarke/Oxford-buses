package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.adapters.FavouriteStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListFavouriteStopsActivity extends ListActivity
{
	List<Stop> stopArray;
	SimpleCursorAdapter adapter;
	StopProvider sp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setTitle(getString(R.string.favourite_title));

		updateStopsArray();
		this.setListAdapter(new FavouriteStopAdapter(this, stopArray));
		ListView lv = this.getListView();
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id)
			{
				// get the data cursor for the view
				Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
				Toast.makeText(ListFavouriteStopsActivity.this, getString(R.string.favourite_showing_timetable), Toast.LENGTH_LONG).show();
				Intent i = new Intent(Intent.ACTION_VIEW, 
						OxontimeUtils.getTimesUri(selectedStop.getNaptanCode(), ListFavouriteStopsActivity.this));
				ListFavouriteStopsActivity.this.startActivity(i);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long id)
			{
				Stop selectedStop = (Stop)parent.getAdapter().getItem(pos);
				final String stopName = selectedStop.getStopName(), stopNaptan = selectedStop.getNaptanCode();
				AlertDialog.Builder builder = new AlertDialog.Builder(ListFavouriteStopsActivity.this);
				builder.setTitle(getString(R.string.favouritedialogs_remove_stop));
				builder.setMessage(getString(R.string.favouritedialogs_remove_stop_from_favourite, stopName));
				builder.setCancelable(true);
				builder.setPositiveButton(getString(R.string.favouritedialogs_yes), new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						StopProvider sp = new StopProvider(ListFavouriteStopsActivity.this);
						sp.deleteFavourite(stopNaptan);
						Toast.makeText(ListFavouriteStopsActivity.this, getString(R.string.favouritedialogs_removed, stopName), Toast.LENGTH_LONG).show();
						ListFavouriteStopsActivity.this.updateStopsArray();
						ListFavouriteStopsActivity.this.setListAdapter(new FavouriteStopAdapter(ListFavouriteStopsActivity.this, stopArray));
					}
				});
				builder.setNegativeButton(getString(R.string.favouritedialogs_no), new android.content.DialogInterface.OnClickListener()
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listfavouritestops_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.addstop:
			LayoutInflater inflater = this.getLayoutInflater();
			View layout = inflater.inflate(R.layout.addfavouritedialog, (ViewGroup) findViewById(R.id.layout_root));
			AlertDialog.Builder builder = new Builder(this);
			final EditText favstopNumber = (EditText)layout.findViewById(R.id.favstopnumber);
			final EditText favstopName = (EditText)layout.findViewById(R.id.favstopname);

			builder.setView(layout);
			builder.setPositiveButton(getString(R.string.favouritedialogs_add_stop),  new android.content.DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					ListFavouriteStopsActivity.this.sp.insertFavourite(favstopName.getText().toString(), favstopNumber.getText().toString());
					ListFavouriteStopsActivity.this.updateStopsArray();
					ListFavouriteStopsActivity.this.setListAdapter(new FavouriteStopAdapter(ListFavouriteStopsActivity.this, stopArray));
				}
			});
			builder.create().show();
			return true;

		default:
			return false;
		}
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
		stopArray = sp.getAllFavourites();
	}
}