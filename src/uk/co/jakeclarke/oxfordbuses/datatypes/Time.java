package uk.co.jakeclarke.oxfordbuses.datatypes;

/**
 * Represents a time from OxonTime
 *
 */
public class Time
{
	private String service;
	private String destination;
	private int delay;

	public Time (String service, String destination, int delay)
	{
		this.service = service;
		this.destination = destination;
		this.delay = delay;
	}

	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}

	@Override
	public String toString()
	{
		String value = "Service: " + service + "\n- Destination: " + destination + "\n- Departure: ";
		if (delay==0)
		{
			value += "DUE";
		}
		else
		{
			value += delay + " mins";
		}
		return value;
	}
}