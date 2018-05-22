package cz.mff.mobapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class ShareBundleActivity extends Activity implements AuthenticatedActivity {

    Requester requester;
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

        try {
            JSONObject req = new JSONObject();
            req.put("lost", "thegame");

            requester.postRequest("bundles/" + String.valueOf(uuid) + "/token/", req, new TryCatch<>(
                    resp -> {
                        final UUID ticketId = UUID.fromString(resp.getObjectData().getString("id"));

                        Intent intent = new Intent(this, ShareTicketActivity.class);
                        intent.putExtra(ShareTicketActivity.TICKET_ID, ticketId);
                        startActivity(intent);
                    }, this::handleError
            ));

        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.requester = serviceLocator.getRequester();
    }

    private void handleError(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }
}
