package cz.mff.mobapp.model;

public interface ContactInfo {
    String TYPE = "type";
    String VERSION = "version";
    String DATA = "data";

    EntityHandler<ContactInfo> getHandler();
}
