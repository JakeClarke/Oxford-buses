package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.ListStopsActivity;
import uk.co.jakeclarke.oxfordbuses.ListTimesActivity;
import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Action listener of the ListStopsListener activity
 *
 */
public class ListStopsListener implements OnItemClickListener, OnItemLongClickListener, OnClickListener
{
	private ListStopsActivity context;
	
	public ListStopsListener(ListStopsActivity context)
	{
		this.context = context;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Get the selected bus Stop from the list
		Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
		
		// Get its name
		final String stopName = selectedStop.getStopName(), stopNaptan = selectedStop.getNaptanCode();
		
		// Show it to the user
		Toast.makeText(context, stopName, Toast.LENGTH_SHORT).show();

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
		context.showDialog(Constants.LISTSTOP_DIALOG);
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch(which)
		{
			// The user chose to add the bus Stop to the favourites
			case DialogInterface.BUTTON_POSITIVE:
				// Insert the bus Stop in the favourites
				context.getSp().insertFavourite(context.getSelectedStop().getStopName(), context.getSelectedStop().getNaptanCode());

				// Display a message to the user
				Toast.makeText(context, context.getString(R.string.liststopsdialogs_added), Toast.LENGTH_LONG).show();
				break;

			// The user chose not to add the bus Stop to the favourites
			case DialogInterface.BUTTON_NEGATIVE:
				// Just hide the dialog
				dialog.dismiss();
				break;
		}
	}
}
