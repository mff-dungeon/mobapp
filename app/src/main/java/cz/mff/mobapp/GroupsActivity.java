package cz.mff.mobapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class GroupsActivity extends Activity implements AuthenticatedActivity {

    private ServiceLocator serviceLocator;
    private boolean authenticated = false;

    private ListView contactList;
    private ListAdapter adapter;

    ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactList = findViewById(R.id.contact_list);
        contactList.setOnItemClickListener((adapterView, view, position, row) -> {
            showGroupDetail(groups.get(position).getId());
        });

        contactList.setEmptyView(findViewById(R.id.contact_list_empty));

        ServiceLocator.create(this);
    }

    @Override
    public void onAuthenticated() {
        final Manager<Group, UUID> manager = serviceLocator.getGroupAPIManager();

        manager.listAll(new TryCatch<>(this::groupsLoaded, Throwable::printStackTrace));
    }

    private void groupsLoaded(ArrayList<Group> groups) {

        this.groups = groups;

        adapter = new ArrayAdapter<Group>(this,
                android.R.layout.simple_list_item_1, groups) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(GroupsActivity.this)
                            .inflate(R.layout.contact_list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.contact_list_text))
                        .setText(groups.get(position).getLabel());
                return convertView;
            }
        };

        contactList.setAdapter(adapter);
    }

    private void showGroupDetail(UUID id) {
        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
