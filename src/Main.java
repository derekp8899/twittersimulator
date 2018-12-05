import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Main {
 
    private static List<Account> allAccounts = new ArrayList<Account>();
    private static Random rand = new Random();

    public static void main(String[] args) {

        //Create initial accounts - # of initial Accounts should be an initial parameter
	double followerRate = .005;
	double retweetRate = .01;
        for (int i = 0; i < 10; i++) {
            allAccounts.add(new Account(
                                    .1, //avgTweetQuality
                                    NormalRNG(0.25,0.5), //tweetProbability
                                    0.01,       //popularityMultiplier
                                    0,                 //baseHumanFollowerCount
                                    i*10                    //baseBotFollowerCount
                            ));
        }
        //Print all initial account stats
        // System.out.println("INITIAL STATISTICS\n\n"+
        //                    "Total Accounts --> " + Account.totalAccounts +"\n"+
        //                    "Total Tweets ----> " + Tweet.totalTweets + "\n");

        // for (int i = 0; i < allAccounts.size(); i++) {
        //     System.out.println(allAccounts.get(i));
        // }

	while (Tweet.totalTweets < 10000) {
	    for (int i = 0; i < allAccounts.size(); i++) {
		Account currentAccount = allAccounts.get(i);
		double currentAvgTweetQuality = exponRNG(currentAccount.getAvgTweetQuality());
		int currentHumanFollowers = currentAccount.getHumanFollowerCount();
		double currentProbabilityMult = currentAccount.getPopularityMult();
		currentAccount.generateTweet(currentAvgTweetQuality, currentAccount.getID());
		Tweet currentTweet = currentAccount.getMostRecentTweet();
		int totalTweetImpressions = genImpressions(currentHumanFollowers, currentProbabilityMult, NormalRNG(.1,.3));
		totalTweetImpressions += currentAccount.getBotFollowerCount();
		int retweets = genRetweets(totalTweetImpressions,retweetRate);
		retweets += currentAccount.getBotFollowerCount();
		totalTweetImpressions += retweets*exponRNG(1);
		int newFollows = binomTest(totalTweetImpressions-currentHumanFollowers,followerRate);
		currentAccount.setPopularityMult(currentAccount.getPopularityMult()+popUpdate(newFollows,.01));
		currentAccount.incFollows(newFollows);
		currentTweet.setImpressions(totalTweetImpressions);
		currentAccount.incrementTotalImpressions(totalTweetImpressions);
	    }
	}
        calculateInfluenceScores();

        //Print all final account stats
        System.out.println("FINAL STATISTICS\n\n"+
                           "Total Accounts --> " + Account.totalAccounts +"\n"+
                           "Total Tweets ----> " + Tweet.totalTweets + "\n");

        for (int i = 0; i < allAccounts.size(); i++) {
            Account currentAccount = allAccounts.get(i);
            System.out.println(currentAccount);
        }
    }


    public static double NormalRNG(int stdDev, int mean) {
        double value = 0;
        value = (rand.nextGaussian()*stdDev)+mean;
        return value;
    }

    public static double NormalRNG(double stdDev, double mean) {
        double value = 0;
        value = (rand.nextGaussian()*stdDev)+mean;
        if (value > 1.0) {
            value = 1.0;
        } else if (value < 0.0) {
            value = 0.01;
        }
        return value;
    }

    public static double exponRNG(double mean) {
        double lambda = 1/mean;
        double value = 0;
        value = Math.log(1-rand.nextDouble())/(-lambda);
        return value;
    }

    public static int binomTest(int numTests, double prob){
        int x = 0;//the number of successes
        for (int i = 0; i < numTests; i++){
            if(Math.random() < prob)
            x++;
        }
        return x;
    }

    public static int genImpressions(int followers, double popularityMultiplier, double tweetQuality){
        int impressions = 0;
        double mean = followers * ((1 + popularityMultiplier) * (1 + tweetQuality));
	if (mean == 0 )
	    mean = 1;
        impressions = (int)exponRNG(mean);
	//	System.out.println("impresssion gened: " + impressions+ " from mean : " + mean);
        return impressions;
    }

    public static int genRetweets(int impressions, double rate){
        int retweets = 0;
        retweets = binomTest(impressions, rate);
        return retweets;
    }

    public static int genNewFollowers(int impressions, double rate){
        int followers = 0;
        followers = binomTest(impressions, rate);
        return followers;
    }

    // public static double updateInfluence(int newFollows,int currentInfluence){
    //     for(int i = 0; i < newFollows; i++){
    //         double inc = 0;
    //         inc = exponRNG(.01);
    //     }
    //     return inc;
    // }

    public static double popUpdate(int newFollows, double popRate){

	int x = 0;
	x = binomTest(newFollows,popRate);
	return x*.001;
	
    }

    //influence score is just each account's percentage of the total impressions.
    //This calculation needs more thought and more needs to be considered to 
    //determine the influence score
    public static void calculateInfluenceScores() {
        long overallTotalImpressions = 0;
        for (int i = 0; i < allAccounts.size(); i++) {
            Account currentAccount = allAccounts.get(i); 
            long accountTotalImpressions = currentAccount.getTotalImpressions();
            overallTotalImpressions += accountTotalImpressions;
        }
        for (int i = 0; i < allAccounts.size(); i++) {
            Account currentAccount = allAccounts.get(i); 
            long accountTotalImpressions = currentAccount.getTotalImpressions();
            double influenceScore = ((double)accountTotalImpressions/overallTotalImpressions)*100;
            currentAccount.setInfluenceScore(influenceScore);
        }
    }
}
