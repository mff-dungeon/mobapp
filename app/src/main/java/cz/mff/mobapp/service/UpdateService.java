package cz.mff.mobapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class UpdateService extends IntentService {


    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Gets data from the incoming Intent
        intent.getDataString();
        // Do work here, based on the contents of dataString
    }
}
