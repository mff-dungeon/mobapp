package cz.mff.mobapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(view -> performLogin());
    }

    private void performLogin() {
        EditText emailView = findViewById(R.id.emailView);
        EditText passwordView = findViewById(R.id.passwordView);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        // TODO: validate that email and password make sense

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        // TODO: ask the API
        loginFailed("API call not implemented.");
    }

    private void loginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void loginSucceeded(UUID token) {
        // TODO: save token somewhere
    }

}
