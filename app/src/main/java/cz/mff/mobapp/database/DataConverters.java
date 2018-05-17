package cz.mff.mobapp.database;

import android.arch.persistence.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class DataConverters {
    @TypeConverter
    public static JSONObject toJSONObject(String data) {
        try {
            return new JSONObject(data);
        }
        catch (JSONException e) {
            return null;
        }
    }

    @TypeConverter
    public static String fromJSONObject(JSONObject object) {
        return object.toString();
    }

    @TypeConverter
    public static JSONArray toJSONArray(String data) {
        try {
            return new JSONArray(data);
        }
        catch (JSONException e) {
            return null;
        }
    }

    @TypeConverter
    public static String fromJSONArray(JSONArray object) {
        return object.toString();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static UUID StringtoUUID(String value) {
        return UUID.fromString(value);
    }

    @TypeConverter
    public static String fromUUID(UUID value) {
        return value.toString();
    }
}
