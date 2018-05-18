package cz.mff.mobapp.model.infos;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Email implements ContactInfo {

    public static final EntityHandler<Email> handler = new SimpleEntityHandler<Email>(Email.class, Email::new) {
        @Override
        public void loadFromJSON(Email object, JSONObject jsonObject) throws Exception {

        }

        @Override
        public void storeToJSON(Email object, JSONObject jsonObject) throws Exception {

        }

        @Override
        public void update(Email from, Email to) throws Exception {
        }
    };

    public static void register(EntityHandlerRepository repo) {
        repo.register(handler);
    }

    @Override
    public EntityHandler getHandler() {
        return handler;
    }
}
