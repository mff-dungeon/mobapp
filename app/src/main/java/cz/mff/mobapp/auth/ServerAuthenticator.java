package cz.mff.mobapp.auth;

import android.content.Context;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.mff.mobapp.api.BasicAuthProvider;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;

public class ServerAuthenticator {

    private Requester requester;

    public ServerAuthenticator(Context context) {
        requester = new Requester(null);
        requester.initializeQueue(context);
    }

    public void retrieveToken(String email, String password, Listener<String> tokenListener) {
        requester.getRequest("token/", new BasicAuthProvider(email, password), new TryCatch<>(
                data -> {
                    JSONObject object = data.getObjectData();
                    tokenListener.doTry(object.getString("token"));
                },
                tokenListener
        ));
    }

}
