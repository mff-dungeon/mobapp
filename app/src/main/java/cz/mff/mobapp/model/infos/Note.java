package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Note implements ContactInfo {

    private static final String NOTE_CONTENT_KEY = "content";

    private String content;

    public static final EntityHandler<Note> handler = new SimpleEntityHandler<Note>(Note.class, Note::new) {
        @Override
        public void loadFromJSON(Note object, JSONObject jsonObject) throws Exception {
            object.content = jsonObject.optString(NOTE_CONTENT_KEY);
        }

        @Override
        public void storeToJSON(Note object, JSONObject jsonObject) throws Exception {
            jsonObject.put(NOTE_CONTENT_KEY, object.content);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(Note object, ContentProviderOperation.Builder builder) throws Exception {
            return builder.withValue(ContactsContract.CommonDataKinds.Note.NOTE, object.content)
                    .withValue(ContactsContract.CommonDataKinds.Note.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Note from, Note to) throws Exception {
            to.content = from.content;
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
