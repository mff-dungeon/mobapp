package cz.mff.mobapp;

import android.app.Activity;

import cz.mff.mobapp.gui.ServiceLocator;

public interface AuthenticatedActivity {

    Activity getActivity();

    void onAuthenticated();

    void finish();

    void setServiceLocator(ServiceLocator serviceLocator);

}
