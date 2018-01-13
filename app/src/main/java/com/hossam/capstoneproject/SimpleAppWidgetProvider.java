package com.hossam.capstoneproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.hossam.capstoneproject.activities.MainActivity;
import com.hossam.capstoneproject.models.SongModel;
import com.hossam.capstoneproject.utils.Utils;

/**
 * Created by hossam on 06/12/17.
 */

public class SimpleAppWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
//    public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";

    public SimpleAppWidgetProvider() {
        super();
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, SimpleAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {

            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, SimpleAppWidgetProvider.class));


        }


        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,

                         int[] appWidgetIds) {


        // update each of the app widgets with the remote adapter

        for (int i = 0; i < appWidgetIds.length; ++i) {


            // Set up the intent that starts the ListViewService, which will

            // provide the views for this collection.

//            Intent intent = new Intent(context, ListViewRemoveViewsService.class);

            // Add the app widget ID to the intent extras.

//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.simpe_app_widget_info);
            Intent mainIntent = new Intent(context, MainActivity.class).putExtra("fromWidget", "true");
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget_text_view, mainPendingIntent);

            Gson gson = new Gson();
            String json = Utils.getFromPreference(context, "SongModel");
            SongModel songModel = gson.fromJson(json, SongModel.class);
            if (songModel != null)
                rv.setTextViewText(R.id.widget_text_view, "Now Playing ... " + songModel.getSongName());
            // Set up the RemoteViews object to use a RemoteViews adapter.

            // This adapter connects

            // to a RemoteViewsService  through the specified intent.

            // This is how you populate the data.

//            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_listview, intent);

            // Trigger listview item click

//            Intent startActivityIntent = new Intent(context, MainActivity.class);
//
//            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            rv.setPendingIntentTemplate(R.id.widget_listview, startActivityPendingIntent);
//
//             The empty view is displayed when the collection has no items.

//             It should be in the same layout used to instantiate the RemoteViews  object above.
//
//            rv.setEmptyView(R.id.widget_listview, R.id.widget_listview);

            //

            // Do additional processing specific to this app widget...

            //


            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
}
