package cafe.adriel.androidaudioconverter.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import static cafe.adriel.androidaudioconverter.sample.ClassAdapter.match_name;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void onclasstimetableclicked(View v){
        Intent intent = new Intent(getApplicationContext(),class_database.class);
        startActivity(intent);
    }


    public void onrecordclicked(View v){
        Intent intent = new Intent(getApplicationContext(),recordclass.class);
        String path2=getExternalPath();
        File file=new File(path2+"autorecorder");
        file.mkdir();
        Log.d("main_matchname=",match_name);
        startActivity(intent);
    }

    /*매 정시에 시간 체크하여 녹음 여부 결정!!*/
    public void timecheck(){
        Intent intent = new Intent(getApplicationContext(), recordclass.class);
        startActivity(intent);
        //match_name

       // ClassAdapter.class(on)
    }


    public void ontextclicked(View v){
        Intent intent = new Intent(getApplicationContext(),listenlist.class);
        startActivity(intent);
    }

}
