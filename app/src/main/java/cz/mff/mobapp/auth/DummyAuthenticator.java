package cz.mff.mobapp.auth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DummyAuthenticator {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static Map<String, String> mCredentialsRepo;

    static {
        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put("test@test.com", "test");
        mCredentialsRepo = Collections.unmodifiableMap(credentials);
    }

    public String signIn(String email, String password) {
        String authToken = null;
        final DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");

        if (mCredentialsRepo.containsKey(email)) {
            if (password.equals(mCredentialsRepo.get(email))) {
                authToken = email + "-" + df.format(new Date());
            }
        }

        return authToken;
    }

}
