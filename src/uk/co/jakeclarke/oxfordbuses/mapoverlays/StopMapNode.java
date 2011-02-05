package uk.co.jakeclarke.oxfordbuses.mapoverlays;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;

public class StopMapNode
{
	private List<Stop> childStops;
	private String coords;

	public StopMapNode(Stop s)
	{
		this.childStops = new ArrayList<Stop>();
		this.coords = s.getCoords();
		this.childStops.add(s);
	}

	public List<Stop> getChildStops()
	{
		return childStops;
	}

	public void setChildStops(List<Stop> childStops)
	{
		this.childStops = childStops;
	}

	public String getCoords()
	{
		return coords;
	}

	public void setCoords(String coords)
	{
		this.coords = coords;
	}

	public String GetNodeDetails()
	{
		String res = "";
		for(Stop s : childStops)
		{
			res = res + s.getStopName() + " - " + s.getNaptanCode() + " - " + s.getStopBearing() + "\n";
		}
		return res;
	}

	@Override
	public String toString()
	{
		return childStops.size() + " stops at co-ords: " ;
	}
}