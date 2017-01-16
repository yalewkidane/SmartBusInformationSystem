package org.gs1.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class InputID extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_id);
    }

    public void onSearchClicked(View v) {

        EditText editText = (EditText) findViewById(R.id.object_id_get);
        String objectID = editText.getText().toString();

        Intent intent = new Intent(getApplicationContext(), SearchServices.class);
        intent.putExtra("objectID", objectID);
        startActivity(intent);
    }


}
