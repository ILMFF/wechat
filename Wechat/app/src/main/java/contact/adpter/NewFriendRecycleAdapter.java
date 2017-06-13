package contact.adpter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Xmpp.GetRealName;
import butterknife.BindView;
import butterknife.ButterKnife;
import contact.bean.RequestBean;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.requestlist;
import static Xmpp.GetRealName.getRealName;
import static contact.ContactFragment_newfriends.newFriendRecycleRequestAdapter;

/**
 * Created by Administrator on 2017/3/24.
 */

public class NewFriendRecycleAdapter extends RecyclerView.Adapter<NewFriendRecycleAdapter.ViewHold> {

    List<RequestBean> list;



    public NewFriendRecycleAdapter(List<RequestBean> list) {
        this.list = list;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactfragment_requestlist, null);

        ViewHold hold = new ViewHold(inflate);

        return hold;
    }

    @Override
    public void onBindViewHolder(ViewHold holder, final int position) {

        switch (list.get(position).type) {

            case "add":    //请求添加好友广播

                holder.contactfragmentRequestIcon.setImageBitmap(list.get(position).searchSession.friend_icon);
                holder.contactfragmentRequestBz.setText(list.get(position).searchSession.description);
                holder.contactfragmentRequestNickname.setText(list.get(position).searchSession.nickname);
                break;

            case "sure_add":  //这是返回信息只需要列举出来即可

                holder.contactfragmentRequestIcon.setImageBitmap(list.get(position).searchSession.friend_icon);
                holder.contactfragmentRequestBz.setText(list.get(position).searchSession.description);
                holder.contactfragmentRequestNickname.setText(list.get(position).searchSession.nickname);
                holder.contactfragmentRequestaddBtn.setVisibility(View.INVISIBLE);  //把接受按钮消失掉
                holder.contactfragmentRequestRefusebtn.setVisibility(View.INVISIBLE);  //拒绝按钮消失


                break;

            case "refuse":  //这是对方拒绝后返回的消息

                holder.contactfragmentRequestIcon.setImageBitmap(list.get(position).searchSession.friend_icon);
                holder.contactfragmentRequestBz.setText(list.get(position).searchSession.description);
                holder.contactfragmentRequestNickname.setText(list.get(position).searchSession.nickname);
                holder.contactfragmentRequestaddBtn.setVisibility(View.INVISIBLE);  //把接受按钮消失掉
                holder.contactfragmentRequestRefusebtn.setVisibility(View.INVISIBLE);  //拒绝按钮消失

                break;

            case "delete": //显示删除信息

                holder.contactfragmentRequestIcon.setImageBitmap(list.get(position).searchSession.friend_icon);
                holder.contactfragmentRequestBz.setText(list.get(position).searchSession.description);
                holder.contactfragmentRequestNickname.setText(list.get(position).searchSession.nickname);
                holder.contactfragmentRequestaddBtn.setVisibility(View.INVISIBLE);  //把接受按钮消失掉
                holder.contactfragmentRequestRefusebtn.setVisibility(View.INVISIBLE);  //拒绝按钮消失

                break;

        }

        //点击同意按钮事件
        holder.contactfragmentRequestaddBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击同意添加后，发回一条同意通知

                Presence subscription=new Presence(Presence.Type.subscribed);
                subscription.setTo(list.get(position).searchSession.username+"@"+con.getServiceName());
                con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                subscription.setProperty("autoadd","NO");
                con.sendPacket(subscription);


                //同意后，这边也要发一条添加好友信息，让双方变成共享好友both状态
                Presence subscriptionAG=new Presence(Presence.Type.subscribe);
                subscriptionAG.setTo(list.get(position).searchSession.username+"@"+con.getServiceName());
                subscriptionAG.setProperty("return","YES");
                con.sendPacket(subscriptionAG);



                Log.e("请求Request.size",list.size()+"             "+position);

                //点击同意后删除好友请求显示

                for (int i = 0; i < requestlist.size() ; i++) {

                   if (requestlist.get(i).searchSession.username.equals(list.get(position).searchSession.username)){

                       requestlist.remove(i); //删除
                       newFriendRecycleRequestAdapter.notifyDataSetChanged();//请求列表刷新

                       break;
                   }

                }


                Log.e("同意好友请求","==========================");

            }

        });




        //点击拒绝按钮事件
        holder.contactfragmentRequestRefusebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送拒绝请求
                Presence subscription=new Presence(Presence.Type.unsubscribed);
                subscription.setTo(list.get(position).searchSession.username+"@"+con.getServiceName());
                con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                subscription.setProperty("autorefuse","NO");
                con.sendPacket(subscription);


                //有问题 获取不了订阅表，为0
                //把相关订阅列表删除
                Collection<RosterEntry> roscol = con.getRoster().getEntries();
                Iterator<RosterEntry> iterator = roscol.iterator();
                Log.e("roscol",roscol.size()+"  ");
                while (iterator.hasNext()) {
                    RosterEntry entry = iterator.next();
                    String realName = GetRealName.getRealName(entry.getUser());
                    if (list.get(position).searchSession.username.equals(realName)) {
                        Log.e("删除",list.get(position).searchSession.username+"    "+realName);
                        try {
                            con.getRoster().removeEntry(entry);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }

                    }
                }





                //点击拒绝后删除好友请求显示

                for (int i = 0; i < requestlist.size() ; i++) {

                    if (requestlist.get(i).searchSession.username.equals(list.get(position).searchSession.username)){

                        requestlist.remove(i); //删除
                        newFriendRecycleRequestAdapter.notifyDataSetChanged();//请求列表刷新

                        break;
                    }

                }

                Log.e("拒绝好友请求","==========================");

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size() == 0 || list == null ? 0 : list.size();
    }

    public class ViewHold extends RecyclerView.ViewHolder {

        @BindView(R.id.contactfragment_request_icon)
        ImageView contactfragmentRequestIcon;
        @BindView(R.id.contactfragment_request_nickname)
        TextView contactfragmentRequestNickname;
        @BindView(R.id.contactfragment_request_bz)
        TextView contactfragmentRequestBz;
        @BindView(R.id.contactfragment_request_addbtn)
        Button contactfragmentRequestaddBtn;
        @BindView(R.id.contactfragment_request_refusebtn)
        Button contactfragmentRequestRefusebtn;
        @BindView(R.id.search_friend_layout)
        RelativeLayout searchFriendLayout;

        public ViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
