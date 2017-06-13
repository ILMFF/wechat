package weixin.bean;

import android.app.Activity;
import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class ChatRoom_Msg {

    public String name;
    public String nickname;
    public Bitmap icon;

    public ChatRoom_Msg(String name, String nickname, Bitmap icon) {
        this.name = name;
        this.nickname = nickname;
        this.icon = icon;
    }
}
