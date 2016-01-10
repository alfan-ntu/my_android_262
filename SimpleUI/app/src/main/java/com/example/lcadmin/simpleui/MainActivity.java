package com.example.lcadmin.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/*
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
*/

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private CheckBox hideCheckBox;
    private ListView historyListView;
    private Spinner storeInfoSpinner;
    private ImageView photoImageView;

/*
    Use SharedPreferences to store default values of inputText and checkBox checked status
 */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;
    private String menuResult;
    private boolean hasPhoto = false;
    private List<ParseObject> queryResult;

/*
    private CallbackManager callbackManager;
    private LoginButton loginButton;
*/

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeInfoSpinner = (Spinner) findViewById(R.id.storeInfoSpinner);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        photoImageView = (ImageView) findViewById(R.id.photo);

        editText = (EditText)findViewById(R.id.inputText);
/*
    updates setText from constant string to something read from sharedPreferences
    demonstrate how to getString from sharedPreferences
 */
        editText.setText(sharedPreferences.getString("inputText", ""));

        editText.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        submit(v);
                        return true;
                    }
                }
                return false;
            }
        });

/*
    demonstrates how to use sharedReferences to store boolean value
 */
        hideCheckBox = (CheckBox) findViewById(R.id.hideCheckBox);
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", isChecked);
                editor.commit();
            }
        });
        hideCheckBox.setChecked(sharedPreferences.getBoolean("hideCheckBox", false));

        historyListView = (ListView) findViewById(R.id.historyListView);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       goToOrderDetail(position);

                   }
               }
        );

        progressDialog = new ProgressDialog(this);
        setHistory();
        setStoreInfo();
//        setupFacebook();
    }

/*
    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                
            }
        });
    }
*/


    private void goToOrderDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);
        ParseObject object = queryResult.get(position);
        intent.putExtra("storeInfo", object.getString("storeInfo"));
        intent.putExtra("note", object.getString("note"));
        startActivity(intent);
    }

    private void setStoreInfo() {
        String[] stores = getResources().getStringArray(R.array.storeInfo);

        ParseQuery<ParseObject> query = new ParseQuery<>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                String[] stores = new String[objects.size()];
                for (int i = 0; i < stores.length; i++) {
                    ParseObject object = objects.get(i);
                    stores[i] = object.getString("name") + "," +
                            object.getString("address");
                }
                ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, stores);
                storeInfoSpinner.setAdapter(storeAdapter);
            }
        });

    }

    /*
        demonstrates how to list string array to a ListView
     */
    private void setHistory() {
        String[] rawData = Utils.readFile(this, "history.txt").split("\n");
        List<Map<String, String>> data = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                queryResult = objects;
                List<Map<String, String>> data = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    JSONArray array = object.getJSONArray("menu");

                    Map<String, String> item = new HashMap<String, String>();

                    item.put("note", note);
                    item.put("drinkNum", "15");
                    item.put("storeInfo", storeInfo);

                    data.add(item);
                }
                String[] from = {"note", "drinkNum", "storeInfo"};
                int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                        data, R.layout.listview_item, from, to);

                historyListView.setAdapter(adapter);
            }
        });
    }

/*
    "note" : this is a note
    "menu" : [....]
*/

    public void submit(View view){
        progressDialog.setTitle("Loading....");
        progressDialog.show();
        String text = editText.getText().toString();
/*
    demonstrates how to use SharedPreferences editor to store values
    method commit() of sharedReferences.edit() confirms the updates to the storage
 */
        editor.putString("inputText", text);
        editor.commit();
/*
    applies writeFile method from class Utils
 */
    try {
        JSONObject orderData = new JSONObject();
        if (menuResult == null)
            menuResult = "[]";
        JSONArray array = new JSONArray(menuResult);
        orderData.put("note", text);
        orderData.put("menu", array);
        Utils.writeFile(this, "history.txt", orderData.toString() + "\n");

        ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
/*
    store storeInfo as a parse object to parse database
 */
        orderObject.put("storeInfo", storeInfoSpinner.getSelectedItem());
        orderObject.put("menu", array);
        if (hasPhoto == true) {
            Uri uri = Utils.getPhotoUri();
            Log.d("SimpleUI", uri.toString());

            ParseFile parseFile = new ParseFile("photo.png", Utils.uriToBytes(this, uri));
            orderObject.put("photo", parseFile);
            hasPhoto = false;
        }
        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null){
                    Toast.makeText(MainActivity.this,
                            "[SaveCallback] OK", Toast.LENGTH_LONG).show();
                } else {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "[SaveCallback] Failed", Toast.LENGTH_LONG).show();
                }
                setHistory();
            }
        });
    } catch (JSONException e){
        e.printStackTrace();
    }

/*
 if hideCheckBox is checked, editText content will be hided
  */
        if(hideCheckBox.isChecked()){
            text = "********";
            editText.setText(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_take_photo){
            Toast.makeText(this, "taking photo", Toast.LENGTH_LONG).show();
            goToCamera();
       }
        return super.onOptionsItemSelected(item);
    }

/*
    demonstrates how to launch camera and capture an image
 */
    private void goToCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }


    /*
        demonstrates how to implement goToMenu method
     */
    public void goToMenu(View view){
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_MENU_ACTIVITY){
            if(resultCode == RESULT_OK){
                menuResult = data.getStringExtra("result");
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK){
                Uri uri = Utils.getPhotoUri();
                photoImageView.setImageURI(uri);
                hasPhoto = true;
            }
        }
    }
}
