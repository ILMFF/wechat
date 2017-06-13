package weixin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.Chat_ChatRoomInvite;
import weixin.bean.ChatRoom_Msg;

import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.RoomMember_list;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.muc_map;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class Chat_ChatRoomMsgAdapter extends BaseAdapter {
    List<ChatRoom_Msg> list;
    Context context;
    String owner;//群主
    String target;

    public Chat_ChatRoomMsgAdapter(List<ChatRoom_Msg> list, Context context, String owner, String target) {
        this.list = list;
        this.context = context;
        this.owner = owner;
        this.target = target;
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View converview, ViewGroup viewGroup) {

        ChatRoomViewHolder holder = null;
        if (converview == null) {

            converview = LayoutInflater.from(context).inflate(R.layout.chat_chatroommsg_gridlist, null);
            holder = new ChatRoomViewHolder(converview);
            converview.setTag(holder);
        } else {

            holder = (ChatRoomViewHolder) converview.getTag();
        }


        if (position == list.size() ) { //只有群主有邀请权力

            if ( owner.equals(MyUserName)) {
                holder.chatroomMemberNickname.setText("");
                holder.chatroomMemberIcon.setImageResource(R.mipmap.chatroom_invite);
                holder.chatroomMemberIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, Chat_ChatRoomInvite.class);
                        context.startActivity(intent);

                    }
                });
            }else {
                holder.chatroomMemberIcon.setVisibility(View.GONE);
            }

        } else {
            Log.e("成员显示",list.get(position).name+"   "+list.get(position).nickname);
            holder.chatroomMemberNickname.setText(list.get(position).nickname);
            holder.chatroomMemberIcon.setImageBitmap(list.get(position).icon);

            holder.chatroomMemberLayout.setEnabled(false);
            holder.chatroomMemberLayout.setClickable(false);
            holder.chatroomMemberLayout.setFocusable(false);

            if (owner.equals(MyUserName)) { //群主有踢人的权力

                if (list.get(position).nickname.equals(MyNickName) == false) {

                    holder.chatroomMemberIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final AlertDialog.Builder exit_builder = new AlertDialog.Builder(context);
                            exit_builder.setMessage("是否把此人踢出该群").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {
                                        Log.e("踢人",list.get(position).name);
                                        MultiUserChat multiUserChat = muc_map.get(target);
                                        multiUserChat.kickParticipant(list.get(position).nickname, "");

                                        multiUserChat.revokeMembership(list.get(position).name+"@"+con.getServiceName()); //踢出成员表

                                        Presence subscription=new Presence(Presence.Type.values()[1]);
                                        subscription.setTo(list.get(position).name+"@"+con.getServiceName());
                                        con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                                        subscription.setProperty("RoomName",target);
                                        con.sendPacket(subscription);

                                        list.remove(position);
//                                        Log.e("来来来",list.size()+" ");
//                                        for (int j = 0; j < list.size() ; j++) {
//                                            Log.e("来了来了",list.get(j).name);
//                                        }
                                        notifyDataSetChanged();

                                    } catch (XMPPException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();  //关闭对话框
                                }
                            });
                            AlertDialog exit_dialog = exit_builder.create();
                            exit_dialog.show();

                        }
                    });
                }
            }


        }

        return converview;
    }

    class ChatRoomViewHolder {
        @BindView(R.id.chatroom_member_icon)
        ImageView chatroomMemberIcon;
        @BindView(R.id.chatroom_member_nickname)
        TextView chatroomMemberNickname;
        @BindView(R.id.chatroom_member_layout)
        RelativeLayout chatroomMemberLayout;

        ChatRoomViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
