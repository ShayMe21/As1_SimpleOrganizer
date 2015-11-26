package com.example.arsalan.as1_simpleorganizer;
/*
DECLARATION
I hold a copy of this assignment that I can produce if the original is lost or damaged.
I hereby certify that no part of this assignment/product has been copied from any other student’s work or from any other source except where due acknowledgement is made in the assignment.
No part of this assignment/product has been written/produced for me by another person except where such collaboration has been authorised by the subject lecturer/tutor concerned.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        /* navigate to sections when user picks one of the options.*/
        findViewById(R.id.imgBtnFriends).setOnClickListener(new HandleButton());
        findViewById(R.id.imgBtnTask).setOnClickListener(new HandleButton2());
        findViewById(R.id.imgBtnEvents).setOnClickListener(new HandleButton3());
        findViewById(R.id.imgBtnGallery).setOnClickListener(new HandleButton4());
    }
    class HandleButton implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Friends.class);
            startActivity(intent);
        }
    }
    class HandleButton2 implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Tasks.class);
            startActivity(intent);
        }
    }
    class HandleButton3 implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Events.class);
            startActivity(intent);
        }
    }
    class HandleButton4 implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Gallery.class);
            startActivity(intent);
        }
    }


}
