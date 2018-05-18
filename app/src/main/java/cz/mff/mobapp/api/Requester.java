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

    private static final String API = "https://mobapp-server.herokuapp.com/api/";
    private static final String TAG = "requester";

    private RequestQueue queue = null;
    private RequestAuthProvider defaultAuthProvider;

    public Requester(RequestAuthProvider defaultAuthProvider) {
        this.defaultAuthProvider = defaultAuthProvider;
    }

    public RequestAuthProvider getDefaultAuthProvider() {
        return defaultAuthProvider;
    }

    public void setDefaultAuthProvider(RequestAuthProvider defaultAuthProvider) {
        this.defaultAuthProvider = defaultAuthProvider;
    }

    public void initializeQueue(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void stopQueue() {
        if (queue == null) return;
        queue.stop();
    }

    private void request(int method, String url, JSONObject data, RequestAuthProvider authProvider, Listener<Response> listener) {
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
                Map<String, String> headers = authProvider != null ? authProvider.getAuthorizationHeaders() : new HashMap<>();
                // TODO: add more headers?
                return headers;
            }
        };

        Log.v(TAG, String.format("%s %s", methodToString(method), API + url));

        queue.add(jsonRequest);
    }

    private static String methodToString(int method) {
        switch (method) {
            case Request.Method.GET: return "GET";
            case Request.Method.POST: return "POST";
            case Request.Method.PUT: return "PUT";
            case Request.Method.DELETE: return "DELETE";
        }

        return "UNKNOWN";
    }

    public void getRequest(String url, RequestAuthProvider authProvider, Listener<Response> listener) {
        request(Request.Method.GET, url, null, authProvider, listener);
    }

    public void putRequest(String url, JSONObject jsonObject, RequestAuthProvider authProvider, Listener<Response> listener) {
        request(Request.Method.PUT, url, jsonObject, authProvider, listener);
    }

    public void postRequest(String url, JSONObject jsonObject, RequestAuthProvider authProvider, Listener<Response> listener) {
        request(Request.Method.POST, url, jsonObject, authProvider, listener);
    }

    public void deleteRequest(String url, RequestAuthProvider authProvider, Listener<Response> listener) {
        request(Request.Method.DELETE, url, null, authProvider, listener);
    }

    public void getRequest(String url, Listener<Response> listener) {
        request(Request.Method.GET, url, null, defaultAuthProvider, listener);
    }

    public void putRequest(String url, JSONObject jsonObject, Listener<Response> listener) {
        request(Request.Method.PUT, url, jsonObject, defaultAuthProvider, listener);
    }

    public void postRequest(String url, JSONObject jsonObject, Listener<Response> listener) {
        request(Request.Method.POST, url, jsonObject, defaultAuthProvider, listener);
    }

    public void deleteRequest(String url, Listener<Response> listener) {
        request(Request.Method.DELETE, url, null, defaultAuthProvider, listener);
    }

}
