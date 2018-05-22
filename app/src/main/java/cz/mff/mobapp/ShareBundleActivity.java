package cz.mff.mobapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import java.util.UUID;

import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class ShareBundleActivity extends Activity implements AuthenticatedActivity {

    private ServiceLocator serviceLocator;
    private UUID uuid;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uuid = (UUID) getIntent().getSerializableExtra("uuid");
    }

    @Override
    protected void onStart() {
        super.onStart();

        ServiceLocator.create(this);
    }

    @Override
    public void onAuthenticated() {
        // TODO: offer customization (editable, shareable, ...)

        // TODO: find proper ticket ID
        final UUID ticketId = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");

        Intent intent = new Intent(this, ShareTicketActivity.class);
        intent.putExtra(ShareTicketActivity.TICKET_ID, ticketId);
        startActivity(intent);
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
