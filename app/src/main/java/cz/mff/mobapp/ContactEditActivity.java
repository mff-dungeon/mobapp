package cz.mff.mobapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.Manager;
import cz.mff.mobapp.model.infos.Email;
import cz.mff.mobapp.model.infos.Name;
import cz.mff.mobapp.model.infos.Nickname;
import cz.mff.mobapp.model.infos.Phone;

public class ContactEditActivity extends Activity implements AuthenticatedActivity {

    public static final String PARAM_NEW = "new";
    public static final String PARAM_UUID = "uuid";

    private ServiceLocator serviceLocator;
    private UUID uuid;

    private Contact contact;
    private Manager<Contact, UUID> contactAPIManager;

    private TextView heading;
    private EditText name;
    private EditText nickname;
    private EditText email;
    private EditText phone;

    boolean creating;
    private String empty_heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        heading = findViewById(R.id.heading_activity);
        name = findViewById(R.id.text_name);
        nickname = findViewById(R.id.text_nickname);
        email = findViewById(R.id.text_email);
        phone = findViewById(R.id.text_phone);

        final Intent intent = getIntent();

        creating = intent.getBooleanExtra(PARAM_NEW, false);
        empty_heading = creating ? "New contact" : "Edit contact";

        uuid = (UUID) intent.getSerializableExtra(PARAM_UUID);
        if (!creating && uuid == null)
            finish();

        ServiceLocator.create(this);
    }

    @Override
    public void onAuthenticated() {
        if (creating) {
            this.contact = new Contact();
            return;
        }

        contactAPIManager.retrieve(uuid, new TryCatch<>(c -> {
            this.contact = c;
            updateFieldsFromContact();
        }, this::handleError));
    }

    private void updateFieldsFromContact() {
        if (contact == null)
            return;

        for (ContactInfo ci : contact.getContactInfos()) {
            if (ci instanceof Name) {
                Name name = (Name) ci;
                this.name.setText(name.getDisplayName());
                continue;
            }
            if (ci instanceof Nickname) {
                Nickname nick = (Nickname) ci;
                this.nickname.setText(nick.getNickname());
                continue;
            }
            if (ci instanceof Email) {
                Email mail = (Email) ci;
                this.email.setText(mail.getAddress());
                continue;
            }
            if (ci instanceof Phone) {
                Phone phone = (Phone) ci;
                this.phone.setText(phone.getNumber());
                continue;
            }

            Log.w(getClass().getSimpleName(), "Ignoring CI for simple edit!");
        }
    }

    private void updateContactFromFields() {
        if (contact == null)
            return;

        final ArrayList<ContactInfo> ci = contact.getContactInfos();
        ci.clear();

        final String name = this.name.getText().toString();
        if (name.length() != 0) {
            ci.add(new Name(name));
            contact.setLabel(name);
        }

        final String nickname = this.nickname.getText().toString();
        if (nickname.length() != 0)
            ci.add(new Nickname(nickname));

        final String email = this.email.getText().toString();
        if (email.length() != 0)
            ci.add(new Email(email));

        final String phone = this.phone.getText().toString();
        if (phone.length() != 0)
            ci.add(new Phone(phone));

        heading.setText(name.length() > 0 ? name : empty_heading);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                onSave();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSave() {
        updateContactFromFields();
        contactAPIManager.save(contact, new TryCatch<>(c -> {
            Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();

            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(serviceLocator.getAccountSession().getAccount(), "com.android.contacts", settingsBundle);

            finish();
        }, this::handleError));

    }

    private void handleError(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        contactAPIManager = serviceLocator.getContactAPIManager();
    }
}
