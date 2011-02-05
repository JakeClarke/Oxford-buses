package uk.co.jakeclarke.oxfordbuses.scrappers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;

import com.csvreader.CsvReader;

public class StopScrape
{
	private List<Stop> stops = new ArrayList<Stop>();
	private URL retAddress;
	private int map;
	
	public void scrape()
	{
		CsvReader csvR = null;
		try
		{
			csvR = new CsvReader(new InputStreamReader(retAddress.openStream()));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		String[] headers = {Constants.SCRAP_STOP,
							Constants.SCRAP_35,
							Constants.SCRAP_NAPTAN_CODES,
							Constants.SCRAP_COORD,
							Constants.SCRAP_STOP_NAMES,
							Constants.SCRAP_STOP_BEARINGS,
							Constants.SCRAP_COUNT};
		csvR.setHeaders(headers);
		csvR.setDelimiter('|');
		try
		{
			while(csvR.readRecord())
			{
				int count = Integer.parseInt(csvR.get(Constants.SCRAP_COUNT));
				Stop[] recordStop = new Stop[count];
				
				if (count > 1)
				{
					CsvReader stopReader = CsvReader.parse(csvR.get(Constants.SCRAP_STOP_NAMES));
					stopReader.setDelimiter('^');
					stopReader.readRecord();
					CsvReader bearingReader = CsvReader.parse(csvR.get(Constants.SCRAP_STOP_BEARINGS));
					bearingReader.setDelimiter('^');
					bearingReader.readRecord();
					CsvReader naptanReader = CsvReader.parse(csvR.get(Constants.SCRAP_NAPTAN_CODES));
					naptanReader.setDelimiter('^');
					naptanReader.readRecord();
					
					for (int index = 0; index < count; index++)
					{
						recordStop[index] = new Stop();
						recordStop[index].setCoords(csvR.get(Constants.SCRAP_COORD));
						recordStop[index].setStopName(stopReader.get(index));
						recordStop[index].setParentMap(map);
						recordStop[index].setStopBearing(Integer.parseInt(bearingReader.get(index)));
						recordStop[index].setNaptanCode(naptanReader.get(index));
					}
				}
				else
				{
					recordStop[0] = new Stop();
					recordStop[0].setStopName(csvR.get(Constants.SCRAP_STOP_NAMES));
					recordStop[0].setNaptanCode(csvR.get(Constants.SCRAP_STOP_BEARINGS));
					recordStop[0].setCoords(csvR.get(Constants.SCRAP_COORD));
					recordStop[0].setStopBearing(Integer.parseInt(csvR.get(Constants.SCRAP_STOP_BEARINGS)));
					recordStop[0].setParentMap(map);
				}
				
				for(Stop tempStop : recordStop)
				{
					stops.add(tempStop);
				}
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public URL getRetAddress()
	{
		return retAddress;
	}
	
	public List<Stop> getStops(int Map)
	{
		try
		{
			this.retAddress = new URL(String.format(Constants.OXFORDSHIRE_SOURCE, Map));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		this.map = Map;
		scrape();
		return this.stops;
	}
}
