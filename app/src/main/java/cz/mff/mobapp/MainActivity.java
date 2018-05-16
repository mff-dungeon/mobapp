package cz.mff.mobapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import cz.mff.mobapp.requests.Requester;

public class MainActivity extends Activity {

    private Requester requester;

    private void sendRequest() {
        requester.sendGetRequest("bundles/", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                showResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(error);
            }
        });
    }

    private void showResponse(JSONArray response) {
        ((TextView) findViewById(R.id.responseText)).setText(response.toString() + " " + response.length());
    }

    private void showError(VolleyError error) {
        ((TextView) findViewById(R.id.errorText)).setText(error.getMessage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requester = new Requester();
        requester.initializeQueue(this);

        setContentView(R.layout.activity_main);

        findViewById(R.id.requestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
    }
}
