package cz.mff.mobapp.api;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;


public abstract class Response {

    public final int API_VERSION = 1;
    static final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    protected Date time;

    protected int code;
    protected JSONObject response;

    protected Response(int code) {
        this.code = code;
    }

    protected Response(int code, JSONObject response) throws ErrorResponse.ServerErrorException {
        this.code = code;
        fillFromJSON(response);
    }

    private void fillFromJSON(JSONObject response) throws ErrorResponse.ServerErrorException {
        try {
            final int version = response.getInt("version");

            if (version > API_VERSION)
                throw new ErrorResponse(601, "Newer API, cannot parse!").getException();

            time = timeFormat.parse(response.getString("server_time"));
            this.response = response;
        } catch (JSONException | ParseException e) {
            throw ErrorResponse.wrapException(e);
        }
    }

    public abstract JSONArray getArrayData() throws ErrorResponse.ServerErrorException;
    public abstract JSONObject getObjectData() throws ErrorResponse.ServerErrorException;
}
