package weixin.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/2/23.
 */

public class MessageBean {

    public  String name;
    public  String message;
    public  String time;
    public  int informationnums = 0;
    public  String from;
    public  String nickname;
    public  Bitmap icon;
    public String ChatRoomOrNot;
    public String MemberSender;






    public MessageBean(String name, String message, String time, int informationnums, String from
            , String nickname, Bitmap icon, String ChatRoomOrNot, String MemberSender) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.informationnums = informationnums;
        this.from = from;
        this.nickname = nickname;
        this.icon = icon;
        this.ChatRoomOrNot = ChatRoomOrNot;
        this.MemberSender = MemberSender;
    }




    public MessageBean(String name, String message, String time, String from, String nickname, Bitmap icon
            , String ChatRoomOrNot, String MemberSender) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.from = from;
        this.nickname = nickname;
        this.icon = icon;
        this.ChatRoomOrNot = ChatRoomOrNot;
        this.MemberSender = MemberSender;
    }


}
