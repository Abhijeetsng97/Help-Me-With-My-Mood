package com.abhijeet.helpmewithmymood;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String result = "Joy";
    TextView heading;
    Button refreshed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        sharedpreferences = getSharedPreferences("key", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("Result")){
            result = sharedpreferences.getString("Result","");
            if(result.toLowerCase().contentEquals("nil"))
                result = "Joy";
        }

        heading = findViewById(R.id.heading);
        heading.setText("RESULT\nWe think that your current mood is: "+result.toUpperCase().charAt(0)+result.substring(1));
        refreshed = findViewById(R.id.video);
        refreshed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(ResultActivity.this, getMoodString(result));
            }
        });

    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public String getMoodString(String s){
        if(s.contentEquals("anger")){
            return "YwlwSByGOWY";
        }
        else if(s.contentEquals("fear")){
            return "CZ9FaLhOkWo";
        }
        else if(s.contentEquals("joy")){
            return "nDzm7xrg7o0";
        }
        else if(s.contentEquals("sadness")){
            return "VFX2Nqwwm44";
        }
        else if(s.contentEquals("analytical")){
            return "t4f6MYPOL1o";
        }
        else if(s.contentEquals("confident")){
            return "1bJz9yzmKXs";
        }
        else if(s.contentEquals("tentative")){
            return "C_WCcuqHeDk";
        }
        return "ZbZSe6N_BXs";
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
