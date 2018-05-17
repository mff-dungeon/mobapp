package cz.mff.mobapp.database;

import android.support.annotation.NonNull;

interface DaoMapper<T, E> {
    void convertToDao(@NonNull T from, @NonNull E to);
    void convertFromDao(@NonNull E from, @NonNull T to);

    @NonNull E createDao();
    @NonNull T createObject();
}
