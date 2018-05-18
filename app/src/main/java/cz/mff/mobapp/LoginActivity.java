package cz.mff.mobapp;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.auth.ServerAuthenticator;
import cz.mff.mobapp.event.SyncTryCatch;

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String ARG_ACCOUNT_TYPE = "accountType";
    public static final String ARG_AUTH_TOKEN_TYPE = "authTokenType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
    public static final String PARAM_USER_PASSWORD = "password";

    private AccountManager accountManager;
    private UserLoginTask authTask = null;
    private ServerAuthenticator serverAuthenticator;

    private String username;
    private String password;

    private EditText usernameView;
    private EditText passwordView;
    private View loginFormView;
    private View loginStatusView;
    private TextView loginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountManager = AccountManager.get(this);
        serverAuthenticator = new ServerAuthenticator(this);

        username = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        usernameView = findViewById(R.id.email);
        usernameView.setText(username);

        passwordView = findViewById(R.id.password);
        loginFormView = findViewById(R.id.login_form);
        loginStatusView = findViewById(R.id.login_status);
        loginStatusMessageView = findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(view -> performLogin());

        if (null != username) {
            if (!username.isEmpty()) {
                passwordView.requestFocus();
            }
        }
    }

    private void performLogin() {
        if (authTask != null) {
            return;
        }

        usernameView.clearFocus();
        passwordView.clearFocus();

        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        username = usernameView.getText().toString();
        password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (password.length() < 4) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            authTask = new UserLoginTask(serverAuthenticator);
            authTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            loginStatusView.setVisibility(View.VISIBLE);
            loginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            loginFormView.setVisibility(View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {
        private ServerAuthenticator serverAuthenticator;

        public UserLoginTask(ServerAuthenticator serverAuthenticator) {
            this.serverAuthenticator = serverAuthenticator;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            final SyncTryCatch<String> waitListener = new SyncTryCatch<>();
            serverAuthenticator.retrieveToken(username, password, waitListener);

            // block until the token is available
            final String authToken = waitListener.getOrNull();

            final Intent res = new Intent();
            res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
            res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
            res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
            res.putExtra(PARAM_USER_PASSWORD, password);

            return res;
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            authTask = null;
            showProgress(false);

            if (null == intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)) {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            } else {
                finishLogin(intent);
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;
            showProgress(false);
        }

        private void finishLogin(Intent intent) {
            final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            final String accountPassword = intent.getStringExtra(PARAM_USER_PASSWORD);
            final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                accountManager.addAccountExplicitly(account, accountPassword, null);
                accountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
            } else {
                accountManager.setPassword(account, accountPassword);
            }

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(AccountAuthenticatorActivity.RESULT_OK, intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(AccountAuthenticatorActivity.RESULT_CANCELED);
        super.onBackPressed();
    }

}
