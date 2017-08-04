package com.bigkoo.pickerviewdemo;

import java.util.ArrayList;

/**
 * Created by lilin on 2017/7/13.
 * func :
 */
public class KaoQiBean {
    private String kaoqiName;
    private int id;
    private ArrayList<LunCiBean> luncis;

    public KaoQiBean(String kaoqiName, int id, ArrayList<LunCiBean> luncis) {
        this.kaoqiName = kaoqiName;
        this.id = id;
        this.luncis = luncis;
    }

    public KaoQiBean() {
    }

    public String getKaoqiName() {
        return kaoqiName;
    }

    public void setKaoqiName(String kaoqiName) {
        this.kaoqiName = kaoqiName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<LunCiBean> getLuncis() {
        return luncis;
    }

    public void setLuncis(ArrayList<LunCiBean> luncis) {
        this.luncis = luncis;
    }

    public static class LunCiBean {
        private int id;
        private String lunciName;

        public LunCiBean() {
        }

        public LunCiBean(int id, String lunciName) {
            this.id = id;
            this.lunciName = lunciName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLunciName() {
            return lunciName;
        }

        public void setLunciName(String lunciName) {
            this.lunciName = lunciName;
        }
    }
}
