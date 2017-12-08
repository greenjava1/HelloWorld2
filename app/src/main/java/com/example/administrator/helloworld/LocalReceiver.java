package com.example.administrator.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/7.
 */

public class LocalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "LocalReceiver Received", Toast.LENGTH_LONG).show();
    }
}
