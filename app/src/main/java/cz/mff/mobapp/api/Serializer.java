package cz.mff.mobapp.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.model.*;

public class Serializer {

    public static void loadBundle(Bundle bundle, JSONObject jsonObject) throws JSONException, ParseException {
        assert bundle.isContact() == jsonObject.getBoolean("is_contact");

        bundle.setId(UUID.fromString(jsonObject.getString("id")))
                .setLastModified(Response.timeFormat.parse(jsonObject.getString("last_modified")));
    }

    public static void storeBundle(JSONObject r, Bundle bundle) throws JSONException {
        r.put("id", bundle.getId().toString());
        r.put("is_contact", bundle.isContact());
    }

}
