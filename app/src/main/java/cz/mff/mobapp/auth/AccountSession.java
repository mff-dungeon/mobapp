package cz.mff.mobapp.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cz.mff.mobapp.LoginActivity;
import cz.mff.mobapp.event.Listener;

public class AccountSession {

    private static final int REQ_SIGNUP = 1;

    private Activity activity;

    private AccountManager mAccountManager;
    private AuthPreferences mAuthPreferences;
    private String authToken;

    public AccountSession(Activity activity) {
        this.activity = activity;

        this.authToken = null;
        this.mAuthPreferences = new AuthPreferences(activity);
        this.mAccountManager = AccountManager.get(activity);
    }

    public void retrieveToken(Listener<String> tokenListener) {
        this.mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE,
                null, activity, null, null, new GetAuthTokenCallback(tokenListener), null);
    }

    public String getAuthToken() {
        return authToken;
    }

    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
        private Listener<String> tokenListener;

        GetAuthTokenCallback(Listener<String> tokenListener) {
            this.tokenListener = tokenListener;
        }

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;

            try {
                bundle = result.getResult();

                final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (null != intent) {
                    activity.startActivityForResult(intent, REQ_SIGNUP);
                } else {
                    authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                    // Save session username & auth token
                    mAuthPreferences.setAuthToken(authToken);
                    mAuthPreferences.setUsername(accountName);

                    // If the logged account didn't exist, we need to create it on the device
                    Account account = AccountUtils.getAccount(activity, accountName);
                    if (null == account) {
                        account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
                        mAccountManager.addAccountExplicitly(account, bundle.getString(LoginActivity.PARAM_USER_PASSWORD), null);
                        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
                    }

                    tokenListener.doTry(authToken);
                }
            } catch (Exception e) {
                tokenListener.doCatch(e);
            }
        }
    }


}
