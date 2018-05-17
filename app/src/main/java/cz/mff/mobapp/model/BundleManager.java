package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.Serializer;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;

public class BundleManager {

    private final Requester requester;

    public BundleManager(Requester requester) {
        this.requester = requester;
    }

    public void loadBundle(UUID id, Listener<? super Bundle> listener) {
        requester.sendGetRequest("bundles/" + id.toString(), new TryCatch<>(
                response -> {
                    final Bundle bundle = new Bundle();
                    Serializer.loadBundle(bundle, response.getObjectData());
                    listener.doTry(bundle);
                },
                listener
        ));
    }
}
