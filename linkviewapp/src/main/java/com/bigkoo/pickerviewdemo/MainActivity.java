package com.bigkoo.pickerviewdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.bigkoo.pickerview.TimePopupWindow;
import com.bigkoo.pickerview.TimePopupWindow.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePopupWindow.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<ArrayList<ArrayList<String>>>();
    private TextView tvTime, tvOptions, tvKaoqis;
    TimePopupWindow pwTime;
    OptionsPopupWindow pwOptions;
    OptionsPopupWindow pwKaoqis;
    private ArrayList<KaoQiBean> datas;
    private ArrayList<String> kaoqis;
    private ArrayList<ArrayList<String>> luncis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvOptions = (TextView) findViewById(R.id.tvOptions);
        tvKaoqis = (TextView) findViewById(R.id.tvKaoqis);
        //时间选择器
        pwTime = new TimePopupWindow(this, Type.ALL);
        //时间选择后回调
        pwTime.setOnTimeSelectListener(new OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                tvTime.setText(getTime(date));
            }
        });
        //弹出时间选择器
        tvTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pwTime.showAtLocation(tvTime, Gravity.BOTTOM, 0, 0, new Date());
            }
        });

        //选项选择器
        pwOptions = new OptionsPopupWindow(this);

        //选项1
        options1Items.add("广东");
        options1Items.add("湖南");

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<String>();
        options2Items_01.add("广州");
        options2Items_01.add("佛山");
        options2Items_01.add("东莞");
        ArrayList<String> options2Items_02 = new ArrayList<String>();
        options2Items_02.add("长沙");
        options2Items_02.add("岳阳");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);

        //选项3
        ArrayList<ArrayList<String>> options3Items_01 = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> options3Items_02 = new ArrayList<ArrayList<String>>();
        ArrayList<String> options3Items_01_01 = new ArrayList<String>();
        options3Items_01_01.add("白云");
        options3Items_01_01.add("天河");
        options3Items_01_01.add("海珠");
        options3Items_01_01.add("越秀");
        options3Items_01.add(options3Items_01_01);
        ArrayList<String> options3Items_01_02 = new ArrayList<String>();
        options3Items_01_02.add("南海");
        options3Items_01.add(options3Items_01_02);
        ArrayList<String> options3Items_01_03 = new ArrayList<String>();
        options3Items_01_03.add("常平");
        options3Items_01_03.add("虎门");
        options3Items_01.add(options3Items_01_03);

        ArrayList<String> options3Items_02_01 = new ArrayList<String>();
        options3Items_02_01.add("长沙1");
        options3Items_02.add(options3Items_02_01);
        ArrayList<String> options3Items_02_02 = new ArrayList<String>();
        options3Items_02_02.add("岳1");
        options3Items_02.add(options3Items_02_02);

        options3Items.add(options3Items_01);
        options3Items.add(options3Items_02);

        //三级联动效果
        pwOptions.setPicker(options1Items, options2Items, options3Items, true);
        //设置选择的三级单位
        pwOptions.setLabels("省", "市", "区");
        //设置默认选中的三级项目
        pwOptions.setSelectOptions(0, 0, 0);
        //监听确定选择按钮
        pwOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1)
                        + options2Items.get(options1).get(option2)
                        + options3Items.get(options1).get(option2).get(options3);
                tvOptions.setText(tx);
            }
        });
        //点击弹出选项选择器
        tvOptions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pwOptions.showAtLocation(tvTime, Gravity.BOTTOM, 0, 0);
            }
        });

        //选项选择器
        pwKaoqis = new OptionsPopupWindow(this);

        initData();
        //二级联动效果
        pwKaoqis.setPicker(kaoqis, luncis, true);
        //设置选择的二级单位
//        pwKaoqis.setLabels("考期");
        //设置默认选中的二级项目
        pwKaoqis.setSelectOptions(0, 0);
        //监听确定选择按钮
        pwKaoqis.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是二级别的选中位置
                String tx = kaoqis.get(options1)
                        + luncis.get(options1).get(option2);
                tvKaoqis.setText(tx);
//                pwKaoqis.setTitle(kaoqis.get(options1), luncis.get(options1).get(option2));
            }
        });
        tvKaoqis.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pwKaoqis.setWidth(dip2px(MainActivity.this, 280));
                pwKaoqis.showAtLocation(tvKaoqis, Gravity.CENTER, 0, 0);
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
     * 初始化数据
     */
    private void initData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            KaoQiBean kaoQiBean = new KaoQiBean();
            kaoQiBean.setId(i);
            kaoQiBean.setKaoqiName("kaoqi" + i);
            ArrayList<KaoQiBean.LunCiBean> lunCiBeans = new ArrayList<>();
            for (int j = 0; j < 6 - i; j++) {
                KaoQiBean.LunCiBean lunCiBean = new KaoQiBean.LunCiBean(j, "第" + j + "轮 6.01-7.01");
                lunCiBeans.add(lunCiBean);
            }
            kaoQiBean.setLuncis(lunCiBeans);
            datas.add(kaoQiBean);
        }

        kaoqis = new ArrayList<String>();
        for (int i = 0; i < datas.size(); i++) {
            kaoqis.add(datas.get(i).getKaoqiName());
        }
        luncis = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < datas.size(); i++) {
            ArrayList<String> lunci = new ArrayList<>();
            for (int j = 0; j < datas.get(i).getLuncis().size(); j++) {
                lunci.add(datas.get(i).getLuncis().get(j).getLunciName());
            }
            luncis.add(lunci);
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

}
