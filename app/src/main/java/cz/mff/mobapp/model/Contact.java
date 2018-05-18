package cz.mff.mobapp.model;

import org.json.JSONObject;

import java.util.UUID;

import cz.mff.mobapp.api.Response;

public final class Contact extends Bundle {

    private String label;

    @Override
    public Boolean isContact() {
        return true;
    }

    public String getLabel() {
        return label;
    }

    public Contact setLabel(String label) {
        this.label = label;
        return this;
    }

    public static final EntityHandler<Contact> handler = new SimpleEntityHandler<Contact>(Contact.class, Contact::new) {
        @Override
        public void loadFromJSON(Contact c, JSONObject jsonObject) throws Exception {
            c.id = UUID.fromString(jsonObject.getString(ID));
            c.lastModified = Response.timeFormat.parse(jsonObject.getString(LAST_MODIFIED));
            c.label = jsonObject.getString("label");
            c.isContact = true;
        }

        @Override
        public void storeToJSON(Contact c, JSONObject jsonObject) throws Exception {
            if (c.getId() != null)
                jsonObject.put(ID, c.getId().toString());
            if (c.getLastModified() != null)
                jsonObject.put(LAST_MODIFIED, c.getLastModified());
            if (c.getLabel() != null)
                jsonObject.put(LABEL, c.getLastModified());
            jsonObject.put(IS_CONTACT, true);
        }

        @Override
        public void update(Contact from, Contact to) throws Exception {
            Bundle.handler.update(from, to);
            to.label = from.getLabel();
        }
    };
}
