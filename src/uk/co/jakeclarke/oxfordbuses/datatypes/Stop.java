package uk.co.jakeclarke.oxfordbuses.datatypes;

import uk.co.jakeclarke.oxfordbuses.providers.Maps;
import android.database.Cursor;

public class Stop
{
	public static final String DIRECTIONS[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

	private String naptanCode = "";
	private String coords = "";
	private String stopName = "";
	private int stopBearing = 0;
	private int parentMap = 0;

	public Stop(){}

	public Stop(Cursor c)
	{
		naptanCode = c.getString(0);
		coords = c.getString(1);
		stopName = c.getString(2);
		stopBearing = c.getInt(3);
		parentMap = c.getInt(4);
	}

	public String getNaptanCode()
	{
		return naptanCode;
	}

	public void setNaptanCode(String naptanCode)
	{
		this.naptanCode = naptanCode;
	}

	public String getCoords()
	{
		return coords;
	}

	public void setCoords(String coords)
	{
		this.coords = coords;
	}

	public String getStopName()
	{
		return stopName;
	}

	public void setStopName(String stopName)
	{
		this.stopName = stopName;
	}

	public int getStopBearing()
	{
		return stopBearing;
	}

	public void setStopBearing(int stopBearing)
	{
		this.stopBearing = stopBearing;
	}

	public int getParentMap()
	{
		return parentMap;
	}

	public void setParentMap(int parentMap)
	{
		this.parentMap = parentMap;
	}

	public String getDirection()
	{
		int direction = ((stopBearing + 22) % 360) / 45;
		return DIRECTIONS[direction];
	}

	public MapCoordsData getAbsPosition()
	{

		int splitLoc = this.coords.indexOf(",");
		int relX = 0;

		try
		{
			relX = Integer.parseInt(this.coords.substring(0, splitLoc));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}

		int relY = 0;

		try
		{
			relY = Integer.parseInt(this.coords.substring(splitLoc + 1,this.coords.length()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		MapCoordsData md = new MapCoordsData();
		md.setX(Maps.getM().get(this.parentMap).getX() + relX * 2.2004998202088553);
		md.setY(Maps.getM().get(this.parentMap).getY() - relY * 2.1987468516915558);
		return md;
	}
	
	@Override
	public String toString()
	{
		return this.stopName + "\n- " + this.naptanCode + "\n- direction: " + this.getDirection();
	}
}
