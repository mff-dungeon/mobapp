package cz.mff.mobapp.gui;

import java.util.UUID;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Manager;

public class ApplicationManager<T extends Identifiable<UUID>> {
    private final Manager<T, UUID> databaseManager;
    private final Manager<T, UUID> apiManager;

    public ApplicationManager(Manager<T, UUID> databaseManager, Manager<T, UUID> apiManager) {
        this.databaseManager = databaseManager;
        this.apiManager = apiManager;
    }

    public void retrieve(UUID id, Listener<? super T> listener) {
        databaseManager.retrieve(id, new TryCatch<>((result) -> {
            if (result == null) {
                // TODO: call background service update
            }
            else {
                listener.doTry(result);
            }
        }, listener));
    }

}
