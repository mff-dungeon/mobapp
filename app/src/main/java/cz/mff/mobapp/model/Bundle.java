package cz.mff.mobapp.model;

import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.api.Response;

public class Bundle implements Identifiable<UUID> {

    protected static final String ID = "id";
    protected static final String LAST_MODIFIED = "last_modified";
    protected static final String IS_CONTACT = "is_contact";
    protected static final String LABEL = "label";

    protected UUID id = null;
    protected Boolean isContact;
    protected Date lastModified;

    public UUID getId() {
        return id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Boolean isContact() {
        return isContact;
    }


    public static final SimpleEntityHandler<Bundle> handler = new SimpleEntityHandler<Bundle>(Bundle.class, Bundle::new) {
        @Override
        public void loadFromJSON(Bundle b, JSONObject jsonObject) throws Exception {
            b.id = UUID.fromString(jsonObject.getString(ID));
            b.isContact = jsonObject.getBoolean(IS_CONTACT);
            b.lastModified = Response.timeFormat.parse(jsonObject.getString(LAST_MODIFIED));
        }

        @Override
        public void storeToJSON(Bundle b, JSONObject jsonObject) throws Exception {
            jsonObject.put(ID, b.getId().toString());
            jsonObject.put(IS_CONTACT, b.isContact());
            jsonObject.put(LAST_MODIFIED, b.getLastModified());
        }

        @Override
        public void update(Bundle from, Bundle to) throws Exception {
            if (from.getId() != null)
                to.id = from.getId();
            if (from.getLastModified() != null)
                to.lastModified = from.getLastModified();
            if (from.isContact() != null)
                to.isContact = from.isContact();
        }
    };

}
