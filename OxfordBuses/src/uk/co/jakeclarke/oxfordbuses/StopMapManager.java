package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public final class StopMapManager {
	
	private Context context;
	private GoogleMap map;
	private RequestQueue requestQueue;
	private static final String requestUrl = "http://www.oxontime.com/MapWebService.asmx/GetMarker";
	private static final String requestBody = "{\"Layers\":\"naptanbus\",\"DateType\":0,\"FromDate\":\"\",\"ToDate\":\"\",\"ZoomLevel\":\"135.0\",\"Easting\":402451,\"Northing\":149757,\"EastingEnd\":530431,\"NorthingEnd\":263157,\"IsLonLat\":false}";
	private JSONObject request;
	private final Listener<JSONObject> listener;
	private final ErrorListener errorListener;
	private JsonObjectRequest jr;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	
	public StopMapManager(Context context, final GoogleMap map) {
		this.context = context;

		
		this.listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				stops.clear();
				try {
					JSONArray d = response.getJSONArray("d");
					JSONObject d_0 = d.getJSONObject(0);
					// get all clusters.
					JSONArray clusters = d_0.getJSONArray("Clusters");
					
					for(int i = 0; i < clusters.length(); i++) {
						try {
							// get the cluster of markers
							JSONObject cluster = clusters.getJSONObject(i);
							// get all the markers
							JSONArray markers = cluster.getJSONArray("Markers");
							for (int mi = 0; mi < markers.length(); mi++) {
								// create a stop!
								try {
									Stop s = Stop.fromJSON(markers
											.getJSONObject(mi));
									stops.add(s);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					showErrorDialog();
				}
				
				Log.d("Stops parser", "Number of stops retrieved: " + stops.size());
				
				map.clear();
				
				for(int i = 0; i < stops.size(); i++) {
					// this does nothing.
					map.addMarker(new MarkerOptions()
			        .position(new LatLng(0, 0))
			        .title(stops.get(i).Name));
				}
			}
			
			
		};
		
		this.errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showErrorDialog();
			}
			
		};
		
		this.requestQueue = Volley.newRequestQueue(this.context);
		
		try {
			this.request = new JSONObject(requestBody);
			
			jr = new JsonObjectRequest(Method.POST, requestUrl, this.request, this.listener, this.errorListener);
		} catch (JSONException e) {
			e.printStackTrace();
			showErrorDialog();
			
		}
		
	}
	
	public void updateStops() {
		if(jr != null)
			this.requestQueue.add(jr);
		else {
			showErrorDialog();
		}
	}
	
	private static class Stop {
		public String Name;
		public String Scn;
		public int Northing;
		public int Easting;
		public String Naptan;
		
		public static Stop fromJSON(JSONObject jo) throws JSONException {
			Stop s = new Stop();
			s.Easting = jo.getInt("Easting");
			s.Northing = jo.getInt("Northing");
			s.Scn = jo.getString("Scn");
			JSONArray summary = jo.getJSONArray("Summary");
			s.Name = summary.getString(1);
			s.Naptan = summary.getString(2);
			
			return s;
		}

	}
	
	private void showErrorDialog() {
		
	}
	

	
	
}
