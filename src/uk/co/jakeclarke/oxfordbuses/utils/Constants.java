package uk.co.jakeclarke.oxfordbuses.utils;

public class Constants
{
	public static final String TAG = "StopProvider";
	public static final String DATABASE_NAME = "stop.db";
	public static final int DATABASE_VERSION = 3;
	public static final String TABLE_NAME = "stops";
	public static final String FAVOURITES_TABLE_NAME = "favourites";
	public static final String KEY_NAPTAN = "naptancode";
	public static final String KEY_COORDS = "coords";
	public static final String KEY_STOPNAME = "stopName";
	public static final String KEY_STOPBEARING = "stopBearing";
	public static final String KEY_PARENTMAP = "parentMap";
	public static final String KEY_ID = " _id";
	
	// Caution: Parameterized String
	public static final String OXFORDSHIRE_SOURCE = "http://oxfordshire.acislive.com/pda/mainfeed.asp?type=STOPS&maplevel=2&SessionID=%1$s&systemid=35&stopSelected=34000000701";

	public static final String SCRAP_STOP = "STOP";
	public static final String SCRAP_35 = "35";
	public static final String SCRAP_NAPTAN_CODES = "naptan-codes";
	public static final String SCRAP_COORD = "x,y";
	public static final String SCRAP_STOP_NAMES = "stop-names";
	public static final String SCRAP_STOP_BEARINGS = "stop-bearings";
	public static final String SCRAP_COUNT = "count";
	
	public static final int STOPMAP_DIALOG_SEARCH = 0;
	public static final int STOPMAP_DIALOG_PROMPT = 1;
	public static final int STOPMAP_DIALOG_TAP = 2;
	
	public static final int LISTSTOP_DIALOG = 0;
	
	public static final int LISTFAVOURITESTOP_FAVOURITE = 0;
	public static final int LISTFAVOURITESTOP_OPTION = 1;
}
