package cz.mff.mobapp;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.event.APIStorage;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Manager;
import cz.mff.mobapp.model.Storage;

public class MainActivity extends Activity {

    private Requester requester;
    private Manager<Bundle, UUID> manager;

    private void sendRequest() {
        requester.sendGetRequest("bundles/", new TryCatch<>(
            response -> {
                JSONArray data = response.getArrayData();
                ((TextView) findViewById(R.id.responseText)).setText(data.toString());
            }, e -> ((TextView) findViewById(R.id.errorText)).setText(e.getMessage())));
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requester = new Requester("test", "test");
        requester.initializeQueue(this);

        Storage<Bundle, UUID> storage = new APIStorage<>("bundles", requester, SerializerFactory.getBundleSerializer(), Bundle::new);
        manager = new Manager<>(storage);
        
        setContentView(R.layout.activity_main);

        findViewById(R.id.requestButton).setOnClickListener(view -> sendRequest());
        findViewById(R.id.retrieveButton).setOnClickListener(view -> retrieveBundle());
    }

    private void retrieveBundle() {
        UUID id = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");
        manager.retrieve(id, new TryCatch<>(bundle -> {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(bundle.getLastModified());
            ((TextView) findViewById(R.id.responseText)).setText("Last modified at " + currentDateTimeString);
        }, e -> ((TextView) findViewById(R.id.errorText)).setText(e.getMessage())));
    }
}
