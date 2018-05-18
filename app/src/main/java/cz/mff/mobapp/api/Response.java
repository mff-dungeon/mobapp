package cz.mff.mobapp.api;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;


public class Response {

    public final int API_VERSION = 1;
    private final int code;

    @SuppressLint("SimpleDateFormat")
    public static final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected Date time;

    protected JSONObject response;

    public Response(int code) {
        this.code = code;
    }

    public Response(JSONObject response) throws JSONException, IncompatibleVersionException, ParseException {
        final int version = response.getInt("version");

        if (version > API_VERSION)
            throw new IncompatibleVersionException();

        time = timeFormat.parse(response.getString("server_time"));
        this.response = response;
        this.code = 200;
    }

    public JSONObject getObjectData() throws JSONException {
        return response.getJSONObject("data");
    }

    public JSONArray getArrayData() throws JSONException {
        return response.getJSONArray("data");
    }

    public int getCode() {
        return code;
    }
}
