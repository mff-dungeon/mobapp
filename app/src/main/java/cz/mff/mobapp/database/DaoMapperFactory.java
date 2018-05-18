package cz.mff.mobapp.database;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import cz.mff.mobapp.model.Contact;

public class DaoMapperFactory {
    public static DaoMapper<Contact, ContactData> getContactDaoMapper() {
        return new DaoMapper<Contact, ContactData>() {

            @Override
            public void convertToDao(@NonNull Contact from, @NonNull ContactData to) {
                to.setId(from.getId());
                to.setLastModified(from.getLastModified());

                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.putOpt("label", from.getLabel());
                } catch (JSONException ignored) {
                    // Doesn't happen, cause we create from empty object
                }
                to.setData(jsonData);
            }

            @Override
            public void convertFromDao(@NonNull ContactData from, @NonNull Contact to) {
                //to.setId(from.getId());
                //to.setLastModified(from.getLastModified());
                JSONObject jsonObject = from.getData();

                String label = jsonObject.optString("label");
                if (label != null) to.setLabel(label);
            }

            @NonNull
            @Override
            public ContactData createDao() {
                return new ContactData();
            }

            @NonNull
            @Override
            public Contact createObject() {
                return new Contact();
            }
        };
    }
}
