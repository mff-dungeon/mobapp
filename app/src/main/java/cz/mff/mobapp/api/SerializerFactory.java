package cz.mff.mobapp.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.UUID;

import cz.mff.mobapp.model.Bundle;

public class SerializerFactory {

    public static Serializer<Bundle> getBundleSerializer() {
        return new Serializer<Bundle>() {
            @Override
            public void load(Bundle bundle, JSONObject jsonObject) throws JSONException, ParseException {
                bundle
                    .setContact(jsonObject.getBoolean("is_contact"))
                    .setLastModified(Response.timeFormat.parse(jsonObject.getString("last_modified")))
                    .setId(UUID.fromString(jsonObject.getString("id")));
            }

            @Override
            public void store(Bundle bundle, JSONObject jsonObject) throws JSONException {
                jsonObject.put("id", bundle.getId().toString());
                jsonObject.put("is_contact", bundle.isContact());
            }
        };
    }
}
