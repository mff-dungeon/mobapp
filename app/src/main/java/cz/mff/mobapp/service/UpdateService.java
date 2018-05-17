package cz.mff.mobapp.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import cz.mff.mobapp.MainActivity;

public class UpdateService extends IntentService {

    public static final int UPDATE_CODE = 42;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        scheduleUpdate();
        return ret;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Gets data from the incoming Intent
        String action = intent.getAction();
        if (intent.getAction().equals(Intent.ACTION_RUN)) {
            updateDatabase();
        }
        if (intent.hasExtra("requestId")) {
            Intent reply = new Intent(MainActivity.UPDATE_DONE);
            reply.putExtra("requestId", intent.getStringExtra("requestId"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(reply);
        }
    }

    void scheduleUpdate() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(Intent.ACTION_RUN);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UPDATE_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }

    void cancelUpdate() {
        // TODO: do we need this?
        Context context = getApplicationContext();
        Intent intent = new Intent(context, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UPDATE_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    void updateDatabase() {
        // TODO: update the database from remote
    }
}
