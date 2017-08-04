package com.xhw.android.mygride;

/**
 * Created by lilin on 2017/6/12.
 * func :
 */
public class Data {
    private int id;//用户id
    private String name;//姓名
    private String position;//职位
    private String imageUrl;

    public Data(int id, String name, String position, String imageUrl) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
