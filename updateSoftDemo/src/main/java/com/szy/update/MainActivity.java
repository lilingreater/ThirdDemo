package com.szy.update;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */
public class MainActivity extends Activity {
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ///storage/emulated/0
        Log.e("TAG", "Environment.getExternalStorageDirectory" + Environment.getExternalStorageDirectory().getAbsolutePath());
        ///storage/emulated/0/xuehuiwang
        Log.e("TAG", "Environment.getExternalStoragePublicDirectory" + Environment.getExternalStoragePublicDirectory("xuehuiwang"));
        ///data
        Log.e("TAG", "Environment.getDataDirectory" + Environment.getDataDirectory().getAbsolutePath());
        ///cache
        Log.e("TAG", "Environment.getDownloadCacheDirectory" + Environment.getDownloadCacheDirectory().getAbsolutePath());
        ///system
        Log.e("TAG", "Environment.getRootDirectory" + Environment.getRootDirectory().getAbsolutePath());
        ///storage/emulated/0/Android/data/com.szy.update/files/Documents
        Log.e("TAG", "getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)" + getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS));
        ///storage/emulated/0/Android/data/com.szy.update/files/Download
        Log.e("TAG", "getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)" + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        //)/storage/emulated/0/Android/data/com.szy.update/files/Pictures
        Log.e("TAG", "getExternalFilesDir(Environment.DIRECTORY_PICTURES)" + getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        ///storage/emulated/0/Android/data/com.szy.update/files/Movies
        Log.e("TAG", "getExternalFilesDir(Environment.DIRECTORY_MOVIES)" + getExternalFilesDir(Environment.DIRECTORY_MOVIES));
        ///storage/emulated/0/Android/data/com.szy.update/files/Music
        Log.e("TAG", "getExternalFilesDir(Environment.DIRECTORY_MUSIC)" + getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        Log.e("TAG", "getExternalFilesDir(apk)" + getExternalFilesDir("apk"));
        ///storage/emulated/0/Android/data/com.szy.update/cache
        Log.e("TAG", "getExternalCacheDir()" + getExternalCacheDir());


        Button updateBtn = (Button) findViewById(R.id.btnUpdate);
        updateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager manager = new UpdateManager(MainActivity.this);
                try {
                    manager.getVersionName(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 检查软件更新
                manager.checkUpdate();
            }
        });

    }
}