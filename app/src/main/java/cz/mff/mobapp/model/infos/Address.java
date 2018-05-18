package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Address implements ContactInfo {

    private static final String ADDRESS_CONTENT_KEY = "address";
    private static final String ADDRESS_TYPE_KEY = "type";

    private String address; // data1
    private int type; // data2

    public static final EntityHandler<Address> handler = new SimpleEntityHandler<Address>(Address.class, Address::new) {
        @Override
        public void loadFromJSON(Address object, JSONObject jsonObject) throws Exception {
            object.address = jsonObject.optString(ADDRESS_CONTENT_KEY);
            object.type = jsonObject.optInt(ADDRESS_TYPE_KEY);
        }

        @Override
        public void storeToJSON(Address object, JSONObject jsonObject) throws Exception {
            jsonObject.put(ADDRESS_CONTENT_KEY, object.address);
            jsonObject.put(ADDRESS_TYPE_KEY, object.type);
        }

        @Override
        public Builder storeToBuilder(Address object, Builder builder) {
            return builder.withValue(StructuredPostal.FORMATTED_ADDRESS, object.address)
                    .withValue(StructuredPostal.TYPE, object.type)
                    .withValue(StructuredPostal.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Address from, Address to) throws Exception {
            to.address = from.address;
            to.type = from.type;
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
