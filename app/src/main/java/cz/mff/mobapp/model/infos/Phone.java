package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds;

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
            object.number = jsonObject.optString(PHONE_NUMBER_KEY);
            object.type = jsonObject.optInt(PHONE_TYPE_KEY);
        }

        @Override
        public void storeToJSON(Phone object, JSONObject jsonObject) throws Exception {
            jsonObject.put(PHONE_NUMBER_KEY, object.number);
            jsonObject.put(PHONE_TYPE_KEY, object.type);
        }

        @Override
        public Builder storeToBuilder(Phone object, Builder builder) throws Exception {
                return builder
                        .withValue(CommonDataKinds.Phone.NUMBER, object.number)
                        .withValue(CommonDataKinds.Phone.TYPE, object.type)
                        .withValue(CommonDataKinds.Phone.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Phone from, Phone to) throws Exception {
            to.number = from.number;
            to.type = from.type;
        }
    };

    public Phone() {}

    public Phone(String phone) {
       this.number = phone;
    }

    public static void register(EntityHandlerRepository<ContactInfo> repo) {
        repo.register(handler);
    }

    @Override
    public EntityHandler getHandler() {
        return handler;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number;
    }
}
