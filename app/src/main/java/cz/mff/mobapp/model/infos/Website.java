package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Website implements ContactInfo {

    private static final String WEBSITE_URL_KEY = "url";

    private String url; // data1

    public static final EntityHandler<Website> handler = new SimpleEntityHandler<Website>(Website.class, Website::new) {
        @Override
        public void loadFromJSON(Website object, JSONObject jsonObject) throws Exception {
            object.url = jsonObject.getString(WEBSITE_URL_KEY);
        }

        @Override
        public void storeToJSON(Website object, JSONObject jsonObject) throws Exception {
            jsonObject.put(WEBSITE_URL_KEY, object.url);
        }

        @Override
        public Builder storeToBuilder(Website object, Builder builder) throws Exception {
            return builder
                    .withValue(CommonDataKinds.Website.URL, object.url);
        }

        @Override
        public void update(Website from, Website to) throws Exception {
            to.url = from.url;
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
