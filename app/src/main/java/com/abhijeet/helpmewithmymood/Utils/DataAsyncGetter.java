package com.abhijeet.helpmewithmymood.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.simple.*;

import java.util.ArrayList;
import java.util.Iterator;

public class DataAsyncGetter extends AsyncTask<String, String, JSONObject> {

    private static final String TAG = "DataAsyncGetter";
    private String token;
    private int type;
    private ArrayList<String> recents = new ArrayList<>();

    public DataAsyncGetter(String token, int type){
        this.token = token;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG,"onPreExecute: Started");
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        Log.d(TAG,"doInBackground: Started");
        JSONParser jParser = new JSONParser();
        String url = "";
        if(SocialMediaType.INSTA == type) {
            Log.d("json url", token);
            url = "https://api.instagram.com/v1/users/self/media/recent/?access_token=" + token;
        }
        JSONObject json = jParser.getJSONFromUrl(url);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        Log.d(TAG,"onPostExecute: Started");
        try {
            InstagramDataParser(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getInstaList(){
        return recents;
    }

    public void InstagramDataParser(JSONObject jsonObject) {
        try {
            JSONArray data = (JSONArray) jsonObject.get("data");
            Iterator<JSONObject> iterator = data.iterator();
            while(iterator.hasNext()){
                JSONObject post = iterator.next();
                try {
                    JSONObject caption = (JSONObject) post.get("caption");
                    String text = (String) caption.get("text");
                    String time_str =(String) caption.get("created_time");
                    Long upload_time = Long.parseLong(time_str);
                    Long unixTime = System.currentTimeMillis() / 1000L;
                    if(unixTime-upload_time<=24*3600){
                        recents.add(text);
                    }
                }
                catch(Exception e){

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
