package me;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Xmpp.AppTemp;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.adapter.FavoriteAdapter;
import me.bean.MyFavorite;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.favorite_context;
import static Xmpp.AppTemp.favorite_list;
import static Xmpp.AppTemp.friendlist;
import static teacher.davisstore.com.wechat.BaseActivity.favoriteadapter;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class MeFragment_favorite extends Activity {

    @BindView(R.id.myfavorite_list)
    ListView myfavoriteList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfavorite);
        ButterKnife.bind(this);
        favorite_context = this;
        init();
    }

    private void init() {

        VCard card = new VCard();
        try {
            card.load(con, MyUserName + "@" + con.getServiceName());

        }catch (XMPPException e) {
            e.printStackTrace();
        }
        String favorite_content = card.getMiddleName();
        if (favorite_content != null) {
            SpliteFavorite(favorite_content, card);
        }


        myfavoriteList.setAdapter(favoriteadapter);
    }

    private void SpliteFavorite(String favorite_content,VCard card) {

        favorite_list.clear();
        byte[] avatar = null;
        Bitmap bitmap = null;
        String nickName = null;
        String time = null;
        String username = null;
        String content = null;

        Pattern pat=Pattern.compile("\\{([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|([^\\|^\\}]+)\\}");
        Matcher matcher = pat.matcher(favorite_content);


        while (matcher.find()){
            VCard fcard = new VCard();
            username = matcher.group(1);
            try {
                fcard.load(con, username + "@" + con.getServiceName());

            }catch (XMPPException e) {
                e.printStackTrace();
            }

            time = matcher.group(2) ;
            content = matcher.group(3);
            avatar = fcard.getAvatar();
            nickName = fcard.getNickName();
            bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
            Log.e("收藏夹加载",username+"   "+time+"   "+content);
            favorite_list.add(new MyFavorite(username,nickName,bitmap,time,content));
        }


        favoriteadapter.notifyDataSetChanged();

    }


    @OnClick(R.id.myfavorite_btn_back)
    public void onClick() {
        finish();
    }
}
