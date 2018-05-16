package cz.mff.mobapp.api;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ErrorResponse extends Exception {

    final int code;
    final String errorMessage;

    ErrorResponse(VolleyError error) throws JSONException {
        final NetworkResponse response = error.networkResponse;
        final String data = new String(response.data, StandardCharsets.UTF_8);
        final JSONObject object = new JSONObject(data);

        this.code = response.statusCode;
        this.errorMessage = object.getJSONObject("data").getString("detail");
    }

}
