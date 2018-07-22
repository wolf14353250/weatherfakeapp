package com.example.kwolf.weather;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private static final int UPDATE_CONTENT = 0;

    private ArrayList<String> parseXMLWithPull(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xml));
        ArrayList<String> list = new ArrayList<>();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("string".equals(parser.getName())) {
                        String str = parser.nextText();
                        list.add(str);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return list;
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key","Begin the connection");
                    connection = (HttpURLConnection) ((new URL(url.toString())).openConnection());
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                    EditText e1 = (EditText) findViewById(R.id.name2);
                    String request = e1.getText().toString();;
                    request = URLEncoder.encode(request,"utf-8");
                    out.writeBytes("theCityCode="+request+"&theUserID=");
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = parseXMLWithPull(response.toString());
                    handler.sendMessage(message);

                } catch (Exception e) {

                }
                finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    ArrayList<String> list = (ArrayList<String>)message.obj;
                    Iterator l1 = list.iterator();
                    if (l1.hasNext()) {
                        String s = l1.next().toString();
                        if (s.equals("发现错误：免费用户24小时内访问超过规定数量。http://www.webxml.com.cn/")) {
                            Toast.makeText(Main3Activity.this,"免费用户24小时内访问超过规定数量50次",Toast.LENGTH_SHORT).show();
                        }
                        else if (s.equals("查询结果为空。http://www.webxml.com.cn/")) {
                            Toast.makeText(Main3Activity.this,"当前城市不存在，请重新输入",Toast.LENGTH_SHORT).show();
                        }
                        else if (s.equals("发现错误：免费用户不能使用高速访问。http://www.webxml.com.cn/")) {
                            Toast.makeText(Main3Activity.this,"你的点击速度过快，二次查询间隔<600ms",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            EditText e1 = (EditText) findViewById(R.id.name2);
                            database db = new database(Main3Activity.this);
                            String name = e1.getText().toString();
                            db.insert2DB(name);
                            Log.i("Key","add "+name);
                            Intent intent1 = new Intent(Main3Activity.this,MainActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("city",name);
                            intent1.putExtras(bundle1);
                            startActivity(intent1);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

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

                        ////
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                            sendRequestWithHttpURLConnection();
                        }
                        else {
                            Toast.makeText(Main3Activity.this,"当前没有可用网络！",Toast.LENGTH_SHORT).show();
                        }

                        ////
                        /*
                        String name = e1.getText().toString();
                        db.insert2DB(name);
                        Log.i("Key","add "+name);
                        Intent intent1 = new Intent(Main3Activity.this,MainActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("city",name);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);*/
                    }
                }
            }
        });
    }
}
