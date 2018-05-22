package cz.mff.mobapp.gui;

import android.app.Activity;
import android.content.Context;

import java.util.UUID;

import cz.mff.mobapp.AuthenticatedActivity;
import cz.mff.mobapp.api.APIStorage;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.TokenAuthProvider;
import cz.mff.mobapp.auth.AccountSession;
import cz.mff.mobapp.di.CachedService;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class ServiceLocator {

    private Activity activity;

    protected ServiceLocator(Activity activity) {
        this.activity = activity;
    }

    private CachedService<AccountSession> accountSession = new CachedService<>(() -> new AccountSession(this.activity));

    public AccountSession getAccountSession() {
        return accountSession.get();
    }

    private void ensureAuthenticated(Listener<Void> listener) {
        getAccountSession().retrieveToken(new TryCatch<>(
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

    private APIStorage<Bundle, UUID> createBundleAPIStorage() {
        return new APIStorage<>("bundles", getRequester(), Bundle.handler);
    }

    public APIStorage<Contact, UUID> createContactAPIStorage()
    {
        return new APIStorage<>("contacts", getRequester(), Contact.handler);
    }

    private APIStorage<Group, UUID> createGroupAPIStorage() {
        return new APIStorage<>("groups", getRequester(), Group.handler);
    }

    private CachedService<Manager<Bundle, UUID>> bundleAPIManagerCached = new CachedService<>(() -> new Manager<>(createBundleAPIStorage(), Bundle.handler));
    private CachedService<Manager<Contact, UUID>> contactAPIManagerCached = new CachedService<>(() -> new Manager<>(createContactAPIStorage(), Contact.handler));
    private CachedService<Manager<Group, UUID>> groupAPIManagerCached = new CachedService<>(() -> new Manager<>(createGroupAPIStorage(), Group.handler));

    public Manager<Contact, UUID> getContactAPIManager() { return contactAPIManagerCached.get(); }
    public Manager<Bundle, UUID> getBundleAPIManager() { return bundleAPIManagerCached.get(); }
    public Manager<Group, UUID> getGroupAPIManager() { return groupAPIManagerCached.get(); }

    public static void create(AuthenticatedActivity authenticatedActivity) {
        ServiceLocator sl = new ServiceLocator(authenticatedActivity.getActivity());
        authenticatedActivity.setServiceLocator(sl);
        sl.ensureAuthenticated(new TryCatch<>(
                authenticated -> {
                    authenticatedActivity.onAuthenticated();
                },
                error -> {
                    error.printStackTrace();
                    authenticatedActivity.finish();
                }
        ));
    }
}
