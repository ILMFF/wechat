package discover;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import discover.adapter.FriendCricleEmojiAdapter;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MySex;
import static Xmpp.AppTemp.MySignature;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.GetTime.getDate;

import static teacher.davisstore.com.wechat.Register_main.BitmapToBytes;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class DiscoverFragment_sendmessage extends Activity {

    @BindView(R.id.friendcircle_message)
    EditText friendcircleMessage;
    @BindView(R.id.emoji_gridview)
    GridView emojiGridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discoverfragment_sendfriendcircle);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        FriendCricleEmojiAdapter adapter = new FriendCricleEmojiAdapter(Image_Emoji, this, friendcircleMessage);
        emojiGridview.setAdapter(adapter);
    }



    @OnClick({R.id.friendcircle_back_btn, R.id.friendcircle_send_btn, R.id.friendcircle_emoji_switcher})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friendcircle_back_btn:
                finish();
                break;
            case R.id.friendcircle_send_btn: //发送朋友圈
                String Message = friendcircleMessage.getText().toString();
                Log.e("Message",Message);
                String sendtime = getDate();
                SendFriendCircle_Meaasge(Message,sendtime);
                finish();
//                fc_adapter.notifyDataSetChanged();
                Intent intent_friendcircle = new Intent("com.broadcast.FriendCircleRefresh");
                sendBroadcast(intent_friendcircle);
                break;
            case R.id.friendcircle_emoji_switcher:  //表情开关
                if (emojiGridview.getVisibility() == View.VISIBLE) {
                    emojiGridview.setVisibility(View.GONE);
                } else {
                    emojiGridview.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void SendFriendCircle_Meaasge(String Message,String sendtime) {

        VCard card = new VCard();
        try {
            card.load(con,MyUserName+ "@" + con.getServiceName());

            Message = "{"+Message+"|"+sendtime+"|//"+"}";
            String MyFriendCircle = card.getField("FriendCircle");
            if (MyFriendCircle !=null) {
                Log.e("MyFriendCircle", MyFriendCircle);
                MyFriendCircle += Message;
            }else {
                MyFriendCircle = Message;
            }
            card.setField("FriendCircle",MyFriendCircle);

            byte[] avatar = card.getAvatar();
            String encodeImage = StringUtils.encodeBase64(avatar);

            card.setEncodedImage(encodeImage);
            card.setAvatar(avatar);
            card.setProperty("PHOTO", encodeImage);
            card.setNickName(MyNickName);
            card.setFirstName(MySex);
            card.setLastName(MySignature);
            card.save(con);

        } catch (XMPPException e) {
            e.printStackTrace();
        }

/*        String MyFriendCircle = (String) card.getProperty("FriendCircle");
        Log.e("MyFriendCircle",MyFriendCircle);
        MyFriendCircle = MyFriendCircle + Message;
//        String MyFriendCircle = card.getOrganization();
//        MyFriendCircle += Message;
//        Log.e("MyFriendCircle",MyFriendCircle);
//        card.setOrganization(MyFriendCircle);

        card.setProperty("FriendCircle",MyFriendCircle);*/


    }
}
