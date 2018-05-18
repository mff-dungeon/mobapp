package cz.mff.mobapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class ContactDetailActivity extends Activity implements AuthenticatedActivity {

    private Contact contact;
    private Manager<Contact, UUID> manager;
    private ServiceLocator serviceLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        ServiceLocator.create(this);
    }

    @Override
    public void onAuthenticated() {
        manager = serviceLocator.createContactManager();

        loadContact((UUID) getIntent().getSerializableExtra("uuid"));
    }

    private void loadContact(UUID uuid) {
        manager.retrieve(uuid, new TryCatch<>(
                this::showContact,
                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
        ));
    }

    private void showContact(Contact c) {
        this.contact = c;
        ((TextView) findViewById(R.id.contact_detail_id_value)).setText(c.getId().toString());
        ((TextView) findViewById(R.id.contact_detail_modified_value)).setText(c.getLastModified().toString());
        ((TextView) findViewById(R.id.contact_detail_label)).setText(c.getLabel());
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

}
