package cz.mff.mobapp.model;

import java.util.HashMap;

import cz.mff.mobapp.model.infos.Email;

public class ContactInfoHandlerRepository {

    private final HashMap<String, ContactInfoHandler> handlers = new HashMap<>();

    public ContactInfoHandlerRepository() {
        Email.register(this);
    }

    public void register(ContactInfoHandler handler) {
        handlers.put(getKey(handler.getType(), handler.getVersion()), handler);
    }

    private String getKey(String type, int version) {
        return type + "(" + version + ")";
    }
}
