package weixin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.OfflineMessageManager;

import java.util.Iterator;

import Xmpp.AppTemp;
import Xmpp.GetALLFriend;
import Xmpp.GetBitmapToString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.MessageListAdapter;
import weixin.bean.Online_Message;

import static Xmpp.AppTemp.addnews;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.messageListAdapter;
import static Xmpp.AppTemp.onlinemsg_list;

/*import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;*/


/**
 * Created by Administrator on 2017/2/11.
 */

public class WeiXinFragment_main extends Fragment {

    @BindView(R.id.user_friendlist)
    public  ListView userFriendlist;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.weixinfragment_main, null);

        ButterKnife.bind(this, inflate);

        if (addnews) { //只添加一次,原因未知，因为程序自动都用多次该该位置方法，所以用开关限制

            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
            Cursor cursor = alterDataBase.GetAllNewsRecord();
            Log.e("cursor.size", cursor.getCount() + "         消息数量");
            Log.e("onlinemesg.size", AppTemp.onlinemsg_list.size() + "         消息数量");
            if (cursor.moveToFirst()) {
//            Log.e("-------------","--------------------");
                do {

                    String Content = cursor.getString(cursor.getColumnIndex("Content"));
                    String SenderName = cursor.getString(cursor.getColumnIndex("SenderName"));
                    String SenderNickName = cursor.getString(cursor.getColumnIndex("SenderNickName"));
                    String SenderIcon = cursor.getString(cursor.getColumnIndex("SenderIcon"));
                    String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
//                String ContentFrom = cursor.getString(cursor.getColumnIndex("ContentFrom"));  //未读消息固定"IN"
                    String ChatRoomOrNot = cursor.getString(cursor.getColumnIndex("ChatRoomOrNot"));
                    String MemberSender = cursor.getString(cursor.getColumnIndex("MemberSender"));
                    byte[] bitmapArray;
                    bitmapArray = Base64.decode(SenderIcon, Base64.DEFAULT);
//                Log.e("bitmapArray",bitmapArray.length+"");
                    Bitmap sendericons = BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
//                    SenderName = SenderName + "@wechat-server/Smack";


//                    Log.e("离线后再次上线的聊天记录",SenderName+"        "+SenderNickName+"    "+Content+"" +
//                            "   "+ChatRoomOrNot+"   "+MemberSender);

                    if (ChatRoomOrNot.equals("NO"))
                        SenderName = SenderName + "@wechat-server/Smack";
                    else
                        SenderName = SenderName + "@conference."+con.getServiceName();

//                     Log.e("获取到的未读消息",SenderName+"        "+CreateTime);

                    boolean flag = true; //为了控制群聊的离线聊天未读的离线消息重复添加
                    for (int i = 0; i <onlinemsg_list.size() ; i++) {
                        if (onlinemsg_list.get(i).name.equals(SenderName) && onlinemsg_list.get(i).ChatRoomOrNot.equals("YES")
                                && onlinemsg_list.get(i).time.equals(CreateTime) && onlinemsg_list.get(i).message.equals(Content)){
                            flag = false;
                            break;
                        } //证明是群聊离线问题，前面baseactivity已经添加,避免这里再次添加进未读消息表
                    }
                    if (flag) {
                        onlinemsg_list.add(new Online_Message(SenderName, Content, CreateTime, SenderNickName, sendericons, ChatRoomOrNot, MemberSender));
                    }
                    /*  for (int i = 0; i < onlinemsg_list.size(); i++) {

                        Log.e("在线未读聊天表内容",onlinemsg_list.get(i).message);
                    }*/

                } while (cursor.moveToNext());


            }
//            Log.e("离线上线长度",onlinemsg_list.size()+"");
            cursor.close();
        }
        addnews = false;

//        Log.e("添加进在线聊天列表后长度",AppTemp.onlinemsg_list.size()+"");
//        for (int i = 0; i < onlinemsg_list.size() ; i++) {
//
//            Log.e("未读消息内容",onlinemsg_list.get(i).message);
//
//        }
        
        


        messageListAdapter = new MessageListAdapter(getActivity());
        userFriendlist.setAdapter(messageListAdapter);

        return inflate;
    }




    public static class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_SHORT).show();
              AppTemp.messageListAdapter.notifyDataSetChanged();
        }
    }


}
