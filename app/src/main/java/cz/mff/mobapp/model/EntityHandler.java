package cz.mff.mobapp.model;

import android.content.ContentProviderOperation.Builder;

import org.json.JSONObject;

public interface EntityHandler<T> {
    T create();

    String getType();
    int getVersion();

    void loadFromJSON(T object, JSONObject jsonObject) throws Exception;
    void storeToJSON(T object, JSONObject jsonObject) throws Exception;

    Builder storeToBuilder(T object, Builder builder) throws Exception;

    void update(T from, T to) throws Exception;
}

