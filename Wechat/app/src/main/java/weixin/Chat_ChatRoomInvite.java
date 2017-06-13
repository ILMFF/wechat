package weixin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;
import title.ChatInvitation;
import title.adapter.InvitationAdapter;
import weixin.bean.ChatRoom_Msg;

import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.InToChatRoom;
import static Xmpp.AppTemp.Invitation_list;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.RoomMember_list;
import static Xmpp.AppTemp.con;
import static weixin.Chat_ChatRoomMessage.chat_chatRoomMsgAdapter;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class Chat_ChatRoomInvite extends Activity {
    @BindView(R.id.invitation_layout)
    RelativeLayout invitationLayout;
    @BindView(R.id.invitation_titletext)
    TextView invitationTitletext;
    @BindView(R.id.invitation_list)
    RecyclerView invitationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattogether_invitation);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        List<String> Invitelist = new ArrayList<>();

        boolean flag = true;
        String target = null;
        //已经是成员就没必要进入邀请目标
        for (int i = 0; i < ContactPersonList.size() ; i++) {
            target = ContactPersonList.get(i);
            for (int j = 0; j < RoomMember_list.size() ; j++) {

//                Log.e("比较接近",RoomMember_list.get(j).name+"     "+ContactPersonList.get(i));
                if (RoomMember_list.get(j).name.equals(ContactPersonList.get(i))){
                    flag = false;
                    break;
                }
            }
            if (flag){
                Invitelist.add(target);
                flag = true;
            }
        }

        for (int i = 0; i < Invitelist.size() ; i++) {

            Log.e("受邀目标",Invitelist.get(i));
        }


        InvitationAdapter invitationAdapter = new InvitationAdapter(this,Invitelist);
        invitationList.setLayoutManager(new LinearLayoutManager(this));
        invitationList.setAdapter(invitationAdapter);
    }

    @OnClick({R.id.invitation_btn_back, R.id.invitation_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invitation_btn_back:
                finish();
                break;
            case R.id.invitation_btn:
                if (Invitation_list.size() > 0) {
                    MultiUserChat muc = new MultiUserChat(con,ChatTarget+"@conference."+con.getServiceName());
                    invitation(muc);
                    finish();

                }else {
                    Toast.makeText(Chat_ChatRoomInvite.this,"至少邀请一个人",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void invitation(MultiUserChat muc){

        VCard card = new VCard();
        for (int i = 0; i < Invitation_list.size() ; i++) {
            try {
                card.load(con,Invitation_list.get(i)+"@"+con.getServiceName());
                byte[] avatar = card.getAvatar();
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                RoomMember_list.add(new ChatRoom_Msg(Invitation_list.get(i),card.getNickName(),bitmap));

            } catch (XMPPException e) {
                e.printStackTrace();
            }

            muc.invite(Invitation_list.get(i)+"@"+con.getServiceName(),MyNickName);
        }

        Invitation_list.clear(); //发送完以后清空
        chat_chatRoomMsgAdapter.notifyDataSetChanged();

    }
}
