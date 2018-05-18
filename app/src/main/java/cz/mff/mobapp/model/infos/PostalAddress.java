package cz.mff.mobapp.model.infos;

import org.json.JSONObject;

import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.EntityHandlerRepository;
import cz.mff.mobapp.model.SimpleEntityHandler;

public class PostalAddress implements ContactInfo {

    private static final String ADDRESS_FORMATTED_KEY = "formatted";
    private static final String ADDRESS_TYPE_KEY = "type";
    private static final String ADDRESS_STREET_KEY = "street";
    private static final String ADDRESS_CITY_KEY = "city";
    private static final String ADDRESS_REGION_KEY = "region";
    private static final String ADDRESS_POSTCODE_KEY = "postcode";
    private static final String ADDRESS_COUNTRY_KEY = "country";

    private String formattedAddress; // data1
    private String addressType; // data2
    private String street; // data4
    private String city; // data7
    private String region; // data8
    private String postcode; // data9
    private String country; // data10

    public static final EntityHandler<PostalAddress> handler = new SimpleEntityHandler<PostalAddress>(PostalAddress.class, PostalAddress::new) {
        @Override
        public void loadFromJSON(PostalAddress object, JSONObject jsonObject) throws Exception {
            object.formattedAddress = jsonObject.getString(ADDRESS_FORMATTED_KEY);
            object.addressType = jsonObject.getString(ADDRESS_TYPE_KEY);
            object.street = jsonObject.getString(ADDRESS_STREET_KEY);
            object.city = jsonObject.getString(ADDRESS_CITY_KEY);
            object.region = jsonObject.getString(ADDRESS_REGION_KEY);
            object.postcode = jsonObject.getString(ADDRESS_POSTCODE_KEY);
            object.country = jsonObject.getString(ADDRESS_COUNTRY_KEY);
        }

        @Override
        public void storeToJSON(PostalAddress object, JSONObject jsonObject) throws Exception {
            jsonObject.put(ADDRESS_FORMATTED_KEY, object.formattedAddress);
            jsonObject.put(ADDRESS_TYPE_KEY, object.addressType);
            jsonObject.put(ADDRESS_STREET_KEY, object.street);
            jsonObject.put(ADDRESS_CITY_KEY, object.city);
            jsonObject.put(ADDRESS_REGION_KEY, object.region);
            jsonObject.put(ADDRESS_POSTCODE_KEY, object.postcode);
            jsonObject.put(ADDRESS_COUNTRY_KEY, object.country);
        }

        @Override
        public void update(PostalAddress from, PostalAddress to) throws Exception {
            to.formattedAddress = from.formattedAddress;
            to.addressType = from.addressType;
            to.street = from.street;
            to.city = from.city;
            to.region = from.region;
            to.postcode = from.postcode;
            to.country = from.country;
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
