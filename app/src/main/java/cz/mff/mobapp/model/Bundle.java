package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

public class Bundle implements Identifiable<UUID> {

    private UUID id = null;
    private Boolean isContact;
    private Date lastModified;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Bundle setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public Boolean isContact() {
        return isContact;
    }

    public Bundle setContact(boolean isContact) {
        this.isContact = isContact;
        return this;
    }

    public static void copy(Bundle from, Bundle to) {
        if (from.getId() != null)
            to.setId(from.getId());
        if (from.getLastModified() != null)
            to.setLastModified(from.getLastModified());
        if (from.isContact() != null)
            to.setContact(from.isContact());
    }

}
