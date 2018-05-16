package cz.mff.mobapp.requests;

import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Requester {

    private static final String API = "http://test:test@mobapp-server.herokuapp.com/api/";
    private static final String USER = "test";
    private static final String PASS = "test";

    private RequestQueue queue = null;

    private String encodeUserPass() {
        try {
            return Base64.encodeToString((USER + "" + PASS).getBytes("UTF-8"),
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

    public void sendGetRequest(String url, Response.Listener<JSONArray> responseListener,
                               Response.ErrorListener errorListener) {

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                API + url,
                null,
                responseListener,
                errorListener
            )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + encodeUserPass()); // dGVzdDp0ZXN0
                return headers;
            }
        };

        queue.add(jsonRequest);
    }
}
