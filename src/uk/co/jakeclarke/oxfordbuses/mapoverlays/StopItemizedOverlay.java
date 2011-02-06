package uk.co.jakeclarke.oxfordbuses.mapoverlays;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.StopMapActivity;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.providers.StopProvider;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StopItemizedOverlay extends ItemizedOverlay
{
	private StopProvider sp;
	private StopMapActivity context;
	private List<Stop> stopArray;
	private List<StopMapNode> mapNodes;

	public StopItemizedOverlay(Drawable defaultMarker, StopMapActivity c)
	{
		super(boundCenterBottom(defaultMarker));
		this.context = c;
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
		context.setCurrentNodeStops(mapNodes.get(index).getChildStops());
		context.showDialog(Constants.STOPMAP_DIALOG_TAP);
		return true;
	}
}
