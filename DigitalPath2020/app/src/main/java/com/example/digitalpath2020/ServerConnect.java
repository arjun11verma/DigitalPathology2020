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
    private String serverUrl = "http://f93dbd2048d4.ngrok.io"; // Server url
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
                        String stitchedData =  (String) response.get("imageData");

                        if((response.get("response")).equals("N")) {
                            activity.changeView(new PostUploadView(activity, "Your upload was NOT successful, please try again.", null));
                        } else {
                            activity.changeView(new PostUploadView(activity, "Your upload was successful!", stitchedData));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.changeView(new PostUploadView(activity, "Your upload was NOT successful, please try again.", null));
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

    public void sendUpload() {
        String postUrl = serverUrl + "/uploadImage";

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("name", activity.getName());
        } catch (JSONException e) {
            System.out.println("Failed Put");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, postObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (((String)response.get("response")).equals("Y")) {
                        System.out.println("Uploaded!");
                    } else {
                        System.out.println("Failed!");
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(600000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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
