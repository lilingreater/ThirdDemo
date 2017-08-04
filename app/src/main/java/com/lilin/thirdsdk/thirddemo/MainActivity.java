package com.lilin.thirdsdk.thirddemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CusCircleProgressBar cusprogress, roundProgressBar;
    private CusNumProgressBar cusNumProgress;
    private Button btn_pj;
    private LinearLayout ll;
    private View popupView;
    private TextView tv_num;
    private SeekBar sb_num;
    private EditText et_intro;
    private Button btn_commit;
    private int screenWidth;
    private PopupWindow popupWindow;
    private int quotaWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cusprogress = (CusCircleProgressBar) findViewById(R.id.cusprogress);
        roundProgressBar = (CusCircleProgressBar) findViewById(R.id.roundProgressBar);
        cusNumProgress = (CusNumProgressBar) findViewById(R.id.cusNumProgress);
        cusprogress.setMax(100);
        cusprogress.setProgress(30);
        cusprogress.setTextIsDisplayable(true);
        roundProgressBar.setMax(100);
        roundProgressBar.setProgress(20);
        roundProgressBar.setTextIsDisplayable(true);
        cusNumProgress.setMax(100);
        cusNumProgress.setProgress(20);
        cusNumProgress.setTextSize(19);
        pingjia();


    }

    private void pingjia() {
        ll = (LinearLayout) findViewById(R.id.ll);
        btn_pj = (Button) findViewById(R.id.btn_pj);
        btn_pj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPJPop();
            }
        });
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

    /**
     * 显示评价
     */
    private void showPJPop() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        Log.e("TAG", "screeHeight=="+screenHeight);
        darkenBackground(this, 0.7f);
        popupView = View.inflate(this, R.layout.pop_pj, null);
        popupWindow = new PopupWindow(popupView, (int) (screenWidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        initPopView();


        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(ll, Gravity.CENTER, 0, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(MainActivity.this, 1f);
            }
        });

    }

    /**
     * 获取手机型号
     * 手机型号:Che2-TL00,系统版本:4.4.2,屏幕分辨率:720*1280
     * 手机型号:Google Nexus 5 - 6.0.0 - API 23 - 1080x1920,系统版本:6.0,屏幕分辨率:1080*1776
     */
    public static String getHandSetInfo(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        String s = ",屏幕分辨率:" + dm.widthPixels + "*" + dm.heightPixels;
        String handSetInfo =
                "手机型号:" + android.os.Build.MODEL +
                        ",系统版本:" + android.os.Build.VERSION.RELEASE +
                        s;
        Log.e("handsetinfo=", handSetInfo);
        return handSetInfo;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initPopView() {

        tv_num = (TextView) popupView.findViewById(R.id.tv_num);
        sb_num = (SeekBar) popupView.findViewById(R.id.sb_num);
        et_intro = (EditText) popupView.findViewById(R.id.et_intro);
        btn_commit = (Button) popupView.findViewById(R.id.btn_commit);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_num.measure(spec, spec);
        quotaWidth = tv_num.getMeasuredWidth();
        // popupwindow的宽度= 屏幕宽度 *0.9， - seekar padding 和 margin  获得到seekbar的宽度 ，然后*0.8 为当前位置，+ seekbar padding 和margin左边  - textview的1/2宽度 即为当前textview 距离左边的位置
        float initLeft = (float) ((screenWidth * 0.9 - sb_num.getLeft() * 2 - dip2px(this, 15) * 2 - sb_num.getPaddingStart() * 2) * 0.8 + dip2px(this, 15) + sb_num.getPaddingStart() - (double) quotaWidth / 2);
        tv_num.setX(initLeft);
        sb_num.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tv_num.setText(progress + "分");
                float seekBarX = seekBar.getX();
                int paddingStart = seekBar.getPaddingStart();
                int paddingEnd = seekBar.getPaddingEnd();
                int seekBarWidth = seekBar.getWidth() - paddingStart - paddingEnd;
                float tvLeft = (float) (((double) progress / seekBar.getMax()) * seekBarWidth + seekBarX + paddingStart - (double) tv_num.getWidth() / 2);
                tv_num.setX(tvLeft);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
//                Toast.makeText(MainActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                getHandSetInfo(MainActivity.this);
            }
        });

    }
}
