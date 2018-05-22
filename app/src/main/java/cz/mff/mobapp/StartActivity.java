package cz.mff.mobapp;

import android.Manifest;
import android.accounts.Account;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import cz.mff.mobapp.api.APIEndpoints;
import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;

public class StartActivity extends Activity implements ActionBar.TabListener, AuthenticatedActivity {

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 7;
    private static final String CONTACTS_AUTHORITY = "com.android.contacts";

    private ServiceLocator serviceLocator;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        loadingLayout = findViewById(R.id.start_layout);

        ServiceLocator.create(this);
    }

    private void checkAutoSync() {
        Account account = AccountUtils.getAccount(this, serviceLocator.getAccountSession().getAccountName());
        if(!ContentResolver.getSyncAutomatically(account, CONTACTS_AUTHORITY)) {
            // Ask user if they want to enable sync
            new AlertDialog.Builder(this)
                    .setTitle("Synchronization")
                    .setMessage("Contact synchronization is disabled. Do you want to turn it on?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> ContentResolver.setSyncAutomatically(account, CONTACTS_AUTHORITY, true))
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Possibly add "Do not show again" and save the value here
                    })
                    .show();
        }
    }

    private void initializePager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.start_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(sectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        loadingLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_ticket:
                intent = new Intent(this, ShareTicketActivity.class);

                //TODO: replace with real ticket
                final String ticketId = "33319b2f-f891-40b0-a23f-bcdaa9b71857";
                intent.putExtra(ShareTicketActivity.TICKET_ID, ticketId);
                startActivity(intent);
                return true;

            case R.id.action_new_contact:
                intent = new Intent(this, ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.PARAM_NEW, true);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAutoSync();
                    initializePager();
                }
                else {
                    ((TextView) findViewById(R.id.start_layout_text)).setText("Unable to access system contacts.");
                }
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onAuthenticated() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                PERMISSION_REQUEST_READ_CONTACTS);

        tryHandleIntent(getIntent());
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ContactsFragment contactsFragment = new ContactsFragment();
                    contactsFragment.initialize(StartActivity.this, serviceLocator, getContentResolver());
                    return contactsFragment;
                case 1:
                    GroupFragment groupFragment = new GroupFragment();
                    groupFragment.initialize(StartActivity.this, serviceLocator, getContentResolver());
                    return groupFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Contacts";
                case 1:
                    return "Groups";
            }
            return null;
        }
    }

    private void subscribeToTicket(String ticketId) {
        System.out.printf("cloning ticket %s\n", ticketId);
        serviceLocator.getRequester().putRequest(String.format(APIEndpoints.CLONE_ENDPOINT, ticketId), new JSONObject(),
                new TryCatch<>(response -> {
                    JSONObject data = response.getObjectData();
                    System.out.printf("ticket clone succeeded, clone has id: %s\n", data.get("id"));

                    // TODO: sync data store from the backend
                }, err -> {
                    System.out.println("ticket clone failed");

                    // TODO: show UI to indicate failure
                }));
    }

    private boolean tryHandleIntent(Intent intent) {
        if (intent == null) {
            // no intent provided
            return false;
        }

        String action = intent.getAction();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String ticketId = data.getLastPathSegment();
            subscribeToTicket(ticketId);
            return true;
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                for (Parcelable rawMessage : rawMessages) {
                    NdefMessage message = (NdefMessage) rawMessage;

                    for (NdefRecord record : message.getRecords()) {
                        String payloadString = new String(record.getPayload());
                        System.out.println("have record: " + payloadString);
                    }
                }
            }
            return true;
        }

        return false;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        tryHandleIntent(intent);
        // TODO: error here
    }
}
