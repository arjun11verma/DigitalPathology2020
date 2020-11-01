package com.example.digitalpath2020;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class ServerConnect {
    private RequestQueue queue;
    private String serverUrl = "http://ef5bd2cc8acc.ngrok.io";
    private MainActivity activity;
    private ImageLoader imageLoader;
    private boolean done = false;

    public ServerConnect(MainActivity activity) {
        this.activity = activity;
        queue = Volley.newRequestQueue(activity);
    }

    public void makePost(JSONObject postObject) {
        String postUrl = serverUrl + "/acceptImages";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, postObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                done = true;
                System.out.println("Post successful");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                done = true;
                System.out.println(error);
            }
        });

        queue.add(request);
    }

    public boolean getDone() {
        return done;
    }
}
