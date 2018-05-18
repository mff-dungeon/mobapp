package cz.mff.mobapp.model;

import java.util.HashMap;

public class EntityHandlerRepository {

    private final HashMap<String, EntityHandler> handlers = new HashMap<>();

    public void register(EntityHandler handler) {
        handlers.put(getKey(handler.getType(), handler.getVersion()), handler);
    }

    private String getKey(String type, int version) {
        return type + "(" + version + ")";
    }
}
