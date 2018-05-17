package cz.mff.mobapp.database;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import cz.mff.mobapp.model.Contact;

public class DaoMapperFactory {
    public static DaoMapper<Contact, ContactData> getContactDaoMapper() {
        return new DaoMapper<Contact, ContactData>() {

            @Override
            public void convertToDao(@NonNull Contact from, @NonNull ContactData to) {
                to.setId(from.getId());
                to.setLastModified(from.getLastModified());
            }

            @Override
            public void convertFromDao(@NonNull ContactData from, @NonNull Contact to) {
                to.setId(from.getId());
                to.setLastModified(from.getLastModified());
                to.setContact(true);
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
