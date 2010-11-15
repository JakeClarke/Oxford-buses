package uk.co.jakeclarke.oxfordbuses.datatypes;

import android.database.Cursor;

import com.csvreader.CsvReader;

import uk.co.jakeclarke.oxfordbuses.Maps;
import uk.me.jstott.jcoord.*;


public class Stop {
	

	public String naptancode = "";
	public String coords = "";
	public String stopName = "";
	public int stopBearing = 0;
	public int parentMap = 0;
	
	@Override
	public String toString()
	{
		return this.stopName + "\n - " + this.naptancode + "\n - direction: " + this.getDirection();
		
	}
	
	public String getDirection()
	{
		
		String directions[] = {"N", "NE", "E", "SE", "S", "SW", "NW"};
		int direction = (int)Math.floor(  ((double)stopBearing % 360) / 45);
		
		direction %= 7;
		return directions[ direction ];
		
		
	}
	
	public Stop(){}
	
	public Stop(Cursor c)
	{
		naptancode = c.getString(0);
		coords = c.getString(1);
		stopName = c.getString(2);
		stopBearing = c.getInt(3);
		parentMap = c.getInt(4);
	}
	
	
	public MapCoordsData getAbsPosition()
	{
		
		int splitLoc = this.coords.indexOf(",");
		int relX = 0;

		try {
			relX = Integer.parseInt(this.coords.substring(0, splitLoc));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int relY = 0;

		try {
			relY = Integer.parseInt(this.coords.substring(splitLoc + 1,this.coords.length()));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		MapCoordsData md = new MapCoordsData();
		md.X = Maps.getM().get(this.parentMap).X + relX * 2.2004998202088553;
		md.Y = Maps.getM().get(this.parentMap).Y - relY * 2.1987468516915558;
		return md;
	}
	
	

}
