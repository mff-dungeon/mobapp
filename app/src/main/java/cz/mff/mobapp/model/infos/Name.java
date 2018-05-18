package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Name implements ContactInfo {

    private static final String NAME_DISPLAY_KEY = "display";
    private static final String NAME_GIVEN_KEY = "given";
    private static final String NAME_FAMILY_KEY = "family";
    private static final String NAME_PREFIX_KEY = "prefix";
    private static final String NAME_MIDDLE_KEY = "middle";
    private static final String NAME_SUFFIX_KEY = "suffix";

    private String displayName;
    private String givenName;
    private String familyName;
    private String prefix;
    private String middleName;
    private String suffix;

    public static final EntityHandler<Name> handler = new SimpleEntityHandler<Name>(Name.class, Name::new) {
        @Override
        public void loadFromJSON(Name object, JSONObject jsonObject) throws Exception {
            object.displayName = jsonObject.getString(NAME_DISPLAY_KEY);
            object.givenName = jsonObject.getString(NAME_GIVEN_KEY);
            object.familyName = jsonObject.getString(NAME_FAMILY_KEY);
            object.prefix = jsonObject.getString(NAME_PREFIX_KEY);
            object.middleName = jsonObject.getString(NAME_MIDDLE_KEY);
            object.suffix = jsonObject.getString(NAME_SUFFIX_KEY);
        }

        @Override
        public void storeToJSON(Name object, JSONObject jsonObject) throws Exception {
            jsonObject.put(NAME_DISPLAY_KEY, object.displayName);
            jsonObject.put(NAME_GIVEN_KEY, object.givenName);
            jsonObject.put(NAME_FAMILY_KEY, object.familyName);
            jsonObject.put(NAME_PREFIX_KEY, object.prefix);
            jsonObject.put(NAME_MIDDLE_KEY, object.middleName);
            jsonObject.put(NAME_SUFFIX_KEY, object.suffix);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(Name object, ContentProviderOperation.Builder builder) throws Exception {
            return builder.withValue(StructuredName.DISPLAY_NAME, object.displayName)
                    .withValue(StructuredName.GIVEN_NAME, object.givenName)
                    .withValue(StructuredName.FAMILY_NAME, object.familyName)
                    .withValue(StructuredName.PREFIX, object.prefix)
                    .withValue(StructuredName.MIDDLE_NAME, object.middleName)
                    .withValue(StructuredName.SUFFIX, object.suffix);
        }

        @Override
        public void update(Name from, Name to) throws Exception {
            to.displayName = from.displayName;
            to.givenName = from.givenName;
            to.familyName = from.familyName;
            to.prefix = from.prefix;
            to.middleName = from.middleName;
            to.suffix = from.suffix;
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
