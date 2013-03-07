package com.everendingloop.other.matschema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class LunchWidgetProvider extends AppWidgetProvider {

    private String TODAY_PREFS = "today";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	boolean today = toggleToday(context);

	Calendar c = Calendar.getInstance();
	if (today == false)
	    c.add(Calendar.DATE, 1);

	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);

	int[] allWidgetIds = getAllWidgetIds(context, appWidgetManager);
	for (int widgetId : allWidgetIds) {
	    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lunch_widget);

	    try {
		if (dayOfWeek == 1 || dayOfWeek == 7) {
		    // sunday or saturday
		    remoteViews.setTextViewText(R.id.widget_food, context.getText(R.string.no_lunch));
		} else {
		    JSONObject entireLunchList = getJsonObjectFromRes(context, R.raw.lunches);
		    JSONObject thisWeek = entireLunchList.getJSONObject(getWeekCode(weekOfYear));

		    String todaysLunch = (String) thisWeek.get("" + dayOfWeek);
		    remoteViews.setTextViewText(R.id.widget_food, todaysLunch);
		}
	    } catch (JSONException e) {
		e.printStackTrace();
	    } finally {
		String dateString = (today == true) ? "Idag " : "Imorgon ";
		dateString += getReadableDayOfWeek(dayOfWeek);
		remoteViews.setTextViewText(R.id.widget_date, dateString);
	    }

	    registerOnClickListener(context, appWidgetIds, remoteViews);

	    appWidgetManager.updateAppWidget(widgetId, remoteViews);
	}
    }

    private boolean toggleToday(Context context) {
	SharedPreferences settings = context.getSharedPreferences("prefs", 0);
	boolean today = settings.getBoolean(TODAY_PREFS, false);
	today = today == true ? false : true;
	settings.edit().putBoolean(TODAY_PREFS, today).commit();
	return today;
    }

    private String getReadableDayOfWeek(int dayOfWeek) {
	switch (dayOfWeek) {
	case 1:
	    return "söndag";
	case 2:
	    return "måndag";
	case 3:
	    return "tisdag";
	case 4:
	    return "onsdag";
	case 5:
	    return "torsdag";
	case 6:
	    return "fredag";
	case 7:
	    return "lördag";
	default:
	    return "Frabjous day";
	}
    }

    private String getWeekCode(int w) {
	switch (w) {
	case 1:
	    return "a";
	case 2:
	    return "b";
	case 3:
	    return "c";
	case 4:
	    return "a";
	case 5:
	    return "b";
	case 6:
	    return "c";
	case 7:
	    return "d";
	case 8:
	    return "a";
	case 9:
	    return "b";
	case 10:
	    return "c";
	case 11:
	    return "d";
	case 12:
	    return "a";
	case 13:
	    return "b";
	case 14:
	    return "c";
	case 15:
	    return "d";
	case 16:
	    return "a";
	case 17:
	    return "b";
	case 18:
	    return "c";
	case 19:
	    return "d";
	case 20:
	    return "a";
	case 21:
	    return "b";
	case 22:
	    return "c";
	case 23:
	    return "d";
	case 24:
	    return "a";
	case 25:
	    return "b";
	case 26:
	    return "c";
	case 27:
	    return "d";
	case 28:
	    return "a";
	case 29:
	    return "b";
	case 30:
	    return "c";
	case 31:
	    return "d";
	case 32:
	    return "a";
	case 33:
	    return "b";
	case 34:
	    return "c";
	case 35:
	    return "d";
	case 36:
	    return "a";
	case 37:
	    return "b";
	case 38:
	    return "c";
	case 39:
	    return "d";
	case 40:
	    return "a";
	case 41:
	    return "b";
	case 42:
	    return "c";
	case 43:
	    return "d";
	case 44:
	    return "a";
	case 45:
	    return "b";
	case 46:
	    return "c";
	case 47:
	    return "d";
	case 48:
	    return "a";
	case 49:
	    return "b";
	case 50:
	    return "c";
	case 51:
	    return "d";
	case 52:
	    return "a";
	default:
	    return "Invalid week number";
	}
    }

    private int[] getAllWidgetIds(Context context, AppWidgetManager appWidgetManager) {
	ComponentName thisWidget = new ComponentName(context, LunchWidgetProvider.class);
	return appWidgetManager.getAppWidgetIds(thisWidget);
    }

    private void registerOnClickListener(Context context, int[] appWidgetIds, RemoteViews remoteViews) {
	Intent intent = new Intent(context, LunchWidgetProvider.class);

	intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
		PendingIntent.FLAG_UPDATE_CURRENT);
	remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
    }

    public JSONObject getJsonObjectFromRes(Context context, int resourceId) {
	InputStream is = context.getResources().openRawResource(resourceId);
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	String readLine = null, jsonString = "";

	try {
	    while ((readLine = br.readLine()) != null) {
		jsonString += readLine;
	    }
	    is.close();
	    br.close();

	    return new JSONObject(jsonString);
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return new JSONObject();
    }

}
