package cafe.adriel.androidaudioconverter.sample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class Notification_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent=new Intent(context,recordclass.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder buider=new NotificationCompat.Builder(context,"default");

        buider.setSmallIcon(R.mipmap.ic_launcher);
        buider.setContentIntent(pendingIntent);
        buider.setContentTitle("Autorecorder");
        buider.setContentText("수업녹음안내");
        buider.setAutoCancel(true);

        notificationManager.notify(1,buider.build());


        NotificationManager manager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        manager.notify(1,buider.build());

    }
}
