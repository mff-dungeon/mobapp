package cz.mff.mobapp;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.sync.SystemContactStorage.ContactEntry;

public class ContactsFragment extends Fragment {


    private ServiceLocator serviceLocator = null;
    private Context context;

    private ViewGroup rootView;

    private ListView contactList;

    void initialize(Context context, ServiceLocator serviceLocator, ContentResolver contentResolver) {
        this.context = context;
        this.serviceLocator = serviceLocator;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_contacts, container, false);

        contactList = rootView.findViewById(R.id.fragment_contact_list);
        contactList.setEmptyView(rootView.findViewById(R.id.fragment_contact_list_empty));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceLocator.getContactAPIManager().listAll(new TryCatch<>(this::loadContacts, Throwable::printStackTrace));
    }

    private void startContactActivity(Contact entry, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("uuid", entry.getId());
        startActivity(intent);
    }

    private void loadContacts(ArrayList<Contact> entries) {
        if (entries.isEmpty()) {
            contactList.setEmptyView(rootView.findViewById(R.id.fragment_contact_list_empty));
            return;
        }

        final ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(context, R.layout.list_item_share_edit, R.id.item_text, entries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Contact entry = entries.get(position);

                view.findViewById(R.id.item_text)
                        .setOnClickListener(l -> startContactActivity(entry, ContactDetailActivity.class));
                view.findViewById(R.id.btn_edit)
                        .setOnClickListener(l -> startContactActivity(entry, ContactEditActivity.class));
                view.findViewById(R.id.btn_share)
                        .setOnClickListener(l -> startContactActivity(entry, ShareBundleActivity.class));

                return view;
            }
        };

        contactList.setAdapter(adapter);
        contactList.invalidate();
    }

}
