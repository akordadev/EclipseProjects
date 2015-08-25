/**
 * StarConvos designed by Matt Cielecki
 * 
 * This will conversations that celebreties have tweeted each other
 */

package celebTweets;

import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;


/**
 * 
 * @author Matt Cielecki
 * @version 0.0.1c
 * 
 * http://eden.rutgers.edu/~cielecki
 *
 */
public final class StarConvos {
	
	//GLOBAL CONSTANTS
	static final String CONSUMER_KEY = "gimIlAT7GCnEdAnRVTpgA";
	static final String CONSUMER_SECRET = "WWDOMBu8T00j26xpdS15iHJLrYP4JLrfXaAvUk2Y";
	static final String ACCESS_TOKEN = "137441847-1hpxuxobBm8LfJIONUhwGDOwl0TtjjlIP6BxDSsY";
	static final String ACCESS_TOKEN_SECRET = "WhX0XRqyD5hYGVetbu6UToktKZduiz37WQMcmkU";
	static final String SEARCH_REQUEST = "celebrities";
	static final int NUM_PAGES = 1;
	static final int MAX_STATUS = 15;
	static final int MAX_MATCHES = 10;
	
	//GLOBALS
	static int numMatches = 0;
	
	/**
	 * entry point
	 * 
	 * @param args
	 */
    public static void main(String[] args) {    	
        try {
        	ConfigurationBuilder cb = new ConfigurationBuilder();
        	//set oAuth credentials
        	cb.setDebugEnabled(true)
        	  .setOAuthConsumerKey(CONSUMER_KEY)
        	  .setOAuthConsumerSecret(CONSUMER_SECRET)
        	  .setOAuthAccessToken(ACCESS_TOKEN)
        	  .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        	
        	//Build the twitterFacroty
        	TwitterFactory tf = new TwitterFactory(cb.build());
        	Twitter twitter = tf.getInstance();
        	
        	//Set pages, these are pages of results
            int userPage = 1;
            int tweetPage = 1;
            
            //number of statuses checked so far for one user
            int statusCheck = 0;
            
            //List of users from query
            ResponseList<User> users;
            
            do {
                users = twitter.searchUsers(SEARCH_REQUEST, userPage);
                
                //cycle through all users
                for (User user : users) {
                	
                    if (user.getStatus() != null) {
                    	
                    	//only consider users over 1 mil follwoers
                    	if (user.getFollowersCount() > 1000000) {
                    		 //System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
                    		 List<Status> statuses;
                             do {
                             	statuses = twitter.getUserTimeline(user.getScreenName(), new Paging(tweetPage, NUM_PAGES));
                             	++statusCheck;                           	
                             	//only check MAX_STATUS statuses per user 
                         		if(statusCheck >= MAX_STATUS){
                         			break;
                         		}
                             	for (Status status : statuses) {
                             		//Users mentioned per tweet
                             		UserMentionEntity[] sentTo = status.getUserMentionEntities();
                             		for (int i = 0; i < sentTo.length; i++) {
                             			//check if they are mutual friends, more likely to write to each other
                 	        			if (mutualFriends (user.getScreenName(), sentTo[i].getScreenName(), twitter)) {
                 	        				User target = twitter.showUser(sentTo[i].getScreenName());
                 	        				//check if t2 has 125k+ followers
                 	        				if(target.getFollowersCount() > 125000) {
                 	        					//check if they tweeted back
                 	        					celebTweet second = returnedTheFavor(target, twitter, user.getScreenName());
                 	        					if (second != null){
                 	        						//this user replied back, we have found a tuple
                 	        						++numMatches;                	        						
                 	        						celebTweet first = new celebTweet(status.getId(), status.getText(), status.getCreatedAt().toString(), user.getScreenName());
                 	        						printMatch(first, second);
                 	        						//end after MAX_MATCHES have been found
                 	        						if (numMatches >= MAX_MATCHES) {
                 	        							System.out.println("FINISHED");
                 	        							System.exit(0);
                 	        						}
                 	        					}				
                 	        				}
                 	        			}
                             		}
                             	}
                             	tweetPage++;
                             } while (statuses.size() != 0);
                             statusCheck = 0;
                             tweetPage = 1;
                    	}  
                    } 
                    else {
                        // the user is protected
                        System.out.println("@" + user.getScreenName() + " is protected");
                    }
                }
                userPage++;                
            } while (users.size() != 0 && userPage < 5);
            //Only loop through 5 pages of celebreties
            System.err.println("Flood Protector activated");
            System.exit(1);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.err.println("Failed to execute in main: " + te.getMessage());
            //System.exit(-1);
        }
    }
    
    /**
     * 
     * @param user
     * @param twitter
     * @param match
     * @return celebTweet
     */
    private static celebTweet returnedTheFavor(User user, Twitter twitter, String match) {
        List<Status> statuses;
        int userPage = 1;
        int statusCheck = 0;
        // test
        //System.out.println("RTF: Showing @" + user.getScreenName() + "'s home timeline.");
        try {
	        do {
	        	statuses = twitter.getUserTimeline(user.getScreenName(), new Paging(userPage, NUM_PAGES));
	        	++statusCheck;
	        	//System.out.println("statuses left:  " + statusCheck);
         		if(statusCheck >= MAX_STATUS){
         			break;
         		}
	        	for (Status status : statuses) {
	        		UserMentionEntity[] sentTo = status.getUserMentionEntities();
	        		for (int i = 0; i < sentTo.length; i++) {
	        			User target = twitter.showUser(sentTo[i].getScreenName());
		        		if(match.equals(target.getScreenName())) {
		       				celebTweet result = new celebTweet(status.getId(), status.getText(), status.getCreatedAt().toString(), user.getScreenName());
		       				return result;
	        			}
	        		}
	        	}
	        	userPage++;
	        } while (statuses.size() != 0);
	        statusCheck = 0;
	        userPage = 1;
        }
        catch (TwitterException te) {
            te.printStackTrace();
            System.err.println("Failed to execute in RTF: " + te.getMessage());
            //System.exit(-1);
        }
    	return null;
    }
    
    /**
     * 
     * @param one
     * @param two
     * @param twitter
     * @return boolean
     */
    public static boolean mutualFriends(String one, String two, Twitter twitter) {
    	try{
    		boolean isAFollowingB = twitter.existsFriendship(one, two);
    		boolean isBFollowingA = twitter.existsFriendship(two, one);
    		if (isAFollowingB && isBFollowingA) {
    			return true;    			
    		}
    	}
    	catch (TwitterException te) {
            te.printStackTrace();
            System.err.println("Failed to compute mutualFriends: " + te.getMessage());
            //System.exit(-1);   
        }
    	return false;
    }
    
    /**
     * Prints the results of one tuple
     * 
     * @param first
     * @param second
     */
    public static void printMatch(celebTweet first, celebTweet second) {
    	System.out.println("(");
    	System.out.println(first.user);
    	System.out.println(",");
    	System.out.println(second.user);
    	System.out.println(",");
    	System.out.println("Time: " + first.time + ", URL: " + first.url + ", Tweet: " + first.tweet);
    	System.out.println(",");
    	System.out.println("Time: " + second.time + ", URL: " + second.url + ", Tweet: " + second.tweet);
    	System.out.println(")");
    	System.out.println("");
    	
    }
}

/**
 * 
 * celebTweet class
 * 
 * This will store information about the tweet that will later be printed
 *
 */
class celebTweet {
	String url;
	String tweet;
	String time;
	String user;
	long id;
	
	/**
	 * Constructor 
	 * 
	 * @param id
	 * @param tweet
	 * @param time
	 * @param user
	 */
	celebTweet(long id, String tweet, String time, String user) {
		this.id = id;
		this.tweet = tweet;
		this.time = time;
		this.user = user;
		createURL();
	}
	
	/**
	 * Creates the URL for the given tweet.
	 */
	public void createURL(){
		url = "https://twitter.com/" + user + "/status/" + id;
	}
	
}
