package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

public class Bundle {

    private UUID id;
    private Boolean isContact;
    private Date lastModified;

    public UUID getId() {
        return id;
    }

    public Bundle setId(UUID id) {
        this.id = id;
        return this;
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

    void loadFrom(Bundle bundle) {
        if (bundle.getId() != null)
            setId(bundle.getId());
        if (bundle.getLastModified() != null)
            setLastModified(bundle.getLastModified());
        if (bundle.isContact() != null)
            setContact(bundle.isContact());
    }

}
