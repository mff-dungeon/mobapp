package cz.mff.mobapp.model.infos;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Nickname implements ContactInfo {

    private static final String NICKNAME_KEY = "nick";

    private String nickname; // data1

    public static final EntityHandler<Nickname> handler = new SimpleEntityHandler<Nickname>(Nickname.class, Nickname::new) {
        @Override
        public void loadFromJSON(Nickname object, JSONObject jsonObject) throws Exception {
            object.nickname = jsonObject.getString(NICKNAME_KEY);
        }

        @Override
        public void storeToJSON(Nickname object, JSONObject jsonObject) throws Exception {
            jsonObject.put(NICKNAME_KEY, object.nickname);
        }

        @Override
        public void update(Nickname from, Nickname to) throws Exception {
            to.nickname = from.nickname;
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
