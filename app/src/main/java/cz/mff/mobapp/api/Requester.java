package cz.mff.mobapp.api;

import android.content.Context;
import android.util.Base64;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    public void sendGetRequest(String url, ResponseListener result) {
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                API + url,
                null,
                resultListener(result),
                errorListener(result)
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

    private com.android.volley.Response.Listener<JSONObject> resultListener(ResponseListener listener) {
        return (JSONObject response) -> {
            try {
                final Response r = new SuccessResponse(200, response);
                listener.onResponse(r);
            } catch (ErrorResponse.ServerErrorException error) {
                listener.onResponse(error.getResponse());
            }
        };
    }

    private com.android.volley.Response.ErrorListener errorListener(ResponseListener listener) {
        return (VolleyError error) -> {
            try {
                final NetworkResponse response = error.networkResponse;
                final String data = new String(response.data, StandardCharsets.UTF_8);
                final JSONObject object = new JSONObject(data);
                final Response r = new ErrorResponse(response.statusCode, object);
                listener.onResponse(r);
            } catch (Exception e) {
                final Response r = ErrorResponse.wrapException(e).getResponse();
                listener.onResponse(r);
            }
        };
    }

    @FunctionalInterface
    public interface ResponseListener {
        void onResponse(Response response);
    }
}
