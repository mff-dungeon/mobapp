package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Identity implements ContactInfo {

    private static final String IDENTITY_NUMBER_KEY = "number";
    private static final String IDENTITY_NAMESPACE_KEY = "namespace";

    private String number;
    private String namespace;

    public static final EntityHandler<Identity> handler = new SimpleEntityHandler<Identity>(Identity.class, Identity::new) {
        @Override
        public void loadFromJSON(Identity object, JSONObject jsonObject) throws Exception {
            object.number = jsonObject.getString(IDENTITY_NUMBER_KEY);
            object.namespace = jsonObject.getString(IDENTITY_NAMESPACE_KEY);
        }

        @Override
        public void storeToJSON(Identity object, JSONObject jsonObject) throws Exception {
            jsonObject.put(IDENTITY_NUMBER_KEY, object.number);
            jsonObject.put(IDENTITY_NAMESPACE_KEY, object.namespace);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(Identity object, ContentProviderOperation.Builder builder) throws Exception {
            return builder.withValue(ContactsContract.CommonDataKinds.Identity.IDENTITY, object.number)
                    .withValue(ContactsContract.CommonDataKinds.Identity.NAMESPACE, object.namespace);
        }

        @Override
        public void update(Identity from, Identity to) throws Exception {
            to.number = from.number;
            to.namespace = from.namespace;
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
