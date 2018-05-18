package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Email implements ContactInfo {

    private static final String EMAIL_ADDRESS_KEY = "address";
    private static final String EMAIL_TYPE_KEY = "type";
    private static final String EMAIL_DISPLAY_NAME_KEY = "display_name";

    private String address; // data1
    private int type; // data2
    private String displayName; // data4

    public static final EntityHandler<Email> handler = new SimpleEntityHandler<Email>(Email.class, Email::new) {
        @Override
        public void loadFromJSON(Email object, JSONObject jsonObject) throws Exception {
            object.address = jsonObject.getString(EMAIL_ADDRESS_KEY);
            object.type = jsonObject.getInt(EMAIL_TYPE_KEY);
            object.displayName = jsonObject.getString(EMAIL_DISPLAY_NAME_KEY);
        }

        @Override
        public void storeToJSON(Email object, JSONObject jsonObject) throws Exception {
            jsonObject.put(EMAIL_ADDRESS_KEY, object.address);
            jsonObject.put(EMAIL_TYPE_KEY, object.type);
            jsonObject.put(EMAIL_DISPLAY_NAME_KEY, object.displayName);
        }

        @Override
        public Builder storeToBuilder(Email object, Builder builder) throws Exception {
            return builder.withValue(CommonDataKinds.Email.ADDRESS, object.address)
                    .withValue(CommonDataKinds.Email.TYPE, object.type)
                    .withValue(CommonDataKinds.Email.DISPLAY_NAME, object.displayName);
        }

        @Override
        public void update(Email from, Email to) throws Exception {
            to.address = from.address;
            to.type = from.type;
            to.displayName = from.displayName;
        }
    };

    public static void register(EntityHandlerRepository<ContactInfo> repo) {
        repo.register(handler);
    }

    @Override
    public EntityHandler getHandler() {
        return handler;
    }
}
