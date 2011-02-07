package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.jakeclarke.oxfordbuses.datatypes.MapCoordsData;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.handlers.GetStopsListener;
import uk.co.jakeclarke.oxfordbuses.providers.Maps;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Activity scrapping the web to retrieve bus stops and their attributes from a list of bus stop map number
 *
 */
public class GetStopsActivity extends Activity
{
	private ProgressThread progressThread;
	private ProgressDialog progressDialog;
	private Map<Integer, MapCoordsData> md;
	private List<Stop> stops = new ArrayList<Stop>();
	private StopProvider sp;
	private GetStopsListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Maps.parseMaps(GetStopsActivity.this.getAssets());
		
		listener = new GetStopsListener(this);

		md = Maps.getM();
		sp = new StopProvider(this);
		progressDialog = new ProgressDialog(GetStopsActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(md.size());
		progressDialog.setMessage(getString(R.string.getstopsdialogs_getting_stops));
		progressDialog.setCancelable(false);
		progressDialog.show();

		if(progressThread == null || !progressThread.isAlive())
		{
			progressThread = new ProgressThread(this, new Handler()
			{
				public void handleMessage(Message msg)
				{
					int total = msg.getData().getInt("total");
					progressDialog.setProgress(total);
					if (total >= md.size())
					{
						//dismissDialog(0);
						progressDialog.dismiss();
					}
				}
			});
			progressThread.start();
		}
		else
		{
			progressThread.setHandler(new Handler()
			{
				public void handleMessage(Message msg)
				{
					int total = msg.getData().getInt("total");
					progressDialog.setProgress(total);
					if (total >= md.size())
					{
						//dismissDialog(0);
						progressDialog.dismiss();
					}
				}
			});
		}
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
		// When the database is populated, show to the user a count of all the bus stop found
		return new AlertDialog.Builder(this)
			.setMessage(getString(R.string.getstopsdialogs_notifystops_number,  stops.size()))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.getstopsdialogs_notifystops_ok), listener)
			.create();
    }

	public Map<Integer, MapCoordsData> getMd()
	{
		return md;
	}

	public List<Stop> getStops()
	{
		return stops;
	}

	public StopProvider getSp()
	{
		return sp;
	}
}