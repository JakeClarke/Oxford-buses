package uk.co.jakeclarke.oxfordbuses.handlers;

import uk.co.jakeclarke.oxfordbuses.GetStopsActivity;
import uk.co.jakeclarke.oxfordbuses.StopMapActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

public class GetStopsListener implements OnClickListener
{
	private GetStopsActivity context;
	
	public GetStopsListener (GetStopsActivity context)
	{
		this.context = context;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		context.finish();
		Intent i = new Intent(context, StopMapActivity.class);
		context.startActivity(i);
	}
}