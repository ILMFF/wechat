package weixin.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/2/23.
 */

public class ChatRoom_Online_Message {

    public String roomname;
    public String name;
    public String message;
    public String time;
    public String nickname;
    public Bitmap icon;


    public ChatRoom_Online_Message(String roomname, String name, String message, String time, String nickname, Bitmap icon) {
        this.roomname = roomname;
        this.name = name;
        this.message = message;
        this.time = time;
        this.nickname = nickname;
        this.icon = icon;
    }




}
