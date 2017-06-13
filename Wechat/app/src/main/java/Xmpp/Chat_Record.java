package Xmpp;

/**
 * Created by Administrator on 2017/2/24.
 */

public class Chat_Record {

    public  String name;
    public  String message;
    public  String time;
    public  String from;

    public Chat_Record() {
    }

    public Chat_Record(String name, String message, String time, String from) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.from = from;
    }

}
