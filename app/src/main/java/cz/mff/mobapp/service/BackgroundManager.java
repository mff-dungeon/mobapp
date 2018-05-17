package cz.mff.mobapp.service;

import java.util.UUID;

import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Manager;

class BackgroundManager<T extends Identifiable<UUID>> {
    private final Manager<T, UUID> databaseManager;
    private final Manager<T, UUID> apiManager;

    BackgroundManager(Manager<T, UUID> databaseManager, Manager<T, UUID> apiManager) {
        this.databaseManager = databaseManager;
        this.apiManager = apiManager;
    }


}
