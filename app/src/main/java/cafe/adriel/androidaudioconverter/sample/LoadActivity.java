package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;

import static cafe.adriel.androidaudioconverter.sample.ClassAdapter.match_name;

/**
 * This class provides the graphical user interface for the load activity which is the second
 * activity of the application. This activity is used to access the playback functionality and the
 * filtering. It do also provided a popup window which gives the users an opportunity to save the
 * filtered files to a name of their choice.
 */

public class LoadActivity extends AppCompatActivity {





    //--------------------GUI related variables--------------------
    private ListView listView = null;
    private Button windButton = null;
    private Button carButton = null;
    private Button cafeButton = null;
    private LayoutInflater layoutInflater = null;
    private Button save = null;
    private Button discard = null;
    private EditText fileName = null;
    private PopupWindow popupWindow = null;
    private ConstraintLayout constraintLayout = null;
    private InputMethodManager inputMethodManager = null;
    private ViewGroup container = null;


    //--------------------Playback related variables--------------------
    private AudioPlaying audioPlayer = null;
    private int currentFile = -1;
    private Encoding encoding = null;

    //----------------------------Variables-----------------------------

    private boolean saved = false;
    private boolean popupActive = false;
    private String fileNaming = "";

    //--------------------Variables declaration end--------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //--------------------GUI--------------------
        constraintLayout = (ConstraintLayout) findViewById(R.id.load_activity);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        listView = (ListView) findViewById(R.id.fileList);
        windButton = (Button) findViewById(R.id.windFilter);
        carButton = (Button) findViewById(R.id.traffic);
        cafeButton = (Button) findViewById(R.id.cafeNoise);

        //Set up the list view
        updateList();

        //When item in the list is pressed
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //If another file than previusly is pressed
                if (currentFile != position) {
                    //Set to current file
                    currentFile = position;

                    //If the audio is playing stop the old audio
                    if (audioPlayer != null) {
                        audioPlayer.stop();
                    }

                    //Play the new file
                    try {
                        audioPlayer = new AudioPlaying(FileHandler.getFile(position));
                        audioPlayer.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //if it is the same file as before pressed
                } else {
                    //if it is playing pause it otherwise stop the old playing and play it again
                    if (audioPlayer.isPlaying()) {
                        audioPlayer.pause();
                    } else {
                        audioPlayer.stop();
                        try {
                            audioPlayer = new AudioPlaying(FileHandler.getFile(position));
                            audioPlayer.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        });

        //If filter button 1 is pressed send 1 to buttonClick
        windButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick(1);
            }
        });

        //If filter button 2 is pressed send 2 to buttonClick
        cafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick(2);
            }
        });

        //If filter button 3 is pressed send 3 to buttonClick
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick(3);
            }
        });
    }//end onCreate

    //Get the current files in directory and save to the list
    private void updateList() {
        //Set up an adapter which contains the different list Items
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, FileHandler.getList());

        //Apply adapter to list view
        listView.setAdapter(adapter);
    }//end updateList



    //When a button is pressed
    private void buttonClick(final int buttonId) {
        //If a file is not selected exit the current method
        if (currentFile < 0) {
            return;
        }

        encoding = new Encoding();

        //Get the name of the selected file
        try {
            fileNaming = FileHandler.getFile(currentFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Set up a thread for filtering
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Filter the file
                    Filter.filterHandler(buttonId, new File(FileHandler.getFile(currentFile)), encoding);

                    //Once file is filtered stop the original and play the new file
                    audioPlayer.stop();
                    audioPlayer = new AudioPlaying(FileHandler.PATH + "temp" + FileHandler.ENDING);
                    audioPlayer.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        //Once the file is filtered show popup
        try {
            thread.join();
            getSaveOrDiscardPopUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }//end buttonClick

    //Show popup window
    private void getSaveOrDiscardPopUp() {
        popupActive = true;

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        container = (ViewGroup) layoutInflater.inflate(R.layout.save_or_discard, null);


        //Bind buttons to popup
        save = (Button) container.findViewById(R.id.save);
        discard = (Button) container.findViewById(R.id.discard);

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

        //Hide buttons and list
        listView.setVisibility(View.INVISIBLE);
        carButton.setVisibility(View.INVISIBLE);
        windButton.setVisibility(View.INVISIBLE);
        cafeButton.setVisibility(View.INVISIBLE);

        //Define the edit text filed
        fileName = (EditText) container.findViewById(R.id.fileName);

        //Get the name of the filtered file
        String fileTitle = fileNaming;

        //Add filter to the end
        fileTitle = fileTitle.substring(0, fileTitle.length() - 4) + "-filter";
        fileTitle = fileTitle.substring(fileTitle.lastIndexOf("/") + 1, fileTitle.length());

        //Set edit text to the new name and preselect it
        fileName.setText(fileTitle);
        fileName.selectAll();
        fileName.requestFocus();
        //Show keyboard
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //If save button pressed
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from edit field
                String newName = " ";
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
                encoding.saveToWave(newName);

                //Hide keyboard
                inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);

                saved = true;

                //Get an updated file list
                updateList();
                currentFile = -1;
                // Remove popup
                popupWindow.dismiss();
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Hide keyboard
                inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
                // Remove popup
                popupWindow.dismiss();
            }
        });

        //When popup is removed
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //if the file was not saved delete temporary file
                if (!saved) {
                    encoding.discard();
                }
                encoding = null; //Reset audio recorder
                inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);

                //Show buttons and list
                carButton.setVisibility(View.VISIBLE);
                windButton.setVisibility(View.VISIBLE);
                cafeButton.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);

                saved = false;
                popupActive = false;
            }
        });
    }//end getSaveOrDiscardPopUp



    @Override
    protected void onPause() {
        super.onPause();
        //Stop player if playing
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            audioPlayer = null;
            currentFile = -1;
        }
    }//end onPause

    @Override
    protected void onStop() {
        super.onStop();
        //Stop player if playing
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            audioPlayer = null;
            currentFile = -1;
        }

        //If popup window is active hide the keyboard
        if (popupActive) {
            inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
        }
    }//end onStop

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            audioPlayer = null;
            currentFile = -1;
        }

        //If popup window is active hide the keyboard and throw away the temp files
        if (popupActive) {
            inputMethodManager.hideSoftInputFromWindow(container.getWindowToken(), 0);
            try {
                encoding.discard();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }//end onDestroy

    @Override
    protected void onResume() {
        super.onResume();

    }//end onResume


}
