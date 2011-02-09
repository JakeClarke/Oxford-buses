package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.ListFavouriteStopsActivity;
import uk.co.jakeclarke.oxfordbuses.ListTimesActivity;
import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Action listener of the ListFavouriteStopsListener activity
 *
 */
public class ListFavouriteStopsListener implements OnItemClickListener, OnItemLongClickListener, OnClickListener
{
	private ListFavouriteStopsActivity context;
	private int type;
	
	public ListFavouriteStopsListener (ListFavouriteStopsActivity context)
	{
		this.context = context;
	}
	
	public ListFavouriteStopsListener (ListFavouriteStopsActivity context, int type)
	{
		this.context = context;
		this.type = type;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Get the selected bus Stop naptan code from the list
		Stop stop = (Stop)parent.getAdapter().getItem(position);
		String stopName = stop.getStopName();
		String stopNaptan = stop.getNaptanCode();
		
		// Create a new Intent and launch the application abble to read the URI built from the naptan code
		Intent i = new Intent(context, ListTimesActivity.class);
		i.putExtra("stopName", stopName);
		i.putExtra("naptanCode", stopNaptan);
		context.startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
	{
		// Get the selected bus Stop from the list and pass it to the context
		context.setSelectedStop((Stop)parent.getAdapter().getItem(position));
		
		// Show the appropriate dialog
		context.showDialog(Constants.LISTFAVOURITESTOP_FAVOURITE);
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch(this.type)
		{
			// The dialog to remove the bus Stop from favourites has been validated
			case Constants.LISTFAVOURITESTOP_FAVOURITE:
				switch(which)
				{
					// The user chose to remove the bus Stop from the favourites
					case DialogInterface.BUTTON_POSITIVE:
						// Remove the bus Stop from the favourites
						StopProvider sp = new StopProvider(context);
						sp.deleteFavourite(context.getSelectedStop().getNaptanCode());

						// Display a message to the user
						Toast.makeText(context, context.getString(R.string.favouritedialogs_removed, context.getSelectedStop().getStopName()), Toast.LENGTH_LONG).show();

						// Update the list
						context.updateStopsArray(ListFavouriteStopsActivity.GET_FAVOURITE_STOPS);
						break;

					// The user chose not to remove the bus Stop from the favourites
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();
						break;
				}
				break;

			// The form to add a new bus Stop to the favourites has been validated
			case Constants.LISTFAVOURITESTOP_OPTION:
				// Get the bus stop and naptan code given by the user from the dialog interface 
				final EditText favstopNumber = (EditText)((AlertDialog)dialog).findViewById(R.id.favstopnumber);
				final EditText favstopName = (EditText)((AlertDialog)dialog).findViewById(R.id.favstopname);

				// Insert the bus Stop in the favourites
				context.getSp().insertFavourite(favstopName.getText().toString(), favstopNumber.getText().toString());

				// Update the list
				context.updateStopsArray(ListFavouriteStopsActivity.GET_FAVOURITE_STOPS);
				break;
		}
	}
}
