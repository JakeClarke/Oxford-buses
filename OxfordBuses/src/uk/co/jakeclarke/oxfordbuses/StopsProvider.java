package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.jakeclarke.oxfordbuses.db.StopsDatabase;
import uk.me.jstott.jcoord.OSRef;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

/**
 * Provides the stops for the map. Manages the database and queries the service
 * when requested.
 */
public final class StopsProvider {

	private Context context;
	private RequestQueue requestQueue;
	private static final String requestUrl = "http://www.oxontime.com/MapWebService.asmx/GetMarker";
	private static final String requestBody = "{\"Layers\":\"naptanbus\",\"DateType\":0,\"FromDate\":\"\",\"ToDate\":\"\",\"ZoomLevel\":\"3.0\",\"Easting\":402451,\"Northing\":149757,\"EastingEnd\":530431,\"NorthingEnd\":263157,\"IsLonLat\":false}";
	private JSONObject request;
	private final Listener<JSONObject> listener;
	private final ErrorListener errorListener;
	private JsonObjectRequest jr;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private StopUpdateListener updateListener;
	private StopsDatabase stopDB;

	public StopsProvider(final Context context,
			StopUpdateListener updateListener) {
		this.context = context;
		this.stopDB = new StopsDatabase(this.context);
		this.updateListener = updateListener;

		this.listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				stops.clear();
				int errors = 0;
				try {
					JSONArray d = response.getJSONArray("d");
					JSONObject d_0 = d.getJSONObject(0);
					// get all clusters.
					JSONArray clusters = d_0.getJSONArray("Clusters");

					for (int i = 0; i < clusters.length(); i++) {
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
									errors++;
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							errors++;
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					notifyError();
				}

				Log.d("Stops parser",
						"Number of stops retrieved: " + stops.size()
								+ ", errors:" + errors);

				if (errors == 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							synchronized (stopDB) {
								stopDB.open();
								Log.d("Stop db", "Saving stops!");
								stopDB.clear();
								stopDB.insertStops(stops.toArray(new Stop[0]));
								stopDB.close();
							}
							Log.d("Stop db", "Saving stops complete!");
						}
					}).start();
				}
				notifyUpdate();
			}

		};

		this.errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				notifyError();
			}

		};

		this.requestQueue = Volley.newRequestQueue(this.context);

		try {
			this.request = new JSONObject(requestBody);

			jr = new JsonObjectRequest(Method.POST, requestUrl, this.request,
					this.listener, this.errorListener);
		} catch (JSONException e) {
			e.printStackTrace();
			notifyError();

		}

		try {
			synchronized (stopDB) {
				// lets get all our saved stops
				this.stopDB.open();
				Stop[] cachedStops = this.stopDB.getAllStops();
				if (cachedStops != null) {
					// our list of saved stops.
					this.stops.addAll(Arrays.asList(cachedStops));
					notifyUpdate();
				} else {
					// there are no saved stops, get them.
					this.updateStops();
				}
				this.stopDB.close();
			}
		} catch (SQLException e) {
			Toast.makeText(this.context, "Error getting stops",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	/**
	 * Queries the web service and will notify the listener when there is a new
	 * list of stops.
	 */
	public void updateStops() {
		if (jr != null) {
			Toast.makeText(this.context, R.string.act_refreshing,
					Toast.LENGTH_SHORT).show();
			this.requestQueue.add(jr);
		} else {
			notifyError();
		}
	}

	public static class Stop {
		public String Name;
		public String Scn;
		public LatLng latlong;
		public String Naptan;

		public static Stop fromJSON(JSONObject jo) throws JSONException {
			Stop s = new Stop();
			OSRef osRef = new OSRef(jo.getInt("Easting"), jo.getInt("Northing"));
			uk.me.jstott.jcoord.LatLng stopLatLang = osRef.toLatLng();
			stopLatLang.toWGS84();

			s.latlong = new LatLng(stopLatLang.getLat(), stopLatLang.getLng());

			s.Scn = jo.getString("Scn");
			JSONArray summary = jo.getJSONArray("Summary");
			s.Name = summary.getString(1);
			s.Naptan = summary.getString(2);

			return s;
		}

		@Override
		public String toString() {
			return this.Name + ", " + this.Naptan;
		}

	}

	public abstract static class StopUpdateListener {

		abstract void onUpdate(StopsProvider stopMapManager);

		abstract void onError(StopsProvider stopMapManager);
	}

	public void setListener(StopUpdateListener listener) {
		this.updateListener = listener;
	}

	private void notifyError() {
		if (this.updateListener != null)
			this.updateListener.onError(this);
	}

	private void notifyUpdate() {
		if (this.updateListener != null)
			this.updateListener.onUpdate(this);
	}

	public Stop[] getStops() {
		return this.stops.toArray(new Stop[0]);
	}

}
