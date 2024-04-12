package com.example.projectard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {
    public Context c;
    public ConnectionReceiver()
    {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("App", ""+intent.getAction());
        if(intent.getAction().equals("com.example.listview2023.SOME_ACTION"))
            Toast.makeText(context, "SOME_ACTION is received",
                    Toast.LENGTH_SHORT).show();
        else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            ConnectivityManager cm =
                    (ConnectivityManager)
                            context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                try {
                    Toast.makeText(context, "Network is connected",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Network is changed or reconnected",
                        Toast.LENGTH_LONG).show();
            }
        }else if(intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        {
            if (isAirplaneModeOn(context.getApplicationContext())) {
                Toast.makeText(context, "AirPlane mode is on",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "AirPlane mode is off",
                        Toast.LENGTH_SHORT).show();
            }
        }else if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
        {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if(isCharging) {
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

                if(usbCharge) {
                    Toast.makeText(context, "Charging via USB", Toast.LENGTH_SHORT).show();
                } else if(acCharge) {
                    Toast.makeText(context, "Charging via Adapter", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Charging", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu không sạc
                Toast.makeText(context, "Not Charging", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == 1) {
                String msg = "New Phone Call Event. Incoming Number: " + incomingNumber;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(c, msg, duration);
                toast.show();
            }
        }
    }
}
