package cz.mff.mobapp.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Factory;
import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Storage;
import cz.mff.mobapp.model.Updater;

public class APIStorage<T extends Identifiable<I>, I> implements Storage<T, I> {

    private static final String TAG = "APIStorage";
    private final String url;
    private final Requester requester;
    private final Serializer<T> serializer;
    private final Factory<T> factory;
    private final Updater<T, T> updater;

    public APIStorage(String url, Requester requester, Serializer<T> serializer, Factory<T> factory, Updater<T, T> updater) {
        this.url = url;
        this.requester = requester;
        this.serializer = serializer;
        this.factory = factory;
        this.updater = updater;
    }

    private T createInstance(JSONObject jsonObject) throws Exception {
        T instance = factory.create();
        serializer.load(instance, jsonObject);
        return instance;
    }

    private T updateInstance(T instance, JSONObject jsonObject) throws Exception {
        T updated = createInstance(jsonObject);
        updater.update(updated, instance);
        return instance;
    }

    private ArrayList<T> createList(JSONArray jsonArray) throws Exception {
        ArrayList<T> list = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            list.add(createInstance(jsonArray.getJSONObject(i)));
        return list;
    }

    @Override
    public void retrieve(I id, Listener<? super T> listener) {
        requester.getRequest(url + "/" + id.toString() + "/", new TryCatch<>(
                response -> listener.doTry(createInstance(response.getObjectData())),
                listener
        ));
    }

    @Override
    public void update(T object, Listener<? super T> listener) {
        try {
            I id = object.getId();
            JSONObject jsonObject = new JSONObject();
            serializer.store(object, jsonObject);
            requester.putRequest(url + "/" + id.toString() + "/", jsonObject, new TryCatch<>(
                    response -> listener.doTry(updateInstance(object, response.getObjectData())),
                    listener));
        } catch (Exception e) {
            listener.doCatch(e);
        }
    }

    @Override
    public void create(T object, Listener<? super T> listener) {
        assert object.getId() == null;

        try {
            JSONObject jsonObject = new JSONObject();
            serializer.store(object, jsonObject);
            requester.postRequest(url + "/", jsonObject, new TryCatch<>(
                    response -> listener.doTry(updateInstance(object, response.getObjectData())),
                    listener));
        } catch (Exception e) {
            listener.doCatch(e);
        }
    }

    @Override
    public void delete(I id, Listener<Void> listener) {
        requester.deleteRequest(url + "/" + id.toString() + "/", new TryCatch<>(
                foo -> listener.doTry(null),
                listener
        ));
    }

    @Override
    public void listAll(Listener<ArrayList<T>> listener) {
        requester.getRequest(url + "/", new TryCatch<>(
                response -> listener.doTry(createList(response.getArrayData())),
                listener
        ));
    }
}
