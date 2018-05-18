package cz.mff.mobapp.model;

import java.util.HashMap;

public class EntityHandlerRepository<T> {

    private final HashMap<String, EntityHandler<? extends T>> handlers = new HashMap<>();

    public void register(EntityHandler<? extends T> handler) {
        handlers.put(getKey(handler.getType(), handler.getVersion()), handler);
    }

    public EntityHandler<T> lookup(String type, int version) {
        return (EntityHandler<T>) handlers.get(getKey(type, version));
    }

    private String getKey(String type, int version) {
        return type + "(" + version + ")";
    }
}
