package title.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SearchSession implements Serializable{

    public String username;
    public String nickname;
    public Bitmap friend_icon;
    public String description;


    public SearchSession(String username, String nickname, Bitmap friend_icon, String description) {
        this.username = username;
        this.nickname = nickname;
        this.friend_icon = friend_icon;
        this.description = description;
    }
}
