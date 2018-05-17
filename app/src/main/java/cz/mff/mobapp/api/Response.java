package cz.mff.mobapp.api;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;


public class Response {

    public final int API_VERSION = 1;
    static final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
    private final int code;

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
