package com.bigkoo.pickerviewdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

public class ActCusSpinner extends AppCompatActivity {


    private LinearLayout ll_order_type;
    private TextView tv_order_type;
    private TextView tv_select_course;
    private ImageView iv_select_course;
    private LinearLayout ll_select_course;
    Spinner sp_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cus_spinner);
        sp_order = (Spinner) findViewById(R.id.sp_order);
        ll_select_course = (LinearLayout) findViewById(R.id.ll_select_course);
        tv_select_course = (TextView) findViewById(R.id.tv_select_course);
        iv_select_course = (ImageView) findViewById(R.id.iv_select_course);
        ll_select_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectCoursePop();
            }
        });


        ll_order_type = (LinearLayout) findViewById(R.id.ll_order_type);
        tv_order_type = (TextView) findViewById(R.id.tv_order_type);
        ll_order_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectPop();
            }
        });

    }

    /**
     * 选择课程
     */
    private void showSelectCoursePop() {
        iv_select_course.setVisibility(View.VISIBLE);
        View popupView = View.inflate(this, R.layout.pop_select_list, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        ListView pop_lv = (ListView) popupView.findViewById(R.id.pop_lv);
        pop_lv.setAdapter(new ArrayAdapter<String>(ActCusSpinner.this, R.layout.pop_select_item, getResources().getStringArray(R.array.record_course)));

        pop_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                tv_select_course.setText(getResources().getStringArray(R.array.record_course)[i]);

            }
        });
        popupWindow.showAsDropDown(ll_select_course, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_select_course.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 选择日期
     */
    private void showSelectPop() {
        View popupView = View.inflate(this, R.layout.pop_select_list, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, dip2px(ActCusSpinner.this, 100), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        ListView pop_lv = (ListView) popupView.findViewById(R.id.pop_lv);
        pop_lv.setAdapter(new ArrayAdapter<String>(ActCusSpinner.this, R.layout.pop_select_item, getResources().getStringArray(R.array.record_order)));
        pop_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                tv_order_type.setText(getResources().getStringArray(R.array.record_order)[i]);
            }
        });
        popupWindow.showAsDropDown(ll_order_type, 0, 0);

    }

    /**
     * 设置背景色透明度
     *
     * @param activity
     * @param bgcolor
     */
    public static void darkenBackground(Activity activity, Float bgcolor) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);

    }
}
