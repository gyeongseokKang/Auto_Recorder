package cafe.adriel.androidaudioconverter.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class tempconvert extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tempconvert);



    }

    public void onkorenclick(View v){
        Intent intent = new Intent(getApplicationContext(),listenlist.class);
        startActivity(intent);
    }

    public void onenglishclick(View v){
        Intent intent = new Intent(getApplicationContext(),listenlist2.class);
        startActivity(intent);
    }
}
