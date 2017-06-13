package title;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Xmpp.AppTemp;
import Xmpp.GetRealName;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import dao.bean.NewsRecord;
import teacher.davisstore.com.wechat.R;
import title.adapter.InvitationAdapter;
import weixin.bean.Online_Message;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Invitation_list;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.muc_map;
import static Xmpp.GetBitmapToString.getBitmapToString;
import static Xmpp.GetRealName.getRealName;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public class ChatInvitation extends Activity {


    @BindView(R.id.invitation_list)
    RecyclerView invitationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattogether_invitation);
        ButterKnife.bind(this);

        InvitationAdapter invitationAdapter = new InvitationAdapter(this,ContactPersonList);
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
                    dialog();
                }else {
                    Toast.makeText(ChatInvitation.this,"至少邀请一个人",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void dialog() {

        final InvitationDialog dialog = new InvitationDialog(ChatInvitation.this);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomname = MyNickName+"_room";
                EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
                if (editText.getText().toString()!=null && editText.getText().toString().length() > 0 ){
                    roomname = editText.getText().toString();
                    MultiUserChat chatroom = create_chatroom(roomname);//创建聊天室
                    muc_map.put(roomname,chatroom);
                    invitation(chatroom);//发送邀请
                    RoomName_List.add(roomname + "@conference." + con.getServiceName());
                    final String finalRoomname = roomname;
                    chatroom.addMessageListener(new PacketListener() {
                        @Override
                        public void processPacket(Packet packet) {

                            Log.e("群聊信息","收到了"+packet.getFrom()+"的消息");
                            String sender = null;
                            String message_body = null;
                            String time = null;

                            Pattern pattern = Pattern.compile("(.+)\\|\\[(.+)\\]\\|\\[(.+)\\]");
                            Matcher matcher = pattern.matcher(((Message) packet).getBody());
                            while (matcher.find()) {
                                message_body = matcher.group(1);
                                sender = matcher.group(2);
                                time = matcher.group(3);
                            }
                            Log.e("当前群聊信息拆解",message_body+"    "+sender+"    "+time);
                            if (sender.equals(MyUserName) == false) { //不是自己发出来的信息
                                Log.e("进入消息处理","--------------------");
                                ChatTogetherListener(packet, finalRoomname + "@conference." + con.getServiceName(),message_body,sender,time);
                            }
                        }
                    });

                    finish();
                }else {
                    Toast.makeText(ChatInvitation.this,"群聊名字不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public MultiUserChat create_chatroom(String roomname){

        VCard card = new VCard();

        try {
            // 创建聊天室


            MultiUserChat muc = new MultiUserChat(con, roomname+"@conference."+con.getServiceName());
            muc.create(MyNickName); //用户在聊天室的名字

            // 获得聊天室的配置表单
            Form form = null;
            form = muc.getConfigurationForm();

            // 根据原始表单创建一个要提交的新表单。
            Form submitForm = form.createAnswerForm();
            // 向要提交的表单添加默认答复
            for (Iterator fields = form.getFields(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // 设置聊天室的新拥有者
            List owners = new ArrayList();
            owners.add(con.getUser());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_membersonly", true);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            // 进入是否需要密码
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  false);


            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            muc.sendConfigurationForm(submitForm);

            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
            alterDataBase.InsertChatRooms(roomname+"@conference."+con.getServiceName(),MyNickName); //把加入的聊天室加入数据库

            return muc;

        } catch (XMPPException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void invitation(MultiUserChat muc){

        for (int i = 0; i < Invitation_list.size() ; i++) {

            muc.invite(Invitation_list.get(i)+"@"+con.getServiceName(),MyNickName);
        }

        Invitation_list.clear(); //发送完以后清空

    }



    //监听群聊的信息处理--------------------------------------------------------------
    private void ChatTogetherListener(Packet packet, String roomName,String message_body,String sender,String time) {
        String target = null;


        Message message = (Message) packet;

        Log.e("llllllll","---------"+((Message) packet).getBody());

        Pattern pat = Pattern.compile("(.+)\\@.+\\/(.+)");
        Matcher mat = pat.matcher(packet.getFrom());
        while (mat.find()) {
            target = mat.group(1);
        }
        Log.e("发送者群聊",target+"            "+((Message) packet).getBody());



        boolean broadcast_flag = true;  //用于控制广播发送的位置


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.icon);
        AppTemp.onlinemsg_list.add(new Online_Message(roomName, message_body, time, target,bitmap,"YES",sender));


        boolean equals = "none".equals(AppTemp.ChatTarget);

        Log.e("聊天目标",AppTemp.ChatTarget+"           "+equals+"        群名:"+roomName);
        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        if(equals){  //没有目标聊天时
//            Log.e("kkkkkk","没有目标");
            String sendericon = getBitmapToString(bitmap);
            alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(roomName), target, sendericon, message_body, time, "IN","YES",sender));
        } else {

            if (AppTemp.ChatTarget.equals(target) == false) {
                //只有发来的信息与当前聊天目标不一致时，才添加进入未读消息表
//                Log.e("kkkkkk", "不一致");
                String sendericon = getBitmapToString(bitmap);
                alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(roomName),target, sendericon, message_body, time, "IN","YES",sender));

            } else {

                Log.e("发送当前目标广播", "-----------------------------------");
                broadcast_flag = false;
            }
        }


        if (broadcast_flag) {  //发送未读消息广播
            Log.e("发送未读消息广播","-------------------------");
            Intent intent = new Intent("com.broadcast.message");
            sendBroadcast(intent);

        }else { //发送在线聊天广播
            Log.e("发送在线聊天广播","---------------------------");
            Intent intent = new Intent("com.broadcast.DisplayMesaage");
            intent.putExtra("message_body", message_body);
            intent.putExtra("message_from", target);//群聊名字
            intent.putExtra("message_menber",sender); //群成员具体发送人
            intent.putExtra("chatroom_ornot",true);//是否是群聊目标
            intent.putExtra("time",time);

            sendBroadcast(intent);
        }
    }


}
