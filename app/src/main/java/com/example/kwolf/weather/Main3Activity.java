package com.example.kwolf.weather;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Button b = (Button) findViewById(R.id.add1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText e1 = (EditText) findViewById(R.id.name2);
                database db = new database(Main3Activity.this);
                if (TextUtils.isEmpty(e1.getText().toString())) {
                    Toast.makeText(Main3Activity.this, "城市名为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor cursor = db.query1(e1.getText().toString());
                    if (cursor.getCount() > 0) {
                        Toast.makeText(Main3Activity.this,"已存在该城市",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String name = e1.getText().toString();
                        db.insert2DB(name);
                        finish();
                    }
                }
            }
        });
    }
}
