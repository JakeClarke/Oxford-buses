package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Time;
import uk.co.jakeclarke.oxfordbuses.scrappers.OxontimeScrape;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity listing all the times of a bus stop in an activity rather than openning the browser
 *
 */
public class ListTimesActivity extends ListActivity
{
	private String stopName;
	private String naptanCode;
	private List<Time> timesList = new ArrayList<Time>();
    private ProgressDialog mProgressDialog;
    final Handler uiThreadCallback = new Handler();
    
    /**
     * Refresh the list of times
     */
    @Override
    protected void onStart()
    {
    	super.onStart();
    	refreshTimesList();
	}
    
    /**
     * Refresh and display the times of a bus stop
     */
    public void refreshTimesList ()
    {
    	// Launch the progress dialog
    	mProgressDialog = ProgressDialog.show(this,
                "", getResources().getString(R.string.loading), true);

    	// Action when the notes have been requested from the database
		final Runnable runInUIThread = new Runnable()
		{
			public void run()
			{
				// If some departures information have been found, display them, otherwise, display a message
				if (timesList.size()>0)
				{
					ListTimesActivity.this.setListAdapter(new ArrayAdapter<Time>(ListTimesActivity.this, R.layout.stoplistitem, timesList));
				}
				else
				{
					ListTimesActivity.this.showDialog(0);
				}
			}
		};
	
		// Launch the retrieving of the notes in a new thread
		new Thread()
		{
			@Override public void run()
			{
				// Populate the list of Times
				try
				{
					// Go scrap the oxontime's website
					timesList = OxontimeScrape.getInstance().getBusStopTimes(naptanCode);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					
					// If an error occurs (a change in the web page layout for instance), open the browser like before
					Toast.makeText(ListTimesActivity.this, R.string.error_scrapping, Toast.LENGTH_SHORT).show();
					Intent i = new Intent(Intent.ACTION_VIEW, OxontimeUtils.getTimesUri(naptanCode, ListTimesActivity.this));
					ListTimesActivity.this.startActivity(i);
					ListTimesActivity.this.finish();
				}
				uiThreadCallback.post(runInUIThread);
				mProgressDialog.dismiss();
			}
		}.start();
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		stopName = getIntent().getStringExtra("stopName");
		naptanCode = getIntent().getStringExtra("naptanCode");
		
		setTitle(getString(R.string.times_title, stopName));
	}
	
    @Override
    protected Dialog onCreateDialog(int id)
    {
		// Show the error message saying that no departure information are available
		TextView tv = new TextView (ListTimesActivity.this);
		tv.setText(getString(R.string.listtimesdialogs_no_departure_info_available));
		return new AlertDialog.Builder(ListTimesActivity.this)
			.setIcon(R.drawable.alert_dialog_icon)
			.setTitle(R.string.listtimesdialogs_no_departures)
			.setView(tv)
			.show();
    }
}