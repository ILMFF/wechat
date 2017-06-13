package contact.adpter;

import android.content.Context;
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

import java.util.List;

import Xmpp.AppTemp;
import Xmpp.GetRealName;
import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;
import weixin.ChatTogether_Activity;
import weixin.Chat_Activity;

import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.ChatTargetNickName;
import static Xmpp.AppTemp.InToChatRoom;
import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.GetRealName.getRealName;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class ChatRoomAdapter extends BaseAdapter {

    Context context;

    public ChatRoomAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        Log.e("roomnamelist",RoomName_List.size()+"");
        return RoomName_List.size();
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
            converview = LayoutInflater.from(context).inflate(R.layout.contactfragment_chatroom_list, null);
            holder = new ChatRoomViewHolder(converview);
        } else {
            holder = (ChatRoomViewHolder) converview.getTag();
        }

        holder.contactChatroomImg.setImageResource(R.mipmap.icon);
        holder.contactChatroomText.setText(getRealName(RoomName_List.get(position)));

        holder.contactChatroomlistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InToChatRoom = true; //是否是群聊
                ChatTarget = getRealName(RoomName_List.get(position));
                ChatTargetNickName = getRealName(RoomName_List.get(position));
                Intent intent = new Intent(context, Chat_Activity.class);
                context.startActivity(intent);

            }
        });


        return converview;
    }

    class ChatRoomViewHolder {
        @BindView(R.id.contact_chatroom_img)
        ImageView contactChatroomImg;
        @BindView(R.id.contact_chatroom_text)
        TextView contactChatroomText;
        @BindView(R.id.contact_chatroomlist_layout)
        RelativeLayout contactChatroomlistLayout;

        ChatRoomViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
