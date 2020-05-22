package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class handles the playing of sound using the android class AudioTrack.
 * The functions for this class are:
 * - Start playing a audio file
 * - Stop the audio file
 * - Pause the audio file
 * - Find out if the file is currently playing
 */

public class AudioPlaying {

    //--------------------Playback related variables--------------------
    private File file;
    private AudioTrack audioTrack = null;
    Thread thread = null;

    //----------------------------Variables-----------------------------
    private boolean active = true, paused = false;

    //--------------------Variables declaration end--------------------

    public AudioPlaying(String fileName) {
        //Makes sure that the file to play exists
        if (fileName == null) {
            return;
        }
        file = new File(fileName);

        if (!file.isFile()) {
            return;
        }


        //Set up parameters for the audio playing
        int intSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack.Builder().setAudioAttributes(
                new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(AudioRecording.SAMPLERATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(intSize).build();
    }//end AudioPlaying

    //Sets up a new thread for playing the audio
    public void play() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    audioPlayer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }//end play

    //Reads the file and writes it to the audio out
    private void audioPlayer() throws IOException {

        if (audioTrack == null) {
            return;
        }

        int count = 1024; // 1 kb

        byte[] byteData;

        byteData = new byte[(int) count];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int bytesread = 0, ret = 0;
        int size = (int) file.length() - 44;

        //Discards the information in the beginning of the file
        byte tempBuff[] = new byte[44];
        in.read(tempBuff, 0, tempBuff.length);

        //Starts playing the audio file
        audioTrack.play();

        //Reads the information from the file and outputs it to the audio writer until the file ending or it active is false
        while (bytesread < size && active) {
            ret = in.read(byteData, 0, count);
            if (ret != -1) {
                audioTrack.write(byteData, 0, ret);
                bytesread += ret;
            } else {
                break;
            }

            //if paused it will be set in a loop until the loop is deactivated and it can continue playing
            while (paused) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        in.close();
        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;
        active = false;
    }//end audioPlayer

    //Set parameters to false to make the file stop
    public void stop() {
        paused = false;
        active = false;
    }//end stop

    //Toggles the pause function
    public void pause() {
        paused = !paused;

    }//end pause

    //Returns information regarding if it is playing or not
    public boolean isPlaying() {
        return active;
    }//end isPlaying


}

