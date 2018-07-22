package com.example.kwolf.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    static String s = "null";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setImageViewResource(R.id.widget_image,R.drawable.sun1);
        views.setTextViewText(R.id.appwidget_text,"广州");
        views.setTextViewText(R.id.appwidget_temper,"26℃");
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);


        Intent intent = new Intent(context,MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("city",s);
        intent.putExtras(bundle);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.app_widget);
        rv.setOnClickPendingIntent(R.id.widget_image,pi);
        ComponentName me = new ComponentName(context,AppWidget.class);
        appWidgetManager.updateAppWidget(appWidgetIds,rv);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public  void onReceive(Context context,Intent intent) {
        super.onReceive(context,intent);
        RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.app_widget);
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equals("STATICACTION")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            int imag = (int)bundle.get("imag");
            String name = bundle.get("city").toString();
            String weather2 = bundle.get("weather2").toString();
            String temper = bundle.get("temper").toString();
            String temper1 = bundle.get("temper1").toString();

            rv.setImageViewResource(R.id.widget_image,imag);
            rv.setTextViewText(R.id.appwidget_text,name);
            rv.setTextViewText(R.id.weather2,weather2);
            rv.setTextViewText(R.id.appwidget_temper,temper);
            rv.setTextViewText(R.id.temper1,temper1);

            ComponentName me = new ComponentName(context,AppWidget.class);
            appWidgetManager.updateAppWidget(me,rv);
        }
    }

}

