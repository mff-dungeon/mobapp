package cz.mff.mobapp.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;

public class Requester {

    private static final String API = "http://mobapp-server.herokuapp.com/api/";
    private static final String TAG = "requester";

    final String username, password;

    private RequestQueue queue = null;

    public Requester(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private String encodeUserPass() {
        try {
            return Base64.encodeToString((username + ":" + password).getBytes("UTF-8"),
                    Base64.DEFAULT);
        }
        catch (UnsupportedEncodingException ignored) {
            // Doesn't happen for UTF-8
            return null;
        }
    }

    public void initializeQueue(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void stopQueue() {
        if (queue == null) return;
        queue.stop();
    }

    private void request(int method, String url, JSONObject data, Listener<Response> listener) {
        TryCatch<JSONObject> tryListener = new TryCatch<>(
                response -> {
                    Log.v(TAG, "Got OK response: " + response.toString());
                    listener.doTry(new Response(response));
                },
                listener
        );
        TryCatch<VolleyError> catchListener = new TryCatch<>(
                error -> {
                    if (error instanceof ParseError) {
                        // HACK! Fucking volley cannot tell 204 apart from error.
                        listener.doTry(new Response(204));
                        return;
                    }
                    Log.v(TAG, "Got error response");
                    listener.doCatch(new ErrorResponse(error));
                },
                listener
        );
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(
                method,
                API + url,
                data,
                tryListener::doTry,
                catchListener::doTry
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + encodeUserPass());
                return headers;
            }
        };

        Log.v(TAG, "Sending request...");

        queue.add(jsonRequest);
    }

    public void getRequest(String url, Listener<Response> listener) {
        request(Request.Method.GET, url, null, listener);
    }

    public void putRequest(String url, JSONObject jsonObject, Listener<Response> listener) {
        request(Request.Method.PUT, url, jsonObject, listener);
    }

    public void postRequest(String url, JSONObject jsonObject, Listener<Response> listener) {
        request(Request.Method.POST, url, jsonObject, listener);
    }

    public void deleteRequest(String url, Listener<Response> listener) {
        request(Request.Method.DELETE, url, null, listener);
    }

}
