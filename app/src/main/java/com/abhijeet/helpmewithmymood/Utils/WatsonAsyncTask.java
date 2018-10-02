package com.abhijeet.helpmewithmymood.Utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class WatsonAsyncTask extends AsyncTask<String, String, Void> {

    private static final String TAG = "WatsonAsyncTask";
    private String tweet;
    private String toneValue;
    JSONObject jObj = null;
    HttpURLConnection connection;
    StringBuilder result;

    public WatsonAsyncTask(String tweet){
        this.tweet = tweet;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG,"onPreExecute: Started");
    }

    @Override
    protected Void doInBackground(String... args) {
        Log.d(TAG,"doInBackground: Started");
        String url = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2017-09-21&text=" + tweet;
        JSONObject jobj = getJSONFromUrl(url);
        Log.d("jsonWatson",jobj.toString());
        toneValue = getToneValue(jobj);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        Log.d(TAG,"onPostExecute: Started");
    }


    public String getToneValueForTweet(){
        return toneValue;
    }

    public JSONObject getJSONFromUrl(String url) {
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Authorization","Basic YjMyNmIyZTUtZGNiNC00N2RhLWE3NzUtYjM5MzJlNzczMDQ3OlpIUUJhamhxTUhScQ==");
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    public String getToneValue(JSONObject tonesList) {
        Log.d("WatsonError", "a");
        try {
            JSONObject document_tone = (JSONObject) tonesList.get("document_tone");
            JSONArray tones = (JSONArray) document_tone.get("tones");
            //            Iterator<JSONObject> it = tones.iterator();
            Log.d("WatsonError", "b");
            String maxTone = "nil";
            Double maxValue = 0.0;
            JSONObject toneObj = null;
            for (int i = 0; i < tones.length(); i++) {
                Log.d("WatsonError", "c");
                toneObj = tones.getJSONObject(i);
                Double score = (Double) toneObj.get("score");
                String toneName = (String) toneObj.get("tone_id");
                if (maxValue < score) {
                    maxValue = score;
                    maxTone = toneName;
                }
            }
            Log.d("WatsonError", maxTone);
            return maxTone;
        } catch (JSONException e) {
            e.printStackTrace();
            return "joy";
        }
    }
}


