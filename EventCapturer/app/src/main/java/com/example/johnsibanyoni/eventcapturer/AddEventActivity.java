package com.example.johnsibanyoni.eventcapturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Helpers.SQLiteHandler;
import Helpers.SessionManager;
import Helpers.StringHelper;
import models.AppConfig;
import models.AppController;

public class AddEventActivity extends AppCompatActivity {

  private static String TAG = "AddEventActivity";
  private SessionManager session;
  private SQLiteHandler db;
  private EditText edtEventName, edtEventDate, edtEventTime, edtDescription;
  private Spinner spnEventType;
  private ProgressDialog pDialog;
  private Button btnAddEvent;
  private String eventType = "";
  private String[] event_types = {"Entertainment", "Religious", "Art and Culture"};
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_event);

    // Progress dialog
    pDialog = new ProgressDialog(this);
    pDialog.setCancelable(false);

    edtEventName = findViewById(R.id.event_name);
    edtEventDate = findViewById(R.id.date);
    edtEventTime  = findViewById(R.id.time);
    spnEventType = findViewById(R.id.event_type);
    edtDescription = findViewById(R.id.description);
    btnAddEvent = findViewById(R.id.submit_button);

    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, event_types);
    spnEventType.setAdapter(adapter);
    // Session manager
    session = new SessionManager(getApplicationContext());

    // SQLite database handler
    db = new SQLiteHandler(getApplicationContext());

    spnEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        eventType = adapterView.getSelectedItem().toString();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    btnAddEvent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          String eventName = edtEventName.getText().toString();
          String eventDate = edtEventDate.getText().toString();
          String eventTime = edtEventTime.getText().toString();
          String description = edtDescription.getText().toString();

          if (!StringHelper.isNullOrEmpty(eventName) && !StringHelper.isNullOrEmpty(eventDate) && !StringHelper.isNullOrEmpty(eventTime)
          && !StringHelper.isNullOrEmpty(eventType) && !StringHelper.isNullOrEmpty(description)) {
            addEvent(eventName, eventDate, eventTime, eventType, description);
          }
        }catch (Exception ex){
          Log.e(TAG, "Problem adding an event: " + ex.getMessage());
        }
      }
    });
  }

  private void addEvent(final String eventName, final String eventDate, final String eventTime, final String eventType, final String description){
    try{
      // Tag used to cancel the request
      String tag_string_req = "req_addEvent";

      pDialog.setMessage("Adding event ...");
      showDialog();

      StringRequest strReq = new StringRequest(Request.Method.POST,
      AppConfig.URL_ADD_EVENT, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
          Log.d(TAG, "Add event Response: " + response.toString());
          hideDialog();

          try {
            JSONObject jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");
            if (!error) {
              // User successfully stored in MySQL
              // Now store the user in sqlite
              String uid = jObj.getString("uid");

              // Inserting row in event table
              //db.addUser(name, surname, staffNumber, passwordQuestion, passwordAnswer, username, uid, created_at);

              Toast.makeText(getApplicationContext(), "Event successfully added", Toast.LENGTH_LONG).show();
            } else {

              // Error occurred in registration. Get the error
              // message
              String errorMsg = jObj.getString("error_msg");
              Toast.makeText(getApplicationContext(),
              errorMsg, Toast.LENGTH_LONG).show();
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

        }
      }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
          Log.e(TAG, "Registration Error: " + error.getMessage());
          Toast.makeText(getApplicationContext(),
          error.getMessage(), Toast.LENGTH_LONG).show();
          hideDialog();
        }
      }) {

        @Override
        protected Map<String, String> getParams() {
          // Posting params to add event url
          Map<String, String> params = new HashMap<String, String>();
          params.put("eventName", eventName);
          params.put("eventDate", eventDate);
          params.put("eventTime", eventTime);
          params.put("eventType", eventType);
          params.put("description", description);

          return params;
        }

      };
      // Adding request to request queue
      AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }catch (Exception ex) {
      Log.e(TAG, "Problem adding event to server database: " + ex.getMessage());
    }

  }

  private void showDialog() {
    if (!pDialog.isShowing())
      pDialog.show();
  }

  private void hideDialog() {
    if (pDialog.isShowing())
      pDialog.dismiss();
  }
}
