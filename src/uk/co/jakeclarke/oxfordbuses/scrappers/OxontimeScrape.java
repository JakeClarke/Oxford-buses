package uk.co.jakeclarke.oxfordbuses.scrappers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import uk.co.jakeclarke.oxfordbuses.datatypes.Time;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;

public class OxontimeScrape
{
	private static OxontimeScrape instance = null;
	
	private OxontimeScrape ()
	{}
	
	public static OxontimeScrape getInstance()
	{
		if (instance==null)
		{
			instance = new OxontimeScrape();
		}
		return instance;
	}
	
	public List<Time> getBusStopTimes (String naptanCode) throws IOException, XPatherException
	{
		List<Time> times = new ArrayList<Time>();
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);

		URL url = new URL(String.format(Constants.TIMES_URL_PATTERN, naptanCode, "1"));
        URLConnection conn = url.openConnection();
        TagNode node = cleaner.clean(new InputStreamReader(conn.getInputStream()));

        Object[] tags = node.evaluateXPath(Constants.XPATH_TIMES);
        
        for (int j = 0 ; j < tags.length ; j+=3)
        {
        	TagNode current_tag = (TagNode) tags[j];
	        String service = current_tag.getChildren().iterator().next().toString().trim();
	        service = service.substring(0, service.indexOf("&nbsp;"));

	        current_tag = (TagNode) tags[j+1];
	        String destination = current_tag.getChildren().iterator().next().toString().trim();
	        destination = destination.substring(0, destination.indexOf("&nbsp;"));

	        current_tag = (TagNode) tags[j+2];
	        String text_value = current_tag.getChildren().iterator().next().toString().trim();
	        int delay;
	        if (text_value.indexOf("DUE")==-1)
	        {
	        	text_value = text_value.substring(0, text_value.indexOf(" mins"));
		        delay = Integer.parseInt(text_value);
	        }
	        else
	        {
	        	delay = 0;
	        }
	        
	        times.add(new Time(service, destination, delay));
        }
		return times;
	}
}
