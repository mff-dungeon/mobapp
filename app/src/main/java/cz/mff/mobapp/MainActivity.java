package cz.mff.mobapp;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.event.APIStorage;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Manager;
import cz.mff.mobapp.model.Storage;

public class MainActivity extends Activity implements ExceptionListener {

    private Requester requester;
    private Manager<Bundle, UUID> manager;
    private final UUID testBundleId = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");

    private void sendRequest() {
        requester.sendGetRequest("bundles/", new TryCatch<>(
            response -> {
                JSONArray data = response.getArrayData();
                ((TextView) findViewById(R.id.responseText)).setText(data.toString());
            }, this));
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
        findViewById(R.id.updateButton).setOnClickListener(view -> updateBundle());
    }

    private void updateBundle() {
        manager.retrieve(testBundleId, new TryCatch<Bundle>(bundle -> {
                manager.save(bundle, new TryCatch<Bundle>(bundle1 -> {
                    Toast.makeText(this, "Updated.", Toast.LENGTH_SHORT).show();
                }, this));
        }, this));
    }

    private void retrieveBundle() {
        manager.retrieve(testBundleId, new TryCatch<>(bundle -> {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(bundle.getLastModified());
            ((TextView) findViewById(R.id.responseText)).setText("Last modified at " + currentDateTimeString);
        }, this));
    }

    @Override
    public void doCatch(Exception e) {
        ((TextView) findViewById(R.id.errorText)).setText(e.getMessage());
    }
}
