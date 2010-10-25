package uk.co.jakeclarke.oxfordbuses.mapoverlays;

import java.util.ArrayList;

import uk.co.jakeclarke.oxfordbuses.ListStopsActivity;
import uk.co.jakeclarke.oxfordbuses.Maps;
import uk.co.jakeclarke.oxfordbuses.StopProvider;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StopItemizedOverlay extends ItemizedOverlay {
	
	StopProvider sp;
	Context c;
	ArrayList<Stop> stopArray;
	ArrayList<StopMapNode> mapNodes;

	public StopItemizedOverlay(Drawable defaultMarker, Context c) {
		super(boundCenterBottom(defaultMarker));
		this.c = c;
		sp = new StopProvider(c);
		stopArray = new ArrayList<Stop>();
		sp.open();
		// get stops from the db;
		Cursor stopCursor = sp.getAllStops();
		int numberOfrows = sp.NumberOfRows();
		// Convert stops into a array for easy access.
		for (int i = 0; i < numberOfrows; i++)
		{
			Stop tempStop = new Stop();
			// order should be:
			//KEY_NAPTAN, KEY_COORDS, KEY_STOPNAME,KEY_STOPBEARING, KEY_PARENTMAP
			tempStop.naptancode = stopCursor.getString(0);
			tempStop.coords = stopCursor.getString(1);
			tempStop.stopName = stopCursor.getString(2);
			tempStop.stopBearing = stopCursor.getInt(3);
			tempStop.parentMap = stopCursor.getInt(4);
			stopArray.add(tempStop);
			stopCursor.moveToNext();
			
		}
		stopCursor.close();
		
		
		mapNodes = new ArrayList<StopMapNode>();
		StopMapNode currentNode = null;
		for (int i = 0; i < stopArray.size(); i++)
		{
			if (currentNode != null) {
				if (currentNode.getCoords().equals(stopArray.get(i).coords) ) {
					currentNode.AddStop(stopArray.get(i));
				} else {
					currentNode = new StopMapNode(stopArray.get(i));
					mapNodes.add(currentNode);
				}
			}
			else
			{
				currentNode = new StopMapNode(stopArray.get(i));
				mapNodes.add(currentNode);
			}
		}
		sp.close();
		this.populate();
		
	}

	@Override
	protected OverlayItem createItem(int i) {
		//old code
		/*

		GeoPoint g = null;
		OSRef osRef = new OSRef(stopArray.get(i).getAbsPosition().X, stopArray.get(i).getAbsPosition().Y);
		LatLng stopLatLang = osRef.toLatLng();
		stopLatLang.toWGS84();
		g = new GeoPoint((int)(stopLatLang.getLat() * 1000000), (int)(stopLatLang.getLng() * 1000000));
		// generate the overlay item
		OverlayItem oi = new OverlayItem(g, stopArray.get(i).stopName, "penis");
		
		return oi;
		*/
		GeoPoint g = null;
		OSRef osRef = new OSRef(mapNodes.get(i).childStops.get(0).getAbsPosition().X, mapNodes.get(i).childStops.get(0).getAbsPosition().Y);
		LatLng stopLatLang = osRef.toLatLng();
		stopLatLang.toWGS84();
		g = new GeoPoint((int)(stopLatLang.getLat() * 1000000), (int)(stopLatLang.getLng() * 1000000));
		OverlayItem oi = new OverlayItem(g, mapNodes.get(i).toString(), mapNodes.get(i).GetNodeDetails());
		return oi;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub

		return mapNodes.size();
		
	}
	
	@Override
	protected boolean onTap(final int index) {
		
		String[] stopsSting = new String[mapNodes.get(index).childStops.size()];
		for(int i = 0; i < mapNodes.get(index).childStops.size(); i++)
		{
			stopsSting[i] = mapNodes.get(index).childStops.get(i).toString();
		}

		Stop item = stopArray.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(c);
		dialog.setTitle("Pick a stop");
		dialog.setNegativeButton("Close", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		//dialog.setMessage("BlahBlah");
		dialog.setItems(stopsSting, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.oxontime.com/pip/stop.asp?naptan=" 
			    		  + StopItemizedOverlay.this.mapNodes.get(index).childStops.get(which).naptancode + "&textonly=1"));
				StopItemizedOverlay.this.c.startActivity(i);
				
			}
			
		});
		dialog.show();
		return true;
	}
	
	public class StopMapNode
	{
		public ArrayList<Stop> childStops;
		private String coords;
		
		public StopMapNode(Stop s)
		{
			childStops = new ArrayList<Stop>();
			setCoords(s.coords);
			childStops.add(s);
		}
		
		public void AddStop(Stop s)
		{
			childStops.add(s);
		}
		
		@Override
		public String toString()
		{
			
			return childStops.size() + " stops at co-ords: " ;
			
		}

		private void setCoords(String coords) {
			this.coords = coords;
		}

		public String getCoords() {
			return coords;
		}
		
		public String GetNodeDetails()
		{
			String res = "";
			for(Stop s : childStops)
			{
				res = res + s.stopName + " - " + s.naptancode + " - " + s.stopBearing + "\n";
			}
			return res;
		}
	}

}
