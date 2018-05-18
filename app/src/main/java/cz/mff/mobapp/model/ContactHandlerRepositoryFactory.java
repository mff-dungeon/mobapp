package cz.mff.mobapp.model;

import cz.mff.mobapp.model.infos.Email;

public class ContactHandlerRepositoryFactory implements Factory<EntityHandlerRepository> {
    @Override
    public EntityHandlerRepository create() {
        EntityHandlerRepository repo = new EntityHandlerRepository();
        Email.register(repo);
        return repo;
    }
}
