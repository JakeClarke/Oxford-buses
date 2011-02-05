package uk.co.jakeclarke.oxfordbuses.mapoverlays;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.adapters.RegularStopAdapter;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.OxontimeUtils;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StopItemizedOverlay extends ItemizedOverlay
{
	StopProvider sp;
	Context c;
	List<Stop> currentNodeStops;
	List<Stop> stopArray;
	List<StopMapNode> mapNodes;

	public StopItemizedOverlay(Drawable defaultMarker, Context c)
	{
		super(boundCenterBottom(defaultMarker));
		this.c = c;
		sp = new StopProvider(c);

		// get stops from the db;
		stopArray = sp.getAllStops();

		mapNodes = new ArrayList<StopMapNode>();
		StopMapNode currentNode = null;
		for (int i = 0; i < stopArray.size(); i++)
		{
			Stop currentStop = stopArray.get(i);
			if (currentNode != null)
			{
				if (currentNode.getCoords().equals(currentStop.getCoords()))
				{
					currentNode.getChildStops().add(currentStop);
				}
				else
				{
					currentNode = new StopMapNode(currentStop);
					mapNodes.add(currentNode);
				}
			}
			else
			{
				currentNode = new StopMapNode(currentStop);
				mapNodes.add(currentNode);
			}
		}
		this.populate();
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		GeoPoint g = null;
		StopMapNode currentNode = mapNodes.get(i);
		OSRef osRef = new OSRef(currentNode.getChildStops().get(0).getAbsPosition().getX(), currentNode.getChildStops().get(0).getAbsPosition().getY());
		LatLng stopLatLang = osRef.toLatLng();
		stopLatLang.toWGS84();
		g = new GeoPoint((int)(stopLatLang.getLat() * 1000000), (int)(stopLatLang.getLng() * 1000000));
		OverlayItem oi = new OverlayItem(g, currentNode.toString(), currentNode.GetNodeDetails());
		return oi;
	}

	@Override
	public int size()
	{
		return mapNodes.size();
	}

	@Override
	protected boolean onTap(final int index)
	{
		currentNodeStops = mapNodes.get(index).getChildStops();

		AlertDialog.Builder dialog = new AlertDialog.Builder(c);
		dialog.setTitle(c.getString(R.string.stopitemizedoverlay_dialogs_pick_stop));
		dialog.setNegativeButton(c.getString(R.string.stopitemizedoverlay_dialogs_close), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		dialog.setAdapter(new RegularStopAdapter(c, currentNodeStops), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String naptan = currentNodeStops.get(which).getNaptanCode();
				Intent i = new Intent(Intent.ACTION_VIEW, 
						OxontimeUtils.getTimesUri(naptan, c));
				StopItemizedOverlay.this.c.startActivity(i);
			}
		});
		dialog.show();
		return true;
	}
}
