package cz.mff.mobapp.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Storage;

public class APIStorage<T extends Identifiable<I>, I> implements Storage<T, I> {

    private static final String TAG = "APIStorage";
    private final String url;
    private final Requester requester;
    private final EntityHandler<T> handler;

    public APIStorage(String url, Requester requester, EntityHandler<T> handler) {
        this.url = url;
        this.requester = requester;
        this.handler = handler;
    }

    private T createInstance(JSONObject jsonObject) throws Exception {
        T instance = handler.create();
        handler.loadFromJSON(instance, jsonObject);
        return instance;
    }

    private T updateInstance(T instance, JSONObject jsonObject) throws Exception {
        T updated = createInstance(jsonObject);
        handler.update(updated, instance);
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
            handler.storeToJSON(object, jsonObject);
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
            handler.storeToJSON(object, jsonObject);
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
