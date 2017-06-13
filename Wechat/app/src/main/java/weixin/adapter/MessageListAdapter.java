package weixin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Xmpp.GetBitmapToString;
import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import dao.AlterDataBase;
import dao.bean.ChatRecord;
import teacher.davisstore.com.wechat.R;
import weixin.bean.MessageBean;
import weixin.thread.AsyncCount;
import weixin.thread.AsyncDisplayNum;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.RecordNameSize;
import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.onlinemsg_list;
import static Xmpp.GetBitmapToString.getBitmapToString;
import static Xmpp.GetInOfBelong.getInOfBelong;
import static Xmpp.GetRealName.getRealName;

/**
 * Created by Administrator on 2017/2/24.
 */

public class MessageListAdapter extends BaseAdapter {

    Context context;
    private  List<ChatRecord> list ;



    public MessageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {

        AsyncDisplayNum asyncImageLoad = new AsyncDisplayNum();
        asyncImageLoad.execute();

//        Log.e("RecordNameSize",RecordNameSize+"");
        return RecordNameSize;


    }



    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.weixinfragment_list, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        AsyncCount asyncCount = new AsyncCount(context,position,holder.weixinfragmentFriendsicon,holder.weixinfragmentFriendsname
        ,holder.weixinfragmentFriendscontent,holder.weixinfragmentFriendstime,holder.weixinfragmentFriendstipnums,holder.weixinfragmentFriendslayout);
        asyncCount.execute();

//        Log.e("应该显示的list","       "+list.size());
//        Log.e("应该的MESSAGE LIST",list.get(position).SenderName+"   "+list.get(position).Content+"  " +
//                ""+list.get(position).SenderNickName);

       /* byte[] bytes = list.get(position).SenderIcon.getBytes();

        Glide.with(context)
                .load(bytes)
                .error(R.mipmap.error_load)
                .placeholder(R.mipmap.error_load)
                .centerCrop()
                .crossFade()
                .into(holder.weixinfragmentFriendsicon);*/

       /* list = CountRecordNum();

//        AsyncDisplayNum asyncImageLoad = new AsyncDisplayNum(holder.weixinfragmentFriendsicon,geticon(list.get(position).SenderIcon));
//        asyncImageLoad.execute();


        holder.weixinfragmentFriendsname.setText(list.get(position).SenderNickName);
        EmojiSelect(list.get(position).Content,holder.weixinfragmentFriendscontent);
        holder.weixinfragmentFriendstime.setText(list.get(position).CreateTime);

        holder.weixinfragmentFriendsicon.setImageBitmap(geticon(list.get(position).SenderIcon));


        if (list.get(position).InformationNum <= 0) {

            holder.weixinfragmentFriendstipnums.setVisibility(View.GONE);
        } else {
            holder.weixinfragmentFriendstipnums.setVisibility(View.VISIBLE);
            if (list.get(position).InformationNum < 99)
                holder.weixinfragmentFriendstipnums.setText(list.get(position).InformationNum + "");
            else {
                holder.weixinfragmentFriendstipnums.setText("99+");
            }

        }





        //---------------------------------------------------------点击进入与目标聊天

        final ViewHolder finalHolder = holder;
        holder.weixinfragmentFriendslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(position).ChatRoomOrNot.equals("YES")){  //是否进入群聊
                    AppTemp.InToChatRoom = true;
                }else {
                    AppTemp.InToChatRoom = false;
                }

                Intent intent = new Intent(context, Chat_Activity.class);

                AppTemp.ChatTarget = list.get(position).SenderName;
                list.get(position).InformationNum = 0;

                AppTemp.ChatTargetNickName = list.get(position).SenderNickName;
//                Log.e("当前聊天目标昵称",AppTemp.ChatTargetNickName);

                context.startActivity(intent);
            }
        });*/




        return convertView;
    }



    private List<MessageBean> CountLastContent() {

        List<MessageBean> list = new ArrayList<>();
        boolean flag = true;
        Map map = new HashMap();  //保存所有发消息的好友分别发送消息的数量

        /*List<RosterEntry> allFriends = GetALLFriend.getAllFriends();
        //找到发消息的好友分别发了多少条信息
        Iterator<RosterEntry> iterator = allFriends.iterator();
        while (iterator.hasNext()) {

            RosterEntry next = iterator.next();
//            map.put(next.getUser()+"/Spark" , 0);  //spark客户端测试
              map.put(getRealName(next.getUser()) , 0);
//            Log.e("map", map.get(next.getUser()) + " "+ next.getUser());
        }*/

        for (int i = 0; i < ContactPersonList.size() ; i++) {
            map.put(ContactPersonList.get(i),0);
//            Log.e("map", map.get(ContactPersonList.get(i)) + " "+ ContactPersonList.get(i));
        }

        for (int i = 0; i < RoomName_List.size() ; i++) {
//            Log.e("Roomlist",getRealName(RoomName_List.get(i)));
            map.put(getRealName(RoomName_List.get(i)),0);
//            Log.e("map", map.get(RoomName_List.get(i)) + " "+ RoomName_List.get(i));
        }

        //找出最后消息

//        Log.e("当前onlinemsg_list111", onlinemsg_list.size()+"  ");
//        for (int i = 0; i < onlinemsg_list.size() ; i++) {
//            Log.e("当前online_messages2222",onlinemsg_list.get(i).name+"    "+onlinemsg_list.get(i).nickname
//            +"     "+onlinemsg_list.get(i).message);
//        }

        for (int j = 0; j < onlinemsg_list.size(); j++) {


            for (int i = 0; i < list.size(); i++) {  //找到相同的人发的信息就覆盖掉，把消息换成最新


                if (onlinemsg_list.get(j).name.equals(list.get(i).name)) {  //找到重复的

                    int sum = 0;
                    for (int k = 0; k < ContactPersonList.size(); k++) {  //把对应的消息数传出去

                        if (map.containsKey(getRealName(list.get(i).name))) {  //有这个人发来的消息
                            sum = (int) map.get(getRealName(list.get(i).name));
//                            Log.e("当前未读",list.get(i).name+"     :"+sum);
                            sum++;  //计算最新未读消息
//                            Log.e("当前未读1",list.get(i).name+"     :"+sum);
                            //覆盖
                            map.remove(getRealName(list.get(i).name));
                            map.put(getRealName(list.get(i).name), sum);
                            break;

                        }

                    }


//                    Log.e("realName",realName);
//                    list.remove(i);  //把原来的去掉
                    list.set(i, new MessageBean(onlinemsg_list.get(j).name
                            , onlinemsg_list.get(j).message
                            , onlinemsg_list.get(j).time
                            , (int) map.get(getRealName(list.get(i).name))
                            ,"OUT"
                            ,onlinemsg_list.get(j).nickname
                            ,onlinemsg_list.get(j).icon
                            ,onlinemsg_list.get(j).ChatRoomOrNot
                            ,onlinemsg_list.get(j).MemberSender));  //覆盖
                    flag = false;
                    break;

                }

            }

            if (flag) {
//                Log.e("直接添加", "添加");
                int sum = 0;
//                Log.e("map.get",getRealName(online_messages.get(j).name)+"  "+map.get(getRealName(online_messages.get(j).name)));
//                if (map.get(getRealName(online_messages.get(j).name)) == null)
//                    sum = 0;
                sum = (int) map.get(getRealName(onlinemsg_list.get(j).name));
                sum++;
                map.remove(getRealName(onlinemsg_list.get(j).name));
                map.put(getRealName(onlinemsg_list.get(j).name), sum);
//                Log.e(""+online_messages.get(j).name,sum+"");

                list.add(new MessageBean(onlinemsg_list.get(j).name
                        , onlinemsg_list.get(j).message
                        , onlinemsg_list.get(j).time
                        , (int) map.get(getRealName(onlinemsg_list.get(j).name))
                        ,"OUT"
                        ,onlinemsg_list.get(j).nickname
                        ,onlinemsg_list.get(j).icon
                        ,onlinemsg_list.get(j).ChatRoomOrNot
                        ,onlinemsg_list.get(j).MemberSender));

            }

            flag = true;

        }

       /* Log.e("messageBeen888",list.size()+" ");
        for (int i = 0; i < list.size() ; i++) {

            Log.e("messageBeen999",list.get(i).name+"  "+list.get(i).nickname+"   " +
                    ""+list.get(i).message+"   "+list.get(i).MemberSender);

        }*/


        return list;


    }

    private List<ChatRecord> CountRecordNum() {

        List<MessageBean> messageBeen = CountLastContent();  //返回哪个人有消息发送过来

        Bitmap bitmap_chatroomicon = BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon);
        String Bitmap_ChatRoomIcon = GetBitmapToString.getBitmapToString(bitmap_chatroomicon);


        List<ChatRecord> list = new ArrayList<>();
        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());

        Cursor cursor = alterDataBase.QueryAllRecord();
//            Log.e("cursor.alterDataBase",cursor.getCount()+" ");
        if (cursor.moveToFirst()){


            do {

                String senderName = cursor.getString(cursor.getColumnIndex("SenderName"));
                String senderNickName = cursor.getString(cursor.getColumnIndex("SenderNickName"));
                String senderIcon = cursor.getString(cursor.getColumnIndex("SenderIcon"));
                String content = cursor.getString(cursor.getColumnIndex("Content"));
                String createTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                String belong = cursor.getString(cursor.getColumnIndex("Belong"));
                String contentFrom = cursor.getString(cursor.getColumnIndex("ContentFrom"));
                String ChatRoomOrNot = cursor.getString(cursor.getColumnIndex("ChatRoomOrNot"));
                String MemberSender = cursor.getString(cursor.getColumnIndex("MemberSender"));


                if (contentFrom.equals("IN")) {  //该聊天记录不是自己发出的，进行非常判断

                    boolean temp = true;  //判断是否重复
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).Belong.equals(belong)) {  //属于与同一人的聊天记录 ,覆盖

                                if (ChatRoomOrNot.equals("NO"))
                                list.set(i, new ChatRecord(senderName, senderNickName, senderIcon, content, createTime, belong, contentFrom,"NO",""));
                                else
                                    list.set(i, new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                                temp = false;
                                break;
                            }
                        }
                        if (temp) {  //与所有聊天记录人都不匹配 就添加
                            if (ChatRoomOrNot.equals("NO"))
                            list.add(new ChatRecord(senderName, senderNickName, senderIcon, content, createTime, belong, contentFrom,"NO",""));
                            else
                                list.add(new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                        }

                    } else {

                        if (ChatRoomOrNot.equals("NO"))
                            list.add(new ChatRecord(senderName, senderNickName, senderIcon, content, createTime, belong, contentFrom,"NO",""));
                        else
                            list.add(new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                    }

                }else {  //该聊天记录是自己发出的  OUT,就要获取接收者的名字和和头像,通过belong+vcard

                    try {


                       if (ChatRoomOrNot.equals("NO")){

                           String inOfBelong = getInOfBelong(belong);
                           VCard card = new VCard();
                           card.load(XmppTool.getConnection(),inOfBelong+"@"+con.getServiceName());
                           String nickName = card.getNickName();
                           byte[] avatar = card.getAvatar();
                           Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);

                           boolean temp = true;  //判断是否重复
                           if (list.size() > 0) {
                               for (int i = 0; i < list.size(); i++) {
                                   if (list.get(i).Belong.equals(belong)) {  //属于与同一人的聊天记录 ,覆盖

                                       list.set(i, new ChatRecord(inOfBelong, nickName, getBitmapToString(bitmap), content, createTime, belong, contentFrom,"NO",""));
                                       temp = false;
                                       break;
                                   }
                               }
                               if (temp) {  //与所有聊天记录人都不匹配 就添加

                                   list.add(new ChatRecord(inOfBelong, nickName, getBitmapToString(bitmap), content, createTime, belong, contentFrom,"NO",""));
                               }

                           } else {

                               list.add(new ChatRecord(inOfBelong, nickName, getBitmapToString(bitmap), content, createTime, belong, contentFrom,"NO",""));
                           }

                       }else {

                           /*String inOfBelong = getInOfBelong(belong);
                           VCard card = new VCard();
                           card.load(XmppTool.getConnection(),MemberSender+"@"+con.getServiceName());
                           String nickName = card.getNickName();
                           byte[] avatar = card.getAvatar();
                           Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);*/


                           boolean temp = true;  //判断是否重复
                           if (list.size() > 0) {
                               for (int i = 0; i < list.size(); i++) {
                                   if (list.get(i).Belong.equals(belong)) {  //属于与同一人的聊天记录 ,覆盖

                                       list.set(i, new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                                       temp = false;
                                       break;
                                   }
                               }
                               if (temp) {  //与所有聊天记录人都不匹配 就添加

                                   list.add(new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                               }

                           } else {

                               list.add(new ChatRecord(senderName, senderNickName, Bitmap_ChatRoomIcon, content, createTime, belong, contentFrom,"YES",MemberSender));
                           }

                       }


                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }

                }




            }while (cursor.moveToNext());

        }
        cursor.close();


//        for (int i = 0; i < list.size(); i++) {
//
//            Log.e("messageBeen__list_之前",list.get(i).SenderName+"  "+list.get(i).SenderNickName+"   " +
//                    ""+list.get(i).Content+"   "+list.get(i).MemberSender+"   "+list.get(i).ChatRoomOrNot);
//
//        }


        int sum = list.size();
        boolean temp = true;

        for (int i = 0; i < messageBeen.size() ; i++) {  //有未读消息的好友

            if (list.size() > 0) {

                for (int j = 0; j < list.size(); j++) {
//                Log.e("最新消息人：聊天记录人",messageBeen.get(i).name+"   :   "+list.get(j).SenderName);
                    if (getRealName(messageBeen.get(i).name).equals(list.get(j).SenderName)) {

                        list.set(j,new ChatRecord(
                                getRealName(messageBeen.get(i).name),
                                messageBeen.get(i).nickname,
                                getBitmapToString(messageBeen.get(i).icon),
                                messageBeen.get(i).message,
                                messageBeen.get(i).time,
                                list.get(j).Belong,
                                "IN",
                                messageBeen.get(i).informationnums,
                                messageBeen.get(i).ChatRoomOrNot,
                                messageBeen.get(i).MemberSender));
                        temp = false;
                        break;

                    }

                }
                if (temp) {  //没有重复
                    list.add(new ChatRecord(
                            getRealName(messageBeen.get(i).name),
                            messageBeen.get(i).nickname,
                            getBitmapToString(messageBeen.get(i).icon),
                            messageBeen.get(i).message,
                            messageBeen.get(i).time,
                            getRealName(messageBeen.get(i).name)+"/"+MyUserName,
                            "IN",
                            messageBeen.get(i).informationnums,
                            messageBeen.get(i).ChatRoomOrNot,
                            messageBeen.get(i).MemberSender));

                    sum++;
                }
                temp = true;

            }else {  //聊天记录为空，但有未读消息

                list.add(new ChatRecord(
                        getRealName(messageBeen.get(i).name),
                        messageBeen.get(i).nickname,
                        getBitmapToString(messageBeen.get(i).icon),
                        messageBeen.get(i).message,
                        messageBeen.get(i).time,
                        getRealName(messageBeen.get(i).name)+"/"+MyUserName,
                        "IN",
                        messageBeen.get(i).informationnums,
                        messageBeen.get(i).ChatRoomOrNot,
                        messageBeen.get(i).MemberSender));
                sum++;
            }


        }

//        for (int i = 0; i < list.size(); i++) {
//
//            Log.e("messageBeen__list",list.get(i).SenderName+"  "+list.get(i).SenderNickName+"   " +
//                    ""+list.get(i).Content+"   "+list.get(i).MemberSender+"   "+list.get(i).ChatRoomOrNot);
//
//        }

        return list;
    }

    public Bitmap geticon(String bitmap) {

        byte[] bitmapArray;
        bitmapArray = Base64.decode(bitmap, Base64.DEFAULT);
//                Log.e("bitmapArray",bitmapArray.length+"");
        Bitmap sendericons = BitmapFactory.decodeByteArray(bitmapArray, 0,
                bitmapArray.length);
        return sendericons;
    }


    //将显示的信息进行筛选显示表情
    public SpannableString EmojiSelect(String Message , EditText textView){

        String EmojiText = Message;
        textView.setText(EmojiText);
//        Log.e("EmojiText",EmojiText.length()+"");
        Editable text = textView.getText();
        int lastpostion = 0;
        SpannableString spannableString = null;
        Pattern pat=Pattern.compile("\\[emoji([1-9]*[0-9]*)\\]");  //用正则找到目标
        Matcher matcher = pat.matcher(EmojiText);

        while (matcher.find()){

            String group = matcher.group(0); //表情具体名字
            int position = Integer.parseInt(matcher.group(1));//表情具体哪一个
//            Log.e("group",group+"         "+position);
            int i = EmojiText.indexOf(group,lastpostion); //找到目标位置
            lastpostion = i+group.length()-1;

//            Log.e("目标位置",i+"  "+lastpostion);
            spannableString = TranslationEmoji(position, group, textView);
//            Log.e("spannableString",spannableString.length()+"    "+group.length());

            text = text.replace(i, i+group.length() , spannableString);


        }
//        return EmojiText;
        textView.setText(text);

        return spannableString;
    }

    public SpannableString TranslationEmoji(int position, String str ,TextView textView ){


        Drawable drawable = textView.getResources().getDrawable(Image_Emoji[position]);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //需要处理的文本，[smile]是需要被替代的文本
        SpannableString spannable = new SpannableString(str);
        //要让图片替代指定的文字就要用ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        spannable.setSpan(span,0,str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    public static class ViewHolder {
        @BindView(R.id.weixinfragment_friendsicon)
        ImageView weixinfragmentFriendsicon;
        @BindView(R.id.weixinfragment_friendstipnums)
        TextView weixinfragmentFriendstipnums;
        @BindView(R.id.weixinfragment_friendsname)
        TextView weixinfragmentFriendsname;
        @BindView(R.id.weixinfragment_friendscontent)
        EditText weixinfragmentFriendscontent;
        @BindView(R.id.weixinfragment_friendstime)
        TextView weixinfragmentFriendstime;
        @BindView(R.id.weixinfragment_friendslayout)
        RelativeLayout weixinfragmentFriendslayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
