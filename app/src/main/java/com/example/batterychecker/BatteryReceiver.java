package com.example.batterychecker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.widget.ImageView;
import android.widget.TextView;

public class BatteryReceiver extends BroadcastReceiver {
    private int percent;
    private boolean check;
    @Override
    public void onReceive(Context context, Intent intent) {
        TextView statusLabel = ((MainActivity) context).findViewById(R.id.tvStatus);
        TextView percentageLabel = ((MainActivity) context).findViewById(R.id.tvPercent);
        ImageView batteryImage = ((MainActivity) context).findViewById(R.id.imgPercent);
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {

            // Status
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String message = "";

            switch (status) {
                case BatteryManager.BATTERY_STATUS_FULL:
                    message = "Pin đầy";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    message = "Đang sạc";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    message = "Không sạc";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    message = "Không sạc";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    message = "Unknown";
                    break;
            }
            statusLabel.setText(message);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = level * 100 / scale;
            percentageLabel.setText(percentage + "%");


            // Image
            Resources res = context.getResources();

            if (percentage >= 85) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b100));

            } else if (85 > percentage && percentage >= 65) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b75));

            } else if (65 > percentage && percentage >= 40) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b50));

            } else if (40 > percentage && percentage >= 15) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b25));

            } else {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b0));
            }
            //setting
            if(percentage >= percent && check == true){
                Intent intent1 = new Intent(context, Music.class);
                intent1.putExtra("Extra", "On");
                context.startService(intent1);
                this.check = false;
            }

        }
    }
    public void getRequest(int per, boolean check){
        this.percent = per;
        this.check= check;
    }

}
