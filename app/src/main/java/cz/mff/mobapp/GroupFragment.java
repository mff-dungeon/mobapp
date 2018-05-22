package cz.mff.mobapp;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.UUID;

import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Group;
import cz.mff.mobapp.model.Manager;

public class GroupFragment extends Fragment {
    private ServiceLocator serviceLocator = null;
    private ContentResolver contentResolver = null;
    private Context context;

    private ListView groupList;
    private ListAdapter adapter;

    ArrayList<Group> groups;

    private ViewGroup rootView;

    void initialize(Context context, ServiceLocator serviceLocator, ContentResolver contentResolver) {
        this.context = context;
        this.serviceLocator = serviceLocator;
        this.contentResolver = contentResolver;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_groups, container, false);

        groupList = rootView.findViewById(R.id.fragment_group_list);
        groupList.setEmptyView(rootView.findViewById(R.id.fragment_group_list_empty));

        final Manager<Group, UUID> manager = serviceLocator.getGroupAPIManager();
        manager.listAll(new TryCatch<>(this::groupsLoaded, Throwable::printStackTrace));

        return rootView;
    }

    private void groupsLoaded(ArrayList<Group> groups) {

        this.groups = groups;

        adapter = new ArrayAdapter<Group>(context, R.layout.list_item_share_edit, R.id.item_text, groups) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Group entry = groups.get(position);

                view.findViewById(R.id.item_text)
                        .setOnClickListener(l -> startGroupActivity(entry, GroupDetailActivity.class));
                view.findViewById(R.id.btn_edit)
                        .setOnClickListener(l -> startGroupActivity(entry, GroupDetailActivity.class)); // TODO: edit activity
                view.findViewById(R.id.btn_share)
                        .setOnClickListener(l -> startGroupActivity(entry, ShareBundleActivity.class));
                return view;
            }
        };

        groupList.setAdapter(adapter);
    }

    private void startGroupActivity(Group group, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("uuid", group.getId());
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
