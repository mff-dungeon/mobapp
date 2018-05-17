package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

public class Bundle implements Identifiable<UUID>, Updatable<Bundle> {

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

    public void loadFrom(Bundle bundle) {
        if (bundle.getId() != null)
            setId(bundle.getId());
        if (bundle.getLastModified() != null)
            setLastModified(bundle.getLastModified());
        if (bundle.isContact() != null)
            setContact(bundle.isContact());
    }

}
