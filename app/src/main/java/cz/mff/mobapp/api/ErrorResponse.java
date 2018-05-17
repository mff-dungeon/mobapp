package cz.mff.mobapp.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ErrorResponse extends Exception {

    int code;
    String errorMessage;

    ErrorResponse(VolleyError error) throws JSONException {
        super(error);

        final NetworkResponse response = error.networkResponse;
        if (response != null)
            fillFromResponse(response);
        else
            errorMessage = error.getMessage();
    }

    private void fillFromResponse(NetworkResponse response) throws JSONException {
        final String data = new String(response.data, StandardCharsets.UTF_8);
        final JSONObject object = new JSONObject(data);

        this.code = response.statusCode;
        this.errorMessage = object.getJSONObject("data").getString("detail");
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
