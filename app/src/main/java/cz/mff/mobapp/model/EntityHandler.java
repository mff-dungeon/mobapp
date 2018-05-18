package cz.mff.mobapp.model;

import org.json.JSONObject;

public interface EntityHandler<T> {
    T create();

    String getType();
    int getVersion();

    void loadFromJSON(T object, JSONObject jsonObject) throws Exception;
    void storeToJSON(T object, JSONObject jsonObject) throws Exception;

    void update(T from, T to) throws Exception;
}

