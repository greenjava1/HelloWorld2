package com.example.administrator.helloworld;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/8.
 */

public class DownloadTask extends AsyncTask<Void,Integer,Boolean> {
    private static final String TAG = "DownloadTask";
    @Override
    protected  void onPreExecute()
    {
        Log.e(TAG, "DownloadTask onPreExecute~~~");
    }

    @Override
    protected  Boolean doInBackground(Void... param)
    {
        Log.e(TAG, "DownloadTask doInBackground~~~");
        return false;
    }

    @Override
    protected  void onProgressUpdate(Integer... values)
    {
        Log.e(TAG, "DownloadTask onProgressUpdate~~~");
    }

    @Override
    protected  void onPostExecute(Boolean result)
    {
        if(result)
        {
            Toast.makeText(AppMy.getContext(),"test ok", Toast.LENGTH_LONG);
            Log.e(TAG, "DownloadTask onPostExecute ok~~~");
        }
        else
        {
            Toast.makeText(AppMy.getContext(),"test error", Toast.LENGTH_LONG);
            Log.e(TAG, "DownloadTask onPostExecute error~~~");
        }

    }
}
