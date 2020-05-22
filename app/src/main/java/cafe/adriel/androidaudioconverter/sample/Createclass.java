package cafe.adriel.androidaudioconverter.sample;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import static cafe.adriel.androidaudioconverter.sample.TimePickerFragment.settime_hour;
import static cafe.adriel.androidaudioconverter.sample.TimePickerFragment.settime_min;

public class Createclass extends AppCompatActivity {
    ArrayList<String> selection=new ArrayList<String>();
    TextView final_text;
    EditText classname;
    EditText professorname;
    EditText recordday;
    Button savebutton;
    Button deletebutton;

    private static final String TAG="Main";
    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_class);
        final_text=(TextView) findViewById(R.id.final_result);
        final_text.setEnabled(true);

        classname=findViewById(R.id.class_name);
        professorname=findViewById(R.id.professor_name);
        savebutton=findViewById(R.id.savebutton);
        deletebutton=findViewById(R.id.deletebutton);


        final AppDatabase db= Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"production")
                .allowMainThreadQueries()
                .build();


        savebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG,"classname: "+classname.getText().toString());
                Class class1 = new Class(classname.getText().toString(), professorname.getText().toString(), settime_hour+":"+settime_min, final_text.getText().toString());
                    db.classDao().insertAll(class1);

                startActivity(new Intent(Createclass.this,class_database.class));
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db.classDao().deleteAll();
                startActivity(new Intent(Createclass.this,class_database.class));
            }
        });
    }
    public void showAlarmDialog(View view){
        TimePickerFragment timePickerFragment=new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(),"time");
    }

    public void selectItem(View view){
        boolean checked=((CheckBox)view).isChecked();
        String setday="please checkbox";

        switch(view.getId()){
            case R.id.SUN_check:
                if(checked) {
                    selection.add("Sun");
                    setday="Sun";
                }
                else
                    selection.remove("Sun");
                break;

            case R.id.MON_check:
                if(checked) {
                    selection.add("Mon");
                    setday="Mon";
                }
                else
                    selection.remove("Mon");
                break;

            case R.id.TUE_check:
                if(checked) {
                    selection.add("Tue");
                    setday="Tue";
                }
                else
                    selection.remove("Tue");
                break;

            case R.id.WED_check:
                if(checked) {
                    selection.add("Wed");
                    setday="Wed";
                }
                else
                    selection.remove("Wed");
                break;

            case R.id.THU_check:
                if(checked) {
                    selection.add("Thu");
                    setday="Thu";
                }
                else
                    selection.remove("Thu");
                break;

            case R.id.FRI_check:
                if(checked){
                    selection.add("Fri");
                    setday="Fri";

                    final_text.setEnabled(true);
                     Log.d("요일선택","Fri");}
                else
                    selection.remove("Fri");
                break;

            case R.id.SAT_check:
                if(checked) {
                    selection.add("Sat");
                    setday="Sat";
                }
                else
                    selection.remove("Sat");
                break;

        }
        final_text.setText(setday);

        Log.d("요일선택",final_text.getText().toString());
        //Log.d("2222",recordday.getText().toString());

    }

}
