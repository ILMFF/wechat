package title.adapter;

import android.content.Context;
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
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;
import title.bean.SearchSession;
import weixin.Chat_FriendMessage;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SearchRecycleAdapter extends RecyclerView.Adapter<SearchRecycleAdapter.ViewHold> {


    List<SearchSession> list;
    Context context;



    public SearchRecycleAdapter(List<SearchSession> list ,Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlist, null);

        ViewHold hold = new ViewHold(inflate);

        return hold;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onBindViewHolder(final ViewHold holder, final int position) {


            holder.searchFriendIcon.setImageBitmap(list.get(position).friend_icon);
            holder.searchFriendNickname.setText(list.get(position).nickname);
            holder.searchFriendDescription.setText(list.get(position).description);

            for (int i = 0; i < ContactPersonList.size(); i++) {

                if (ContactPersonList.get(i).equals(list.get(position).username)) {  //确定是否已经是好友

                    holder.searchFriendAdd.setVisibility(View.GONE);
                    holder.searchFriendCheckornot.setVisibility(View.VISIBLE);

                }

            }


        holder.searchFriendAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送添加好友请求，订阅对方为好友，让其添加进自己的roster
                Log.e("发送好友请求", list.get(position).username + "@" + con.getServiceName());
                Presence subscription = new Presence(Presence.Type.subscribe);
                con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
                subscription.setTo(list.get(position).username + "@" + con.getServiceName());
                subscription.setProperty("return", "NO");
                con.sendPacket(subscription);


                holder.searchFriendAdd.setVisibility(View.GONE);
                holder.searchFriendAlreadysend.setVisibility(View.VISIBLE);

            }
        });


        //查看搜索目标的个人信息
        holder.searchFriendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_FriendMessage.class);
                intent.putExtra("Target",list.get(position).username);
                intent.putExtra("Adding_check",true);
                context.startActivity(intent);

            }
        });

    }


    public class ViewHold extends RecyclerView.ViewHolder {

        @BindView(R.id.search_friend_icon)
        ImageView searchFriendIcon;
        @BindView(R.id.search_friend_nickname)
        TextView searchFriendNickname;
        @BindView(R.id.search_friend_description)
        TextView searchFriendDescription;
        @BindView(R.id.search_friend_add)
        Button searchFriendAdd;
        @BindView(R.id.search_friend_layout)
        RelativeLayout searchFriendLayout;
        @BindView(R.id.search_friend_alreadysend)
        Button searchFriendAlreadysend;
        @BindView(R.id.search_friend_checkornot)
        Button searchFriendCheckornot;


        public ViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
