package cafe.adriel.androidaudioconverter.sample;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private AlarmManager mAlarmManager;
    SimpleDateFormat mdformat = new SimpleDateFormat("yyMMdd");
    SimpleDateFormat mdformat2 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat mdformat2_d = new SimpleDateFormat("EEE");
    SimpleDateFormat mdformat2_h = new SimpleDateFormat("HH");
    SimpleDateFormat mdformat2_m = new SimpleDateFormat("mm");
    static String settime_min;
    static String settime_hour;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAlarmManager=(AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);


        Calendar c= Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        Log.d("시간",String.valueOf(hour));
        Log.d("시간",String.valueOf(minute));

        return new TimePickerDialog(getContext(), this,hour,minute,
                DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar=Calendar.getInstance();
        String settime;
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        Log.d("시간2",String.valueOf(hourOfDay));
        Log.d("시간2",String.valueOf(minute));
        settime_hour=String.valueOf(hourOfDay);
        settime_min=String.valueOf(minute);



    }



}
