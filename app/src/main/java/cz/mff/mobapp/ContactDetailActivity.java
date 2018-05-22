package cz.mff.mobapp;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;
import android.widget.TextView;
import android.provider.ContactsContract.Data;
import android.widget.Toast;

import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.Manager;

public class ContactDetailActivity extends Activity implements AuthenticatedActivity {

    private Manager<Contact, UUID> contactManager;
    private UUID contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contactId = (UUID) getIntent().getSerializableExtra("uuid");

        ServiceLocator.create(this);
    }

    @Override
    public void onAuthenticated() {
        contactManager.retrieve(contactId, new TryCatch<Contact>(this::showContact, this::handleError));
    }

    private void showContact(Contact contact) {
        ((TextView) findViewById(R.id.contact_detail_id_value)).setText(String.valueOf(contactId));

        StringBuilder info = new StringBuilder();
        for (ContactInfo ci : contact.getContactInfos()) {
            info.append(ci.getHandler().getType())
                    .append(": ")
                    .append(ci.toString())
                    .append("\n");
        }

        ((TextView) findViewById(R.id.contact_detail_label)).setText(info.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        contactManager = serviceLocator.getContactAPIManager();
    }

    private void handleError(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }
}
