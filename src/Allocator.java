package resourceAllocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Date;
import java.sql.Timestamp;
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
		ArrayList<Costumer> costumers = getCostumers(filename, month_iventory_param);
		ArrayList<Costumer> costumers2 = getCostumers(filename, month_iventory_param); //Very ugly but I want to run the method two times
		System.out.println("Allocating resources using naive approach at (" + new Timestamp(new Date().getTime()) + ")");
		allocate_resources(costumers, impressions_param);
		System.out.println("Finish Allocating resources using naive approach at (" + new Timestamp(new Date().getTime()) + ")");
		System.out.println("Allocating resources using DP approach at (" + new Timestamp(new Date().getTime()) + ")");
		allocate_resources_DP(costumers2, impressions_param);
		System.out.println("Finish resources using DP approach at (" + new Timestamp(new Date().getTime()) + ")");
    }
	
	public static ArrayList<Costumer> getCostumers(String filename, String month_inventory) {
		
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
		
		return costumers;	
	}
	
	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}
	 
	public static void printAllocatedResources(ArrayList<Costumer> costumers) {
		//iterate costumers to print the output and also figure out the total
		//number of assigned impressions and the total revenue
		long total_impressions = 0;
		long total_revenue = 0;
		for (Costumer c : costumers){
			System.out.println(c.toString());
			total_impressions += c.getImpressions();
			total_revenue += c.getTotalRevenue();
		}
		System.out.println(String.format("<%d>,<%d>", total_impressions, total_revenue));
	}
	
	private static void setAllArrayValueTo(final int[] array, final int value){
	    for (int i=0;i<array.length;i++) array[i] = value;
	}
	
	private static void setAllIntArrayValueTo(final Long[] array, final Long value){
	    for (int i=0;i<array.length;i++) array[i] = value;
	}
	
    public static void allocate_resources_DP(ArrayList<Costumer> costumers, Long impressions) {
		
    	Long[] valueSum = new Long[ safeLongToInt(impressions + 1) ];
    	int[] itemIndex = new int[ safeLongToInt(impressions + 1) ];
    	Long[] previousSumIndex = new Long[ safeLongToInt(impressions + 1) ];

    	setAllIntArrayValueTo(valueSum,0L);
    	setAllArrayValueTo(itemIndex,0);
    	setAllIntArrayValueTo(previousSumIndex,0L);
    	
        for ( Long i = 1L; i <= impressions; i++ ) {
            Long max = 0L;
            for ( int j = 0; j < costumers.size(); j++ ) {
            	Costumer item = costumers.get(j);
                if ( item.getCampaignSize() > i ) continue;
                long value = item.getCampaignRevenue() + valueSum[safeLongToInt((i - item.getCampaignSize()))];
                if ( value > max ) {
                    valueSum[safeLongToInt(i)] = value;
                    itemIndex[safeLongToInt(i)] = j;
                    previousSumIndex[safeLongToInt(i)] = i - item.getCampaignSize();
                    max = value;
                }
            }
        }
       
        Long i = impressions;
        while ( i > 0 ) {
        	Costumer item = costumers.get(itemIndex[safeLongToInt(i)]);
        	if(i >= item.getCampaignSize()){
        		item.addCampaign();
        	}
            i = previousSumIndex[safeLongToInt(i)];
        }
       
		//print the costumers
		printAllocatedResources(costumers);
    }

	public static void allocate_resources(ArrayList<Costumer> costumers, Long impressions)
	{
		//sort costumers by revenue
		Collections.sort(costumers, new Comparator<Costumer>(){
		     public int compare(Costumer o1, Costumer o2){
		         if(o1.getLambda() == o2.getLambda()) {
		             return 0;
		         }
		         return o1.getLambda() > o2.getLambda() ? -1 : 1;
		     }
		});
		
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

		//print the costumers
		printAllocatedResources(costumers);
	}
}
