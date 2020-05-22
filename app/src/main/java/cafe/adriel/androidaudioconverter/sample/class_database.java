package cafe.adriel.androidaudioconverter.sample;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.api.client.util.NullValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static cafe.adriel.androidaudioconverter.sample.ClassAdapter.match_name;

public class class_database extends AppCompatActivity {

    private static final String TAG="Main";

    private RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FloatingActionButton fab;

    //ArrayList<Class> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_database);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.recycler_view);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat mdformat = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat mdformat2 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat mdformat3 = new SimpleDateFormat("EEE");



        AppDatabase db= Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"production")
                .allowMainThreadQueries()
                .build();

        final List<Class> classes= db.classDao().getAllClasses();

        adapter=new ClassAdapter(classes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        /*시간 비교 -> 파일명 생성->알람*/
        findViewById(R.id.notibutton).setOnClickListener(new View.OnClickListener(){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat mdformat2 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat mdformat3 = new SimpleDateFormat("EEE");
            SimpleDateFormat mdformat2_h = new SimpleDateFormat("HH");
            SimpleDateFormat mdformat2_m = new SimpleDateFormat("mm");
            @Override
            public void onClick(View v) {
                Calendar calendar2=Calendar.getInstance();
                String str;
                Log.d("현재녹음시간",mdformat2.format(calendar.getTime()));

                /*그냥 녹음 알림만 하자... */
                for (int i = 0; i < classes.size(); i++) {
                    str = classes.get(i).getStarttime();
                    String data[] = str.split(":");
                    Log.d("녹음시작hour", data[0]);
                    Log.d("녹음시작min", data[1]);
                    if(data[0].length()<=0 || data[1].length()<=0){
                        calendar2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(data[0]));
                        calendar2.set(Calendar.MINUTE, Integer.parseInt(data[1]));
                        calendar2.set(Calendar.SECOND, 10);
                    }

                }





                /*타이머식 노티*/
                Log.d("ㅇㅇ",String.valueOf(calendar2.getTime()));
                Intent intent=new Intent(getApplicationContext(),Notification_receiver.class);

                PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

            }
        });




        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onclick()");
                startActivity(new Intent(class_database.this,Createclass.class));
            }
        });

    }

    public void createNotification(View view){
        show();
    }
        /*버튼식 노티*/
    private void show(){
        NotificationCompat.Builder buider=new NotificationCompat.Builder(this,"default");

        buider.setSmallIcon(R.mipmap.ic_launcher);
        buider.setContentTitle("Autorecorder");
        buider.setContentText("수업녹음안내1"+match_name);

        Intent intent=new Intent(this,recordclass.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        buider.setContentIntent(pendingIntent);

        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        buider.setLargeIcon(largeIcon);

        Uri ringtonUri= RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_NOTIFICATION);

        buider.setSound(ringtonUri);

        long[] vibrate={0,100,200,300};
        buider.setVibrate(vibrate);
        /*노티선택시 자동 삭제*/
        buider.setAutoCancel(true);

        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        manager.notify(1,buider.build());
    }
    public void removeNotification(View view){
        hide();
    }
    private void hide(){
        NotificationManagerCompat.from(this).cancel(1);
    }
}

