package com.example.arsalan.as1_simpleorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class FullScreenImage extends Activity {
    ImageAdapter imAdapt = new ImageAdapter(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fullscreen_image);
        Intent intent = getIntent();
        ImageView image = (ImageView)findViewById(R.id.image);
        int position = intent.getIntExtra("Friend", 50);
        image.setImageResource(imAdapt.imageID[position]);
    }
}
