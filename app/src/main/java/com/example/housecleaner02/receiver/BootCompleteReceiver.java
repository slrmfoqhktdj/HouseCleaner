package com.example.housecleaner02.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.housecleaner02.MainActivity;

//public class BootCompleteReceiver extends BroadcastReceiver {
//    static final String TAG = "SIMPLE_KIOSK";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        Log.i(TAG, "onReceive: " + action);
//        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
//            Intent i = new Intent(context, MainActivity.class);
//
//            context.startActivity(i);
//        }
//    }
//
//}

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, TAG, Toast.LENGTH_SHORT).show();
        Log.d(TAG,TAG);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")||intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else {
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}