package resourceAllocator;

import java.io.IOException;

/** Simple DTO to store the information related 
 * to a Costumer. This class implements the interface Parsable
 * and gives a basic API to access its variables.
 * @author josefernandeznavarro
 *
 */
public class Costumer implements Parsable {
	
	private String month_id = "";
	private String name = "";
	private long campaign_size = 0; 
	private long campaign_revenue = 0;
	private int assigned_campaigns = 0;
	
	public Costumer() {
		
	}
	
	public Costumer(String _month_id, String _name, long _size, long _revenue) {
		month_id = _month_id;
		name = _name;
		campaign_size = _size;
		campaign_revenue = _revenue;
	}
	
	public void setId(String _month_id) {
		month_id = _month_id;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setCampaignSize(long _size) {
		campaign_size = _size;
	}
	
	public void setCampaignRevenue(long _revenue) {
		campaign_revenue = _revenue;
	}
	
	public String getId() {
		return month_id;
	}
	
	public String getName() {
		return name;
	}
	
	public long getCampaignSize() {
		return campaign_size;
	}
	
	public long getCampaignRevenue() {
		return campaign_revenue;
	}
	
	public long getTotalRevenue() {
		return (long)assigned_campaigns * (long)campaign_revenue;
	}
	
	public void addCampaign() {
		assigned_campaigns++;
	}
	
	public void delCampaign() {
		assigned_campaigns--;
	}
	
	public void setCampaigns(int _campaigns) {
		assigned_campaigns = _campaigns;
	}
	
	public int getCampaigns() {
		return assigned_campaigns;
	}
	
	public long getImpressions() {
		return (long)assigned_campaigns * (long)campaign_size; 
	}
	
	/* this function returns the possible revenue */
	public double getLambda() {
		if(campaign_size == 0 || campaign_revenue == 0 ) {
			return 0.0;
		}
		double value = (double)campaign_size * (double)(campaign_revenue);
		//TODO this is kinda ugly but I want to decrease the resolution
		return value / 1000;
	}
	
	public void parse(String line) throws IOException, NumberFormatException {
		String[] splitted_line = line.split(",");
		if(splitted_line.length != 3) {
			throw new IOException();
		}
		//TODO not nice to rely on the order 
		name = splitted_line[0];
		campaign_size = Long.parseLong(splitted_line[1]);
		campaign_revenue = Long.parseLong(splitted_line[2]);
	}
	
	public String toString() {
		/*<customer>,<number of campaigns to sell>,<total impressions for customer>,<totalrevenue for customer>*/
		return String.format("<%s>,<%d>,<%d>,<%d>", name, assigned_campaigns, getImpressions(), getTotalRevenue());
    }
}
