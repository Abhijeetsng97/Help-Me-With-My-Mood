package com.abhijeet.helpmewithmymood.Utils;

import android.util.Log;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class GetUserTimeline extends Thread{

    public static final String TAG = "GetUserTimeline";
    private String userName;
    private ArrayList<String> tweets = new ArrayList<>();

    public GetUserTimeline(String userName){
        this.userName = userName;
    }

    public ArrayList<String> getTweets(){
        return tweets;
    }

    @Override
    public void run() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("8VIpQFcNGxs7nK2Rg5o9SzPiN")
                    .setOAuthConsumerSecret("7NSIBlDJjlRHhr5h1oL8G7ueTF3kq1k55UTy2RBmY4feEsNdTE")
                    .setOAuthAccessToken("763651503546437632-uoqt2ELP2CiebW0uwnF3gTqckg5szSN")
                    .setOAuthAccessTokenSecret("QmiK8CgqtqIi0qqEDxj22yuEbJb9lZP1A7Ce2Lu2bZVFq");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter1 = tf.getInstance();
            List<Status> statuses;
            statuses = twitter1.getUserTimeline(userName);
            Date date = new Date();
            Long currDate = date.getTime();
            for (Status status : statuses) {
                Log.d(TAG,"@" + status.getUser().getScreenName() + " - " + status.getText());
                if(currDate-status.getCreatedAt().getTime()<=24*3600)
                    tweets.add(status.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            Log.d(TAG,"Failed to get timeline: " + te.getMessage());
        }
    }
}