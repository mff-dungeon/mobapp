package cz.mff.mobapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

import cz.mff.mobapp.api.ErrorResponse;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.Response;

public class MainActivity extends Activity {

    private Requester requester;

    private void sendRequest() {
        requester.sendGetRequest("bundles/", this::showResponse);
    }

    private void showResponse(Response response) {
        try {
            JSONArray data = response.getArrayData();
            ((TextView) findViewById(R.id.responseText)).setText(data.toString());
        } catch (ErrorResponse.ServerErrorException e) {
            ((TextView) findViewById(R.id.errorText)).setText(e.getCode() + " " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requester = new Requester("test", "test");
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
