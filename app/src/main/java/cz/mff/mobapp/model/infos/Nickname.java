package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Nickname implements ContactInfo {

    private static final String NICKNAME_KEY = "nick";

    private String nickname;

    public static final EntityHandler<Nickname> handler = new SimpleEntityHandler<Nickname>(Nickname.class, Nickname::new) {
        @Override
        public void loadFromJSON(Nickname object, JSONObject jsonObject) throws Exception {
            object.nickname = jsonObject.optString(NICKNAME_KEY);
        }

        @Override
        public void storeToJSON(Nickname object, JSONObject jsonObject) throws Exception {
            jsonObject.put(NICKNAME_KEY, object.nickname);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(Nickname object, ContentProviderOperation.Builder builder) throws Exception {
            return builder.withValue(ContactsContract.CommonDataKinds.Nickname.NAME, object.nickname)
                    .withValue(ContactsContract.CommonDataKinds.Nickname.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Nickname from, Nickname to) throws Exception {
            to.nickname = from.nickname;
        }
    };

    public Nickname() {}

    public Nickname(String nickname) {
        this.nickname = nickname;
    }

    public static void register(EntityHandlerRepository<ContactInfo> repo) {
        repo.register(handler);
    }

    @Override
    public EntityHandler getHandler() {
        return handler;
    }

    public String getNickname() {
        return nickname;
    }
}
