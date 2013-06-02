package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.db.StopsDatabase;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListFavouriteStopsActivity extends ListActivity {

	SimpleCursorAdapter adapter;
	StopsDatabase sp;

	public ListFavouriteStopsActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Favourite stops");
		sp = new StopsDatabase(this);
		sp.open();
		Cursor c = sp.getAllFavourites();
		adapter = new SimpleCursorAdapter(this, R.layout.favouritestop, c,
				new String[] { "naptancode", "stopName" }, new int[] {
						R.id.stopnaptan, R.id.stopname });
		this.setListAdapter(adapter);

		ListView lv = this.getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// get the data cursor for the view
				Cursor selectedItem = (Cursor) parent.getAdapter().getItem(
						position);
				Toast.makeText(ListFavouriteStopsActivity.this,
						"Showing timetable", Toast.LENGTH_LONG).show();
				String naptan = selectedItem.getString(1);
				Intent i = new Intent(Intent.ACTION_VIEW, OxontimeUtils
						.getTimesUri(naptan, ListFavouriteStopsActivity.this));
				ListFavouriteStopsActivity.this.startActivity(i);
			}

		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long id) {
				Cursor selectedItem = (Cursor) parent.getAdapter().getItem(pos);
				final String stopName = selectedItem.getString(2), stopNaptan = selectedItem
						.getString(1);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ListFavouriteStopsActivity.this);
				builder.setTitle("Remove stop?");
				builder.setMessage("Would you like to remove the stop, "
						+ stopName + ", from your favourites?");
				builder.setCancelable(true);
				builder.setPositiveButton("Yes",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								StopsDatabase sp = new StopsDatabase(
										ListFavouriteStopsActivity.this);
								sp.open();
								sp.deleteFavourite(stopNaptan);
								sp.close();
								Toast.makeText(ListFavouriteStopsActivity.this,
										"Removed from favourites: " + stopName,
										Toast.LENGTH_LONG).show();
								ListFavouriteStopsActivity.this.adapter
										.getCursor().requery();
							}

						});
				builder.setNegativeButton("No",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				builder.create().show();
				return true;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favouritesmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.addstop:
			LayoutInflater inflater = this.getLayoutInflater();
			View layout = inflater.inflate(R.layout.addfavouritedialog,
					(ViewGroup) findViewById(R.id.layout_root));
			AlertDialog.Builder builder = new Builder(this);
			final EditText favstopNumber = (EditText) layout
					.findViewById(R.id.favstopnumber);
			final EditText favstopName = (EditText) layout
					.findViewById(R.id.stopname);

			builder.setView(layout);
			builder.setPositiveButton("Add stop",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// ListFavouriteStopsActivity.this.sp.insertFavourite(favstopName.getText().toString(),
							// favstopNumber.getText().toString());
						}

					});
			builder.create().show();
			return true;
		default:
			return false;

		}

	}

}
