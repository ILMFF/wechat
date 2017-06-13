package discover.bean;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class FriendCircleDisplay {

    public String friendcircle_username;
    public Bitmap friendcircle_icon;
    public String friendcircle_nickname;
    public String friendcircle_time;
    public String friendcircle_text;


    public FriendCircleDisplay(String friendcircle_username, Bitmap friendcircle_icon, String friendcircle_nickname, String friendcircle_time, String friendcircle_text) {
        this.friendcircle_username = friendcircle_username;
        this.friendcircle_icon = friendcircle_icon;
        this.friendcircle_nickname = friendcircle_nickname;
        this.friendcircle_time = friendcircle_time;
        this.friendcircle_text = friendcircle_text;
    }
}
