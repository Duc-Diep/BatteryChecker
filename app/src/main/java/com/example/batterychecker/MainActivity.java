package com.example.batterychecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Button btnSetting,btnCancelMusic;
    TextView tvNotice;
    int percent=80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNotice = findViewById(R.id.tvNotice);
        btnSetting = findViewById(R.id.btnSetting);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setup);
                dialog.setCanceledOnTouchOutside(false);

                TextView tvPin  = dialog.findViewById(R.id.tvPercent);
                SeekBar sbPin   = dialog.findViewById(R.id.sbPercent);
                Button btnOK    = dialog.findViewById(R.id.btnOk);
                Button btnHuy   = dialog.findViewById(R.id.btnCancel);
                tvPin.setText(sbPin.getProgress() +"%");


                sbPin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        percent = progress;
                        tvPin.setText(progress+"%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(MainActivity.this, "Cài đặt lượng pin cần thông báo", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(MainActivity.this, "Pin đạt " + tvPin.getText()+" sẽ thông báo", Toast.LENGTH_SHORT).show();
                    }
                });
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getSharedPreferences("request", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("percent", percent);
                        editor.putBoolean("check", true);
                        editor.commit();
                        tvNotice.setText("Pin đạt " + percent + "% sẽ thông báo");
                        Intent intent = new Intent(MainActivity.this,Music.class);
                        intent.putExtra("Extra","On");
                        ContextCompat.startForegroundService(MainActivity.this,intent);
                        dialog.dismiss();

                    }
                });
                btnHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }



    });
        btnCancelMusic = findViewById(R.id.btnCancelMusic);
        btnCancelMusic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mBatteryReceiver.getRequest(0,false);
            Intent i = new Intent(MainActivity.this, Music.class);
            stopService(i);
        }
    });

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(mBatteryReceiver,mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerReceiver(mBatteryReceiver,mIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerReceiver(mBatteryReceiver,mIntentFilter);
    }
}