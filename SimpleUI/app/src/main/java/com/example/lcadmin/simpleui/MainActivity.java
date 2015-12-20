package com.example.lcadmin.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

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

/*
    Use SharedPreferences to store default values of inputText and checkBox checked status
 */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String menuResult;

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
    demonstrates how to include class Parse to store data to 'Parse' database
 */
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        storeInfoSpinner = (Spinner) findViewById(R.id.storeInfoSpinner);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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
        setHistory();
        setStoreInfo();
    }

    private void setStoreInfo() {
        String[] stores = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stores);
        storeInfoSpinner.setAdapter(storeAdapter);
    }

    /*
        demonstrates how to list string array to a ListView
     */
    private void setHistory() {
        String[] rawData = Utils.readFile(this, "history.txt").split("\n");
        List<Map<String, String>> data = new ArrayList<>();

        for (int i = 0; i < rawData.length; i++){
            try {
                JSONObject object = new JSONObject(rawData[i]);
                String note = object.getString("note");
                JSONArray array = object.getJSONArray("menu");

                Map<String, String> item = new HashMap<>();
                item.put("note", note);
                item.put("drinkNum", "15");
                item.put("storeInfo", "NTU Store");

                data.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String[] from = {"note", "drinkNum", "storeInfo"};
        int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item,
                from, to);

        historyListView.setAdapter(adapter);
    }

/*
    "note" : this is a note
    "menu" : [....]
*/

    public void submit(View view){
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
        orderObject.put("menu", array);
        orderObject.saveInBackground();
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

        setHistory();
        editText.setText("");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }
    }
}
