package cz.mff.mobapp.model.infos;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Phone implements ContactInfo {

    private static final String PHONE_NUMBER_KEY = "number";
    private static final String PHONE_TYPE_KEY = "type";

    private String number; // data1
    private int type; // data2

    public static final EntityHandler<Phone> handler = new SimpleEntityHandler<Phone>(Phone.class, Phone::new) {
        @Override
        public void loadFromJSON(Phone object, JSONObject jsonObject) throws Exception {
            object.number = jsonObject.getString(PHONE_NUMBER_KEY);
            object.type = jsonObject.getInt(PHONE_TYPE_KEY);
        }

        @Override
        public void storeToJSON(Phone object, JSONObject jsonObject) throws Exception {
            jsonObject.put(PHONE_NUMBER_KEY, object.number);
            jsonObject.put(PHONE_TYPE_KEY, object.type);
        }

        @Override
        public void update(Phone from, Phone to) throws Exception {
            to.number = from.number;
            to.type = from.type;
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
