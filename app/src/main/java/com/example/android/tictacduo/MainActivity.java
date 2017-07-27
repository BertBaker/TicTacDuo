package com.example.android.tictacduo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity
        extends AppCompatActivity
{
    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar paramBundle = getSupportActionBar();
        paramBundle.setTitle("");
        paramBundle.setDisplayUseLogoEnabled(true);
    }

    public void playByBluetooth(View paramView)
    {
        startActivity(new Intent(this, PlayByBluetooth.class));
        overridePendingTransition(0, 0);
        finish();
    }

    public void playPhone(View view)
    {
        paramView = new Bundle();
        paramView.putInt("PROGRESS", 100);
        Intent localIntent = new Intent(this, PlayPhone1.class);
        localIntent.putExtras(paramView);
        startActivity(localIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void playSharing(View paramView)
    {
        startActivity(new Intent(this, PlaySharing.class));
        overridePendingTransition(0, 0);
        finish();
    }
}

