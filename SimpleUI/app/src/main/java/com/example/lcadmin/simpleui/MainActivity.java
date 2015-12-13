package com.example.lcadmin.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private CheckBox hideCheckBox;
/*
    Use SharedPreferences to store default values of inputText and checkBox checked status
 */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editText = (EditText)findViewById(R.id.inputText);
/*
    updates setText from constant string to something read from sharedPreferences
 */
//        editText.setText("1234");
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

        hideCheckBox = (CheckBox) findViewById(R.id.hideCheckBox);
        hideCheckBox.setChecked(true);
    }

    public void submit(View view){
        String text = editText.getText().toString();

/*
    demonstrates how to use SharedPreferences editor to store values
 */
        editor.putString("inputText", text);
        editor.commit();
/*
 if hideCheckBox is checked, editText content will be hided
  */
        if(hideCheckBox.isChecked()){
            text = "********";
            editText.setText(text);
        }

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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
}
