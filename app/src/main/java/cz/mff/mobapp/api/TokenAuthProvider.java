package cz.mff.mobapp.api;

import java.util.HashMap;
import java.util.Map;

public class TokenAuthProvider implements RequestAuthProvider {

    private final String token;

    public TokenAuthProvider(String token) {
        this.token = token;
    }

    @Override
    public Map<String, String> getAuthorizationHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", String.format("Token %s", token));
        return headers;
    }

}
