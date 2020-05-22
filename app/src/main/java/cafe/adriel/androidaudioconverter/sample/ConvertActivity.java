package cafe.adriel.androidaudioconverter.sample;

import android.Manifest;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import static cafe.adriel.androidaudioconverter.sample.recordclass.newName;

public class ConvertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        }

        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void convertAudio(View v){
        Log.d("ddd2","ddd2");
        /**
         *  Update with a valid audio file!
         *  Supported formats: {@link AndroidAudioConverter.AudioFormat}
         */
        File wavFile = new File(Environment.getExternalStorageDirectory(), "/autorecorder/"+newName);
        Log.d("ddd2",Environment.getExternalStorageDirectory().toString()+"/autorecorder/"+newName);
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Toast.makeText(ConvertActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Exception error) {
                Toast.makeText(ConvertActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        Toast.makeText(this, "Converting audio file...", Toast.LENGTH_SHORT).show();
        AndroidAudioConverter.with(this)
                .setFile(wavFile)
                .setFormat(AudioFormat.FLAC)
                .setCallback(callback)
                .convert();
    }

}