package cz.mff.mobapp.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.UUID;

import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Contact;

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

    public static Serializer<Contact> getContactSerializer() {
        return new Serializer<Contact>() {
            @Override
            public void load(Contact contact, JSONObject jsonObject) throws JSONException, ParseException {
                contact
                        .setContact(jsonObject.getBoolean("is_contact"))
                        .setLastModified(Response.timeFormat.parse(jsonObject.getString("last_modified")))
                        .setId(UUID.fromString(jsonObject.getString("id")));
            }

            @Override
            public void store(Contact contact, JSONObject jsonObject) throws JSONException {
                jsonObject.put("id", contact.getId().toString());
                jsonObject.put("is_contact", contact.isContact());
            }
        };
    }
}
