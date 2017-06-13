package contact.adpter;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Xmpp.AppTemp;
import Xmpp.GetALLFriend;
import Xmpp.GetRealName;
import dao.AlterDataBase;
import weixin.Chat_Activity;
import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.newFriendRecycleAdapter;
import static Xmpp.AppTemp.onlinemsg_list;
import static Xmpp.GetRealName.getRealName;
import static Xmpp.XmppTool.openConnection;
import static teacher.davisstore.com.wechat.Login_main.password;

/**
 * Created by Administrator on 2017/2/22.
 */

public class UserFriendRecycleAdapter extends RecyclerView.Adapter<UserFriendRecycleAdapter.FriendViewHold>  {

    Context context;
    VCard card = new VCard();

    public UserFriendRecycleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FriendViewHold onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactfragment_friendlist, null);
        FriendViewHold hold = new FriendViewHold(inflate);

        return hold;
    }

    @Override
    public void onBindViewHolder(FriendViewHold holder, final int position) {


//        for (int i = 0; i < list.size(); i++) {
//
//            Log.e("当前好友列表: ",list.get(i));
//
//        }


        try {

            card.load(con,ContactPersonList.get(position)+"@"+con.getServiceName());

        } catch (XMPPException e) {
            e.printStackTrace();
        }


        Bitmap bitmap =  getICon(card);


        holder.contactFrendText.setText(card.getNickName());

        if (bitmap != null) {
            holder.contactFrendImg.setImageBitmap(bitmap);
        }else{
            holder.contactFrendImg.setImageResource(R.mipmap.icon);
        }



        //点击进入目标的聊天界面 ，单击
        holder.contactFriendlistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_Activity.class);
                AppTemp.ChatTarget = ContactPersonList.get(position);
//                Log.e("ChatTarget_contact",AppTemp.ChatTarget);


                ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
                VCard card = new VCard();
                try {
                    card.load(con,AppTemp.ChatTarget+"@"+con.getServiceName());
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                AppTemp.ChatTargetNickName = card.getNickName();


                context.startActivity(intent);
            }
        });

        //长按删除好友
        holder.contactFriendlistLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


//----------------------------点击删除好友双方自动删除,并把订阅表删除，对方无需同意
                final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(context);
                deletefriend_builder.setPositiveButton("删除好友", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(context,"删除好友",Toast.LENGTH_SHORT).show();
                        //取消我对他订阅
                        Presence subscription=new Presence(Presence.Type.unsubscribe);
                        subscription.setTo(ContactPersonList.get(position)+"@"+con.getServiceName());
                        con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                        subscription.setProperty("autodelete","YES");
                        con.sendPacket(subscription);


                        //----------------------------以下注释是点击删除好友双方自动删除，对方无需同意

                       //取消他对我的订阅
                        Presence re_subscription=new Presence(Presence.Type.unsubscribed);
                        re_subscription.setTo(ContactPersonList.get(position)+"@"+con.getServiceName());
                        con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                        re_subscription.setProperty("autorefuse","NO");
                        re_subscription.setProperty("description","delete");
                        con.sendPacket(re_subscription);


                        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                        alterDataBase.DeleteOneRecord(ContactPersonList.get(position)); //清空相关记录
                        Intent intent = new Intent("com.broadcast.message");
                        context.sendBroadcast(intent);

                        Log.e("删除未读消息",onlinemsg_list.size()+"  ");
                        for (int j = 0; j <onlinemsg_list.size() ; j++) {
                            Log.e("消息显示先试试",getRealName(onlinemsg_list.get(j).name)+"    "+ContactPersonList.get(position)+"  "+onlinemsg_list.get(j).message+"  "+getRealName(onlinemsg_list.get(j).name).equals(ContactPersonList.get(position)));
                            if (getRealName(onlinemsg_list.get(j).name).equals(ContactPersonList.get(position)))
                                onlinemsg_list.remove(j);
                        }
                        Log.e("删除未读消息后",onlinemsg_list.size()+"  ");
                        ContactPersonList.remove(ContactPersonList.get(position));
                        notifyDataSetChanged(); //刷新好友列表

                    }
                });
                AlertDialog deletefriend_dialog = deletefriend_builder.create();
                deletefriend_dialog.show();



                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
//        list = ContactPersonList;
//        Log.e("当前好友长度",ContactPersonList.size()+"   ");
        return ContactPersonList.size() > 0 ? ContactPersonList.size() : 0;
    }




    public Bitmap getICon(VCard card) {


        byte[] avatar = card.getAvatar();

        if (BitmapFactory.decodeByteArray(avatar,0,avatar.length) != null){
            return BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        }else {

            return null;
        }
    }



    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_SHORT).show();
            String target = intent.getStringExtra("target");
            Log.e("接收到了好友删除的广播了e",target);
            ContactPersonList.remove(target);
            Log.e("Remove1111",ContactPersonList.size()+"");
            newFriendRecycleAdapter.notifyDataSetChanged(); //刷新好友列表
        }
    }



    class FriendViewHold extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_frend_img)
        ImageView contactFrendImg;
        @BindView(R.id.contact_frend_text)
        TextView contactFrendText;
        @BindView(R.id.contact_friendlist_layout)
        RelativeLayout contactFriendlistLayout;

        public FriendViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
