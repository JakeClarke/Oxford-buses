package uk.co.jakeclarke.oxfordbuses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity {
	
	StopProvider sp;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // get the list of maps loaded into memory
        Maps.ParseMaps(getAssets());
        
        Button ListStopButton = (Button)findViewById(R.id.ListStopsButton);
        Button MapStopButton = (Button)findViewById(R.id.mapstopsbutton);
        Button RefreshDBButton = (Button)findViewById(R.id.RefreshDBButton);
        RefreshDBButton.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				 Intent i = new Intent(MenuActivity.this, GetStopsActivity.class);
			        startActivity(i);
			}
        });
        
        MapStopButton.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				 Intent i = new Intent(MenuActivity.this, StopMapActivity.class);
			        startActivity(i);
			}
        });
        
        ListStopButton.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				 Intent i = new Intent(MenuActivity.this, ListStopsActivity.class);
			        startActivity(i);
			}
        });
        
        sp = new StopProvider(this);
        updateStopCount();
        
        
    }

	private void updateStopCount() {
		
		sp.open();
        TextView stopCount = (TextView)findViewById(R.id.stopcount);
        stopCount.setText("Current stops in db: " + sp.NumberOfRows());
        sp.close();
	}
    
    @Override 
    protected void onResume ()
    {
    	updateStopCount();
    	super.onResume();
    }
}