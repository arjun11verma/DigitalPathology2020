/**
 * This is a class for the connection to the Python server
 * @author Arjun Verma
 * @version 1.0
 */

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
    private RequestQueue queue; // Volley request queue
    private String serverUrl = "http://ab48c13eb9ba.ngrok.io"; // Server url
    private MainActivity activity; // Instance of the main activity
    private boolean done = false; // Boolean representing whether the call was made
    private boolean success = true; // Boolean representing whether the call was a success

    /**
     * Constructor for the ServerConnect class
     * @param activity Instance of the main activity
     */
    public ServerConnect(MainActivity activity) {
        this.activity = activity;
        queue = Volley.newRequestQueue(this.activity);
    }

    /**
     * Makes a post to the Python server using the Android Volley library
     * Determines whether to the post was successful or not
     * @param postObject JSON object to be sent to the server
     */
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

    // Getters and setters for the fields

    public boolean getDone() {
        return done;
    }

    public void setDone() {
        done = false;
    }

    public boolean getSuccess() { return success; }
}
