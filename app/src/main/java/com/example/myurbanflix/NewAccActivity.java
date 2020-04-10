package com.example.myurbanflix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NewAccActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** This complains for some reason, just ignore it; it works */
        setContentView(R.layout.activity_new_acc);
    }
}
