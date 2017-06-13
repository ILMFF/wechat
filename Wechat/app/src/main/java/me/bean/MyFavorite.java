package me.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class MyFavorite implements Serializable {

    public String usename;
    public String nickname;
    public Bitmap icon;
    public String time;
    public String text;

    public MyFavorite(String usename, String nickname, Bitmap icon, String time, String text) {
        this.usename = usename;
        this.nickname = nickname;
        this.icon = icon;
        this.time = time;
        this.text = text;
    }
}
