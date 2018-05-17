package cz.mff.mobapp.event;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.Serializer;
import cz.mff.mobapp.model.Factory;
import cz.mff.mobapp.model.Storage;

public class APIStorage<T, I> implements Storage<T, I> {

    private final String url;
    private final Requester requester;
    private final Serializer<T> serializer;
    private final Factory<T> factory;

    public APIStorage(String url, Requester requester, Serializer<T> serializer, Factory<T> factory) {
        this.url = url;
        this.requester = requester;
        this.serializer = serializer;
        this.factory = factory;
    }

    @Override
    public void retrieve(I id, Listener<? super T> listener) {
        requester.sendGetRequest(url + "/" + id.toString(), new TryCatch<>(
                response -> {
                    T instance = factory.create();
                    serializer.load(instance, response.getObjectData());
                    listener.doTry(instance);
                },
                listener
        ));
    }
}
