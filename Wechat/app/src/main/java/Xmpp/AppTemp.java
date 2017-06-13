package Xmpp;

import android.content.Context;
import android.graphics.Bitmap;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import contact.adpter.UserFriendRecycleAdapter;
import contact.bean.RequestBean;
import dao.CreateDataBase;
import dao.bean.ChatRecord;
import discover.bean.FriendCircleDisplay;
import me.bean.MyFavorite;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.MessageListAdapter;
import weixin.bean.ChatRoom_Msg;
import weixin.bean.ChatRoom_Online_Message;
import weixin.bean.Friend_ChatBackground;
import weixin.bean.MessageBean;
import weixin.bean.Online_Message;

/**
 * Created by Administrator on 2017/2/23.
 */

public class AppTemp {

   public static List<Online_Message> onlinemsg_list = new ArrayList<>();
   public static String ChatTarget = "none";
   public static ChatManager chatManager;
   public static MessageListAdapter messageListAdapter;
   public static List<MessageBean> LastChatRecord = new ArrayList<>();  //为了保存阅读后的状态
   public static XMPPConnection con = null;
   public static String MyNickName = null;
   public static String MyUserName = null;
   public static Bitmap MyIcon = null ;
   public static String MySex = "未填写" ;
   public static String MySignature = "未填写" ;
   public static String ChatTargetNickName = null;
   public static CreateDataBase dataBase;
   public static Bitmap chat_background = null;
   public static boolean addnews = true;
   public static List<RequestBean> requestlist = new ArrayList<>();  //用于保存好友请求和返回的请求结果
   public static List<Friend_ChatBackground> ChatBackGround_list = new ArrayList<>();
   public static List<FriendCircleDisplay> friendlist = new ArrayList<>();
   public static List<RosterEntry> UserFriendList = new ArrayList<>();
   public static List<String> ContactPersonList ;  //联系人列表
   public static UserFriendRecycleAdapter newFriendRecycleAdapter; //联系人适配器
   public static List<MyFavorite> favorite_list = new ArrayList(); //收藏夹列表
   public static List<String> Invitation_list = new ArrayList(); //邀请列表
   public static int Image_Emoji[] = new int[]{R.mipmap.emoji1, R.mipmap.emoji2, R.mipmap.emoji3, R.mipmap.emoji4
           , R.mipmap.emoji5, R.mipmap.emoji6, R.mipmap.emoji7, R.mipmap.emoji8, R.mipmap.emoji9, R.mipmap.emoji10
           , R.mipmap.emoji11, R.mipmap.emoji12, R.mipmap.emoji13, R.mipmap.emoji14, R.mipmap.emoji15, R.mipmap.emoji16, R.mipmap.emoji17
           , R.mipmap.emoji18, R.mipmap.emoji19, R.mipmap.emoji20, R.mipmap.emoji21, R.mipmap.emoji22, R.mipmap.emoji23, R.mipmap.emoji24
            ,R.mipmap.emoji25};
   public static int Image_Emoji_friendcircle[] = new int[]{R.mipmap.emoji1, R.mipmap.emoji2, R.mipmap.emoji3, R.mipmap.emoji4
           , R.mipmap.emoji5, R.mipmap.emoji6, R.mipmap.emoji7, R.mipmap.emoji8, R.mipmap.emoji9, R.mipmap.emoji10
           , R.mipmap.emoji11, R.mipmap.emoji12, R.mipmap.emoji13, R.mipmap.emoji14, R.mipmap.emoji15, R.mipmap.emoji16, R.mipmap.emoji17
           , R.mipmap.emoji18, R.mipmap.emoji19, R.mipmap.emoji20, R.mipmap.emoji21, R.mipmap.emoji22, R.mipmap.emoji23, R.mipmap.emoji24};

   public static Context favorite_context;
   public static FriendCircleDisplay target_text; //评论的目标信息
   public static String target_friendCircle;//评论的目标人的card
   public static List<ChatRoom_Online_Message> chatroom_onlinemsg_list = new ArrayList<>();
   public static List<String> RoomName_List = new ArrayList<>();
   public static Boolean InToChatRoom = false; //判断是否进入群聊
   public static List<ChatRoom_Msg> RoomMember_list = new ArrayList<>();
   public static Map<String,MultiUserChat> muc_map = new HashMap();

   public static int RecordNameSize = 0;
//   public static boolean MessageListAdapterON = true;




}
