package weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.EmojiGridAdapter;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.GetTime.getDate;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class ChatTogether_Activity extends Activity {

    @BindView(R.id.chattogether_friend_name)
    TextView chattogetherFriendName;
    @BindView(R.id.chattogether_sendmessage_edit)
    EditText chattogetherSendmessageEdit;
    @BindView(R.id.chattogether_emoji_gridview)
    GridView chattogetherEmojiGridview;
    @BindView(R.id.chattogether_emoji_layout)
    LinearLayout chattogetherEmojiLayout;
    @BindView(R.id.chattogethermessage_list)
    ListView chattogethermessageList;
    @BindView(R.id.chat_background_layout)
    RelativeLayout chatBackgroundLayout;
    MultiUserChat muc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattogether_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        Intent intent = getIntent();
        String chatroomname = intent.getStringExtra("chatroomname");
        chattogetherFriendName.setText(chatroomname);
        EmojiGridAdapter adapter = new EmojiGridAdapter(Image_Emoji, this, chattogetherSendmessageEdit);
        chattogetherEmojiGridview.setAdapter(adapter);

        muc = new MultiUserChat(con, chatroomname+"@conference."+con.getServiceName());



    }

    @OnClick({R.id.chattogether_back_btn,R.id.chattogether_send,R.id.chattogether_emoji_switcher
    ,R.id.chattogether_chat_checkfriend_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chattogether_back_btn:
              finish();
                break;
            case R.id.chattogether_send:
                try {
                    muc.sendMessage(chattogetherSendmessageEdit.getText().toString()+"|["+MyUserName+"]"+"|["+getDate()+"]");
                    chattogetherSendmessageEdit.setText("");
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.chattogether_emoji_switcher: //表情开关
                if (chattogetherEmojiLayout.getVisibility() == View.GONE) {
                    chattogetherEmojiLayout.setVisibility(View.VISIBLE);
                } else {
                    chattogetherEmojiLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.chattogether_chat_checkfriend_message: //查看群信息

                break;

        }


    }
}
