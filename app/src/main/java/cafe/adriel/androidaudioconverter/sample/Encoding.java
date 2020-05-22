package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class provides methods to convert and handle a wave audio file as a short array and float
 * array in order to manipulated. The class provides the following functionalities:
 * - Get a short array from an wave file
 * - Get a float array from a short array
 * - Get a short array from a float array
 * - Get a pcm file from a short array
 * - Convert a pcm file to a wave file
 * - Discarding a current pcm file
 */

public class Encoding {

    //--------------------Variables----------------------------------------
    private File tempWave = null;

    //--------------------Variables declaration end--------------------

    //Converts a wave file to a short array
    public short[] audioToArray(File WAV_FILE) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(WAV_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Reads the header so that it will not be included in the precessed file
        byte[] temo = new byte[44];
        in.read(temo, 0, temo.length);

        int read;
        byte[] buff = new byte[1024];

        //Reads 1024 bytes each read
        while ((read = in.read(buff, 0, 1024)) > 0) {
            out.write(buff, 0, read);
        }
        out.flush();

        byte[] audioBytes = out.toByteArray();//Stores the byte data to an array
        short[] audioShort = new short[audioBytes.length / 2]; //Defines a short array to half the size of the byte array
        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioShort); //Saves the bytes as shorts with little endian

        return audioShort;

    }//End audioToArray

    //Converts audio shorts to floats that can be managed by a filter
    //Floats between -1 and 1
    public float[] shortsToFloats(short[] shorts) {
        float[] floats = new float[shorts.length];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = ((float) shorts[i]) / (float) 32768;
            if (floats[i] > 1) {
                floats[i] = 1;
            } else if (floats[i] < -1) {
                floats[i] = -1;
            }
        }

        return floats;
    } //End shortsToFloats

    //Converts a floats back to shorts to be saved
    public short[] floatsToShorts(float[] floats) {
        short[] shorts = new short[floats.length];

        for (int i = 0; i < shorts.length; i++) {
            floats[i] *= 32768;
            if (floats[i] > 32767) {
                floats[i] = 32767;
            } else if (floats[i] < -32768) {
                floats[i] = -32768;
            }

            shorts[i] = (short) floats[i];
        }
        return shorts;
    }//End floatsToShorts


    //Converts a short array back to a pcm file
    public void shortToFile(short writeData[]) {
        String filePath = FileHandler.PATH + "temp.pcm";
        FileOutputStream fileOutputStream = null;
        DataOutputStream dataOutputStream = null;

        //Sets the correct endianness to the array
        for (int i = 0; i < writeData.length; i++) {
            writeData[i] = Short.reverseBytes(writeData[i]);
        }


        //Defines the file streams
        try {
            fileOutputStream = new FileOutputStream(filePath);
            dataOutputStream = new DataOutputStream(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Writes the shorts to the file
        try {
            for (short s : writeData) {
                dataOutputStream.writeShort(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dataOutputStream.flush();
            dataOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File temp = new File(filePath); //Creates a file object of the temp pcm file just created
        tempWave = new File(FileHandler.PATH, "temp" + FileHandler.ENDING); // Sets up a temp wave file
        try {
            rawToWave(temp, tempWave);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Delete the temp pcm file
        temp.delete();
    }// End shortToFile

    //Converts pcm file to wave
    public void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));

            // Sets up the WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, AudioRecording.SAMPLERATE); // sample rate
            writeInt(output, AudioRecording.SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size

            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            //Writes the pcm to the wave
            output.write(readPCMFile(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }//End rawToWave

    //Reads the pcm file and creates a byte array of the file
    private byte[] readPCMFile(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }//End readPCMFile

    //Writes short data to a file as bytes
    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }//End writeInt

    //Writes short data to a file as bytes
    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }//End writeShort

    //Writes String data to a file as bytes
    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }//End writeString

    //Rename the temp wave file to store it
    public void saveToWave(String name) {
        File newName = new File(FileHandler.PATH, name + FileHandler.ENDING);
        try {
            rawToWave(tempWave, newName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discard();
    }// End saveToWave

    //Delete the current file
    public void discard() {
        tempWave.delete();
    }//End discard
}
