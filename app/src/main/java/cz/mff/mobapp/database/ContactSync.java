package cz.mff.mobapp.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class ContactSync {

    private Manager<Contact, UUID> mLocalManager;
    private Manager<Contact, UUID> mRemoteManager;

    public ContactSync(Manager<Contact, UUID> localManager, Manager<Contact, UUID> remoteManager) {
        mLocalManager = localManager;
        mRemoteManager = remoteManager;
    }

    public void synchronize(Iterable<Contact> remoteContacts, Listener<Void> listener) {
        HashSet<UUID> remoteUUIDs = new HashSet<>();

        for (Contact remoteContact : remoteContacts) {
            remoteUUIDs.add(remoteContact.getId());
        }

        UUID[] uuids = new UUID[remoteUUIDs.size()];
        uuids = remoteUUIDs.toArray(uuids);

        mLocalManager.listByIDs(uuids, new TryCatch<>(
                contacts -> {
                    synchronizeWithContacts(contacts, remoteContacts, remoteUUIDs);
                },
                exception -> {
                    listener.doCatch(exception);
                }
        ));
    }

    private void synchronizeWithContacts(ArrayList<Contact> localContacts, Iterable<Contact> remoteContacts, HashSet<UUID> remoteUUIDs) {
        {
            HashMap<UUID, Contact> localContactMap = constructContactMap(localContacts);
            Semaphore semaphore = new Semaphore(-remoteUUIDs.size());

            for (Contact remoteContact : remoteContacts) {
                Contact localContact = localContactMap.get(remoteContact.getId());

                if (localContact == null) {
                    insertContact(remoteContact, new TryCatch<>(
                            nothing -> { semaphore.release(); },
                            exception -> {
                                exception.printStackTrace();
                                semaphore.release();
                            }
                    ));
                } else {
                    updateContact(localContact, remoteContact, new TryCatch<>(
                            nothing -> { semaphore.release(); },
                            exception -> {
                                exception.printStackTrace();
                                semaphore.release();
                            }
                    ));
                }
            }

            try {
                // this blocks until all remote contacts are processed
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        {
            HashSet<UUID> localUUIDs = new HashSet<>();
            // TODO: populate `localUUIDs` with all UUIDs of locally stored contacts

            localUUIDs.removeAll(remoteUUIDs);
            Semaphore semaphore = new Semaphore(-localUUIDs.size());

            for (UUID uuid : localUUIDs) {
                mLocalManager.delete(uuid, new TryCatch<>(
                        nothing -> { semaphore.release(); },
                        exception -> {
                            exception.printStackTrace();
                            semaphore.release();
                        }
                ));
            }

            try {
                // this blocks until all local contacts are processed
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<UUID, Contact> constructContactMap(ArrayList<Contact> contacts) {
        HashMap<UUID, Contact> map = new HashMap<>();

        for (Contact contact : contacts) {
            map.put(contact.getId(), contact);
        }

        return map;
    }

    private void updateContact(Contact local, Contact remote, Listener<Contact> listener) {
        // TODO: perform changes

        mLocalManager.save(local, listener);
    }

    private void insertContact(Contact remote, Listener<Void> listener) {
        // TODO
    }

}
