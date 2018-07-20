package com.example.kwolf.weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database db = new database(MainActivity.this);

        final Button b = (Button) findViewById(R.id.add);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main3Activity.class);
                startActivity(intent);
            }
        });

        final List<Map<String,String>> data = new ArrayList<>();
        final Cursor cursor = db.query();
        if (cursor != null && cursor.getCount() >= 0) {
            while (cursor.moveToNext()) {
                Map<String,String> temp = new LinkedHashMap<>();
                int col1 = cursor.getColumnIndex("name");
                String name = cursor.getString(col1);
                temp.put("name",name);
                data.add(temp);
            }
            ListView ls = (ListView) findViewById(R.id.list);
            final SimpleAdapter sa = new SimpleAdapter(this,data,R.layout.item,new String[]{"name"},new int[]{R.id.city});
            ls.setAdapter(sa);
        }

        ListView ls = (ListView) findViewById(R.id.list);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                String name = map.get("name");
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("city",name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                HashMap<String,String> map = (HashMap<String,String>) parent.getItemAtPosition(position);
                final String name = map.get("name");
                AlertDialog.Builder mybuilder = new AlertDialog.Builder(MainActivity.this);
                mybuilder.setTitle("删除城市");
                mybuilder.setMessage("确定删除"+name);
                mybuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database db = new database(MainActivity.this);
                        db.delete1(name);
                        onStart();
                    }
                });
                mybuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                Dialog a = mybuilder.create();
                a.show();
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        database db = new database(MainActivity.this);
        final List<Map<String,String>> data = new ArrayList<>();
        final Cursor cursor = db.query();
        if (cursor != null && cursor.getCount() >= 0) {
            while (cursor.moveToNext()) {
                Map<String,String> temp = new LinkedHashMap<>();
                int col1 = cursor.getColumnIndex("name");
                String name = cursor.getString(col1);
                temp.put("name",name);
                data.add(temp);
            }
            ListView ls = (ListView) findViewById(R.id.list);
            final SimpleAdapter sa = new SimpleAdapter(this,data,R.layout.item,new String[]{"name"},new int[]{R.id.city});
            ls.setAdapter(sa);
        }
    }
}
