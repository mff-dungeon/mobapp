package cz.mff.mobapp.model;

import android.content.ContentProviderOperation.Builder;

abstract public class SimpleEntityHandler<T> implements EntityHandler<T> {

    private final Class<T> cls;
    private final Factory<T> factory;

    protected SimpleEntityHandler(Class<T> klass, Factory<T> factory) {
        this.cls = klass;
        this.factory = factory;
    }

    @Override
    public T create() {
        return factory.create();
    }

    @Override
    public String getType() {
        return cls.getSimpleName();
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
