package cz.mff.mobapp;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.UUID;
import java.util.concurrent.Executors;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.database.AppDatabase;
import cz.mff.mobapp.database.ContactData;
import cz.mff.mobapp.database.DatabaseStorage;
import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.event.APIStorage;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;
import cz.mff.mobapp.model.Storage;

public class MainActivity extends Activity implements ExceptionListener {

    private static final String TAG = "MainActivity";
    private Requester requester;
    private Manager<Contact, UUID> manager;
    private DatabaseStorage<Contact, ContactData> contactDatabase;
    private final UUID testBundleId = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");

    private void sendRequest() {
        requester.getRequest("bundles/", new TryCatch<>(
            response -> {
                JSONArray data = response.getArrayData();
                ((TextView) findViewById(R.id.responseText)).setText(data.toString());
            }, this));
    }

    private void showBundlesActivity() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requester = new Requester("test", "test");
        requester.initializeQueue(this);

        Storage<Contact, UUID> storage = new APIStorage<>("contacts", requester, SerializerFactory.getContactSerializer(), Contact::new, Contact::copy);
        manager = new Manager<>(storage, Contact::copy);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "test-database2").build();

        contactDatabase = new DatabaseStorage<>(db.contactDao(), Executors.newSingleThreadExecutor(),
                (from, to) -> System.out.println("test"),
                SerializerFactory.getContactSerializer(), Contact::new);
        setContentView(R.layout.activity_main);

        findViewById(R.id.requestButton).setOnClickListener(view -> sendRequest());
        findViewById(R.id.retrieveButton).setOnClickListener(view -> retrieveBundle());
        findViewById(R.id.updateButton).setOnClickListener(view -> updateBundle());
        findViewById(R.id.bundleButton).setOnClickListener(view -> showBundlesActivity());
        findViewById(R.id.createDeleteButton).setOnClickListener(view -> createDeleteBundle());
    }

    private void updateBundle() {
        manager.retrieve(testBundleId, new TryCatch<>(bundle -> {
            manager.save(bundle, new TryCatch<>(bundle1 -> {
                ((TextView) findViewById(R.id.responseText)).setText("Updated.");
            }, this));
        }, this));

        boolean handled = tryHandleIntent(getIntent());
        if (!handled) {
            askUserForTicketId();
        }
    }

    private void askUserForTicketId() {
        // TODO: show some kind of UI to retrieve ticket ID, then call subscribeToTicket()
    }

    private void subscribeToTicket(String ticketId) {
        // TODO: ask backend to copy the ticket and download stuffs
        System.out.println("subscribing to ticket: " + ticketId);
    }

    private boolean tryHandleIntent(Intent intent) {
        if (intent == null) {
            // no intent provided
            return false;
        }

        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        if (appLinkData == null || !Intent.ACTION_VIEW.equals(appLinkAction)) {
            // not app link intent
            return false;
        }

        String ticketId = appLinkData.getLastPathSegment();
        subscribeToTicket(ticketId);
        return true;
    }

    private void createDeleteBundle() {
        Contact c = new Contact();
        c.setContact(true);

        manager.save(c, new TryCatch<>(
                contact -> {
                    ((TextView) findViewById(R.id.responseText)).setText("Created ID " + contact.getId().toString());
                    manager.delete(contact.getId(), new TryCatch<>(foo -> {
                        ((TextView) findViewById(R.id.responseText)).setText("Aaand gone. ID " + contact.getId().toString());
                    }, this));
                }, this
        ));
    }

    private void retrieveBundle() {
        manager.retrieve(testBundleId, new TryCatch<>(bundle -> {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(bundle.getLastModified());
            ((TextView) findViewById(R.id.responseText)).setText("Last modified at " + currentDateTimeString);
        }, this));
    }

    @Override
    public void doCatch(Exception e) {
        e.printStackTrace();
        Log.e(TAG, e.getMessage());
        ((TextView) findViewById(R.id.errorText)).setText(e.getMessage());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean handled = tryHandleIntent(intent);
        if (!handled) {
            askUserForTicketId();
        }
    }
}
