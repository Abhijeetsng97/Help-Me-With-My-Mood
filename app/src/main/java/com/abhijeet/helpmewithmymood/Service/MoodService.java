package com.abhijeet.helpmewithmymood.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.abhijeet.helpmewithmymood.MainActivity;
import com.abhijeet.helpmewithmymood.R;
import com.abhijeet.helpmewithmymood.ResultActivity;
import com.abhijeet.helpmewithmymood.Utils.DataAsyncGetter;
import com.abhijeet.helpmewithmymood.Utils.GetUserTimeline;
import com.abhijeet.helpmewithmymood.Utils.SocialMediaType;
import com.abhijeet.helpmewithmymood.Utils.WatsonAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoodService extends IntentService {

    private static final String TAG = "MoodService";

    public MoodService(){
        super("Mood Service");
    }

    public static final String mypreference = "key";
    public static final String instaAccessToken = "instaAccessToken";
    public static final String twitterHandle = "twitterHandle";
    SharedPreferences sharedpreferences;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "OnHandleIntent: Started");
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        DataAsyncGetter dataFromInsta = new DataAsyncGetter(sharedpreferences.getString(instaAccessToken,""), SocialMediaType.INSTA);;
        GetUserTimeline dataFromTwitter = new GetUserTimeline(sharedpreferences.getString(twitterHandle,""));
        ArrayList<String> listFromInsta = new ArrayList<>(), listFromTwitter = new ArrayList<>();

        if (sharedpreferences.contains(instaAccessToken)){
            Log.d(TAG,sharedpreferences.getString(instaAccessToken,""));
            dataFromInsta.execute();
        }
        if(sharedpreferences.contains(twitterHandle)){
            Log.d(TAG,sharedpreferences.getString(twitterHandle,""));
            dataFromTwitter.start();
        }
        if (sharedpreferences.contains(instaAccessToken)){
            while(dataFromInsta.getStatus() != AsyncTask.Status.FINISHED){}
            listFromInsta = dataFromInsta.getInstaList();
        }
        if(sharedpreferences.contains(twitterHandle)){
            try {
                dataFromTwitter.join();
                listFromTwitter = dataFromTwitter.getTweets();
            }
            catch (Exception e){}
        }

        listFromInsta.addAll(listFromTwitter);
        ArrayList<WatsonAsyncTask> watsonAsyncTasks = new ArrayList<>();
        List<String> toneValue = new ArrayList<>();
        for(int i=0; i<listFromInsta.size(); i++){
            WatsonAsyncTask watsonAsyncTask = new WatsonAsyncTask(listFromInsta.get(i));
            watsonAsyncTask.execute();
            watsonAsyncTasks.add(watsonAsyncTask);
        }

        for(int i=0; i<watsonAsyncTasks.size(); i++){
            while(watsonAsyncTasks.get(i).getStatus() != AsyncTask.Status.FINISHED){}
            Log.d(TAG,listFromInsta.get(i)+" "+watsonAsyncTasks.get(i).getToneValueForTweet());
            toneValue.add(watsonAsyncTasks.get(i).getToneValueForTweet());
//            Log.d("ToneValue",watsonAsyncTasks.get(i).getToneValueForTweet());
        }

        int max = 0;
        int curr = 0;
        String currKey =  null;
        Set<String> unique = new HashSet<String>(toneValue);
        for (String key : unique) {
            curr = Collections.frequency(toneValue, key);
            if(max < curr){
                max = curr;
                currKey = key;
            }
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Result", currKey);
        editor.apply();

        notifyUser("Mood Analysed", "We have your daily result. Click to know more.");

        stopSelf();

    }

    public void notifyUser(String title, String description) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "my_channel_01";
            CharSequence name = "Alert";
            String channelDescription = "Your Daily Report";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, name,importance);
            mChannel.setDescription(channelDescription);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

            mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentIntent(contentIntent)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setChannelId(CHANNEL_ID)
                    .build();
            mNotificationManager.notify(notifyID, notification);
        }
        else {
            Notification notiMail = new Notification.Builder(
                    getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(contentIntent).setAutoCancel(true).build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notiMail);
        }
    }
}
