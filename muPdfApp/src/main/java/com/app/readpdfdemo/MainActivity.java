package com.app.readpdfdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ListView;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;

public class MainActivity extends AppCompatActivity{

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview_main);
        String path = "/storage/emulated/0/Android/data/com.example.feng.xuehuiwang/files/xuehuiwang/file/三级-招聘与配置-20161222（学员版）.pdf";
        MuPDFCore core = null;
        try{
            core = new MuPDFCore(this,path);
        }catch (Exception e){

        }
        MuPDFPageAdapter adapter = new MuPDFPageAdapter(this, core);
        mListView.setAdapter(adapter);
    }
}
