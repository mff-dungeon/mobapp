package cz.mff.mobapp;

import android.app.Activity;
import android.widget.TextView;

import java.util.UUID;

import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Bundle;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class GroupDetailActivity extends Activity implements AuthenticatedActivity {

    private ServiceLocator serviceLocator;
    private UUID id;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        id = (UUID) getIntent().getSerializableExtra("id");

        ServiceLocator.create(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadGroup() {
        final Manager<Group, UUID> manager = serviceLocator.getGroupAPIManager();

        manager.retrieve(id, new TryCatch<>(this::showGroup, Throwable::printStackTrace));
    }

    private void showGroup(Group group) {
        ((TextView) findViewById(R.id.group_detail_id_value)).setText(String.valueOf(group.getId()));
        ((TextView) findViewById(R.id.group_detail_label)).setText(group.getLabel());
        ((TextView) findViewById(R.id.group_detail_modified_value)).setText(group.getLastModified().toString());

        StringBuilder sb = new StringBuilder();

        sb.append(group.getLabel());
        sb.append("\n");

        for (Bundle b : group.getInnerBundles()) {
            sb.append(b.getId());
            sb.append("\n");
        }

        ((TextView) findViewById(R.id.group_detail_label)).setText(sb.toString());
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onAuthenticated() {
        loadGroup();
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }
}
