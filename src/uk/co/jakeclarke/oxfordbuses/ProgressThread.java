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

			for (int Map : localContext.getMd().keySet())
			{
				StopScrape scraper = new StopScrape();
				List<Stop> tempStops = scraper.getStops(Map);

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

			StopProvider sp = localContext.getSp();
			sp.clearStops();

			mHandler.post(new Runnable()
			{
				public void run()
				{
					GetStopsActivity localContext = (GetStopsActivity) context;
					localContext.notifyStops(localContext.getStops().size());
				}
			});
			
			for(Stop istop : ((GetStopsActivity) context).getStops())
			{
				sp.insertStop(istop);
			}

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
		}
	}

}