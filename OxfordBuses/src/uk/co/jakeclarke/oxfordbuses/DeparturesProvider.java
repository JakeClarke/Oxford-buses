package uk.co.jakeclarke.oxfordbuses;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.jakeclarke.oxfordbuses.DeparturesProvider.Departures.Bus;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class DeparturesProvider {

	private static final String REQUESTURL = "http://www.oxontime.com/MapWebService.asmx/GetDepartures";
	private static final int UPDATE_DELAY = 60 * 1000;

	private final Stop stop;
	private final Departures departures;
	private DeparturesUpdateListener listener;
	private final RequestQueue requestQueue;
	private final Context context;
	private final JsonObjectRequest jsonRequest;
	private final Listener<JSONObject> jsonResponseListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {
			Log.d("DeparturesProvider", "Got response! (scn: " + stop.Scn + ")");
			ArrayList<Bus> buses = new ArrayList<Bus>();
			JSONArray d = response.optJSONArray("d");
			if (d == null) {
				departures.buses = buses;
				notifyUpdate();
				return;
			}
			for (int i = 0; i < d.length(); i++) {
				try {
					JSONObject bjo = d.getJSONObject(i);
					Departures.Bus b = new Departures.Bus();
					b.destination = bjo.getString("Destination");
					if (b.destination == "null")
						break;
					b.destination = b.destination.replace("&nbsp;", " ");
					b.service = bjo.getString("Service");
					b.service = b.service.replace("&nbsp;", " ");
					b.time = bjo.getString("Time");
					b.time = b.time.replace("&nbsp;", " ");
					buses.add(b);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			departures.buses = buses;
			notifyUpdate();

		}
	};

	private final ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			notifyError();
		}

	};

	private final Handler handler = new Handler();

	private final Runnable updateRunable = new Runnable() {

		@Override
		public void run() {
			Log.d("DeparturesProvider", "Added request.");
			requestQueue.add(jsonRequest);
			if (shouldAutoUpdate)
				handler.postDelayed(updateRunable, UPDATE_DELAY);
		}

	};

	private boolean shouldAutoUpdate = false;

	public DeparturesProvider(Context context, Stop stop) {
		this.stop = stop;
		this.context = context;
		this.departures = new Departures(stop);
		this.requestQueue = Volley.newRequestQueue(this.context);

		JSONObject requestBody = new JSONObject();
		try {
			requestBody.put("SystemCodeNumber", this.stop.Scn);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.jsonRequest = new JsonObjectRequest(Method.POST, REQUESTURL,
				requestBody, this.jsonResponseListener, this.errorListener);

	}

	public void startUpdate() {
		Log.d("DeparturesProvider", "Starting updates! (scn: " + stop.Scn + ")");
		this.shouldAutoUpdate = true;
		this.updateRunable.run();
	}

	public void stopUpdates() {
		Log.d("DeparturesProvider", "Stopping updates! (scn: " + stop.Scn + ")");
		this.shouldAutoUpdate = false;
		this.handler.removeCallbacks(updateRunable);
	}

	public void setDeparturesUpdateListener(DeparturesUpdateListener listener) {
		this.listener = listener;
	}

	private void notifyUpdate() {
		if (this.listener != null) {
			this.listener.onUpdate(this);
		}
	}

	private void notifyError() {
		if (this.listener != null) {
			this.listener.onError(this);
		}
	}

	public Departures getDepartures() {
		return this.departures;
	}

	public static class Departures {
		public final Stop stop;
		public ArrayList<Bus> buses = new ArrayList<Bus>();

		public Departures(Stop stop) {
			this.stop = stop;
		}

		public static class Bus {
			public String destination;
			public String service;
			public String time;
		}
	}

	public abstract static class DeparturesUpdateListener {

		abstract void onUpdate(DeparturesProvider stopMapManager);

		abstract void onError(DeparturesProvider stopMapManager);
	}
}
