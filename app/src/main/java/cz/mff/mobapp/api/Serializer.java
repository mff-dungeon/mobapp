package cz.mff.mobapp.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.model.*;

public class Serializer {

    public static Bundle loadBundle(JSONObject jsonObject) throws JSONException, ParseException {
        final UUID id = UUID.fromString(jsonObject.getString("id"));
        final boolean isContact = jsonObject.getBoolean("is_contact");
        final Date lastModified = Response.timeFormat.parse(jsonObject.getString("last_modified"));

        return new Bundle() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public boolean isContact() {
                return isContact;
            }

            @Override
            public Date getLastModified() {
                return lastModified;
            }
        };
    }

    public static void storeBundle(JSONObject r, Bundle bundle) throws JSONException {
        r.put("id", bundle.getId().toString());
        r.put("is_contact", bundle.isContact());
    }

}
