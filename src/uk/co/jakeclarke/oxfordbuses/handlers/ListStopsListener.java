package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.ListStopsActivity;
import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

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
		Stop selectedStop = (Stop)parent.getAdapter().getItem(position);
		final String stopName = selectedStop.getStopName(), stopNaptan = selectedStop.getNaptanCode();
		Toast.makeText(context, stopName, Toast.LENGTH_SHORT).show();
		Intent i = new Intent(Intent.ACTION_VIEW, 
				OxontimeUtils.getTimesUri(stopNaptan, context));
		context.startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
	{
		context.setSelectedStop((Stop)parent.getAdapter().getItem(position));
		context.showDialog(Constants.LISTSTOP_DIALOG);
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch(which)
		{
			case DialogInterface.BUTTON_POSITIVE:
				context.getSp().insertFavourite(context.getSelectedStop().getStopName(), context.getSelectedStop().getNaptanCode());
				Toast.makeText(context, context.getString(R.string.liststopsdialogs_added), Toast.LENGTH_LONG).show();
				break;
			
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
		}
	}
}
