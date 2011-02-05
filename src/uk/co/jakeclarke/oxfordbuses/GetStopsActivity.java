package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.jakeclarke.oxfordbuses.datatypes.MapCoordsData;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.Maps;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GetStopsActivity extends Activity
{
	private ProgressThread progressThread;
	private ProgressDialog progressDialog;
	private Map<Integer, MapCoordsData> md;
	private List<Stop> stops = new ArrayList<Stop>();
	private StopProvider sp;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Maps.parseMaps(GetStopsActivity.this.getAssets());

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

	public void notifyStops(int stops)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.getstopsdialogs_notifystops_number,  stops));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.getstopsdialogs_notifystops_ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				GetStopsActivity.this.finish();
				Intent i = new Intent(GetStopsActivity.this, StopMapActivity.class);
				GetStopsActivity.this.startActivity(i);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
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