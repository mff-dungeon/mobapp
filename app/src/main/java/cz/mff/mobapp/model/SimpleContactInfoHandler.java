package cz.mff.mobapp.model;

abstract public class SimpleContactInfoHandler<T extends ContactInfo> implements ContactInfoHandler {

    private final Class<T> cls;
    private final Factory<T> factory;

    protected SimpleContactInfoHandler(Class<T> klass, Factory<T> factory) {
        this.cls = klass;
        this.factory = factory;
    }

    @Override
    public ContactInfo create() {
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
