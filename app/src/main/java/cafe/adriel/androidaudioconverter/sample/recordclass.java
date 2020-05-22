package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

import static cafe.adriel.androidaudioconverter.sample.ClassAdapter.match_name;

/**
 * This class provides the graphical user interface for the main context which is thee first
 * context accessed when starting the application. This interface provides access to the audio
 * recording functions as well as a path to the load context. It do also provided a popup window
 * which gives the users an opportunity to save the recorded file to a name of their choosing.
 */


public class recordclass extends AppCompatActivity {
    static String newName;



    public String getExternalPath() {
        String sdPath="";
        String ext= Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
            Log.d("sdpath= ",sdPath);

        }
        else{
            sdPath=getFilesDir()+"";
            Toast.makeText(getApplicationContext(),sdPath,Toast.LENGTH_SHORT).show();
        }
        return sdPath;
    }

    public void onclasstimetableclicked(View v){
        Intent intent = new Intent(getApplicationContext(),class_database.class);
        startActivity(intent);
    }


    public void onrecordclicked(){
        Intent intent = new Intent(getApplicationContext(),recordclass.class);
        String path2=getExternalPath();
        File file=new File(path2+"autorecorder");
        file.mkdir();
        Log.d("main_matchname=",match_name);

    }

    public void ontextclicked(View v){
        Intent intent = new Intent(getApplicationContext(),tempconvert.class);
        startActivity(intent);


    }


    //--------------------GUI related variables--------------------

    private Button startRecord = null;
    private Button stopRecord = null;
    private Button loadMenu = null;
    private Button loadMenu2 = null;
    private Button save = null;
    private Button discard = null;
    private RelativeLayout saveOrDiscardWindow = null;
    private EditText fileName = null;
    private PopupWindow popupWindow = null;
    private LayoutInflater layoutInflater = null;
    private ConstraintLayout constraintLayout = null;
    private Chronometer mChronometer = null;
    private InputMethodManager inputMethodManager = null;
    private ViewGroup container = null;
    private Context context = null;
    private recordclass activity = null;


    //--------------------Recording related variables--------------------

    private AudioRecording audioRecorder;

    //--------------------Variables----------------------------------------

    private String timeDate = null;
    private boolean saved = false;
    private boolean popupActive = false;
    private final int MY_PERMISSIONS_REQUEST_READ = 1;


    SimpleDateFormat mdformat = new SimpleDateFormat("yyMMdd");
    SimpleDateFormat mdformat2 = new SimpleDateFormat("H:mm");
    SimpleDateFormat mdformat2_d = new SimpleDateFormat("EEE",new Locale("en","US"));
    SimpleDateFormat mdformat2_h = new SimpleDateFormat("H");
    SimpleDateFormat mdformat2_m = new SimpleDateFormat("mm");

    public void makematchname() {
        Log.d("11111", "11111");
        Calendar calendar = Calendar.getInstance();
        String temp = "";


        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();
        List<Class> classes = db.classDao().getAllClasses();
        Log.d("11111", calendar.toString());

        for (int i = 0; i < classes.size(); i++) {
            String str;
            str = classes.get(i).getStarttime();
            String data[] = str.split(":");

            Log.d("make22", data[0]);
            Log.d("make22", classes.get(i).getStarttime());
            Log.d("make22", mdformat2.format(calendar.getTime()));
            Log.d("make22", mdformat2_d.format(calendar.getTime()));
            Log.d("make22", classes.get(i).getRecordday());


            if (data[0].equals(mdformat2_h.format(calendar.getTime()))) {
                Log.d("222222", classes.get(i).getStarttime());
                if (classes.get(i).getRecordday().equals(mdformat2_d.format(calendar.getTime()))) {
                    Log.d("222221", classes.get(i).getStarttime());
                    match_name = mdformat.format(calendar.getTime()) + classes.get(i).getRecordday() + "_" + classes.get(i).getClassname() + "_" + classes.get(i).getProfessorname();
                    Toast.makeText(recordclass.this, "들어옴" + match_name, Toast.LENGTH_SHORT).show();
                    Log.d("222221", match_name);
                }


            } else {
                //Log.d("22222", classes.get(i).getStarttime());
                Log.d("시간이다르다`~~", classes.get(i).getStarttime());
                match_name = mdformat.format(calendar.getTime()) + "_" + mdformat2.format(calendar.getTime());

            }


        }
        if (match_name.equals("default")||classes.size()==0) {
            Log.d("33333", "기본값으로 설정되서 바꿈");
            Toast.makeText(recordclass.this, "기본값으로 설정 :" + match_name, Toast.LENGTH_SHORT).show();
            match_name = mdformat.format(calendar.getTime()) + "_" + mdformat2.format(calendar.getTime());
        }
    }
        //--------------------Variables declaration end--------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        onrecordclicked();

        context = this;
        activity = this;

        //--------------------GUI--------------------

        startRecord = (Button) findViewById(R.id.recordButton);
        stopRecord = (Button) findViewById(R.id.stopButton);
        loadMenu = (Button) findViewById(R.id.loadButton);
        loadMenu2 = (Button) findViewById(R.id.loadButton2);
        mChronometer = (Chronometer) findViewById(R.id.mChronometer);
        constraintLayout = (ConstraintLayout) findViewById(R.id.main_activity);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Hide chronometer
        mChronometer.setVisibility(View.INVISIBLE);


        //Checks permission and asks for it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_READ);
        }

        //If record button is pressed
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if permission not granted exit program
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    activity.finishAndRemoveTask();
                    return;
                }
                makematchname();
                startRecord.setVisibility(View.INVISIBLE);
                loadMenu.setVisibility(View.INVISIBLE);
                loadMenu2.setVisibility(View.INVISIBLE);
                stopRecord.setVisibility(View.VISIBLE);

                //Set up chronometer and start
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();

                //Start to record
                audioRecorder = new AudioRecording();
                audioRecorder.start();
            }
        });

        //When recording is stopped
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide stop button show record button
                stopRecord.setVisibility(View.INVISIBLE);
                startRecord.setVisibility(View.VISIBLE);
                //Stop chronometer
                mChronometer.stop();

                //Stop Recording
                audioRecorder.stop();

                //Show save/discard popup
                getSaveOrDiscardPopUp();


            }
        });

        //If load button is pressed
        loadMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if permission not granted exit program
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.finishAndRemoveTask();
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/wav");
                intent.putExtra("folderPath", Environment.getExternalStorageDirectory().getPath()+ "/autorecorder/");
                if(intent!=null){
                    Log.d("12223","123");
                    startActivity(intent);
                }

                //Load new context

            }
        });

    }//onCreate end

    //--------------------Methods--------------------

    private void getSaveOrDiscardPopUp() {
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        container = (ViewGroup) layoutInflater.inflate(R.layout.save_or_discard, null);

        saveOrDiscardWindow = (RelativeLayout) findViewById(R.id.popup_window);
        fileName = (EditText) container.findViewById(R.id.fileName);

        //Bind buttons to popup
        save = (Button) container.findViewById(R.id.save);
        discard = (Button) container.findViewById(R.id.discard);

        popupActive = true;

        //Get information about the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //Store information about screen
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //Define the popup window
        popupWindow = new PopupWindow(container, (int) (width * 1.0), (int) (height * 0.6), true);
        popupWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 0, 50);
        popupWindow.setOutsideTouchable(false);
        container.setFocusable(true);

        // Get current day and time
        timeDate = Calendar.getInstance().getTime().toString();
        timeDate = timeDate.substring(0, 19);
        timeDate.replaceAll("\\s", "");


        //Pre set the edit field to the date and time and preselect it
        fileName.setText(match_name);
        fileName.selectAll();
        fileName.requestFocus();
        //Show keyboard
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);



        //If save button pressed
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from edit field
                newName = fileName.getText().toString();

                //Check to make sure that another file is not over written
                if (FileHandler.fileExists(newName)) {

                    //If the file does already exist, it will be given a name with the lowest available number
                    for (int i = 2; i < Integer.MAX_VALUE; i++) {
                        if (FileHandler.fileExists(newName + "(" + i + ")")) {
                            continue;
                        } else {
                            newName += "(" + i + ")";
                            break;
                        }
                    }
                }

                //Save "permanently" to memory
                audioRecorder.saveToWave(newName);
                saved = true;
                convertAudio();



                //Hide keyboard
                inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);

                // Remove popup
                popupWindow.dismiss();
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide keyboard
                inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
                //Remove popup
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupActive = false;
                discardPopUp();
            }
        });
    }//End getSaveOrDiscardPopUp

    private void discardPopUp() {

        //If the file was not saved discard the temp file
        if (!saved) {
            //Delete temporary file
            audioRecorder.discard();
        }
        audioRecorder = null; //Reset audio recorder

        //Turn of chronometer
        mChronometer.setVisibility(View.INVISIBLE);
        loadMenu.setVisibility(View.VISIBLE);
        loadMenu2.setVisibility(View.VISIBLE);

        saved = false;

    }//End discardPopUp

    @Override
    protected void onStop() {
        super.onStop();
        //Stop player if playing
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder.discard();

            mChronometer.setVisibility(View.INVISIBLE);
            startRecord.setVisibility(View.VISIBLE);
            stopRecord.setVisibility(View.INVISIBLE);
            loadMenu.setVisibility(View.VISIBLE);
            loadMenu2.setVisibility(View.VISIBLE);

        }
        //If popup window is active hide the keyboard
        if (popupActive) {
            inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
        }
    }//end onStop

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (audioRecorder != null) {
            audioRecorder.stop();
        }

        //If popup window is active hide the keyboard and throw away the temp files
        if (popupActive) {
            inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
            try {
                audioRecorder.discard();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }//end onDestroy

    // Wav->Flac
    public void convertAudio(){
        Log.d("ddd3","ddd");

        Log.d("ddd3",newName);
        /**
         *  Update with a valid audio file!
         *  Supported formats: {@link AndroidAudioConverter.AudioFormat}
         */
        File wavFile = new File(Environment.getExternalStorageDirectory(), "/autorecorder/"+newName+".wav");
        Log.d("ddd3",Environment.getExternalStorageDirectory().toString()+"/autorecorder/"+newName+".wav");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Toast.makeText(recordclass.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Exception error) {
                Toast.makeText(recordclass.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        Toast.makeText(recordclass.this, "Converting audio file...", Toast.LENGTH_SHORT).show();
        AndroidAudioConverter.with(this)
                .setFile(wavFile)
                .setFormat(AudioFormat.FLAC)
                .setCallback(callback)
                .convert();
    }

}
