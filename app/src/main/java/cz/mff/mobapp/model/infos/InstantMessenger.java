package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;

import org.json.JSONObject;

import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class InstantMessenger implements ContactInfo {

    private static final String IM_ID_KEY = "id";
    private static final String IM_PROTOCOL_KEY = "protocol";

    private String id;
    private int protocol;

    public static final EntityHandler<InstantMessenger> handler = new SimpleEntityHandler<InstantMessenger>(InstantMessenger.class, InstantMessenger::new) {
        @Override
        public void loadFromJSON(InstantMessenger object, JSONObject jsonObject) throws Exception {
            object.id = jsonObject.optString(IM_ID_KEY);
            object.protocol = jsonObject.optInt(IM_PROTOCOL_KEY);
        }

        @Override
        public void storeToJSON(InstantMessenger object, JSONObject jsonObject) throws Exception {
            jsonObject.put(IM_ID_KEY, object.id);
            jsonObject.put(IM_PROTOCOL_KEY, object.protocol);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(InstantMessenger object, ContentProviderOperation.Builder builder) throws Exception {
            return builder.withValue(ContactsContract.CommonDataKinds.Im.DATA, object.id)
                    .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, object.protocol)
                    .withValue(ContactsContract.CommonDataKinds.Im.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(InstantMessenger from, InstantMessenger to) throws Exception {
            to.id = from.id;
            to.protocol = from.protocol;
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
