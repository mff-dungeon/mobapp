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
import android.widget.TextView;

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
        groupList.setOnItemClickListener((adapterView, view, position, row) -> showGroupDetail(groups.get(position).getId()));
        groupList.setEmptyView(rootView.findViewById(R.id.fragment_group_list_empty));

        final Manager<Group, UUID> manager = serviceLocator.getGroupAPIManager();
        manager.listAll(new TryCatch<>(this::groupsLoaded, Throwable::printStackTrace));

        return rootView;
    }

    private void groupsLoaded(ArrayList<Group> groups) {

        this.groups = groups;

        adapter = new ArrayAdapter<Group>(context,
                android.R.layout.simple_list_item_1, groups) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.contact_list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.contact_list_text))
                        .setText(groups.get(position).getLabel());
                return convertView;
            }
        };

        groupList.setAdapter(adapter);
    }

    private void showGroupDetail(UUID id) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
