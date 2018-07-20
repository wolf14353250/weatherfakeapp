package com.example.kwolf.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.widget_location, "深圳");
        views.setTextViewText(R.id.widget_temp,"26℃/34℃");
        views.setImageViewResource(R.id.widget_image,R.drawable.default_hover);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        Intent i = new Intent(context,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);
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
    public void onReceive(Context context,Intent intent) {
        super.onReceive(context,intent);
        RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.app_widget);
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equals("STATICACTION")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            String name = bundle.get("name").toString();
            String temp = bundle.get("temp").toString();
            int imag = (int)bundle.get("imag");
            rv.setTextViewText(R.id.widget_location,name);
            rv.setTextViewText(R.id.widget_temp,temp);
            rv.setImageViewResource(R.id.widget_image,imag);
            ComponentName me = new ComponentName(context,AppWidget.class);
            appWidgetManager.updateAppWidget(me,rv);
        }
    }
}

