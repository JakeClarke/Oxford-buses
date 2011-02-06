package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.ListFavouriteStopsActivity;
import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
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
		Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
		Toast.makeText(context, context.getString(R.string.favourite_showing_timetable), Toast.LENGTH_LONG).show();
		Intent i = new Intent(Intent.ACTION_VIEW, 
				OxontimeUtils.getTimesUri(selectedStop.getNaptanCode(), context));
		context.startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
	{
		context.setSelectedStop((Stop)parent.getAdapter().getItem(position));
		context.showDialog(Constants.LISTFAVOURITESTOP_FAVOURITE);
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch(this.type)
		{
			case Constants.LISTFAVOURITESTOP_FAVOURITE:
				switch(which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						StopProvider sp = new StopProvider(context);
						sp.deleteFavourite(context.getSelectedStop().getNaptanCode());
						Toast.makeText(context, context.getString(R.string.favouritedialogs_removed, context.getSelectedStop().getStopName()), Toast.LENGTH_LONG).show();
						context.updateStopsArray();
						break;
					
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();
						break;
				}
				break;

			case Constants.LISTFAVOURITESTOP_OPTION:
				final EditText favstopNumber = (EditText)((AlertDialog)dialog).findViewById(R.id.favstopnumber);
				final EditText favstopName = (EditText)((AlertDialog)dialog).findViewById(R.id.favstopname);
				
				context.getSp().insertFavourite(favstopName.getText().toString(), favstopNumber.getText().toString());
				context.updateStopsArray();
				break;
		}
	}
}
