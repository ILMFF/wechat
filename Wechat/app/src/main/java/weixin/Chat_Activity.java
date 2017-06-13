package weixin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Xmpp.AppTemp;
import Xmpp.GetTime;
import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.ChatAdapter;
import weixin.adapter.EmojiGridAdapter;
import weixin.bean.Friend_ChatBackground;
import weixin.bean.MessageBean;
import weixin.bean.Online_Message;

import static Xmpp.AppTemp.ChatBackGround_list;
import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.ChatTargetNickName;
import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.InToChatRoom;
import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.chatManager;
import static Xmpp.AppTemp.chat_background;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.messageListAdapter;
import static Xmpp.GetRealName.getRealName;
import static Xmpp.GetTime.getDate;

/**
 * Created by Administrator on 2017/2/23.
 */

public class Chat_Activity extends Activity {
    @BindView(R.id.friend_name)
    TextView friendName;
    @BindView(R.id.sendmessage_edit)
    EditText sendmessageEdit;
    @BindView(R.id.chatmessage_list)
    ListView chatmessageList;
    static List<MessageBean> list_message;
    public static ChatAdapter chatAdapter;
    private static Bitmap bitmap;
    @BindView(R.id.emoji_gridview)
    GridView emojiGridview;
    @BindView(R.id.emoji_layout)
    LinearLayout emojiLayout;
    @BindView(R.id.chat_background_layout)
    RelativeLayout chatBackgroundLayout;

    private Chat chat;
    private AlterDataBase alterDataBase;
    public static Context Chat_Activity_context = null;


    private static Handler handler = new Handler() {

        public void handleMessage(android.os.Message message) {

            switch (message.what) {

                case 1: //发送信息
                    Log.e("更新在线信息", "刷新");
                    String[] arg = (String[]) message.obj;
                    Log.e("接收到的",arg[0]+"  "+arg[1]+"  "+arg[2]+"  "+arg[3]+"  "+arg[4]+"  "+arg[5]+"  "+arg[6]);
                    list_message.add(new MessageBean(arg[0], arg[1], arg[2], arg[3], arg[4], bitmap,arg[5],arg[6]));

                    chatAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        ButterKnife.bind(this);
        list_message = new ArrayList<>();
        Log.e("Connection station", XmppTool.getConnection().isConnected() + "");
        Log.e("user",con.getUser()+"   ");
        Chat_Activity_context = Chat_Activity.this;
        init();

    }

    private void init() {

        friendName.setText(ChatTargetNickName);
        chatAdapter = new ChatAdapter(list_message, this);
        chatmessageList.setAdapter(chatAdapter);
        chatmessageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);  //总是滚动到最后一条

        EmojiGridAdapter adapter = new EmojiGridAdapter(Image_Emoji, this, sendmessageEdit);
        emojiGridview.setAdapter(adapter);

        for (int i = 0; i < ChatBackGround_list.size() ; i++) {  //匹配显示对应聊天背景

            if ( ChatBackGround_list.get(i).usename.equals(ChatTarget) && chat_background != null ) {
                Drawable drawable = new BitmapDrawable(chat_background);
                chatBackgroundLayout.setBackgroundDrawable(drawable);
            }

        }



        //进入目标聊天框，先把聊天记录添加进去
        alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        Cursor cursor = alterDataBase.QueryAllRecord();
//        Log.e("tag","---------------------------------");
//        Log.e("list_message",list_message.size()+"");
//        Log.e("cursor查询所有聊天记录数量",cursor.getCount()+"");

        if (cursor.moveToFirst()) {

            do {
                String Belong = cursor.getString(cursor.getColumnIndex("Belong"));
                String Content = cursor.getString(cursor.getColumnIndex("Content"));
                String SenderName = cursor.getString(cursor.getColumnIndex("SenderName"));
                String SenderNickName = cursor.getString(cursor.getColumnIndex("SenderNickName"));
                String SenderIcon = cursor.getString(cursor.getColumnIndex("SenderIcon"));
                String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                String ContentFrom = cursor.getString(cursor.getColumnIndex("ContentFrom"));
                String ChatRoomOrNot = cursor.getString(cursor.getColumnIndex("ChatRoomOrNot"));
                String MemberSender = cursor.getString(cursor.getColumnIndex("MemberSender"));
//                Log.e("sendericons",SenderIcon);
                byte[] bitmapArray;
                bitmapArray = Base64.decode(SenderIcon, Base64.DEFAULT);
//                Log.e("bitmapArray",bitmapArray.length+"");
                Bitmap sendericons = BitmapFactory.decodeByteArray(bitmapArray, 0,
                        bitmapArray.length);


                String str = AppTemp.ChatTarget + "/" + MyUserName;
                Log.e("name", AppTemp.ChatTarget + MyUserName + "           " + Belong + "  " + Content + "    ");
//                Log.e("对比",Belong.equals(str)+"   "+str.length()+"   "+Belong.length());
                if (Belong.equals(str) == true) {  //找到对应人的聊天记录
                    Log.e("进入添加","----------"+SenderName+"       "+Content+"     "+ChatRoomOrNot+"     "+MemberSender);
                    list_message.add(new MessageBean(SenderName, Content, CreateTime, ContentFrom, SenderNickName, sendericons,ChatRoomOrNot,MemberSender));

                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        chatAdapter.notifyDataSetChanged();


        Log.e("onlinemsg_list", AppTemp.onlinemsg_list.size() + "");
        Iterator<Online_Message> onlinemsg_list = AppTemp.onlinemsg_list.iterator();  //未接收信息
        while (onlinemsg_list.hasNext()) {

            Online_Message next = onlinemsg_list.next();

            Log.e("next.name",next.name+"        "+ChatTarget);

            if (next.name.contains(AppTemp.ChatTarget)) {  //查看是否有当前聊天目标的未接收信息,加载后删除
//                Log.e("next.name",next.name);
                list_message.add(new MessageBean(getRealName(next.name), next.message, next.time, "IN", next.nickname, next.icon,next.ChatRoomOrNot,next.MemberSender)); //刷新显示
                chatAdapter.notifyDataSetChanged();
                onlinemsg_list.remove();
                alterDataBase.DeleteOneNewsRecord(getRealName(next.name), next.time, next.message); //更新数据库表
            }

        }

//        chatManager = con.getChatManager();
      if (InToChatRoom == false){
          chat = chatManager.createChat(AppTemp.ChatTarget + "@" + con.getServiceName(), null);
      }


    }


    public static class MessageReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(context,"当前聊天框也接受到了信息",Toast.LENGTH_SHORT).show();

            String message_body = intent.getStringExtra("message_body");
            String message_from = intent.getStringExtra("message_from");
            String message_menber = intent.getStringExtra("message_menber");
            Boolean chatroom_ornot = intent.getBooleanExtra("chatroom_ornot",false);
            String time = intent.getStringExtra("time");
//            AppTemp.messageListAdapter.notifyDataSetChanged();
            Log.e("当前聊天框", "接受到了信息:"+message_menber);

            if (chatroom_ornot  ==  false){  //不是群聊

                if (message_from.contains(AppTemp.ChatTarget)) {  //监听到聊天目标发送来的消息，立马显示在屏幕上
                    //获取用户、消息、时间、IN、名称
                    Log.e("单聊接收","--------------------");
                    Log.e("单聊 body", AppTemp.ChatTarget + "     " + message_from);
                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                    VCard card = new VCard();
                    try {
                        card.load(XmppTool.getConnection(), message_from + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    String[] args = new String[]{message_from, message_body, time, "IN", card.getNickName(),"NO",""+message_menber};

                    //在handler里取出来显示消息
                    android.os.Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = args;
                    msg.sendToTarget();

                    Iterator<Online_Message> iterator = AppTemp.onlinemsg_list.iterator();  //删除已读消息
                    while (iterator.hasNext()) {
                        if (iterator.next().message.equals(message_body)) {
                            iterator.remove();
                        }
                    }

                }
            } else {

                if (message_from.contains(AppTemp.ChatTarget)) {  //监听到聊天目标发送来的消息，立马显示在屏幕上
                    //获取用户、消息、时间、IN、名称
                    Log.e("群聊接收","--------------------");
                    Log.e("群聊 body", AppTemp.ChatTarget + "     " + message_from+"    "+message_menber);
                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                    VCard card = new VCard();
                    try {
                        card.load(XmppTool.getConnection(), message_menber + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    String[] args = new String[]{ChatTarget, message_body, time, "IN", ChatTarget,"YES",message_menber};

                    //在handler里取出来显示消息
                    android.os.Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = args;
                    msg.sendToTarget();

                    Iterator<Online_Message> iterator = AppTemp.onlinemsg_list.iterator();  //删除已读消息
                    while (iterator.hasNext()) {
                        if (iterator.next().message.equals(message_body)) {
                            iterator.remove();
                        }
                    }

                }

            }
        }
    }


    @OnClick({R.id.back_btn, R.id.send, R.id.emoji_switcher, R.id.chat_checkfriend_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn: //返回
//                AppTemp.MessageListAdapterON = true;
                AppTemp.ChatTarget = "none";
                ChatTargetNickName = "none";
                AppTemp.InToChatRoom = false;
                messageListAdapter.notifyDataSetChanged();
                finish();
                break;
            case R.id.send:  //发送消息

                if (AppTemp.InToChatRoom == false) {  //不是群聊

                    Message message = new Message();
                    message.setBody(sendmessageEdit.getText().toString());
                    message.setProperty("time", GetTime.getDate());
                    Log.e("发送时间", (String) message.getProperty("time") + "     " + AppTemp.ChatTarget);
                    if (message.getBody().length() > 0) {
                        list_message.add(new MessageBean(AppTemp.ChatTarget, message.getBody(), (String) message.getProperty("time"), "OUT", MyNickName, MyIcon, "NO", MyUserName));
                        chatAdapter.notifyDataSetChanged();  //发送消息后刷新适配器，让信息显示屏幕
                        emojiLayout.setVisibility(View.GONE);
                        try {

                            chat.sendMessage(message);  //发送消息给聊天目标
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Chat_Activity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Chat_Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘
                    sendmessageEdit.setText("");  //发消息栏清空
                    sendmessageEdit.clearFocus(); //去除焦点

                }else {
                    try {
                        Log.e("群聊发送","]]]]]]]]]]]]]]]]]]]]]]]]]]]");
                        if (sendmessageEdit.getText().toString().length() > 0){
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.icon);
                            list_message.add(new MessageBean(AppTemp.ChatTarget, sendmessageEdit.getText().toString(), GetTime.getDate(), "OUT", AppTemp.ChatTarget, bitmap, "YES", MyUserName));
                            MultiUserChat  muc = new MultiUserChat(con, AppTemp.ChatTarget+"@conference."+con.getServiceName());
                            muc.sendMessage(sendmessageEdit.getText().toString()+"|["+MyUserName+"]"+"|["+getDate()+"]");
                            chatAdapter.notifyDataSetChanged();
                            emojiLayout.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(Chat_Activity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                        }
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Chat_Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘
                        sendmessageEdit.setText("");  //发消息栏清空
                        sendmessageEdit.clearFocus(); //去除焦点

                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }

                }


                break;

            case R.id.emoji_switcher: //表情开关控制
                if (emojiLayout.getVisibility() == View.GONE) {
                    emojiLayout.setVisibility(View.VISIBLE);
                } else {
                    emojiLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.chat_checkfriend_message: //查看好友
                if (AppTemp.InToChatRoom == false) { //不是群聊
                    Intent intent = new Intent(Chat_Activity.this, Chat_FriendMessage.class);
                    intent.putExtra("Target", ChatTarget);
                    startActivityForResult(intent, 1);
                }else {

                    Intent intent = new Intent(Chat_Activity.this,Chat_ChatRoomMessage.class);
                    intent.putExtra("Target", ChatTarget);
                    startActivity(intent);
                 }


                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("更改","1111111111111111111");
        if (chat_background !=null ) {
            Log.e("更改","2222222222222222222");
            Drawable drawable = new BitmapDrawable(chat_background);
            chatBackgroundLayout.setBackground(drawable);

            ChatBackGround_list.add(new Friend_ChatBackground(ChatTarget,chat_background));

        }


    }
}
