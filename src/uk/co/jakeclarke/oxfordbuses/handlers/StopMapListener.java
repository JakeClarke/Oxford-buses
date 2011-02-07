package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.GetStopsActivity;
import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.StopMapActivity;
import uk.co.jakeclarke.oxfordbuses.adapters.RegularStopAdapter;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

/**
 * Action listener of the StopMapListener activity
 *
 */
public class StopMapListener implements OnClickListener
{
	private StopMapActivity context;
	private int type;

	public StopMapListener (StopMapActivity context, int type)
	{
		this.context = context;
		this.type = type;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		Intent i = null ;
		switch(this.type)
		{
			// The search form validated
			case Constants.STOPMAP_DIALOG_SEARCH:
				// Retrieve the naptan code typed by the user
				String naptanCode = ((EditText)((AlertDialog)dialog).findViewById(R.id.NaptanEditText)).getText().toString();
				
				// Create a new Intent and launch the application abble to read the URI built from the naptan code
				i = new Intent(Intent.ACTION_VIEW, OxontimeUtils.getTimesUri(naptanCode, context));
				context.startActivity(i);
				break;

			// The prompt asks the user to build and populate the database
			case Constants.STOPMAP_DIALOG_PROMPT:
				switch(which)
				{
					// The user's answer is Yes
					case DialogInterface.BUTTON_POSITIVE:
						// Create a new Intent and launch the GetStopsActivity activity
						i = new Intent(context, GetStopsActivity.class);
						context.startActivity(i);
						
						// Finish the current activity
						context.finish();
						break;

					// The user's answer is No
					case DialogInterface.BUTTON_NEGATIVE:
						// Just finish the current activity
						context.finish();
						break;
				}
				break;

			// The user has Tapped a bus stop Node on the map
			case Constants.STOPMAP_DIALOG_TAP:
				switch(which)
				{
					// The user cancel the action
					case DialogInterface.BUTTON_NEGATIVE:
						// Hide the dialog
						dialog.dismiss();
						break;
		
					// The user selects a bus Stop from the list
					default:
						// Get the naptan code of the selected bus Stop
						String naptan = ((RegularStopAdapter)((AlertDialog)dialog).getListView().getAdapter()).getItem(which).getNaptanCode();

						// Create a new Intent and launch the application abble to read the URI built from the naptan code
						i = new Intent(Intent.ACTION_VIEW, OxontimeUtils.getTimesUri(naptan, context));
						context.startActivity(i);
				}
				break;
		}
	}

	public void setType (int type)
	{
		this.type = type;
	}
}
