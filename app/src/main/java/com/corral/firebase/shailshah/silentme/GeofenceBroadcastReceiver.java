package com.corral.firebase.shailshah.silentme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by shailshah on 10/30/17.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError())
        {
            Log.e(TAG,String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }
        // Get the Transition type..
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        //Check which transition type has triggered this event

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
        }else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            setRingerMode(context,AudioManager.RINGER_MODE_NORMAL);
        }
        else
        {
            //Log Error
            Log.e(TAG, String.format("Unknoen transition : %d", geofenceTransition));
            //No need to do anything else
            return;
        }
        sentNotification(context,geofenceTransition);
    }

    private void sentNotification(Context context, int transitionType)
    {
        // Create explicit intent that start the main acticity..
        Intent notificationIntent = new Intent(context,MainActivity.class);

        //Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        //Add main activity to the task stack as the parent
        stackBuilder.addParentStack(MainActivity.class);

        //Push the content onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        //GEt a PendingIntent containing the entire Backstack.
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //Get Notification BuildeR
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Check the transition type to display the relevant icon image
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle("Silent Mode Activated");
        }
        else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            builder.setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_volume_up_white_24dp))
                    .setContentTitle("Back to Normal");
        }

        //Continut building the notification
        builder.setContentText("Touch to Launch the Application");
        builder.setContentIntent(notificationPendingIntent);

        //Dismiss Notification once user touches it..
        builder.setAutoCancel(true);

        //Get an instance of the notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        //Issue the notification
        mNotificationManager.notify(0,builder.build());

    }

    private void setRingerMode(Context context, int mode)
    {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Check for DND permission for API 24+
        if (Build.VERSION.SDK_INT < 24 || (Build.VERSION.SDK_INT >= 24 && ! nm.isNotificationPolicyAccessGranted()))
        {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

}
