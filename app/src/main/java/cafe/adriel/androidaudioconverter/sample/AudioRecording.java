package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class handles the recording of sound in the format wave. The class uses the android
 * AudioRecord class. The functions for this class are:
 * - Start recording an audio file
 * - Stop the recording
 * - Save the recording as a wave file with a costume name
 * - Discard the current recording
 */

public class AudioRecording {

    //--------------------Variables----------------------------------------
    private AudioRecord audioRecorder = null;
    private File audioFile = null;
    private Thread recordingThread = null;
    private boolean currentlyRecording = false;

    private final int BUFFERELEMENTS = 4096;

    //--------------------Static Variables----------------------------------------
    public static final int SAMPLERATE = 44100;

    //--------------------Variables declaration end--------------------


    //Sets up file and parameters for the recording
    public AudioRecording() {

        File directory = new File(FileHandler.PATH);
        //If directory don't exist create it
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        audioFile = new File(directory, "temp.pcm");

        audioRecorder = new AudioRecord.Builder().setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLERATE).setChannelMask(AudioFormat.CHANNEL_IN_MONO).build())
                .setBufferSizeInBytes(BUFFERELEMENTS).build();


    }//end AudioRecording

    //Start recording
    public void start() {
        audioRecorder.startRecording();
        currentlyRecording = true;

        //New thread for recording to not overload the graphical
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioFile();
            }
        });

        recordingThread.start();


    }//end start

    //Stop and save temp file
    public void stop() {

        if (audioRecorder != null) {
            currentlyRecording = false;
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            recordingThread = null;
        }
    }//end stop

    private void writeAudioFile() {

        short bufferData[] = new short[BUFFERELEMENTS];

        FileOutputStream fileOutputStream = null;


        try {
            fileOutputStream = new FileOutputStream(audioFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Goes on until currentlyRecording is turned off
        while (currentlyRecording) {
            //Reads microphone to buffer
            audioRecorder.read(bufferData, 0, BUFFERELEMENTS, AudioRecord.READ_BLOCKING);

            //converts shorts to byte
            byte writeData[] = shortToByte(bufferData);

            //writes bytes to temp file
            try {
                fileOutputStream.write(writeData, 0, BUFFERELEMENTS * 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end writeAudioFile

    //Converts short data to byte
    private byte[] shortToByte(short[] shortData) {
        int shortArrayLength = shortData.length;
        byte[] byteData = new byte[shortArrayLength * 2];
        for (int i = 0; i < shortArrayLength; i++) {
            byteData[i * 2] = (byte) (shortData[i] & 0x00FF);
            byteData[(i * 2) + 1] = (byte) (shortData[i] >> 8);
            shortData[i] = 0;
        }
        return byteData;

    }//end shortToByte

    //Saves to Wave and remove temp pcm
    public void saveToWave(String name) {
        File newName = new File(FileHandler.PATH, name + FileHandler.ENDING);
        try {
            new Encoding().rawToWave(audioFile, newName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discard();
    }//end saveToWave

    //Delete the current file
    public void discard() {
        audioFile.delete();
    }//end discard

}


