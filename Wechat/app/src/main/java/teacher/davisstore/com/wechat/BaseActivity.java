package teacher.davisstore.com.wechat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import Xmpp.AppTemp;
import Xmpp.GetALLFriend;
import Xmpp.GetRealName;
import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import contact.ContactFragment_main;
import contact.adpter.UserFriendRecycleAdapter;
import contact.bean.RequestBean;
import dao.AlterDataBase;
import dao.bean.NewsRecord;
import discover.DiscoverFragment_main;
import discover.adapter.FriendCircleAdapter;
import me.MeFragment_main;
import me.adapter.FavoriteAdapter;
import teacher.davisstore.com.wechat.adapter.ViewpageAdapter;
import teacher.davisstore.com.wechat.bean.ControlChatRoomMsg;
import title.AddNewFriend;
import title.ChatInvitation;
import title.adapter.PopmenuAdapter;
import title.bean.SearchSession;
import weixin.WeiXinFragment_main;
import weixin.bean.Online_Message;

import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MySex;
import static Xmpp.AppTemp.MySignature;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.AppTemp.chatManager;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.favorite_list;
import static Xmpp.AppTemp.messageListAdapter;
import static Xmpp.AppTemp.muc_map;
import static Xmpp.AppTemp.newFriendRecycleAdapter;
import static Xmpp.AppTemp.onlinemsg_list;
import static Xmpp.AppTemp.requestlist;
import static Xmpp.GetBitmapToString.getBitmapToString;
import static Xmpp.GetRealName.getRealName;
import static contact.ContactFragment_chattogether.roomAdapter;
import static weixin.Chat_Activity.Chat_Activity_context;


public class BaseActivity extends FragmentActivity implements AdapterView.OnItemClickListener, ChatManagerListener, ViewPager.OnPageChangeListener {


    @BindView(R.id.Popmenu_list)
    ListView PopmenuList;
    @BindView(R.id.Title_Popmenu)
    ImageView TitlePopmenu;
    @BindView(R.id.Title_Search)
    ImageView TitleSearch;
    @BindView(R.id.Title_Popmenu_layout)
    LinearLayout TitlePopmenuLayout;

    @BindView(R.id.wechat_img)
    ImageView wechatImg;
    @BindView(R.id.wechat_text)
    TextView wechatText;
    @BindView(R.id.bottom_wechat)
    RelativeLayout bottomWechat;
    @BindView(R.id.contact_img)
    ImageView contactImg;
    @BindView(R.id.contact_text)
    TextView contactText;
    @BindView(R.id.bottom_contact)
    RelativeLayout bottomContact;
    @BindView(R.id.discover_img)
    ImageView discoverImg;
    @BindView(R.id.discover_text)
    TextView discoverText;
    @BindView(R.id.bottom_discover)
    RelativeLayout bottomDiscover;
    @BindView(R.id.myself_img)
    ImageView myselfImg;
    @BindView(R.id.myself_text)
    TextView myselfText;
    @BindView(R.id.bottom_myself)
    RelativeLayout bottomMyself;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.wechat_tip_point)
    TextView wechatTipPoint;
    int[] popmenu_img = {R.mipmap.people_chat, R.mipmap.addfriend, R.mipmap.saoyisao};
    String[] popmenu_text = {"发起群聊", "添加好友"};
    @BindView(R.id.replace_viewpage)
    ViewPager replaceViewpage;
    Roster roster;
    public static FavoriteAdapter favoriteadapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();  //用于检查当前用户,用于获取对应用户的资料和其好友列表,还有初始化,启动消息监听
        PopmenuList.setAdapter(new PopmenuAdapter(popmenu_text, popmenu_img, this));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.replace_layout, new WeiXinFragment_main());
        transaction.commit();
        wechatImg.setImageResource(R.mipmap.weixin_pressed);
        wechatText.setTextColor(getResources().getColor(R.color.green));

        PopmenuList.setOnItemClickListener(this);
        replaceViewpage.setOnPageChangeListener(this);



    }

    private void init() {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("UserName");  //获取当前登录的用户
        String nickName = intent.getStringExtra("NickName");
        MyNickName = nickName;
        MyUserName = userName;
        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
        VCard card = new VCard();
        try {

            card.load(con,MyUserName+"@"+con.getServiceName());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        byte[] avatar = card.getAvatar();
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        MyIcon = bitmap;
        MySex =  card.getFirstName();
        if (MySex == null || MySex.length() <= 0){
            MySex = "未填写";
        }
        MySignature = card.getLastName();
        if (MySignature == null || MySignature.length() <= 0){
            MySignature = "未填写";
        }

        Log.e("性别和签名",MySex+"          "+MySignature);


        Toast.makeText(BaseActivity.this, "当前用户为: " + userName + "    昵称为: " + nickName, Toast.LENGTH_SHORT).show();

        chatManager = XmppTool.getConnection().getChatManager();
        chatManager.addChatListener(this);  //消息监听启动

        addSubscriptionListener(); //好友请求监听启动

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new WeiXinFragment_main());
        fragmentList.add(new ContactFragment_main());
        fragmentList.add(new DiscoverFragment_main());
        fragmentList.add(new MeFragment_main());
        ViewpageAdapter adapter = new ViewpageAdapter(getSupportFragmentManager(), fragmentList);
        replaceViewpage.setAdapter(adapter);


        roster = con.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);  //将处理请求模式换成换成手动处理


        favoriteadapter = new FavoriteAdapter(favorite_list,this);


//        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
        ContactPersonList = new ArrayList<>();
        newFriendRecycleAdapter = new UserFriendRecycleAdapter(this);
        //获取联系人
        List<RosterEntry> allFriends = GetALLFriend.getAllFriends();
        Iterator<RosterEntry> iterator = allFriends.iterator();
        while (iterator.hasNext()) {

            ContactPersonList.add(GetRealName.getRealName(iterator.next().getUser()));
        }


        JoinChatRoom(); //重新加入曾经已经加入过的聊天室



        //监听群聊邀请
        MultiUserChat.addInvitationListener(con,
                new InvitationListener() {
                    // 对应参数：连接、 房间JID、房间名、附带内容、密码、消息
                    @Override
                    public void invitationReceived(Connection conn,
                                                   final String room, String inviter, String reason,
                                                   String password, Message message) {

                        MultiUserChat muc = null;
                        Log.e("收到聊天室邀请", "收到来自 " + inviter + " 的聊天室邀请。邀请附带内容："
                                + reason+"   房名是"+room+"     "+message);

                        boolean flag = true;
                        for (int i = 0; i <RoomName_List.size() ; i++) {
                            if (RoomName_List.get(i).equals(room)){
                                flag = false;
                                break;
                            }
                        }
                        if (flag){
                            RoomName_List.add(room);
                            muc = new MultiUserChat(con, room);
                            try {
                                muc.join(MyNickName);
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                            muc_map.put(getRealName(room),muc);
                        }

                            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                            alterDataBase.InsertChatRooms(room,MyNickName); //保存进数据库

                            muc.addMessageListener(new PacketListener() {
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
                                        ChatTogetherListener(packet, room,message_body,sender,time);
                                    }

                                }
                            });

                    }
                });





       /* if (AppTemp.onlinemsg_list.size() > 0) {
            if (wechatTipPoint.getVisibility() == View.GONE) {
                Log.e("总消息", "开始显示");
                wechatTipPoint.setVisibility(View.VISIBLE);
            }
            if (AppTemp.onlinemsg_list.size() < 99) {
                Log.e("总消息","设置1");
                wechatTipPoint.setText(AppTemp.onlinemsg_list.size() + "");
            }
            else {
                Log.e("总消息","设置2");
                wechatTipPoint.setText("99+");
            }
        }else {
            Log.e("总消息","开始消失");
            if (wechatTipPoint.getVisibility() == View.VISIBLE) {
                Log.e("总消息","进入消失状态");
                wechatTipPoint.setVisibility(View.GONE);
            }
        }*/


    }

    private void JoinChatRoom() {
        List<ControlChatRoomMsg> controlChatRoomMsgs = new ArrayList<>();

        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        Cursor cursor = alterDataBase.GetQueryChatRooms();
        MultiUserChat muc = null;
        if (cursor.moveToFirst()){
            do {

                final String roomName = cursor.getString(cursor.getColumnIndex("RoomName"));
                String roomNickName = cursor.getString(cursor.getColumnIndex("RoomNickName"));
                Log.e("roomname",roomName);
                boolean flag = true;
                for (int i = 0; i <RoomName_List.size() ; i++) {
                    if (RoomName_List.get(i).equals(roomName)){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    RoomName_List.add(roomName);
                    muc = new MultiUserChat(con, roomName);
                    try {
                        muc.join(roomNickName);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    muc_map.put(getRealName(roomName),muc);
                }


                controlChatRoomMsgs =  ControlMsgPack();



                if (roomName != null && roomNickName != null ){

                    final List<ControlChatRoomMsg> finalControlChatRoomMsgs = controlChatRoomMsgs;
                    muc.addMessageListener(new PacketListener() {
                            @Override
                            public void processPacket(Packet packet) {
                                //该接收器本身问题，只要发送过一次就会每次上线都发送，可能是服务器问题

                                    boolean flag = true; //控制冗余信息开关
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


                                for (int i = 0; i < finalControlChatRoomMsgs.size() ; i++) {

                                    if (finalControlChatRoomMsgs.get(i).Name.equals(getRealName(packet.getFrom())) && finalControlChatRoomMsgs.get(i).Time.equals(time) ){
                                            flag = false;
                                            break;
                                    } //找到重复的就不处理
                                }

                                if (flag){
                                    if (sender.equals(MyUserName) == false) { //不是自己发出来的信息
                                        Log.e("小心进入处理","----------");
                                        ChatTogetherListener(packet, roomName,message_body,sender,time);
                                    }
                                }

                            }
                        });


                }
            }while (cursor.moveToNext());
        }
    }

    private List<ControlChatRoomMsg> ControlMsgPack() {
        List<ControlChatRoomMsg> controlChatRoomMsgs = new ArrayList<>();
        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        Cursor cursor1 = alterDataBase.QueryAllRecord();
        if (cursor1.moveToFirst()) {

            do {
                String SenderName = cursor1.getString(cursor1.getColumnIndex("SenderName"));
                String CreateTime = cursor1.getString(cursor1.getColumnIndex("CreateTime"));
                controlChatRoomMsgs.add(new ControlChatRoomMsg(SenderName,CreateTime));

            } while (cursor1.moveToNext());
        }
        cursor1.close();
        Cursor cursor2 = alterDataBase.GetAllNewsRecord();
        if (cursor2.moveToFirst()) {

            do {
                String SenderName = cursor2.getString(cursor2.getColumnIndex("SenderName"));
                String CreateTime = cursor2.getString(cursor2.getColumnIndex("CreateTime"));
                controlChatRoomMsgs.add(new ControlChatRoomMsg(SenderName,CreateTime));
            } while (cursor2.moveToNext());
        }
        cursor2.close();

        return controlChatRoomMsgs;
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
        Log.e("发送者群聊",target+"            "+((Message) packet).getBody()+"   "+roomName+"  "+getRealName(roomName));



        boolean broadcast_flag = true;  //用于控制广播发送的位置


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.icon);
        AppTemp.onlinemsg_list.add(new Online_Message(roomName, message_body, time, target,bitmap,"YES",sender));


        boolean equals = "none".equals(AppTemp.ChatTarget);

        Log.e("聊天目标",AppTemp.ChatTarget+"           "+equals);
        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        if(equals){  //没有目标聊天时
            Log.e("kkkkkk","没有目标");
            String sendericon = getBitmapToString(bitmap);
            alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(roomName), target, sendericon, message_body, time, "IN","YES",sender));
        } else {

            if (AppTemp.ChatTarget.equals(target) == false) {
                //只有发来的信息与当前聊天目标不一致时，才添加进入未读消息表
                Log.e("kkkkkk", "不一致");
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








    //监听单人聊天的信息处理--------------------------------------------------------------
    @Override
    public void chatCreated(Chat chat, boolean b) {
        chat.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if (message.getBody() != null) {
                    String sender = message.getFrom();

                    Log.e("OtherSenders", "接收到来自好友 " + sender + " ：信息     " + message.getBody());
                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
                    VCard card = new VCard();
                    try {
                        card.load(XmppTool.getConnection(), getRealName(sender) + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
//                  Log.e("nickname",card.getNickName()+"ssssssssss   "+getRealName(sender)+"@"+con.getServiceName());
                    byte[] avatar = card.getAvatar();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    AppTemp.onlinemsg_list.add(new Online_Message(sender, message.getBody(), (String)message.getProperty("time"), card.getNickName(),bitmap,"NO",getRealName(sender)));
                    //有新的未读消息刷新微信列表
                    Log.e("当前onlinemsg_list", onlinemsg_list.size()+"  ");
//                    String realName = getRealName(AppTemp.ChatTarget);
//                    String Sender = getRealName(sender);
                    //正在进行对话的时候，信息就不需要再加入未读聊天表

                    boolean flag = true;  //用于控制广播发送的位置

                    boolean equals = "none".equals(AppTemp.ChatTarget);

                    Log.e("聊天目标",AppTemp.ChatTarget+"           "+equals);
                    AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                    if(equals){  //没有目标聊天时
                        Log.e("kkkkkk","没有目标");
                        String sendericon = getBitmapToString(bitmap);

                        alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(sender), card.getNickName(), sendericon, message.getBody(), (String) message.getProperty("time"), "IN","NO",getRealName(sender)));
                    } else {


                        if (AppTemp.ChatTarget.equals(getRealName(sender)) == false) {
                            //只有发来的信息与当前聊天目标不一致时，才添加进入未读消息表
                            Log.e("kkkkkk","不一致");
                            String sendericon = getBitmapToString(bitmap);
                            alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(sender), card.getNickName(), sendericon, message.getBody(), (String) message.getProperty("time"), "IN","NO",getRealName(sender)));

                        }else {

                            Log.e("发送当前目标广播","-----------------------------------");
                            flag = false;
                        }

                    }






                    if (flag) {  //发送未读消息广播
                        Log.e("发送未读消息广播","-------------------------");
                        Intent intent = new Intent("com.broadcast.message");
                        intent.putExtra("message", message.getBody());
                        intent.putExtra("time", (String) message.getProperty("time"));
                        sendBroadcast(intent);

                    }else { //发送在线聊天广播
                        Log.e("发送在线聊天广播","---------------------------"+getRealName(sender));
                        Intent intent = new Intent("com.broadcast.DisplayMesaage");
                        intent.putExtra("message_body", message.getBody());
                        intent.putExtra("message_from", getRealName(message.getFrom()));
                        intent.putExtra("message_menber",getRealName(sender));
                        intent.putExtra("chatroom_ornot",false);//是否是群聊目标
                        intent.putExtra("time",(String)message.getProperty("time"));
                        sendBroadcast(intent);
                    }


                }
            }
        });
    }



   /* available: 表示处于在线状态
    unavailable: 表示处于离线状态
    subscribe: 表示发出添加好友的申请
    unsubscribe: 表示发出删除好友的申请
    unsubscribed: 表示拒绝添加对方为好友
    error: 表示presence信息报中包含了一个错误消息。
    */

    //好友请求监听
    private void addSubscriptionListener() {
        final PacketFilter filter = new PacketFilter() {   //请求过滤器
            @Override
            public boolean accept(Packet packet) {
//                Log.e("接收到请求",packet.getFrom());

               /* if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType().equals(Presence.Type.subscribe)) {

                        Log.e("接收到好友请求","--------------------------------");
                        return true;
                    }
                }*/
                if (packet instanceof Presence){
                    Presence presence = (Presence) packet;
                    if (getRealName(presence.getFrom()).equals(MyUserName) == false) //不是自己发出的请求
                    {
                        return true;
                    }
                }

                return false;

            }
        };
        con.addPacketListener(subscriptionPacketListener, filter);

    }
    private PacketListener subscriptionPacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {


                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
//                    Log.e("packet","++++++++++++++++++++++++++++++++=");
                    String username = getRealName(presence.getFrom());
                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
                    VCard card = new VCard();
                    try {
                        card.load(con, username + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    String nickName = card.getNickName();


                    if (presence.getType().equals(Presence.Type.subscribe)) { //有好友申请的消息

                        String aReturn = (String) presence.getProperty("return");
                        Log.e("aReturn", aReturn);

                        if (aReturn.equals("NO")) {
//                            Log.e("收到有好友请求", "-----------------------");
                            SearchSession search = new SearchSession(username, nickName, bitmap, "对方请求添加你为好友");
                            requestlist.add(new RequestBean("add", search));//添加好友的消息类型,标签标记
                        } else {
                            //返回自动添加信息
//                            Log.e("收到返回好友自动添加请求", "-----------------------");
                            Presence subscription = new Presence(Presence.Type.subscribed);
                            subscription.setTo(getRealName(presence.getFrom()) + "@" + con.getServiceName());
                            subscription.setProperty("autoadd", "YES");
                            con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
                            con.sendPacket(subscription);

                            //更新联系人
                            boolean flag = true;
                            for (int j = 0; j < ContactPersonList.size(); j++) {
                                if (ContactPersonList.get(j).equals(getRealName(presence.getFrom()))) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
//                                Log.e("我要添加新的好友", "==============" + presence.getFrom());
                                ContactPersonList.add(getRealName(presence.getFrom()));
                                Intent intent = new Intent("com.broadcast.contactchange");
                                sendBroadcast(intent);
                            }


                        }

                    } else if (presence.getType().equals(Presence.Type.subscribed)) {  //收到好友同意好友添加的返回信息

                        String autoadd = (String) presence.getProperty("autoadd");
                        Log.e("autoadd", autoadd);

                        if (autoadd.equals("NO")) {
//                            Log.e("收到好友 同意我的添加请求", "-----------------------");
                            SearchSession search = new SearchSession(username, nickName, bitmap, "对方已同意添加你为好友");
                            requestlist.add(new RequestBean("sure_add", search));
                        } else {

//                            Log.e("对方已经主动添加我好友"," 我默认同意 关系变成both");
//                            Log.e("cccccccccccccccccc","-------------------------"+ContactPersonList.isEmpty());
                            //更新联系人
                            boolean flag = true;
                            for (int j = 0; j < ContactPersonList.size(); j++) {
                                if (ContactPersonList.get(j).equals(getRealName(presence.getFrom()))) {
                                    Log.e("rrrrrrrrrr", ContactPersonList.get(j) + "    " + getRealName(presence.getFrom()));
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
//                                    Log.e("我要添加新的好友","++++++++++"+getRealName(presence.getFrom()));
                                ContactPersonList.add(getRealName(presence.getFrom()));
                                Intent intent = new Intent("com.broadcast.contactchange");
                                sendBroadcast(intent);
                            }


                        }


                    } else if (presence.getType().equals(Presence.Type.unsubscribe)) {  //表示发送拒绝添加对方为好友

                        String autodelete = (String) presence.getProperty("autodelete");
                        Log.e("autodelete", autodelete);
                        Log.e("收到好友 删除好友请求", "-----------------------");
                        if (autodelete.equals("YES")) {

                            //自动发送同意拒绝订阅
                            Presence subscription = new Presence(Presence.Type.unsubscribed);
                            subscription.setTo(getRealName(presence.getFrom()) + "@" + con.getServiceName());
                            con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                            subscription.setProperty("autorefuse", "YES");
                            con.sendPacket(subscription);


                        }

                    } else if (presence.getType().equals(Presence.Type.unsubscribed)) {  //表示同意删除好友的申请


                        String autorefuse = (String) presence.getProperty("autorefuse");
                        String description = (String) presence.getProperty("description");
                        Log.e("descri", description);
                        Log.e("autorefuse", autorefuse);
                        if (autorefuse.equals("NO")) {
                            SearchSession search = null;
                            Log.e("收到好友 拒绝我的添加请求", "-----------------------");
                            if (description != null && description.equals("delete")) {
//                                Log.e("descri", "111111");
                                search = new SearchSession(username, nickName, bitmap, "对方已删除你为好友");
                                requestlist.add(new RequestBean("delete", search));
                                ContactPersonList.remove(GetRealName.getRealName(presence.getFrom()));
//                                Log.e("Remove1111", ContactPersonList.size() + "");
                                if (ChatTarget.equals(getRealName(presence.getFrom()))) //如果还处于已删除目标的聊天窗，立刻删除
                                {
                                    ((Activity)Chat_Activity_context).finish();
                                    ChatTarget = "none";
                                }
                                Log.e("删除未读消息",onlinemsg_list.size()+"  ");
                                for (int j = 0; j <onlinemsg_list.size() ; j++) {
                                    Log.e("消息显示先试试",getRealName(onlinemsg_list.get(j).name)+"    "+getRealName(presence.getFrom())+"  "+onlinemsg_list.get(j).message+"  "+getRealName(onlinemsg_list.get(j).name).equals(getRealName(presence.getFrom())));
                                    if (getRealName(onlinemsg_list.get(j).name).equals(getRealName(presence.getFrom())))
                                        onlinemsg_list.remove(j);
                                }
                                Log.e("删除未读消息后",onlinemsg_list.size()+"  ");
                                AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                                alterDataBase.DeleteOneRecord(getRealName(presence.getFrom())); //清空相关记录
                                Intent intent = new Intent("com.broadcast.message");
                                sendBroadcast(intent);
                            } else {
//                                Log.e("descri", "222222");
                                search = new SearchSession(username, nickName, bitmap, "对方拒绝添加你为好友");
                                requestlist.add(new RequestBean("refuse", search));
                            }

                            //发送删除请求，互相删除订阅
                            Presence subscription = new Presence(Presence.Type.unsubscribe);
                            subscription.setTo(getRealName(presence.getFrom()) + "@" + con.getServiceName());
                            con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                            subscription.setProperty("autodelete", "YES");
                            con.sendPacket(subscription);

                        }

                    }


                    if (presence.getType().equals(Presence.Type.values()[0])){

                        String roomname = (String) presence.getProperty("roomname");
                        if (roomname != null) {
                            Log.e("收到删除群聊天成员的请求","==================="+getRealName(presence.getFrom()));
                            MultiUserChat multiUserChat = muc_map.get(roomname);
                            try {
                                multiUserChat.revokeMembership(getRealName(presence.getFrom()) + "@" + con.getServiceName());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    if (presence.getType().equals(Presence.Type.values()[1])){

                        String roomname = (String) presence.getProperty("RoomName");
                        String str = roomname+"@conference."+con.getServiceName();
//                        Log.e("str2222",str);
                        if (roomname != null) {
                            Log.e("你被"+roomname+"的群主踢了","------------");
                            Log.e("删除拴成111",ChatTarget+"       "+roomname+"    "+ChatTarget.equals(roomname));
                            if (ChatTarget.equals(roomname)) //如果还处于已删除目标的聊天窗，立刻删除
                            {
                                ((Activity)Chat_Activity_context).finish();
                                ChatTarget = "none";
                            }
                            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
                            alterDataBase.DeleteChatRooms(str);
                            alterDataBase.DeleteOneRecord(roomname);
                            for (int j = 0; j <onlinemsg_list.size() ; j++) {
//                                    Log.e("消息显示先试试",onlinemsg_list.get(j).name+"  "+onlinemsg_list.get(j).message);
                                if (getRealName(onlinemsg_list.get(j).name).equals(roomname))
                                    onlinemsg_list.remove(j);
                            }
                            Intent intent = new Intent("com.broadcast.message");
                            sendBroadcast(intent);
                            boolean remove = RoomName_List.remove(roomname + "@conference." + con.getServiceName());
                            if (remove) {
                                roomAdapter.notifyDataSetChanged();
                            }
                        }


                    }

                    if (presence.getType().equals(Presence.Type.available)) {

                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        VCard mycard = new VCard();
                        try {
                            mycard.load(con, MyUserName + "@" + con.getServiceName());


                            String newfriendCircle = (String) presence.getProperty("newfriendCircle");
                            String deleteFriendCircle = (String) presence.getProperty("deleteFriendCircle");

                            if (newfriendCircle != null || deleteFriendCircle != null) { //两个之一不为空，防止XMPP乱发指令

                                if (newfriendCircle != null && newfriendCircle.length() > 0) {
                                    byte[] myavatar = mycard.getAvatar();
                                    String encodeImage = StringUtils.encodeBase64(myavatar);
                                    String sex = mycard.getFirstName();
                                    String signature = mycard.getLastName();
                                    Log.e("已经收到点赞了", "newfriendCircle :" + newfriendCircle);
                                    mycard.setEncodedImage(encodeImage);
                                    mycard.setAvatar(myavatar);
                                    mycard.setProperty("PHOTO", encodeImage);
                                    mycard.setNickName(MyNickName);
                                    mycard.setFirstName(sex);
                                    mycard.setLastName(signature);
                                    mycard.setField("FriendCircle", newfriendCircle);
                                    mycard.save(con);
                                } else {
                                    byte[] myavatar = mycard.getAvatar();
                                    String encodeImage = StringUtils.encodeBase64(myavatar);
                                    String sex = mycard.getFirstName();
                                    String signature = mycard.getLastName();
                                    Log.e("已经取消点赞了", "deleteFriendCircle :" + deleteFriendCircle);
                                    mycard.setEncodedImage(encodeImage);
                                    mycard.setAvatar(myavatar);
                                    mycard.setProperty("PHOTO", encodeImage);
                                    mycard.setNickName(MyNickName);
                                    mycard.setFirstName(sex);
                                    mycard.setLastName(signature);
                                    mycard.setField("FriendCircle", deleteFriendCircle);
                                    mycard.save(con);
                                }

                                    Intent intent_friendcircle = new Intent("com.broadcast.FriendCircleRefresh");
                                    sendBroadcast(intent_friendcircle);


                            }


                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }

                    if (presence.getType().equals(Presence.Type.unavailable)) {


                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        VCard mycard = new VCard();

                        try {
                            mycard.load(con, MyUserName + "@" + con.getServiceName());

                            String newfriendCircle = (String) presence.getProperty("newfriendCircle");
                            if (newfriendCircle != null && newfriendCircle.length() > 0) {
                                Log.e("收到了别人的评论", "-----------");
                                byte[] myavatar = mycard.getAvatar();
                                String encodeImage = StringUtils.encodeBase64(myavatar);
                                String sex = mycard.getFirstName();
                                String signature = mycard.getLastName();
                                mycard.setEncodedImage(encodeImage);
                                mycard.setAvatar(myavatar);
                                mycard.setProperty("PHOTO", encodeImage);
                                mycard.setNickName(MyNickName);
                                mycard.setFirstName(sex);
                                mycard.setLastName(signature);
                                mycard.setField("FriendCircle", newfriendCircle);
                                mycard.save(con);


                                    Intent intent_friendcircle = new Intent("com.broadcast.FriendCircleRefresh");
                                    sendBroadcast(intent_friendcircle);


                            }

                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }

                    }

                }

        }
    };






    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmppTool.closeConnection();
    }


    @OnClick({R.id.Title_Popmenu, R.id.Title_Search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Title_Popmenu:
                if (TitlePopmenuLayout.getVisibility() == View.GONE) {
                    TitlePopmenuLayout.setVisibility(View.VISIBLE);
                } else {
                    TitlePopmenuLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.Title_Search:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {

            case 0: //发起群聊
                Toast.makeText(this, "群聊", Toast.LENGTH_SHORT).show();
                Intent chattogether = new Intent(BaseActivity.this, ChatInvitation.class);
                startActivity(chattogether);
                break;

            case 1://添加好友
                Toast.makeText(this, "添加好友", Toast.LENGTH_SHORT).show();
                Intent addfriend = new Intent(BaseActivity.this, AddNewFriend.class);
                startActivity(addfriend);
                break;

            case 2://扫一扫
                Toast.makeText(this, "扫一扫", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;


        }
        TitlePopmenuLayout.setVisibility(View.GONE);

    }


    @OnClick({R.id.bottom_wechat, R.id.bottom_contact, R.id.bottom_discover, R.id.bottom_myself})
    public void click(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (view.getId()) {

            case R.id.bottom_wechat:
                replaceViewpage.setCurrentItem(0, false);
                break;
            case R.id.bottom_contact:
                replaceViewpage.setCurrentItem(1, false);
                break;
            case R.id.bottom_discover:
                replaceViewpage.setCurrentItem(2, false);
                break;
            case R.id.bottom_myself:
                replaceViewpage.setCurrentItem(3, false);
                break;

            default:
                break;

        }
        transaction.commit();

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (position) {

            case 0:
                wechatImg.setImageResource(R.mipmap.weixin_pressed);
                contactImg.setImageResource(R.mipmap.contact_list_normal);
                discoverImg.setImageResource(R.mipmap.find_normal);
                myselfImg.setImageResource(R.mipmap.profile_normal);
                wechatText.setTextColor(getResources().getColor(R.color.green));
                contactText.setTextColor(getResources().getColor(R.color.black));
                discoverText.setTextColor(getResources().getColor(R.color.black));
                myselfText.setTextColor(getResources().getColor(R.color.black));
                TitlePopmenuLayout.setVisibility(View.GONE);   //每当直接切换fragment的时候，溢出菜单消失
                break;
            case 1:
                wechatImg.setImageResource(R.mipmap.weixin_normal);
                contactImg.setImageResource(R.mipmap.contact_list_pressed);
                discoverImg.setImageResource(R.mipmap.find_normal);
                myselfImg.setImageResource(R.mipmap.profile_normal);
                wechatText.setTextColor(getResources().getColor(R.color.black));
                contactText.setTextColor(getResources().getColor(R.color.green));
                discoverText.setTextColor(getResources().getColor(R.color.black));
                myselfText.setTextColor(getResources().getColor(R.color.black));
                TitlePopmenuLayout.setVisibility(View.GONE);
                break;
            case 2:
                wechatImg.setImageResource(R.mipmap.weixin_normal);
                contactImg.setImageResource(R.mipmap.contact_list_normal);
                discoverImg.setImageResource(R.mipmap.find_pressed);
                myselfImg.setImageResource(R.mipmap.profile_normal);
                wechatText.setTextColor(getResources().getColor(R.color.black));
                contactText.setTextColor(getResources().getColor(R.color.black));
                discoverText.setTextColor(getResources().getColor(R.color.green));
                myselfText.setTextColor(getResources().getColor(R.color.black));
                TitlePopmenuLayout.setVisibility(View.GONE);
                break;
            case 3:
                wechatImg.setImageResource(R.mipmap.weixin_normal);
                contactImg.setImageResource(R.mipmap.contact_list_normal);
                discoverImg.setImageResource(R.mipmap.find_normal);
                myselfImg.setImageResource(R.mipmap.profile_pressed);
                wechatText.setTextColor(getResources().getColor(R.color.black));
                contactText.setTextColor(getResources().getColor(R.color.black));
                discoverText.setTextColor(getResources().getColor(R.color.black));
                myselfText.setTextColor(getResources().getColor(R.color.green));
                TitlePopmenuLayout.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
