package p1;

/**
 *  @author YOUR NAME SHOULD GO HERE
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}

	public int sumOfHighestBids(){
		int sum=0;
		for(int i: highestBids.keySet()){
			sum +=highestBids.get(i);

		}
		return sum;
	}

	public int sizeItemsAndIDs(){
		return itemsAndIDs.size();
	}
	public int sizeItemsUpForBidding(){
		return itemsUpForBidding.size();
	}
	
	


	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	//Count of seller where item's opening price is greater than 75, if the count is 3 then the seller is disqualified

	private HashMap<String, Integer> potentialDisqualifiedSeller = new HashMap<String, Integer>();


	//
	private HashMap<String, Integer> potentialDisqualifiedSellerUnbidItems = new HashMap<String, Integer>();

	//Disqualified Seller list
	private ArrayList<String> diaqualifiedSellerList= new ArrayList<>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	 private Object sellerLock = new Object(); 
	 private Object bidderLock = new Object(); 









	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public  int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		//System.out.println("Bidding amount is :"+lowestBiddingPrice+ " and seller is "+ sellerName );
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.
		//System.out.println("Disqualified seller list :" + diaqualifiedSellerList.size());
		
		synchronized (sellerLock) {

		     
		if(itemsAndIDs.size()<serverCapacity){
			
				
			if(itemsPerSeller.containsKey(sellerName)){
				if(itemsPerSeller.get(sellerName)<maxSellerItems){
									
					
									//System.out.println("I am here in if and amount is "+ lowestBiddingPrice+" and seller is "+sellerName);
									if(lowestBiddingPrice>75)
									{
										//System.out.println("Caught price more than 75 from if for seller " + sellerName);
										
											
										
										if(potentialDisqualifiedSeller.containsKey(sellerName)){
											potentialDisqualifiedSeller.put(sellerName, potentialDisqualifiedSeller.get(sellerName)+1);
											if(potentialDisqualifiedSeller.get(sellerName)>2){
												System.out.println("Seller disqualified for 3 items more than $75");
												diaqualifiedSellerList.add(sellerName);
												
											}
										}
										else{
											potentialDisqualifiedSeller.put(sellerName, 1);
										}
									}
									for(String name: diaqualifiedSellerList){
										//System.out.println("I am here");
										if(name.equalsIgnoreCase(sellerName)){
											System.out.println("Seller cannot submit as he is disqualified");
											return -1;
										}
									}
									
									lastListingID+=1;
									//System.out.println("I am here in if and amount is "+ lowestBiddingPrice+" and seller is "+sellerName);
									Item item=new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
									//System.out.println("Item price is:" +item.lowestBiddingPrice());
									itemsAndIDs.put(lastListingID, item);
									itemsUpForBidding.add(item);
									//highestBids.put(lastListingID, lowestBiddingPrice);
									itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName)+1);
									//System.out.println("Item posted :"+lastListingID+" by seller: "+sellerName);
									
									
								}
								else{
									return -1;
								}
								
								//System.out.println("Size of disqualified list is :"+ diaqualifiedSellerList.size());
								return lastListingID;
							}
							else{
								
								lastListingID+=1;
								//System.out.println("Listing id from else "+ lastListingID);
								Item item=new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
								
								itemsAndIDs.put(lastListingID, item);
								itemsUpForBidding.add(item);
								//System.out.println("Item posted :"+lastListingID+" by seller: "+sellerName);
								//highestBids.put(lastListingID, lowestBiddingPrice);
								itemsPerSeller.put(sellerName, 1);
								if(lowestBiddingPrice>75)
								{
									//System.out.println("Caught price more than 75 from else for seller " + sellerName);
									
									
									potentialDisqualifiedSeller.put(sellerName, 1);
								}
								return lastListingID;
		
		
					}
			
			}
	}

		return -1;
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public  List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		synchronized (sellerLock) {
			
		
			ArrayList<Item> copyOfItemsUpForBidding= new ArrayList<>(itemsUpForBidding);
			return copyOfItemsUpForBidding;
		}

		
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public  boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place
	//System.out.println("Bidder submitted amount: "+biddingAmount);
	
		synchronized (bidderLock) {
			
			
		
		if(itemsAndIDs.containsKey(listingID))
		{
			//System.out.println("Item and id contains item during submit bid: "+ listingID);
			if(itemsUpForBidding.contains(itemsAndIDs.get(listingID))){
				if(itemsAndIDs.get(listingID).biddingOpen()){
					if(highestBidders.containsKey(listingID)&&bidderName.equalsIgnoreCase(highestBidders.get(listingID))){
						return false;
					}
					else if(itemsPerBuyer.containsKey(bidderName) && itemsPerBuyer.get(bidderName)==maxBidCount){
						return false;
					}
					else if(highestBids.containsKey(listingID)){
						if(biddingAmount < highestBids.get(listingID)){
							return false;
						}
					}
					else if(biddingAmount < itemPrice(listingID)){
						return false;

					}
					else{
						if(highestBidders.containsKey(listingID))
							itemsPerBuyer.put(highestBidders.get(listingID), itemsPerBuyer.get(highestBidders.get(listingID))-1);
						highestBidders.put(listingID, bidderName);
						//System.out.println("Adding to highest bids amount: "+biddingAmount);
						highestBids.put(listingID, biddingAmount);
						if(itemsPerBuyer.containsKey(bidderName))
							itemsPerBuyer.put(bidderName,itemsPerBuyer.get(bidderName)+1);
						else
							itemsPerBuyer.put(bidderName, 1);
						//System.out.println("Bid success item: "+listingID+" bidder: "+bidderName+" amount: "+biddingAmount);
						return true;

					}
				}
			}
		}
		else
		
			System.out.println("");
			return false;
		}
		
	}
		
	

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
		/*if(itemsAndIDs.containsKey(listingID)){
			if(itemsAndIDs.get(listingID).biddingOpen()){

			}
		}*/
	
		synchronized (bidderLock) {
			
		
	
			if(!itemsAndIDs.get(listingID).biddingOpen() && highestBidders.containsKey(listingID) && highestBidders.get(listingID).equalsIgnoreCase(bidderName))
			{	
				if(itemsUpForBidding.contains(itemsAndIDs.get(listingID)))
				{
					//System.out.println("Removing item : "+listingID);
					itemsUpForBidding.remove(itemsAndIDs.get(listingID));
				}
				itemsPerBuyer.put(bidderName,itemsPerBuyer.get(bidderName)-1);
				itemsPerSeller.put(itemsAndIDs.get(listingID).seller(),itemsPerSeller.get(itemsAndIDs.get(listingID).seller())-1);
				revenue+=highestBids.get(listingID);
				//System.out.println("Revenur now is :" +revenue);
				soldItemsCount++;
				//System.out.println("Bidding is going to be succefull "+ listingID);
				return 1;
			}
			if(itemsAndIDs.get(listingID).biddingOpen()){
				//System.out.println("Bidding is still open");
				return 2;

			}
			
			if(!itemsAndIDs.get(listingID).biddingOpen() && highestBidders.containsKey(listingID) && !highestBidders.get(listingID).equalsIgnoreCase(bidderName)){
					//itemsUpForBidding.remove(itemsAndIDs.get(listingID));
					System.out.println("Bidding won by someone else");
					return 3;


			}
			if(itemUnbid(listingID)){
						//System.out.println("Item went unbid");
						return 3;
			}
				
			
		}
		
		return -1;
		}
	
	

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public  int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		synchronized (bidderLock) {
			
		

if(itemsAndIDs.containsKey(listingID) && itemsAndIDs.get(listingID).biddingOpen())
		{
			if(!highestBids.containsKey(listingID) && itemsAndIDs.containsKey(listingID) )
			  return itemsAndIDs.get(listingID).lowestBiddingPrice();
			else
			  return highestBids.get(listingID);
		}
		else
		{
		    return -1;
		}
		}
	}
	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public  boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		synchronized (sellerLock) {
			
		
		if(itemsAndIDs.containsKey(listingID)){
			if(!itemsUpForBidding.contains(itemsAndIDs.get(listingID))){
				if(!itemsAndIDs.get(listingID).biddingOpen() && !highestBidders.containsKey(listingID)){
					//System.out.println("This item went unbid");
					if(potentialDisqualifiedSellerUnbidItems.containsKey(itemsAndIDs.get(listingID).seller())){
						potentialDisqualifiedSellerUnbidItems.put(itemsAndIDs.get(listingID).seller(), potentialDisqualifiedSellerUnbidItems.get(itemsAndIDs.get(listingID).seller())+1);
						//System.out.println("Seller is potential disqualifier again");
						if(potentialDisqualifiedSellerUnbidItems.get(itemsAndIDs.get(listingID).seller())>4){
							diaqualifiedSellerList.add(itemsAndIDs.get(listingID).seller());
							//System.out.println("Seller disqualified due to 5 of items submitted went unbid ");
						}
						
						return true;
					}
					else{
						potentialDisqualifiedSellerUnbidItems.put(itemsAndIDs.get(listingID).seller(),1);
						//System.out.println("Seller is potential disqualifier for the first time");
						return true;
					}

				}
			}
		}
		return false;
		}
	}
	
	public  boolean itemUnbid(String sellername)
	{
		// TODO: IMPLEMENT CODE HERE
		synchronized (sellerLock) {
			
		
		for(Integer listingID: itemsAndIDs.keySet())
		{
			if(!itemsUpForBidding.contains(itemsAndIDs.get(listingID))){
				if(!itemsAndIDs.get(listingID).biddingOpen() && !highestBidders.containsKey(listingID)&& itemsAndIDs.get(listingID).seller().equalsIgnoreCase(sellername)){
					//System.out.println("This item went unbid");
					if(potentialDisqualifiedSellerUnbidItems.containsKey(itemsAndIDs.get(listingID).seller())){
						potentialDisqualifiedSellerUnbidItems.put(itemsAndIDs.get(listingID).seller(), potentialDisqualifiedSellerUnbidItems.get(itemsAndIDs.get(listingID).seller())+1);
						//System.out.println("Seller is potential disqualifier again");
						if(potentialDisqualifiedSellerUnbidItems.get(itemsAndIDs.get(listingID).seller())>4){
							diaqualifiedSellerList.add(itemsAndIDs.get(listingID).seller());
							//System.out.println("Seller disqualified due to 5 of items submitted went unbid ");
						}
						
						return true;
					}
					else{
						potentialDisqualifiedSellerUnbidItems.put(itemsAndIDs.get(listingID).seller(),1);
						//System.out.println("Seller is potential disqualifier for the first time");
						return true;
					}

				}
			}
		}
		return false;
		}
	}

public HashMap<String, Integer> potentialDisqualifiedHashmap() {
		// TODO Auto-generated method stub
		return potentialDisqualifiedSeller;
	}

public int disqualifiedSellers(){
	// TODO Auto-generated method stub
	return diaqualifiedSellerList.size();
}
	
	


}
