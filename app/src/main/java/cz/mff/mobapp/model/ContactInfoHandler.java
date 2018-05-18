package cz.mff.mobapp.model;

import org.json.JSONObject;

public interface ContactInfoHandler {
    ContactInfo create();

    String getType();
    int getVersion();

    void loadFromJSON(ContactInfo object, JSONObject jsonObject) throws Exception;
    void storeToJSON(ContactInfo object, JSONObject jsonObject) throws Exception;
}

