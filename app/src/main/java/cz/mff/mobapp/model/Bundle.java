package cz.mff.mobapp.model;

import java.util.Date;
import java.util.UUID;

public interface Bundle {

    UUID getId();
    boolean isContact();
    Date getLastModified();

}
