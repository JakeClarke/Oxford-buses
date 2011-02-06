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
			case Constants.STOPMAP_DIALOG_SEARCH:
				EditText naptanEt = (EditText)((AlertDialog)dialog).findViewById(R.id.NaptanEditText);
				i = new Intent(Intent.ACTION_VIEW, 
						OxontimeUtils.getTimesUri(naptanEt.getText().toString(), context));
				context.startActivity(i);
				break;

			case Constants.STOPMAP_DIALOG_PROMPT:
				switch(which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						i = new Intent(context, GetStopsActivity.class);
						context.startActivity(i);
						context.finish();
						break;
		
					case DialogInterface.BUTTON_NEGATIVE:
						context.finish();
						break;
				}
				break;

			case Constants.STOPMAP_DIALOG_TAP:
				switch(which)
				{
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();
						break;
		
					default:
						String naptan = ((RegularStopAdapter)((AlertDialog)dialog).getListView().getAdapter()).getItem(which).getNaptanCode();
						i = new Intent(Intent.ACTION_VIEW, 
								OxontimeUtils.getTimesUri(naptan, context));
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
