package uk.co.jakeclarke.oxfordbuses.datatypes.grabbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.csvreader.*;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;

public class StopScrape {
	
	ArrayList<Stop> Stops = new ArrayList<Stop>();
	
	private URL retAddress;
	int map;
	
	
	public void Scrape()
	{
		CsvReader csvR = null;
		try {
			csvR = new CsvReader(new InputStreamReader(retAddress.openStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] headers = {"STOP", "35", "naptan-codes", "x,y", "stop-names", "stop-bearings", "count"};
		csvR.setHeaders(headers);
		csvR.setDelimiter('|');
		try {
			while(csvR.readRecord())
			{
				int count = Integer.parseInt(csvR.get("count"));
				Stop[] recordStop = new Stop[count];
				
				if (count > 1) {
					CsvReader stopReader = CsvReader.parse(csvR.get("stop-names"));
					stopReader.setDelimiter('^');
					stopReader.readRecord();
					CsvReader bearingReader = CsvReader.parse(csvR.get("stop-bearings"));
					bearingReader.setDelimiter('^');
					bearingReader.readRecord();
					CsvReader naptanReader = CsvReader.parse(csvR.get("naptan-codes"));
					naptanReader.setDelimiter('^');
					naptanReader.readRecord();
					
					for (int index = 0; index < count; index++) {
						recordStop[index] = new Stop();
						recordStop[index].coords = csvR.get("x,y");
						recordStop[index].stopName = stopReader.get(index);
						recordStop[index].parentMap = map;
						recordStop[index].stopBearing = Integer.parseInt(bearingReader.get(index));
						recordStop[index].naptancode = naptanReader.get(index);
					}
				}
				else {
					recordStop[0] = new Stop();
					recordStop[0].stopName = csvR.get("stop-names");
					recordStop[0].naptancode = csvR.get("naptan-codes");
					recordStop[0].coords = csvR.get("x,y");
					recordStop[0].stopBearing = Integer.parseInt(csvR.get("stop-bearings"));
					recordStop[0].parentMap = map;
				}
				
				
				for(Stop tempStop : recordStop){
					Stops.add(tempStop);
				}
				
				
				
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public URL getRetAddress() {
		return retAddress;
	}
	
	public ArrayList<Stop> getStops(int Map)
	{
		try {
			this.retAddress = new URL("http://oxfordshire.acislive.com/pda/mainfeed.asp?type=STOPS&maplevel=2&SessionID="+ Map +"&systemid=35&stopSelected=34000000701");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.map = Map;
		Scrape();
		return this.Stops;
	}

}
