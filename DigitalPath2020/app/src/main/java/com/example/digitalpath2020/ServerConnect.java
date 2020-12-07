package com.example.digitalpath2020;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerConnect {
    private RequestQueue queue;
    private String serverUrl = "http://ab48c13eb9ba.ngrok.io";
    private MainActivity activity;
    private boolean done = false;
    private boolean success = true;

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
                    try {
                        if((response.get("response")).equals("Data not posted right. You're gonna have to try again!")) {
                            activity.changeView(new PostUploadView(activity, "Your upload was NOT successful, please try again."));
                        } else {
                            activity.changeView(new PostUploadView(activity, "Your upload was successful!"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.changeView(new PostUploadView(activity, "Your upload was NOT successful, please try again."));
                    success = false;
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

    public boolean getSuccess() { return success; }
}
