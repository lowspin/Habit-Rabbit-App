package com.teachableapps.capstoneproject.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.teachableapps.capstoneproject.R;

/**
 * Implementation of App Widget functionality.
 */
public class HabitWidget extends AppWidgetProvider {

    private int mNumProjects=-1;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int numP = sharedPreferences.getInt(context.getString(R.string.KEY_NUM_PROJECTS),0);
        CharSequence widgetText = String.valueOf(numP);
        float avg_completion = sharedPreferences.getFloat(context.getString(R.string.KEY_AVG_COMPLETION),100);
        int progress = (int) (avg_completion*100);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.habit_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setProgressBar(R.id.widget_proj_progress_ring, 100, progress,false);

        // Create Intent to launch Main Activity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,0);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_proj_progress_ring, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateHabitWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

