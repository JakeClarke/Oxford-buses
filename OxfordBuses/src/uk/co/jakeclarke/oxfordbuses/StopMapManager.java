package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.me.jstott.jcoord.OSRef;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

public final class StopMapManager {

	private Context context;
	private RequestQueue requestQueue;
	private static final String requestUrl = "http://www.oxontime.com/MapWebService.asmx/GetMarker";
	private static final String requestBody = "{\"Layers\":\"naptanbus\",\"DateType\":0,\"FromDate\":\"\",\"ToDate\":\"\",\"ZoomLevel\":\"135.0\",\"Easting\":402451,\"Northing\":149757,\"EastingEnd\":530431,\"NorthingEnd\":263157,\"IsLonLat\":false}";
	private JSONObject request;
	private final Listener<JSONObject> listener;
	private final ErrorListener errorListener;
	private JsonObjectRequest jr;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private StopUpdateListener updateListener;

	public StopMapManager(Context context) {
		this.context = context;

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

	}

	public void updateStops() {
		if (jr != null)
			this.requestQueue.add(jr);
		else {
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

	}

	public abstract static class StopUpdateListener {

		abstract void onUpdate(StopMapManager stopMapManager);

		abstract void onError(StopMapManager stopMapManager);
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
