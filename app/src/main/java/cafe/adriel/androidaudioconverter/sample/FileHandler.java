package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import android.os.Environment;

import java.io.File;

/**
 * This class provides the access to the file directory NoiseOff and its files.
 * The class provides the following operations:
 * - Getting the path to the directory
 * - Getting the standard file ending for the wave format
 * - Getting a list of all the current files residing in the NoiseOff directory
 * - Finding which file is where in the file list
 * - Check if a file exists in the NoiseOff directory
 */

public class FileHandler {

    //Gives the path to where files will be saved
    public final static String PATH = Environment.getExternalStorageDirectory().toString() + "/autorecorder/";

    //Sets standard file ending for the wave format
    public final static String ENDING = ".wav";

    //Gives a list of files in the directory
    public static String[] getList() {

        //Sets up empty list
        String[] nameList = {" "};

        //Checks if directory exists
        File dir = new File(PATH);
        if (dir.isDirectory()) {

            //Save files from directory to file array
            File[] files = new File(PATH).listFiles();

            nameList = new String[files.length];


            //Convert file array to Strings of there names
            for (int i = 0; i < files.length; i++) {
                nameList[i] = files[i].getName();
            }
        }

        return nameList;
    }//end getList

    //Gets the name of the file with the given position in the list
    public static String getFile(int postion) throws Exception {
        String[] tempArray = getList();
        if (tempArray[0].equalsIgnoreCase(" ")) {
            throw new Exception();
        }

        return PATH + "/" + tempArray[postion];
    }//end getFile

    //Checks if the given file name exists in the path
    public static boolean fileExists(String fileName) {
        File file = new File(PATH, fileName + ENDING);

        if (file.isFile()) {
            return true;
        }

        return false;
    }//end fileExists
}
