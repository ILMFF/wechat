package weixin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.ChatAdapter;

import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static weixin.Chat_Activity.chatAdapter;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Chat_FriendMessage extends Activity {

    @BindView(R.id.friend_now_icon)
    ImageView friendNowIcon;
    @BindView(R.id.friend_now_nickname)
    TextView friendNowNickname;
    @BindView(R.id.friend_now_sex)
    TextView friendNowSex;
    @BindView(R.id.friend_now_signature)
    TextView friendNowSignature;

    @BindView(R.id.friend_check_chatrecord)
    TextView friendCheckChatrecord;
    @BindView(R.id.friend_clear_chatrecord)
    TextView friendClearChatrecord;
    @BindView(R.id.friend_chatbackground)
    TextView friendChatbackground;

    String target;
    Boolean adding_check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_friendmessage);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

        VCard vcard = new VCard();
        Intent intent = getIntent();
        target = intent.getStringExtra("Target");
        adding_check = intent.getBooleanExtra("Adding_check", false);

        try {
            vcard.load(con, target + "@" + con.getServiceName());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        byte[] friend_icon = vcard.getAvatar();
        String friend_nickName = vcard.getNickName();
        String friend_sex = vcard.getFirstName();
        if (friend_sex == null){
            friend_sex = "未填写";
        }
        String friend_signature = vcard.getLastName();
        if (friend_signature == null){
            friend_signature = "未填写";
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(friend_icon, 0, friend_icon.length);
        friendNowIcon.setImageBitmap(bitmap);
        friendNowNickname.setText(friend_nickName);
        friendNowSex.setText(friend_sex);
        friendNowSignature.setText(friend_signature);

        //如果是在添加好友的时候进行查看好友，把其他功能全部隐蔽
        if (adding_check) {

            friendCheckChatrecord.setVisibility(View.GONE);
            friendClearChatrecord.setVisibility(View.GONE);
            friendChatbackground.setVisibility(View.GONE);
        }

    }


    @OnClick({R.id.chat_friend_back_btn, R.id.friend_chatbackground, R.id.friend_check_chatrecord, R.id.friend_clear_chatrecord})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_friend_back_btn:
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.friend_chatbackground: //更改聊天背景
                Intent intent_chatbackground = new Intent(Chat_FriendMessage.this, Chat_AlterBackground.class);
                startActivity(intent_chatbackground);
                break;

            case R.id.friend_check_chatrecord: //查询聊天记录
                Intent intent_chatrecord = new Intent(Chat_FriendMessage.this, Chat_CheckChatRecord.class);
                startActivity(intent_chatrecord);
                break;

            case R.id.friend_clear_chatrecord://清空与目标的聊天记录
//                Log.e("target",target);
                final AlertDialog.Builder exit_builder = new AlertDialog.Builder(this);
                exit_builder.setMessage("是否清空与该人的聊天记录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                        alterDataBase.DeleteOneRecord(target);

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();  //关闭对话框
                    }
                });
                AlertDialog exit_dialog = exit_builder.create();
                exit_dialog.show();

//                chatAdapter.notifyDataSetChanged();

                break;

            default:
                break;
        }
    }
}
