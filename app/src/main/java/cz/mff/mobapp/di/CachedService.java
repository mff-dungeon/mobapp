package cz.mff.mobapp.di;

import cz.mff.mobapp.model.Factory;

public final class CachedService<T> {

    private T service;
    private Factory<T> factory;

    public CachedService(Factory<T> factory) {
        this.factory = factory;
    }

    public T get() {
        if (service == null)
            service = factory.create();

        return service;
    }
}
