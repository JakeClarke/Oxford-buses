package uk.co.jakeclarke.oxfordbuses;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.scrappers.StopScrape;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ProgressThread extends Thread
{
	private Activity context;
	private Handler mHandler;
	private int total;

	public ProgressThread(Activity context, Handler handler)
	{
		this.context = context;
		this.mHandler = handler;
	}

	void setHandler(Handler handler)
	{
		mHandler = handler;
	}

	public void run()
	{
		if(context instanceof GetStopsActivity)
		{
			GetStopsActivity localContext = (GetStopsActivity) context;
			total = 0;

			// For each key in the Csv file
			for (int map : localContext.getMd().keySet())
			{
				// Go scrape the web and retrieve a list of Stops
				StopScrape scraper = new StopScrape();
				List<Stop> tempStops = scraper.getStops(map);

				// For each Stop in the list, add it to the Stop list of the Activity
				for(Stop istop : tempStops)
				{
					localContext.getStops().add(istop);
				}

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("total", total + 1);
				msg.setData(b);
				mHandler.sendMessage(msg);
				total++;
			}

			// Clear the database (the stops table, not the favourites)
			StopProvider sp = localContext.getSp();
			sp.clearStops();

			// Display a ProgressDialog for the user to wait
			mHandler.post(new Runnable()
			{
				public void run()
				{
					ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage(context.getString(R.string.getstopsdialogs_saving_stops));
					progressDialog.show();
				}
			});
			
			// For each Stop retrieved later, save in into the database
			for(Stop istop : localContext.getStops())
			{
				sp.insertStop(istop);
			}

			// When the database is populated, show to the user a count of all the bus stop found
			mHandler.post(new Runnable()
			{
				public void run()
				{
					GetStopsActivity localContext = (GetStopsActivity) context;
					localContext.showDialog(0);
				}
			});
		}
	}

}