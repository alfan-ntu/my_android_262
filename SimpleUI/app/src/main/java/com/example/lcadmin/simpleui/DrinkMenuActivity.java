package com.example.lcadmin.simpleui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lcadmin on 2015/12/16.
 * added a new activity class "DrinkMenuActivity"
 */
public class DrinkMenuActivity extends AppCompatActivity {

    private int count;
    private LinearLayout rootLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
    applies layout resource activity_drink_menu.xml
 */
        setContentView(R.layout.activity_drink_menu);

        rootLinearLayout = (LinearLayout) findViewById(R.id.root);
        count = rootLinearLayout.getChildCount();

    }

    public void add(View view){
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

/*
    implement done method to return menu data to MainActivity
  */
    public void done(View view){
        JSONArray jsonData = getData();
        Intent data = new Intent();
        data.putExtra("result", jsonData.toString());
        setResult(RESULT_OK, data);
        finish();
    }

/*
    implement cancel method to reset order data
*/
    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }


/*   applied JSON data object format
[
    {"name": "black tea", "l": 2, "m": 0, "s": 0},
    {"name": "milk tea", "l": 10, "m": 3, "s": 1},
    {"name": "green tea", "l": 5, "m": 3, "s": 2}
]
* */

    public JSONArray getData(){
        Log.d("SimpleUI child count", String.valueOf(count));

        JSONArray array = new JSONArray();

        for (int i=0 ; i < count - 1 ; i++ ){
            LinearLayout ll = (LinearLayout) rootLinearLayout.getChildAt(i);

            TextView drinkNameTextView = (TextView) ll.getChildAt(0);
            Button lButton = (Button) ll.getChildAt(1);
            Button mButton = (Button) ll.getChildAt(2);
            Button sButton = (Button) ll.getChildAt(3);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());
            int sNumber = Integer.parseInt(sButton.getText().toString());

            Log.d("SimpleUI-item_quantity", drinkName+" l "+String.valueOf(lNumber)
                    +" m "+String.valueOf(mNumber)+" s "+String.valueOf(sNumber));
/*
    compose JSONArray using data got from LinearArray
 */
            try {
                JSONObject object = new JSONObject();
                object.put("name", drinkName);
                object.put("l", lNumber);
                object.put("m", mNumber);
                object.put("s", sNumber);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }
}
