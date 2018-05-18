package cz.mff.mobapp.api;

import org.json.JSONObject;

public interface Serializer<T> {
    void load(T object, JSONObject jsonObject) throws Exception;
    void store(T object, JSONObject jsonObject) throws Exception;
}
