package cz.mff.mobapp.model;

import android.content.ContentProviderOperation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import cz.mff.mobapp.api.Response;

public final class Contact extends Bundle {

    private String label;

    private ArrayList<ContactInfo> contactInfos;

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

    public static final String INFORMATION = "information";

    public static final EntityHandler<Contact> handler = new SimpleEntityHandler<Contact>(Contact.class, Contact::new) {
        @Override
        public void loadFromJSON(Contact c, JSONObject jsonObject) throws Exception {
            c.id = UUID.fromString(jsonObject.getString(ID));
            c.lastModified = Response.timeFormat.parse(jsonObject.getString(LAST_MODIFIED));
            c.label = jsonObject.getString(LABEL);
            c.isContact = true;

            EntityHandlerRepository<ContactInfo> repo = ContactInfoHandlerRepository.get();
            c.contactInfos = new ArrayList<>();
            JSONArray arr = jsonObject.getJSONArray(INFORMATION);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonCI = arr.getJSONObject(i);
                String type = jsonCI.getString(ContactInfo.TYPE);
                int version = jsonCI.getInt(ContactInfo.VERSION);
                EntityHandler<ContactInfo> handler = repo.lookup(type, version);
                ContactInfo contactInfo = handler.create();
                handler.loadFromJSON(contactInfo, jsonCI.getJSONObject(ContactInfo.DATA));
                c.contactInfos.add(contactInfo);
            }
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
        public ContentProviderOperation.Builder storeToBuilder(Contact object, ContentProviderOperation.Builder builder) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void update(Contact from, Contact to) throws Exception {
            Bundle.handler.update(from, to);
            to.label = from.getLabel();
        }
    };

    public ArrayList<ContactInfo> getContactInfos() {
        return contactInfos;
    }
}
