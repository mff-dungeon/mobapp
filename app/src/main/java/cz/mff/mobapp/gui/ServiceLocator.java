package cz.mff.mobapp.gui;

import android.app.Activity;
import android.content.Context;

import java.util.UUID;

import cz.mff.mobapp.api.APIStorage;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.api.TokenAuthProvider;
import cz.mff.mobapp.auth.AccountSession;
import cz.mff.mobapp.di.CachedService;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class ServiceLocator {

    private Activity activity;
    private AccountSession accountSession;
    private static final String DB_NAME = "test-database2";

    public ServiceLocator(Activity activity) {
        this.activity = activity;
        this.accountSession = new AccountSession(activity);
    }

    public void ensureAuthenticated(Listener<Void> listener) {
        accountSession.retrieveToken(new TryCatch<>(
                token -> {
                    getRequester().setDefaultAuthProvider(new TokenAuthProvider(token));
                    listener.doTry(null);
                },
                listener
        ));
    }

    private CachedService<Requester> requesterCached = new CachedService<>(() -> {
        Requester r = new Requester(null);
        r.initializeQueue(this.activity);
        return r;
    });

    public Requester getRequester()
    {
        return requesterCached.get();
    }

    public APIStorage<Contact, UUID> createContactAPIStorage()
    {
        return new APIStorage<>("contacts", getRequester(), SerializerFactory.getContactSerializer(), Contact::new, Contact::copy);
    }

    private CachedService<Manager<Contact, UUID>> contactAPIManagerCached = new CachedService<>(() -> new Manager<>(createContactAPIStorage(), Contact::copy));

    public Manager<Contact, UUID> getContactAPIManager() {
        return contactAPIManagerCached.get();
    }


    // TODO: This will change return type - Interface?
    public Manager<Contact, UUID> createContactManager()
    {
        // TODO: return ApplicationManager once ready
        return getContactAPIManager();
    }

}
