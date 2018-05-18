package cz.mff.mobapp.api;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthProvider implements RequestAuthProvider {

    private final String userPass;

    public BasicAuthProvider(String username, String password) {
        this.userPass = encodeUserPass(username, password);
    }

    private static String encodeUserPass(String username, String password) {
        String rawUserPass = String.format("%s:%s", username, password);

        try {
            return Base64.encodeToString(rawUserPass.getBytes("UTF-8"), Base64.DEFAULT);
        }
        catch (UnsupportedEncodingException ignored) {
            // Doesn't happen for UTF-8
            return null;
        }
    }

    @Override
    public Map<String, String> getAuthorizationHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", String.format("Basic %s", userPass));
        return headers;
    }

}
