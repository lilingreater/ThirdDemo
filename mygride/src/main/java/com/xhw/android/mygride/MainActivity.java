package com.xhw.android.mygride;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int MAXITEM = 6;
    int id;
    private GridView gv;
    private ArrayList<Data> datas;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv = (GridView) findViewById(R.id.gv);
        initData();
        adapter = new GridAdapter(this, datas);
        gv.setAdapter(adapter);
        setGideViewHeightBasedOnChildren(gv, 4);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//选择上传的图片

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == datas.size()) {
                    addMember();
                }
            }
        });
        adapter.setOnItemDeleteClick(new GridAdapter.onItemDeleteClick() {
            @Override
            public void onItemDelete(int position) {
                deleteMember(position);

            }
        });
    }

    /**
     * 删除成员
     */
    private void deleteMember(int position) {
        adapter.deleteData(position);
        setGideViewHeightBasedOnChildren(gv, 4);
    }


    /**
     * addMember
     */
    private void addMember() {
        id++;
        Data data = new Data(id, "e" + id, "医师助理", "");
        adapter.addData(data);
        setGideViewHeightBasedOnChildren(gv, 4);
    }

    private void initData() {

        datas = new ArrayList<>();
        Data data1 = new Data(1, "aa", "团队长", "");
        Data data2 = new Data(2, "bb", "医师助理", "");
        id = 2;
        datas.add(data1);
        datas.add(data2);
    }

    /**
     * 动态设置gridView的高度
     *
     * @param gridView
     */
    public static void setGideViewHeightBasedOnChildren(GridView gridView, int num) {
        if (gridView == null) return;
        int itemSpacing = 0;
        int heightNum = 0;
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        View listItem = listAdapter.getView(0, null, gridView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            itemSpacing = gridView.getVerticalSpacing();
        }
        listItem.measure(0, 0);
        heightNum = listAdapter.getCount() / (num + 1) + 1;
        totalHeight = listItem.getMeasuredHeight() * heightNum + itemSpacing + (heightNum - 1);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }


}
