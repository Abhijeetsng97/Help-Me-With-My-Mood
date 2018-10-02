package com.abhijeet.helpmewithmymood;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abhijeet.helpmewithmymood.Service.MoodService;
import com.abhijeet.helpmewithmymood.Utils.DataAsyncGetter;
import com.abhijeet.helpmewithmymood.Utils.GetUserTimeline;
import com.abhijeet.helpmewithmymood.Utils.SocialMediaType;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button mInstaLogin, mTwitterLogin, mCheckMoodStart, mCheckMoodStop, mCheckMoodOnce;
    public static final String mypreference = "key";
    public static final String instaAccessToken = "instaAccessToken";
    public static final String twitterHandle = "twitterHandle";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        mInstaLogin = findViewById(R.id.insta_login);
        mInstaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sharedpreferences.contains(instaAccessToken)) {
                    Intent i = new Intent(MainActivity.this, InstagramLogin.class);
                    startActivityForResult(i, 1);
                }
                else{
                    Toast.makeText(MainActivity.this, "We have your Instagram data!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTwitterLogin = findViewById(R.id.twitter_login);
        mTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sharedpreferences.contains(twitterHandle)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Enter Your Twitter Handle:");
                    final EditText input = new EditText(MainActivity.this);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String twitterHandleInput = input.getText().toString();
                            Log.d("twitter_handle", twitterHandleInput);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(twitterHandle, twitterHandleInput);
                            editor.apply();
//                            new GetUserTimeline(MainActivity.this, twitterHandle).start();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Enter Your Twitter Handle:");
                    final EditText input = new EditText(MainActivity.this);
                    builder.setMessage("We have your Twitter Handle ReEnter to change the same.");
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String twitterHandleInput = input.getText().toString();
                            Log.d("twitter_handle", twitterHandleInput);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(twitterHandle, twitterHandleInput);
                            editor.apply();
//                            new GetUserTimeline(MainActivity.this, twitterHandle).start();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });

        mCheckMoodStart = findViewById(R.id.check_mood_start);
        mCheckMoodStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Starting Daily Mood Checking Service");
                if(sharedpreferences.contains(twitterHandle) && sharedpreferences.contains(instaAccessToken)){
                    builder.setMessage("We have your Twitter Handle and Instagram Token!");
                    builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startService();
                        }
                    });
                }
                else if(sharedpreferences.contains(twitterHandle)){
                    builder.setMessage("We have your Twitter Handle!");
                    builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startService();
                        }
                    });
                }
                else if(sharedpreferences.contains(instaAccessToken)){
                    builder.setMessage("We have your Instagram Token!");
                    builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startService();
                        }
                    });
                }
                else {
                    builder.setMessage("We have neither your Twitter Handle nor Instagram Token!");
                }
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        mCheckMoodStop = findViewById(R.id.check_mood_stop);
        mCheckMoodStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, MoodService.class));
            }
        });

        mCheckMoodOnce = findViewById(R.id.check_mood_once);
        mCheckMoodOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"We'll notify you, Go and Relax!",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, MoodService.class);
//                if(sharedpreferences.contains(instaAccessToken)){
//                    i.putExtra(instaAccessToken,sharedpreferences.getString(instaAccessToken,""));
//                }
//                if(sharedpreferences.contains(twitterHandle)){
//                    i.putExtra(twitterHandle,sharedpreferences.getString(twitterHandle,""));
//                }
                startService(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(Activity.RESULT_OK == resultCode){
                String instaToken = data.getStringExtra("token");
                Log.d("insta_token",instaToken);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(instaAccessToken, instaToken);
                editor.apply();
            }
        }
    }

    public void startService(){
        Toast.makeText(MainActivity.this,"We'll notify you every day at this time, Go and Relax!",Toast.LENGTH_SHORT).show();
        startService(new Intent(MainActivity.this, MoodService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(MainActivity.this, MoodService.class);
        PendingIntent pintent = PendingIntent
                .getService(MainActivity.this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every day
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),24*3600*1000, pintent);
    }
}
