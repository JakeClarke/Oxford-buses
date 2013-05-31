package uk.co.jakeclarke.oxfordbuses;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import uk.co.jakeclarke.oxfordbuses.datatypes.MapCoordsData;

import android.content.res.AssetManager;

import com.csvreader.*;

 public class Maps {
	static private Map<Integer, MapCoordsData> m = null;
	
	static public void ParseMaps(AssetManager am)
	{
		if (m == null) {
			
			m = new HashMap<Integer, MapCoordsData>();
			
			// construct the table.
			CsvReader cr = null;
			try {
				cr = new CsvReader(new InputStreamReader(am.open("maps.csv")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while (cr.readRecord()) {
					m.put(Integer.parseInt(cr.get(0)), new MapCoordsData(Double
							.parseDouble(cr.get(1)), Double.parseDouble(cr
							.get(2))));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	static public Map<Integer, MapCoordsData> getM() {
		return m;
	}


}
 

