package cz.mff.mobapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

import cz.mff.mobapp.model.Contact;

public class ContactDetailActivity extends Activity {

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        loadContact((UUID) getIntent().getSerializableExtra("uuid"));
    }

    private void loadContact(UUID uuid) {
        // TODO: Ask manager(s) to load contact

        // Testing data:
        Contact c = new Contact();
        c.setId(uuid);
        c.setLastModified(new Date(System.currentTimeMillis()));
        showContact(c);
    }

    private void showContact(Contact c) {
        this.contact = c;
        ((TextView) findViewById(R.id.contact_detail_id_value)).setText(c.getId().toString());
        ((TextView) findViewById(R.id.contact_detail_modified_value)).setText(c.getLastModified().toString());
    }
}
