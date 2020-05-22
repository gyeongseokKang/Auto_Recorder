package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Eulias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 *  * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 *  * You are free to share, copy, distribute, transmit the work, to remix and adapt the work bt
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import java.io.File;
import java.io.IOException;

/**
 * This class provides a filter which manipulates a a signal of floats. The filter class  provides
 * a low pass and a high pass filter in the second order.
 */

public class Filter {

    //--------------------Variables----------------------------------------

    private float c, a1, a2, a3, b1, b2;

    // Array of input values, latest are in front
    private float[] inputHistory = new float[2];

    // Array of output values, latest are in front
    private float[] outputHistory = new float[3];

    //--------------------Variables declaration end--------------------

    //Sets up parameters for the filter
    private Filter(float frequency, int sampleRate, PassType passType, float resonance) {

        switch (passType) {
            case Lowpass:
                c = 1.0f / (float) Math.tan(Math.PI * frequency / sampleRate);
                a1 = 1.0f / (1.0f + resonance * c + c * c);
                a2 = 2f * a1;
                a3 = a1;
                b1 = 2.0f * (1.0f - c * c) * a1;
                b2 = (1.0f - resonance * c + c * c) * a1;
                break;
            case Highpass:
                c = (float) Math.tan(Math.PI * frequency / sampleRate);
                a1 = 1.0f / (1.0f + resonance * c + c * c);
                a2 = -2f * a1;
                a3 = a1;
                b1 = 2.0f * (c * c - 1.0f) * a1;
                b2 = (1.0f - resonance * c + c * c) * a1;
                break;
        }
    }//end Filter

    //Sets up enum for selection
    private enum PassType {
        Highpass,
        Lowpass,
    }//end PassType

    //Adds value to the rotation
    private void Update(float newInput) {
        float newOutput = a1 * newInput + a2 * inputHistory[0] + a3 * inputHistory[1] - b1 * outputHistory[0] - b2 * outputHistory[1];

        inputHistory[1] = inputHistory[0];
        inputHistory[0] = newInput;

        outputHistory[2] = outputHistory[1];
        outputHistory[1] = outputHistory[0];
        outputHistory[0] = newOutput;
    }//end Update


    //Grabs the latest filtered value
    private float getValue() {
        return this.outputHistory[0];
    }//end getValue

    //Handles the filtering and encoding
    public static void filterHandler(int ID, File audio, Encoding encoding) {

        Database database = new Database(ID);

        int highPassCutoff = database.getHighpass();
        int lowPassCutOff = database.getLowpass();

        //As defined for files recorded by NoiseOff
        int sampleRate = AudioRecording.SAMPLERATE;

        if (encoding == null) {
            encoding = new Encoding();
        }

        try {
            short audioFile[] = encoding.audioToArray(audio); //Convert file to short array
            float floatArray[] = encoding.shortsToFloats(audioFile); //Convert to float array

            //LOW PASS FIRST
            if (!(lowPassCutOff <= 0)) {
                Filter filterL = new Filter(lowPassCutOff, sampleRate, Filter.PassType.Lowpass, 1);
                for (int i = 0; i < floatArray.length; i++) {
                    filterL.Update(floatArray[i]);
                    floatArray[i] = filterL.getValue();
                }
            }

            //High pass:
            if (!(highPassCutoff <= 0)) {
                Filter filterH = new Filter(highPassCutoff, sampleRate, Filter.PassType.Highpass, 1);
                for (int i = 0; i < floatArray.length; i++) {
                    filterH.Update(floatArray[i]);
                    floatArray[i] = filterH.getValue();
                }
            }


            //convert back
            audioFile = encoding.floatsToShorts(floatArray);
            encoding.shortToFile(audioFile);

            System.out.println(database.toString());
        } catch (IOException e)

        {
            e.printStackTrace();
        }
    }//end filterHandler


}
