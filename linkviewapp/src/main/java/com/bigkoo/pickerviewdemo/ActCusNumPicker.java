package com.bigkoo.pickerviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActCusNumPicker extends AppCompatActivity {
    private TextView tv_lunci;
    private ArrayList<KaoQiBean> datas;
    private ArrayList<String> kaoqis;
    private ArrayList<ArrayList<String>> luncis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_num_picker);
        tv_lunci = (TextView) findViewById(R.id.tv_lunci);
        initData();
        tv_lunci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
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

    private void showPop() {
        View popupView = View.inflate(this, R.layout.pop_kaoqi, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, dip2px(this,280), ViewGroup.LayoutParams.WRAP_CONTENT);

        final CusNumberPicker np_kaoqi = (CusNumberPicker) popupView.findViewById(R.id.np_kaoqi);
        Button bt_ok = (Button) popupView.findViewById(R.id.bt_ok);
        Button btn_cancel = (Button) popupView.findViewById(R.id.btn_cancel);

        np_kaoqi.setDisplayedValues(kaoqis.toArray(new String[kaoqis.size()]));
        np_kaoqi.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_kaoqi.setMinValue(0);
        np_kaoqi.setMaxValue(kaoqis.size() - 1);
        np_kaoqi.setFocusable(false);
        np_kaoqi.setFocusableInTouchMode(false);

//        CusNumberPicker.setNumberPickerDividerColor(this, np_kaoqi);
        np_kaoqi.setWrapSelectorWheel(false);
        final CusNumberPicker np_lunci = (CusNumberPicker) popupView.findViewById(R.id.np_lunci);
//        CusNumberPicker.setNumberPickerDividerColor(this, np_lunci);
        np_lunci.setDisplayedValues(luncis.get(0).toArray(new String[luncis.get(0).size()]));
        np_lunci.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_lunci.setMinValue(0);
        np_lunci.setMaxValue(luncis.get(0).size() - 1);
        np_lunci.setFocusable(false);
        np_lunci.setFocusableInTouchMode(false);
        np_lunci.setWrapSelectorWheel(false);
        np_kaoqi.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                np_lunci.setValue(0);
                if (luncis.get(newVal).size() < luncis.get(oldVal).size()) {
                    np_lunci.setMaxValue(luncis.get(newVal).size() - 1);
                    np_lunci.setDisplayedValues(luncis.get(newVal).toArray(new String[luncis.get(newVal).size()]));
                } else {
                    np_lunci.setDisplayedValues(luncis.get(newVal).toArray(new String[luncis.get(newVal).size()]));
                    np_lunci.setMaxValue(luncis.get(newVal).size() - 1);
                }
                np_lunci.setMinValue(0);
                np_lunci.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//设置禁止手动输入数据
                np_lunci.setWrapSelectorWheel(false);
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActCusNumPicker.this, "您选择的是" + ":"
                                + kaoqis.get(np_kaoqi.getValue())
                                + luncis.get(np_kaoqi.getValue()).get(np_lunci.getValue())
                        , Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                tv_lunci.setText(kaoqis.get(np_kaoqi.getValue())
                        + luncis.get(np_kaoqi.getValue()).get(np_lunci.getValue()));
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
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

}
