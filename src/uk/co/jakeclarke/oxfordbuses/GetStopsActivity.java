package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import uk.co.jakeclarke.oxfordbuses.datatypes.MapCoordsData;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.datatypes.grabbers.*;

public class GetStopsActivity extends Activity {

	int MapNumbers[] = {2507, 2511,2512, 2513, 2516, 2517, 2520};
	static ProgressThread progressThread;
	ProgressDialog progressDialog;
	Map<Integer, MapCoordsData> md;
	
	ArrayList<Stop> stops = new ArrayList<Stop>();
	
	StopProvider sp;
	
	final Runnable stopsDialog = new Runnable(){
		public void run() {
			GetStopsActivity.this.notifyStops(stops.size());
		}
	};
	
	final Runnable saveStops = new Runnable()
	{
		public void run()
		{
			ProgressDialog progressDialog = new ProgressDialog(GetStopsActivity.this);
	        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progressDialog.setMessage(getString(R.string.getstopsdialogs_saving_stops));
	        progressDialog.show();
	        
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Maps.ParseMaps(GetStopsActivity.this.getAssets());
        
        
        md = Maps.getM();
        progressDialog = new ProgressDialog(GetStopsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(md.size());
        progressDialog.setMessage(getString(R.string.getstopsdialogs_getting_stops));
        progressDialog.setCancelable(false);
        sp = new StopProvider(this);
        progressDialog.show();
        
        if(progressThread == null || !progressThread.isAlive()){
        	progressThread = new ProgressThread(new Handler() {
                public void handleMessage(Message msg) {
                    int total = msg.getData().getInt("total");
                    progressDialog.setProgress(total);
                    if (total >= md.size()){
                        //dismissDialog(0);
                        progressDialog.dismiss();
                    }
                }
            });
        	progressThread.start();
        }
        else {
            
            progressThread.setHandler(new Handler() {
                public void handleMessage(Message msg) {
                    int total = msg.getData().getInt("total");
                    progressDialog.setProgress(total);
                    if (total >= md.size()){
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
    	builder.setPositiveButton(getString(R.string.getstopsdialogs_notifystops_ok), new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   GetStopsActivity.this.finish();
        	   Intent i = new Intent(GetStopsActivity.this, StopMapActivity.class);
        	   GetStopsActivity.this.startActivity(i);
           }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    
    private class ProgressThread extends Thread {
        Handler mHandler;
        int total;
       
        ProgressThread(Handler h) {
            mHandler = h;
        }
        
        void setHandler(Handler handler)
        {
        	mHandler = handler;
        }
       
        public void run() {
        	
            total = 0;
            
            for (int Map : GetStopsActivity.this.md.keySet() ) {
            	StopScrape scraper = new StopScrape();
            	ArrayList<Stop> tempStops = null;
            	tempStops = scraper.getStops(Map);
				
				
				for(Stop istop : tempStops) {
					stops.add(istop);
				}
				
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total + 1);
                msg.setData(b);
                mHandler.sendMessage(msg);
                total++;
            }
            
            sp.open();
            sp.clear();
            
            
            mHandler.post(saveStops);
            for(Stop istop : stops)
            {
            	sp.insertStop(istop.stopName, istop.naptancode, istop.coords, istop.stopBearing, istop.parentMap);
            }
            
            sp.close();
            mHandler.post(stopsDialog);
        }
        
    }
}
