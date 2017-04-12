package p1;

import java.util.HashMap;

/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
    public static void main(String[] args)
    {                
        int nrSellers = 50;
        int nrBidders = 20;
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"Seller"+i, 
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
            bidders[i] = new Bidder(
            		AuctionServer.getInstance(), 
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // TODO: Add code as needed to debug
        
        
        	System.out.println("Revenue = "+AuctionServer.getInstance().revenue());
        	System.out.println("Sum of Highest Bids = "+AuctionServer.getInstance().sumOfHighestBids());
        	int sum =0;
        	for(Bidder b: bidders){
        		sum = sum + b.cashSpent();
        	}
        	System.out.println("cash spent by bidders "+sum);
        	System.out.println("Number of sold items = "+AuctionServer.getInstance().soldItemsCount());
        	System.out.println("Number of Up for Bidding= "+ AuctionServer.getInstance().sizeItemsUpForBidding());
        	System.out.println("Number of Items in the server: "+ AuctionServer.getInstance().sizeItemsAndIDs());
        	System.out.println("Number of disqualified sellers: "+ AuctionServer.getInstance().disqualifiedSellers());
        
        
        
        HashMap<String, Integer> hashmap= AuctionServer.getInstance().potentialDisqualifiedHashmap();
        for(String s: hashmap.keySet()){
        	System.out.println("Seller is: "+s+ " Items > 75: "+hashmap.get(s));
        }
        
    }
}