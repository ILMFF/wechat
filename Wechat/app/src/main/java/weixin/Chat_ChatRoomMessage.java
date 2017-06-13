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
import android.widget.Button;
import android.widget.GridView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.packet.VCard;

import java.util.Collection;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.Chat_ChatRoomMsgAdapter;
import weixin.bean.ChatRoom_Msg;

import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.RoomMember_list;
import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.messageListAdapter;
import static Xmpp.AppTemp.muc_map;
import static Xmpp.GetRealName.getRealName;
import static contact.ContactFragment_chattogether.roomAdapter;
import static weixin.Chat_Activity.Chat_Activity_context;
import static weixin.Chat_Activity.chatAdapter;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class Chat_ChatRoomMessage extends Activity {
    @BindView(R.id.chatroom_memberlist)
    GridView chatroomMemberlist;
    @BindView(R.id.chatroom_exit)
    Button chatroomExit;
    String target = null;
    MultiUserChat muc;
    public static Chat_ChatRoomMsgAdapter chat_chatRoomMsgAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_chatroommessage);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        RoomMember_list.clear();
        VCard card = new VCard();
        Intent intent = getIntent();
        target = intent.getStringExtra("Target");
        muc = new MultiUserChat(con, target + "@conference." + con.getServiceName());

        try {
            Collection<Affiliate> admins = muc.getOwners();
            Iterator<Affiliate> iterator1 = admins.iterator();
            while (iterator1.hasNext()) {
                Affiliate next = iterator1.next();
                card.load(con, next.getJid());
                String realName = getRealName(next.getJid());
                byte[] avatar = card.getAvatar();
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                RoomMember_list.add(new ChatRoom_Msg(realName, card.getNickName(), bitmap));
                Log.e("群主信息", next.getJid() + "   " + next.getAffiliation() + "    " + next.getNick() + "    " + next.getRole());
            }

            Collection<Affiliate> members = muc.getMembers();
            Iterator<Affiliate> iterator2 = members.iterator();
            while (iterator2.hasNext()) {

                Affiliate next = iterator2.next();
                card.load(con, next.getJid());
                String realName = getRealName(next.getJid());
                byte[] avatar = card.getAvatar();
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                RoomMember_list.add(new ChatRoom_Msg(realName, card.getNickName(), bitmap));
                Log.e("群成员信息", next.getJid() + "   " + next.getAffiliation() + "    " + next.getNick() + "    " + next.getRole());

            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        chat_chatRoomMsgAdapter = new Chat_ChatRoomMsgAdapter(RoomMember_list, this, RoomMember_list.get(0).name,target);
        chatroomMemberlist.setAdapter(chat_chatRoomMsgAdapter);

    }

    @OnClick({R.id.chat_chatroom_back_btn, R.id.chatroom_check_chatrecord, R.id.chatroom_clear_chatrecord, R.id.chatroom_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_chatroom_back_btn:
                finish();
                break;

            case R.id.chatroom_check_chatrecord:
                Intent intent_chatrecord = new Intent(Chat_ChatRoomMessage.this, Chat_CheckChatRecord.class);
                startActivity(intent_chatrecord);

                break;
            case R.id.chatroom_clear_chatrecord:
                final AlertDialog.Builder exit_builder = new AlertDialog.Builder(this);
                exit_builder.setMessage("是否清空与该群的聊天记录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

            case R.id.chatroom_exit:


                    final AlertDialog.Builder destroy_builder = new AlertDialog.Builder(this);
                    destroy_builder.setMessage("是否删除并退出此群").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                           /* try {

                                muc.destroy(target,"解散"+target);
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }*/
                            MultiUserChat multiUserChat = muc_map.get(target);
//                            Log.e("multiUserChat",multiUserChat.getRoom());
                            String Owner = null;
                            try {
                                Collection<Affiliate> owners = multiUserChat.getOwners();
                                Iterator<Affiliate> iterator = owners.iterator();
                                while (iterator.hasNext()){
                                    Owner = getRealName(iterator.next().getJid());
                                }
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                            Log.e("Owner",Owner);
                            try {

                                if (Owner.equals(MyUserName)){ //群主要把权力转让给下一个人

                                    if (RoomMember_list.size() > 1) {  //有两个人的，就群主让权,只有群主的话就删掉群
                                        multiUserChat.grantOwnership(RoomMember_list.get(1).name + "@" + con.getServiceName());
                                        multiUserChat.revokeOwnership(MyUserName + "@" + con.getServiceName());
                                        multiUserChat.leave();
                                    }else {
                                        multiUserChat.destroy(target,"解散"+target);
                                    }

                                }else {
                                    multiUserChat.leave();
                                    Presence subscription=new Presence(Presence.Type.values()[0]);
                                    subscription.setTo(Owner+"@"+con.getServiceName());
                                    con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                                    subscription.setProperty("roomname",target);
                                    con.sendPacket(subscription);
                                }


                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                            boolean remove = RoomName_List.remove(target + "@conference." + con.getServiceName());
                            if (remove && roomAdapter!=null) {
                                roomAdapter.notifyDataSetChanged();
                            }
                            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                            alterDataBase.DeleteChatRooms(target+"@conference."+con.getServiceName());
                            alterDataBase.DeleteOneRecord(target);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            messageListAdapter.notifyDataSetChanged();
                            finish();

                            ((Activity)Chat_Activity_context).finish();
                            ChatTarget = "none";

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();  //关闭对话框
                        }
                    });
                    AlertDialog destroy_dialog = destroy_builder.create();
                    destroy_dialog.show();


                break;
        }
    }


}
