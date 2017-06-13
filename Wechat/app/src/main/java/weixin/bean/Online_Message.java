package weixin.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/2/23.
 */

public class Online_Message {

    public String name;
    public String message;
    public String time;
    public String nickname;
    public Bitmap icon;
    public String ChatRoomOrNot;
    public String MemberSender;


    public Online_Message(String name, String message, String time, String nickname, Bitmap icon, String chatRoomOrNot, String memberSender) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.nickname = nickname;
        this.icon = icon;
        ChatRoomOrNot = chatRoomOrNot;
        MemberSender = memberSender;
    }

    public Online_Message() {
    }


}
