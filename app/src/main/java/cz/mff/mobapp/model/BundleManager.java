package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.Response;
import cz.mff.mobapp.api.Serializer;
import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TransformingCaller;

public class BundleManager {

    private final Requester requester;

    public BundleManager(Requester requester) {
        this.requester = requester;
    }

    public void loadBundle(UUID id, Listener<? super Bundle> listener, ExceptionListener exceptionListener) {
        TransformingCaller<? super Bundle, Response, Exception> caller = new TransformingCaller<>(listener, exceptionListener,
                response -> {
                    Bundle b = Serializer.loadBundle(response.getObjectData());
                    return new Entity(b);
                }, e -> e);

        requester.sendGetRequest("bundles/" + id.toString(), caller::call, caller::exception);
    }


    public class Entity implements Bundle {
        UUID id;
        boolean isContact;
        Date lastModified;

        Entity(Bundle b) {
            this.id = b.getId();
            this.isContact = b.isContact();
            this.lastModified = b.getLastModified();
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public boolean isContact() {
            return isContact;
        }

        @Override
        public Date getLastModified() {
            return lastModified;
        }
    }
}
