package com.example.android.tictacduo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;



public class PlayPhone1 extends AppCompatActivity {
    Boolean phoneFirst = false;
    RadioButton playPhoneButton;
    TextView difficultyMessage;
    SeekBar seekBar;

    AlertDialog.Builder alert;
    public void displayAlert(View v) {alert.show();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_phone_1);
        getIntent(); int i = getIntent().getExtras().getInt("PROGRESS");

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playPhoneButton = ((RadioButton)findViewById(R.id.play_phone_button));
        playPhoneButton.setChecked(true);

        difficultyMessage = ((TextView)findViewById(R.id.difficulty_message));

        Switch sb = (Switch) findViewById(R.id.sb);
        sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                phoneFirst = isChecked;
            }
        });

        seekBar = ((SeekBar)findViewById(R.id.seek_bar));
        difficultyMessage.setText("Difficulty: " + i + "%");
        seekBar.setProgress(i);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {difficultyMessage.setText("Difficulty: " + progress + "%");}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void playPhoneN(View view) {

        int progress = seekBar.getProgress();

        Bundle bundle = new Bundle();
        bundle.putBoolean("PHONEFIRST", phoneFirst);
        bundle.putInt("PROGRESS", progress);

        Intent localIntent = new Intent(this, PlayPhoneN.class);
        localIntent.putExtras(bundle);
        startActivity(localIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void playPhone(View paramView) {
        // if playPhone button is clicked then do nothing
    }

    public void playSharing(View view) {
        RadioButton playSharingButton = (RadioButton)findViewById(R.id.play_sharing_button);
        final Intent localIntent = new Intent(this, PlaySharing.class);
        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {startActivity(localIntent);
                overridePendingTransition(0, 0);
                finish();}});
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        playSharingButton.setChecked(false);
        alert.setMessage("Do you want to start over, and play by sharing the phone?");
        alert.show();
    }

    public void playByBluetooth(View view) {
        RadioButton playByBluetoothButton = (RadioButton)findViewById(R.id.play_by_bluetooth_button);
        final Intent localIntent = new Intent(this, PlayByBluetooth.class);
        alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {startActivity(localIntent);
                overridePendingTransition(0, 0);
                finish();}});
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        playByBluetoothButton.setChecked(false);
        alert.setMessage("Do you want to start over, and play by bluetooth?");
        alert.show();
    }

}
