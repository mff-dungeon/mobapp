package cz.mff.mobapp.model.infos;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class Organization implements ContactInfo {

    private static final String ORG_COMPANY_KEY = "company";
    private static final String ORG_TITLE_KEY = "title";
    private static final String ORG_DEPARTMENT_KEY = "department";
    private static final String ORG_JOB_KEY = "job";
    private static final String ORG_SYMBOL_KEY = "symbol";
    private static final String ORG_OFFICE_KEY = "office";

    private String company; // data1
    private String title; // data4
    private String department; // data5
    private String job; // data6
    private String symbol; // data7
    private String office; // data9

    public static final EntityHandler<Organization> handler = new SimpleEntityHandler<Organization>(Organization.class, Organization::new) {
        @Override
        public void loadFromJSON(Organization object, JSONObject jsonObject) throws Exception {
            object.company = jsonObject.optString(ORG_COMPANY_KEY);
            object.title = jsonObject.optString(ORG_TITLE_KEY);
            object.department = jsonObject.optString(ORG_DEPARTMENT_KEY);
            object.job = jsonObject.optString(ORG_JOB_KEY);
            object.symbol = jsonObject.optString(ORG_SYMBOL_KEY);
            object.office = jsonObject.optString(ORG_OFFICE_KEY);
        }

        @Override
        public void storeToJSON(Organization object, JSONObject jsonObject) throws Exception {
            jsonObject.put(ORG_COMPANY_KEY, object.company);
            jsonObject.put(ORG_TITLE_KEY, object.title);
            jsonObject.put(ORG_DEPARTMENT_KEY, object.department);
            jsonObject.put(ORG_JOB_KEY, object.job);
            jsonObject.put(ORG_SYMBOL_KEY, object.symbol);
            jsonObject.put(ORG_OFFICE_KEY, object.office);
        }

        @Override
        public Builder storeToBuilder(Organization object, Builder builder) throws Exception {
            return builder
                    .withValue(CommonDataKinds.Organization.COMPANY, object.company)
                    .withValue(CommonDataKinds.Organization.TITLE, object.title)
                    .withValue(CommonDataKinds.Organization.DEPARTMENT, object.department)
                    .withValue(CommonDataKinds.Organization.JOB_DESCRIPTION, object.job)
                    .withValue(CommonDataKinds.Organization.SYMBOL, object.symbol)
                    .withValue(CommonDataKinds.Organization.OFFICE_LOCATION, object.office)
                    .withValue(CommonDataKinds.Organization.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
        }

        @Override
        public void update(Organization from, Organization to) throws Exception {
            to.company = from.company;
            to.title = from.title;
            to.department = from.department;
            to.job = from.job;
            to.symbol = from.symbol;
            to.office = from.office;
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
