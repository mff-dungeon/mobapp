package cz.mff.mobapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.model.Identifiable;

@Entity
public class ContactData implements Identifiable<UUID> {
    @PrimaryKey
    @NonNull
    private UUID id;

    @ColumnInfo(name = "last_modified")
    private Date lastModified;

    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.TEXT)
    private JSONObject data;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
