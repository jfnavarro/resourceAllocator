package resourceAllocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/** Simple application that will distribute a static number of
 * impressions among a set of costumers obtained from a text file.
 * The file must have the following format :
 * 
 * <monthly inventory>
 * <customer>,<impressions per campaign>,<price per campaign>
 *  ...
 *  <customer>,<impressions per campaign>,<price per campaign>
 *  
 *  The application needs to be called with a text file, a impressions number(optional)
 *  and a month inventory id(optional) and it will
 *  find the most optimal assigment of impressions/campaings to the costumers
 *  and print the results to the output with the following format :
 * 
 *  <customer>,<number of campaigns to sell>,<total impressions for customer>,<total
 *  revenue for customer>
 *  ...
 *  <total number of impressions>,<total revenue>
 *
 *  
 * @author josefernandeznavarro <jc.fernandez.navaro@gmail.com>
 *
 */
public class Allocator {
	
	public static void main(String[] args) throws Exception {
		
		String filename = null;
		String month_iventory_param = null;
		long impressions_param = 32356000; //default value for the assignment
		if (args.length > 0) {
			filename = args[0];
			if(args.length > 1){
			    try {
			    	impressions_param = Long.parseLong(args[1]);
			    } catch (NumberFormatException e) {
			        System.err.println("ERROR : argument " + args[1] + " must be an integer");
			        System.exit(1);
			    }
			    if(args.length > 2) {
					month_iventory_param = args[2];
				}
			}
		}
		else {
			//no input file given
			System.err.println("ERROR : no input file given or wrong parameters");
			System.exit(1);
		}
		// lets allocate the resources...
		allocate_resources(filename, impressions_param, month_iventory_param);
    }
	
	public static void allocate_resources(String filename, Long impressions, String month_inventory)
	{
		ReaderWeirdFormat reader = new ReaderWeirdFormat();
		ArrayList<Costumer> costumers = null; //costumers will be read from input file
		try {
			 costumers = reader.parseFile(filename,Costumer.class);
		}
		catch(Exception e){
			//there was an error parsing the file
			System.err.println("ERROR : parsing " + filename);
			System.exit(1);
		}
		if(costumers.size() == 0) {
			System.err.println("ERROR : the input file " + filename + " does not contain any costumer");
			System.exit(1);
		}
		//sort costumers by revenue
		Collections.sort(costumers, new Comparator<Costumer>(){
		     public int compare(Costumer o1, Costumer o2){
		         if(o1.getLambda() == o2.getLambda()) {
		             return 0;
		         }
		         return o1.getLambda() > o2.getLambda() ? -1 : 1;
		     }
		});
		//we could allocate impressions to costumers for a given month_inventory
		if(month_inventory != null) {
			Iterator<Costumer> it = costumers.iterator();
			while( it.hasNext() ) {
			  Costumer c = it.next();
			  if( !c.getId().equalsIgnoreCase(month_inventory) ) it.remove();
			}
			if(costumers.size() == 0) {
				System.err.println("ERROR : There is no costumer present in the given month inventory " + month_inventory);
				System.exit(1);
			}
		}
		//assign impressions to costumers in order of priority and if only if 
		//it is possible to assign a whole campaign
		for (Costumer c : costumers){
			while(c.getCampaignSize() <= impressions) {
				c.addCampaign();
				impressions -= c.getCampaignSize();
			}
			//I guess there could be a costumer whose campaign size is 1 :)
			//TODO better approach is to get the min campaign size from all costumers
			// but this could imply a full iteration...
			if(impressions == 0) {
				break;
			}
		}
		//iterate costumers to print the output and also figure out the total
		//number of assigned impressions and the total revenue
		long total_impressions = 0;
		long total_revenue = 0;
		for (Costumer c : costumers){
			System.err.println(c.toString());
			total_impressions += c.getImpressions();
			total_revenue += c.getTotalRevenue();
		}
		System.out.println(String.format("<%d>,<%d>", total_impressions, total_revenue));
	}
}
