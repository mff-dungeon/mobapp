package cz.mff.mobapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorResponse extends Response {

    String errorMessage;

    ErrorResponse(int code, String errorMessage) {
        super(code);
        this.errorMessage = errorMessage;
    }

    ErrorResponse(int code, JSONObject jsonObject) throws ServerErrorException {
        super(code, jsonObject);
        try {
            this.errorMessage = response.getJSONObject("data").getString("detail");
        } catch (JSONException e) {
            throw wrapException(e);
        }
    }

    @Override
    public JSONArray getArrayData() throws ServerErrorException {
        throw getException();
    }

    @Override
    public JSONObject getObjectData() throws ServerErrorException {
        throw getException();
    }

    public ServerErrorException getException() {
        return new ServerErrorException();
    }

    public static ServerErrorException wrapException(Exception e) {
        final ErrorResponse er = new ErrorResponse(600, e.getMessage());
        return er.getException();
    }

    public class ServerErrorException extends Exception {
        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return errorMessage;
        }

        public ErrorResponse getResponse() {
            return ErrorResponse.this;
        }
    }
}
