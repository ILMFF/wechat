package Xmpp;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.UserFriendList;
import static Xmpp.AppTemp.con;
import static Xmpp.XmppTool.configure;
import static Xmpp.XmppTool.openConnection;
import static teacher.davisstore.com.wechat.Login_main.password;


/**
 * Created by Administrator on 2017/2/22.
 */

public class GetALLFriend {

   /* public static List<RosterEntry> getAllFriends() {  //获取共享好友
        UserFriendList.clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XMPPConnection newconnect = null;
                    try {

                        configure(ProviderManager.getInstance());
                        //url、端口，也可以设置连接的服务器名字，地址，端口，用户。
                        ConnectionConfiguration connConfig = new ConnectionConfiguration("192.168.1.5", 5222);
                        connConfig.setSendPresence(false);
                        newconnect = new XMPPConnection(connConfig);
                        newconnect.connect();
                        newconnect.login(MyUserName, password);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
//                    con.sendPacket(new Presence(Presence.Type.available));

                    Collection<RosterEntry> roscol = newconnect.getRoster().getEntries();
                    Iterator<RosterEntry> iterator = roscol.iterator();
                    while (iterator.hasNext()) {
                        RosterEntry next = iterator.next();
                        Log.e("我的好友",next.getUser()+"   type="+next.getType());
                        if (next.getType().toString().equals("both")) {  //只有双方都为好友才添加进联系人,
                            // 由于openfire无法实时获取联系人状态所以只要非none都算好友
                            UserFriendList.add(next);
                        }
                    }
//                    newconnect.disconnect();
                }
            }).start();
        Log.e("UserFriendList",UserFriendList.size()+"");
        return UserFriendList;


    }*/
    public static List<RosterEntry> getAllFriends() {  //获取共享好友



        List<RosterEntry> UserFriendList = new ArrayList<>();
        Collection<RosterEntry> roscol = con.getRoster().getEntries();
        Iterator<RosterEntry> iterator = roscol.iterator();
        while (iterator.hasNext()) {
            RosterEntry next = iterator.next();
            Log.e("我的好友",next.getUser()+"   type="+next.getType());
            if (next.getType().toString().equals("both")) {  //只有双方都为好友才添加进联系人,
                // 由于openfire无法实时获取联系人状态所以只要非none都算好友
                UserFriendList.add(next);
            }
        }

        Log.e("UserFriendList",UserFriendList.size()+"");
        return UserFriendList;


    }



}
