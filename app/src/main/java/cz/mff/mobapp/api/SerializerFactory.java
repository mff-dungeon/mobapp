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
                jsonObject.put("last_modified", bundle.getLastModified());
            }
        };
    }

    public static Serializer<Contact> getContactSerializer() {
        return new Serializer<Contact>() {
            @Override
            public void load(Contact contact, JSONObject jsonObject) throws Exception {
                contact
                        .setLabel(jsonObject.getString("label"))
                        .setLastModified(Response.timeFormat.parse(jsonObject.getString("last_modified")))
                        .setId(UUID.fromString(jsonObject.getString("id")));
            }

            @Override
            public void store(Contact contact, JSONObject jsonObject) throws Exception {
                if (contact.getLabel() != null)
                    jsonObject.put("label", contact.getLabel());
                if (contact.getId() != null)
                    jsonObject.put("id", contact.getId().toString());
                if (contact.getLastModified() != null)
                    jsonObject.put("last_modified", contact.getLastModified());
                // TODO: add Contact specific
            }
        };
    }
}
