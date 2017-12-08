package com.example.administrator.helloworld;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ActivityDemo";
    private static final String KEY_NAME = "MyKey";
    private EditText mEditText;
    //定义一个String 类型用来存取我们EditText输入的值
    private String mString;
    private int param = 1;
    private static long UPDATE_EVERY = 200;
    protected TextView counter;
    protected Button start;
    protected Button stop;
    protected boolean timeRunning;

    protected long startedAt;
    protected long lastStopped;
    protected UpdateTimer updateTimer;
    private Handler handler;
    MySQLiteHelper myHelper;

    private IntentFilter intentFilter;
    private NetWorkChangeReceiver networkChangeReceiver;

    private IntentFilter localIntentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private MyHandler myHandler;

    class Box {

        @SerializedName("w")
        public int width;

        @SerializedName("h")
        public int height;

        @SerializedName("d")
        public int depth;

        // Methods removed for brevity
    }

    class NetWorkChangeReceiver extends BroadcastReceiver {

    @Override
        public void onReceive(Context context,Intent intent){
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivitymanager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()) {
            Toast.makeText(context, "network is avaiable", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "network is unavaiable", Toast.LENGTH_LONG).show();
        }
    }

    }

    class MyRunable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "MyRunable run~~~");
            // mEditText.setText("fsdafsadfsa");MyHandler
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }

    }

    class MyHandler  extends Handler {


        public void handleMessage(Message msg){
            counter.setText("fsdafsadfsad");
            mEditText.setText("new bgoold");

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null) {
         //   getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.editText);
       // mEditText.setSaveEnabled(false);
        counter = (TextView) findViewById(R.id.timer);
        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);


        myHandler = new MyHandler();

        myHelper = new MySQLiteHelper(this, "my.db", null, 1);
        //向数据库中插入和更新数据
        insertAndUpdateData(myHelper);
        //查询数据
        String result = queryData(myHelper);

///////////////////
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetWorkChangeReceiver();
       // registerReceiver(networkChangeReceiver,intentFilter);
////////////////////////////
        localIntentFilter  = new IntentFilter();
        localIntentFilter.addAction("com.example.helloworld.LOCAL_BROADCAST");
      localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,localIntentFilter);

        Log.e(TAG, result);
        Log.e(TAG, "start onCreate~~~");
    }

    @Override

    protected void onStop() {
        super.onStop();
        Log.e(TAG, "start onStop~~~");
    }

    @Override
    protected void onStart() {
        super.onStart();
       // mEditText.setText(mString);
        Log.e(TAG, "start onCreate~~~");
    }

    protected void onResume() {
        super.onResume();

        Log.e(TAG, "start onPause~~~");
    }

    protected void onPause() {
        super.onPause();
        //mString = mEditText.getText().toString();
        Log.e(TAG, "start onResume~~~");
    }

    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(networkChangeReceiver);
        Log.e(TAG, "start onDestroy~~~");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "start onRestart~~~");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("param", param);
        Log.i(TAG, "onSaveInstanceState called. put param: " + param);
      //  Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_LONG).show();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        param = savedInstanceState.getInt("param");
      //  Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_LONG).show();
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void clickedStart(View view) {
        timeRunning = true;
        startedAt = System.currentTimeMillis();
        //  enableButtons();-
        handler = new Handler();
        updateTimer = new UpdateTimer();
        SharedPreferences sharePref = this.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        String str = mEditText.getText().toString();
        editor.putString(KEY_NAME, mEditText.getText().toString());
        editor.commit();
        writeFile();

        //      handler.postDelayed(updateTimer,UPDATE_EVERY);

    }

    public void clickedStop(View view) {
        timeRunning = false;
        lastStopped = System.currentTimeMillis();
        // enableButtons();
        SharedPreferences sharePref = this.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        String str = sharePref.getString(KEY_NAME, "none");
        //Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        //       handler.removeCallbacks(updateTimer);
        handler = null;
        updateTimer = null;
        readFile();
    }


    public void clickedFiles(View view) {



        String path2 = this.getFilesDir()+"/"+"yanhui_files.txt";
        writeFile2(path2);

        File file = this.getFilesDir();
        String[]  fileNames = file.list();


        for(String string:fileNames){
            Log.d("MSG", "file -- "+string);
            Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        }
    }
    public void clickedCache(View view) {
        String path1 = this.getCacheDir()+"/"+"yanhui_cache.txt";
        writeFile2(path1);
        String path2 = this.getExternalCacheDir()+"/"+"yanhui_ext_cache.txt";
        writeFile2(path2);
    }
    public void clickedExtern(View view) {
        String path = this.getExternalFilesDir(null).toString()+"/"+"yanhui_ext_files.txt";

        writeFile2(path);
    }
    public void clickedOther(View view) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/"+"yanhui_ext_public_direct.txt";

        writeFile2(path);
    }
    public void clickedStore(View view) {
        OkHttpClient client = new OkHttpClient();

    }

    public void clickedLogin(View view) {

        Intent intent = new Intent(this,LoginActivity.class);
       // Toast.makeText(this, "start LoginActivity", Toast.LENGTH_LONG).show();
        int requestCode = 0;
        startActivityForResult(intent,requestCode);
        //Toast.makeText(this, "end LoginActivity", Toast.LENGTH_LONG).show();
    }

    public void clickedGson(View view) {
        /*
        Gson gson = new GsonBuilder().create();
        gson.toJson("Hello", System.out);
        gson.toJson(123, System.out);
        */
        //test
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        final Box box = new Box();
        box.width = 10;
        box.height = 20;
        box.depth = 30;

        final String json = gson.toJson(box);
        System.out.printf("Serialised: %s%n", json);

        final Box otherBox = gson.fromJson(json, Box.class);
        System.out.printf("Same box: %s%n", box.equals(otherBox));

    }
    public void clickedRofit(View view) {
    /*
        Retrofit retrofit = new Retrofit.Builder()

                .baseUrl("http://fanyi.youdao.com/") // 设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 支持RxJava平台
                .build();
    */
       // Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent("com.example.helloworld.ACTION_START");
        intent.setData(Uri.parse("http://www.baidu.com"));
        intent.putExtra("key","value");

        startActivity(intent);
    }

    public void clickedBroad(View view) {
//
       // Intent intent = new Intent("com.example.helloworld.MY_BROADCAST");
        //sendBroadcast(intent);


        Intent localIntent = new Intent("com.example.helloworld.LOCAL_BROADCAST");

        localBroadcastManager.sendBroadcast(localIntent);

    }

    public void clickedNotification(View view) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");

        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }


    public void clickedService(View view) {
        MyRunable runable = new MyRunable();
        new Thread(runable).start();
        Log.d("DownloadTask", "clickedService");
        new DownloadTask().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        String change01 = data.getStringExtra("change");
       // String change02 = data.getStringExtra("change");
        // 根据上面发送过去的请求吗来区别
        switch (requestCode) {
            case 0:
                Toast.makeText(this, change01, Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this, change01, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, change01, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "000000", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*
    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        System.out.println("Server: " + response.header("Server"));
        System.out.println("Date: " + response.header("Date"));
        System.out.println("Vary: " + response.headers("Vary"));
    }*/
    public void enableButtons() {
        start.setEnabled(!timeRunning);
        stop.setEnabled(!timeRunning);
    }

    public void setTimeDisplay() {
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

        if (timeRunning) {
            timeNow = System.currentTimeMillis();
        } else {
            timeNow = lastStopped;
        }
        diff = lastStopped - timeNow;

        if (diff < 0) {
            diff = 0;
        }

        seconds = diff / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        display = String.format("%d", hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds) + ":";
        counter.setText(display);
    }

    public void writeFile() {

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("yanhui2.txt", Context.MODE_WORLD_READABLE);
            outputStream.write(mEditText.getText().toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void readFile() {
        String path = this.getFilesDir()+"/"+"yanhui2.txt";
        String content = "";


        File file = new File(path);
        try{
            InputStream instream = new FileInputStream(file);
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
            if(instream!=null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while(((line=buffreader.readLine())!=null))
                {
                    content +=line+"\n";
                }

            }
            instream.close();
        }
         catch(Exception e)
        {

        }
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();

    }

    public void writeFile2(String path) {
        File file = new File(path);
        try{
            OutputStream  ostream = new FileOutputStream(file);
            if(ostream!=null)
            {
                String str=path;
                byte[] b=str.getBytes();
                ostream.write(b);//因为是字节流，所以要转化成字节数组进行输出
                ostream.flush();
            }
            ostream.close();
        }
        catch(Exception e)
        {

        }
        Toast.makeText(this, path.toString(), Toast.LENGTH_LONG).show();

    }

    //向数据库中插入和更新数据
    public void insertAndUpdateData(MySQLiteHelper myHelper){
        //获取数据库对象
        SQLiteDatabase db = myHelper.getWritableDatabase();
        //使用execSQL方法向表中插入数据
        db.execSQL("insert into hero_info(name,level) values('bb',0)");
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("name", "xh");
        values.put("level", 5);
        //调用方法插入数据
        db.insert("hero_info", "id", values);
        //使用update方法更新表中的数据
        //清空ContentValues对象
        values.clear();
        values.put("name", "xh");
        values.put("level", 10);
        //更新xh的level 为10
        db.update("hero_info", values, "level = 5", null);
        //关闭SQLiteDatabase对象
        db.close();
    }

    //从数据库中查询数据
    public String queryData(MySQLiteHelper myHelper){
        String result = "";
        //获得数据库对象
        SQLiteDatabase db = myHelper.getReadableDatabase();
        //查询表中的数据
        Cursor cursor = db.query("hero_info", null, null, null, null, null, "id asc");
        //获取name列的索引
        int nameIndex = cursor.getColumnIndex("name");
        //获取level列的索引
        int levelIndex = cursor.getColumnIndex("level");
        for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
            result = result + cursor.getString(nameIndex)+ "\t\t";
            result = result + cursor.getInt(levelIndex)+"       \n";
            Log.d("MYDB", "file -- "+result);
        }
        cursor.close();//关闭结果集
        db.close();//关闭数据库对象
        return result;
    }
}

