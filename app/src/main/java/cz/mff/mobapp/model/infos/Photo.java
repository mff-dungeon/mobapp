package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract.CommonDataKinds;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Photo implements ContactInfo {

    private static final String PHOTO_FILE_ID_KEY = "file_id";
    private static final String PHOTO_FILE_KEY = "file";

    private String fileId; // data14
    // FIXME: the field below is certainly not a String
    private String file; // data15

    public static final EntityHandler<Photo> handler = new SimpleEntityHandler<Photo>(Photo.class, Photo::new) {
        @Override
        public void loadFromJSON(Photo object, JSONObject jsonObject) throws Exception {
            object.fileId = jsonObject.optString(PHOTO_FILE_ID_KEY);
            object.file = jsonObject.optString(PHOTO_FILE_KEY);
        }

        @Override
        public void storeToJSON(Photo object, JSONObject jsonObject) throws Exception {
            jsonObject.put(PHOTO_FILE_ID_KEY, object.fileId);
            jsonObject.put(PHOTO_FILE_KEY, object.file);
        }

        @Override
        public Builder storeToBuilder(Photo object, Builder builder) throws Exception {
            return builder
                    .withValue(CommonDataKinds.Photo.PHOTO_FILE_ID, object.fileId)
                    .withValue(CommonDataKinds.Photo.PHOTO, object.file)
                    .withValue(CommonDataKinds.Photo.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Photo from, Photo to) throws Exception {
            to.fileId = from.fileId;
            to.file = from.file;
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
