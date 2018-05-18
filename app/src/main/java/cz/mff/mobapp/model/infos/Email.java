package cz.mff.mobapp.model.infos;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.ContactInfoHandler;
import cz.mff.mobapp.model.ContactInfoHandlerRepository;
import cz.mff.mobapp.model.SimpleContactInfoHandler;

public class Email implements ContactInfo {

    private static final ContactInfoHandler handler = new SimpleContactInfoHandler(Email.class, Email::new) {
            @Override
            public void loadFromJSON(ContactInfo object, JSONObject jsonObject) throws Exception {

            }

            @Override
            public void storeToJSON(ContactInfo object, JSONObject jsonObject) throws Exception {

            }
        };

    public static void register(ContactInfoHandlerRepository repo) {
        repo.register(handler);
    }

    @Override
    public ContactInfoHandler getHandler() {
        return handler;
    }
}
