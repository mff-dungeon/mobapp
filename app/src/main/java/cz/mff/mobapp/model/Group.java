package cz.mff.mobapp.model;

import android.content.ContentProviderOperation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import cz.mff.mobapp.api.Response;

public final class Group extends Bundle {

    private String label;
    private ArrayList<Bundle> innerBundles;

    @Override
    public Boolean isContact() {
        return false;
    }

    public static final String INNER_BUNDLES = "inner_bundles";
    public static final EntityHandler<Group> handler = new SimpleEntityHandler<Group>(Group.class, Group::new) {
        @Override
        public void loadFromJSON(Group c, JSONObject jsonObject) throws Exception {
            c.id = UUID.fromString(jsonObject.getString(ID));
            c.lastModified = Response.timeFormat.parse(jsonObject.getString(LAST_MODIFIED));
            c.label = jsonObject.getString(LABEL);
            c.isContact = false;

            JSONArray arr = jsonObject.getJSONArray(INNER_BUNDLES);
            c.innerBundles = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                Bundle b = Bundle.handler.create();
                Bundle.handler.loadFromJSON(b, arr.getJSONObject(i));
                c.innerBundles.add(b);
            }
        }

        @Override
        public void storeToJSON(Group c, JSONObject jsonObject) throws Exception {
            if (c.getId() != null)
                jsonObject.put(ID, c.getId().toString());
            if (c.getLastModified() != null)
                jsonObject.put(LAST_MODIFIED, c.getLastModified());
            if (c.getLabel() != null)
                jsonObject.put(LABEL, c.getLastModified());
            jsonObject.put(IS_CONTACT, false);
        }

        @Override
        public ContentProviderOperation.Builder storeToBuilder(Group object, ContentProviderOperation.Builder builder) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void update(Group from, Group to) throws Exception {
            Bundle.handler.update(from, to);
            to.label = from.getLabel();
        }
    };

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Bundle> getInnerBundles() {
        return innerBundles;
    }
}
