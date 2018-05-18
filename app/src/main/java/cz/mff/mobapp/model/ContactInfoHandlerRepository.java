package cz.mff.mobapp.model;

import cz.mff.mobapp.model.infos.*;

public final class ContactInfoHandlerRepository {

    private ContactInfoHandlerRepository() {}

    private static EntityHandlerRepository<ContactInfo> instance;

    private static EntityHandlerRepository create() {
        EntityHandlerRepository<ContactInfo> repo = new EntityHandlerRepository<>();
        Address.register(repo);
        Email.register(repo);
        Identity.register(repo);
        InstantMessenger.register(repo);
        Name.register(repo);
        Nickname.register(repo);
        Note.register(repo);
        Organization.register(repo);
        Phone.register(repo);
        PostalAddress.register(repo);
        Website.register(repo);
        return repo;
    }

    public static EntityHandlerRepository<ContactInfo> get() {
        synchronized (ContactInfoHandlerRepository.class) {
            if (instance == null)
                instance = create();
        }
        return instance;
    }
}
