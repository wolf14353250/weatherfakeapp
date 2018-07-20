package com.example.kwolf.weather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Main2Activity extends AppCompatActivity {

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

                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    final String city = bundle.getString("city");
                    Log.i("key","111"+city);
                    String request = city;
                    request = URLEncoder.encode(request,"utf-8");
                    out.writeBytes("theCityCode="+request+"&theUserID=");
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("key","222");
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
                            Toast.makeText(Main2Activity.this,"免费用户24小时内访问超过规定数量50次",Toast.LENGTH_SHORT).show();
                        }
                        else if (s.equals("查询结果为空。http://www.webxml.com.cn/")) {
                            Toast.makeText(Main2Activity.this,"当前城市不存在，请重新输入",Toast.LENGTH_SHORT).show();
                        }
                        else if (s.equals("发现错误：免费用户不能使用高速访问。http://www.webxml.com.cn/")) {
                            Toast.makeText(Main2Activity.this,"你的点击速度过快，二次查询间隔<600ms",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.i("Key","111");
                            TextView t1 = (TextView) findViewById(R.id.city1);
                            t1.setText(l1.next().toString());
                            l1.next();
                            String time = l1.next().toString();
                            time = time.substring(time.indexOf(" "));
                            TextView t2 = (TextView) findViewById(R.id.time);
                            t2.setText(time);
                            String s1 = l1.next().toString();
                            int x1 = s1.indexOf("；");
                            TextView t3 = (TextView) findViewById(R.id.temp);
                            t3.setText(s1.substring(10,x1));
                            int x2 = s1.indexOf("；",x1+1);
                            TextView t4 = (TextView) findViewById(R.id.wind);
                            t4.setText(s1.substring(x1+7,x2));
                            TextView t5 = (TextView) findViewById(R.id.hum);
                            t5.setText(s1.substring(x2+4));
                            s1 = l1.next().toString();
                            x1 = s1.lastIndexOf("：");
                            x2 = s1.lastIndexOf("。");
                            TextView t6 = (TextView) findViewById(R.id.val);
                            t6.setText(s1.substring(x1+1,x2));
                            s1 = l1.next().toString();
                            List<Map<String,String>> data = new ArrayList<>();
                            x1 = s1.indexOf("：");
                            x2 = s1.indexOf("。");
                            Map<String,String> temp = new LinkedHashMap<>();
                            temp.put("title","紫外线指数");
                            temp.put("content",s1.substring(x1+1,x2));
                            data.add(temp);
                            x1 = s1.indexOf("：",x2+1);
                            x2 = s1.indexOf("。",x2+1);
                            Map<String,String> temp1 = new LinkedHashMap<>();
                            temp1.put("title","血糖指数");
                            temp1.put("content",s1.substring(x1+1,x2));
                            data.add(temp1);
                            x1 = s1.indexOf("：",x2+1);
                            x2 = s1.indexOf("。",x2+1);
                            Map<String,String> temp2 = new LinkedHashMap<>();
                            temp2.put("title","穿衣指数");
                            temp2.put("content",s1.substring(x1+1,x2));
                            data.add(temp2);
                            x1 = s1.indexOf("：",x2+1);
                            x2 = s1.indexOf("。",x2+1);
                            Map<String,String> temp3 = new LinkedHashMap<>();
                            temp3.put("title","洗车指数");
                            temp3.put("content",s1.substring(x1+1,x2));
                            data.add(temp3);
                            x1 = s1.indexOf("：",x2+1);
                            x2 = s1.indexOf("。",x2+1);
                            Map<String,String> temp4 = new LinkedHashMap<>();
                            temp4.put("title","空气污染指数");
                            temp4.put("content",s1.substring(x1+1,x2));
                            data.add(temp4);
                            ListView ls = (ListView) findViewById(R.id.list1);
                            SimpleAdapter simpleAdapter = new SimpleAdapter(Main2Activity.this,data,R.layout.item1,new String[]{"title","content"},new int[]{R.id.item1,R.id.item2});
                            ls.setAdapter(simpleAdapter);

                            l1.next();
                            s1 = l1.next().toString();

                            List<Map<String,String>> data1 = new ArrayList<>();
                            Map<String,String> temp5 = new LinkedHashMap<>();
                            s1 = l1.next().toString();
                            x1 = s1.indexOf(" ");
                            temp5.put("date",s1.substring(0,x1));
                            temp5.put("weather1",s1.substring(x1+1));
                            l1.next();
                            TextView t7 = (TextView) findViewById(R.id.temp2);
                            t7.setText(l1.toString());
                            temp.put("temper",l1.toString());
                            data1.add(temp5);

                            Map<String,String> temp6 = new LinkedHashMap<>();
                            s1 = l1.next().toString();
                            x1 = s1.indexOf(" ");
                            temp6.put("date",s1.substring(0,x1));
                            temp6.put("weather1",s1.substring(x1+1));
                            l1.next();
                            temp6.put("temper",l1.toString());
                            data1.add(temp6);

                            Map<String,String> temp7 = new LinkedHashMap<>();
                            s1 = l1.next().toString();
                            x1 = s1.indexOf(" ");
                            temp7.put("date",s1.substring(0,x1));
                            temp7.put("weather1",s1.substring(x1+1));
                            l1.next();
                            temp7.put("temper",l1.toString());
                            data1.add(temp7);

                            Map<String,String> temp8 = new LinkedHashMap<>();
                            s1 = l1.next().toString();
                            x1 = s1.indexOf(" ");
                            temp8.put("date",s1.substring(0,x1));
                            temp8.put("weather1",s1.substring(x1+1));
                            l1.next();
                            temp8.put("temper",l1.toString());
                            data1.add(temp8);

                            Map<String,String> temp9 = new LinkedHashMap<>();
                            s1 = l1.next().toString();
                            x1 = s1.indexOf(" ");
                            temp9.put("date",s1.substring(0,x1));
                            temp9.put("weather1",s1.substring(x1+1));
                            l1.next();
                            temp9.put("temper",l1.toString());
                            data1.add(temp9);

                            HorizontalListView ls1 = (HorizontalListView) findViewById(R.id.list2);
                            SimpleAdapter sa = new SimpleAdapter(Main2Activity.this,data1,R.layout.item2,new String[]{"date","weather1","temper"},new int[]{R.id.date,R.id.weather1,R.id.temper});
                            ls1.setAdapter(sa);

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
        setContentView(R.layout.activity_main2);


        final Button b = (Button)findViewById(R.id.selection);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            sendRequestWithHttpURLConnection();
        }
        else {
            Toast.makeText(Main2Activity.this,"当前没有可用网络！",Toast.LENGTH_SHORT).show();
        }
    }


}