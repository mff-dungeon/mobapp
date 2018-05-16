package cz.mff.mobapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SuccessResponse extends Response {

    public SuccessResponse(int code, JSONObject response) throws ErrorResponse.ServerErrorException {
        super(code, response);
    }

    public JSONObject getObjectData() throws ErrorResponse.ServerErrorException {
        try {
            return response.getJSONObject("data");
        } catch (JSONException e) {
            throw ErrorResponse.wrapException(e);
        }
    }

    public JSONArray getArrayData() throws ErrorResponse.ServerErrorException {
        try {
            return response.getJSONArray("data");
        } catch (JSONException e) {
            throw ErrorResponse.wrapException(e);
        }
    }
}
