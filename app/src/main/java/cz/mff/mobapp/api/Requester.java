package cz.mff.mobapp.api;

import android.content.Context;
import android.util.Base64;

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

    public void sendGetRequest(String url, Listener<Response> listener) {
        TryCatch<JSONObject> tryListener = new TryCatch<>(
                response -> listener.doTry(new Response(response)),
                listener
        );
        TryCatch<VolleyError> catchListener = new TryCatch<>(
                error -> listener.doCatch(new ErrorResponse(error)),
                listener
        );
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                API + url,
                null,
                tryListener::safeTry,
                catchListener::safeTry
            )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + encodeUserPass());
                return headers;
            }
        };

        queue.add(jsonRequest);
    }

}
