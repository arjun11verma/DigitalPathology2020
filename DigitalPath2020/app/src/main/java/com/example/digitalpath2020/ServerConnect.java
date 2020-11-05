package com.example.digitalpath2020;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ServerConnect {
    private RequestQueue queue;
    private String serverUrl = "http://58b195fce587.ngrok.io";
    private MainActivity activity;
    private boolean done = false;

    public ServerConnect(MainActivity activity) {
        this.activity = activity;
        queue = Volley.newRequestQueue(this.activity);
    }

    public void makePost(JSONObject postObject) {
        if(!done) {
            System.out.println("Method Called!");
            String postUrl = serverUrl + "/acceptImages";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, postObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("Post successful");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("It might not be a perfect post, but we'll take it.");
                    System.out.println(error);
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(600000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(request);
            done = true;
        }
    }

    public boolean getDone() {
        return done;
    }

    public void setDone() {
        done = false;
    }
}
