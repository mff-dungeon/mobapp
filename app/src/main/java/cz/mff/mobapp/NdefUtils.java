package cz.mff.mobapp;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

public class NdefUtils {
    private NdefUtils() {}

    public static void shareUrl(Activity activity, String uri) {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);

        if (nfc != null) {
            nfc.setNdefPushMessageCallback(event -> {
                NdefRecord uriRecord = NdefRecord.createUri(uri);
                return new NdefMessage(new NdefRecord[]{uriRecord});
            }, activity, activity);
        }
    }
}
