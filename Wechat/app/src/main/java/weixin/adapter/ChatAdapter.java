package weixin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import dao.AlterDataBase;
import dao.bean.ChatRecord;
import teacher.davisstore.com.wechat.R;
import weixin.bean.MessageBean;

import static Xmpp.AppTemp.ChatTarget;
import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.friendlist;
import static Xmpp.GetBitmapToString.getBitmapToString;

/**
 * Created by Administrator on 2017/2/22.
 */

public class ChatAdapter extends BaseAdapter {

    List<MessageBean> list;
    Context context;


    public ChatAdapter(List<MessageBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size() > 0 ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChatViewHolder holder = null;


        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.chat_messagelist, null);
            holder = new ChatViewHolder(convertView);
            convertView.setTag(holder);
        } else {

            holder = (ChatViewHolder) convertView.getTag();
        }


        final MessageBean messageBean = list.get(position);
        //在聊天框内每显示一条，就把该消息添加进入聊天记录表内
        AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());

        String SenderIcon = getBitmapToString(messageBean.icon);  //将头像转成String后再保存进聊天记录表


        int count = alterDataBase.QueryOneRecord(messageBean.time);


//        Log.e("count",count+"");
        Log.e("messageBean.name", messageBean.name + "  "+messageBean.nickname+"   " + messageBean.message);
        if (count == 0) {
            alterDataBase.InsertRecord(new ChatRecord(messageBean.name, messageBean.nickname
                    , SenderIcon, messageBean.message
                    , messageBean.time, ChatTarget + "/" + MyUserName
                    , messageBean.from,messageBean.ChatRoomOrNot,messageBean.MemberSender));
        }


        switch (messageBean.from) {

            case "OUT":  //发送消息
                if (messageBean.ChatRoomOrNot.equals("YES")){ //群聊就显示昵称
                    holder.mymessage_Nickname.setVisibility(View.VISIBLE);
                    VCard card = new VCard();
                    try {
                        card.load(con,messageBean.MemberSender+"@"+con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    holder.mymessage_Nickname.setText(card.getNickName());
                    holder.mymessgaeIcon.setImageBitmap(bitmap);
                    EmojiSelect(messageBean.message, holder.mymessageText);
                    holder.friendmessageLayout.setVisibility(View.GONE); //对方的布局隐藏
                    holder.mymessageLayout.setVisibility(View.VISIBLE); //自己发送的布局显示
                    holder.mymessageTime.setText(messageBean.time);

                }else {
                    holder.mymessage_Nickname.setVisibility(View.GONE);
                    EmojiSelect(messageBean.message, holder.mymessageText);
                    holder.friendmessageLayout.setVisibility(View.GONE); //对方的布局隐藏
                    holder.mymessageLayout.setVisibility(View.VISIBLE); //自己发送的布局显示
                    holder.mymessageTime.setText(messageBean.time);
                    holder.mymessgaeIcon.setImageBitmap(messageBean.icon);
                }

                break;


            case "IN":   //接收消息
                if (messageBean.ChatRoomOrNot.equals("YES")){
                    holder.friendmessageNickname.setVisibility(View.VISIBLE);
                    VCard card = new VCard();
                    try {
                        card.load(con,messageBean.MemberSender+"@"+con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    holder.friendmessageNickname.setText(card.getNickName());
                    holder.friendmessgaeIcon.setImageBitmap(bitmap);
                    EmojiSelect(messageBean.message, holder.friendmessageText);
                    holder.mymessageLayout.setVisibility(View.GONE);
                    holder.friendmessageLayout.setVisibility(View.VISIBLE);
                    holder.friendmessageTime.setText(messageBean.time);

                }else {
                    holder.friendmessageNickname.setVisibility(View.GONE);
                    EmojiSelect(messageBean.message, holder.friendmessageText);
                    holder.mymessageLayout.setVisibility(View.GONE);
                    holder.friendmessageLayout.setVisibility(View.VISIBLE);
                    holder.friendmessageTime.setText(messageBean.time);
                    holder.friendmessgaeIcon.setImageBitmap(messageBean.icon);
                }

                break;

            default:
                break;

        }


       holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               Log.e("收藏位置",position+" ");
               Log.e("要收藏的目标",list.get(position).MemberSender+"  "+list.get(position).message+"   "+list.get(position).nickname);
               final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(context);
               deletefriend_builder.setPositiveButton("添加进收藏夹", new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                       Intent intent = new Intent("com.broadcast.myfavorite");

                       intent.putExtra("new_favorite_username",list.get(position).MemberSender);
                       intent.putExtra("new_favorite_time",list.get(position).time);
                       intent.putExtra("new_favorite_content",list.get(position).message);
                       intent.putExtra("new_favorite_icon",list.get(position).icon);
                       intent.putExtra("new_favorite_nickname",list.get(position).nickname);


                       context.sendBroadcast(intent);
                   }
               });
               AlertDialog deletefriend_dialog = deletefriend_builder.create();
               deletefriend_dialog.show();
               return true;
           }
       });


        return convertView;
    }


    //将显示的信息进行筛选显示表情
    public SpannableString EmojiSelect(String Message, EditText textView) {

        String EmojiText = Message;
        textView.setText(EmojiText);
        Log.e("EmojiText", EmojiText.length() + "");
        Editable text = textView.getText();
        int lastpostion = 0;
        SpannableString spannableString = null;
        Pattern pat = Pattern.compile("\\[emoji([1-9]*[0-9]*)\\]");  //用正则找到目标
        Matcher matcher = pat.matcher(EmojiText);

        while (matcher.find()) {

            String group = matcher.group(0); //表情具体名字
            int position = Integer.parseInt(matcher.group(1));//表情具体哪一个
//            Log.e("group",group+"         "+position);
            int i = EmojiText.indexOf(group, lastpostion); //找到目标位置
            lastpostion = i + group.length() - 1;

//            Log.e("目标位置",i+"  "+lastpostion);
            spannableString = TranslationEmoji(position, group, textView);
//            Log.e("spannableString",spannableString.length()+"    "+group.length());

            text = text.replace(i, i + group.length(), spannableString);


        }
//        return EmojiText;
        textView.setText(text);

        return spannableString;
    }

    public SpannableString TranslationEmoji(int position, String str, TextView textView) {


        Drawable drawable = textView.getResources().getDrawable(Image_Emoji[position]);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //需要处理的文本，[smile]是需要被替代的文本
        SpannableString spannable = new SpannableString(str);
        //要让图片替代指定的文字就要用ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        spannable.setSpan(span, 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    class ChatViewHolder {
        @BindView(R.id.friendmessgae_icon)
        ImageView friendmessgaeIcon;
        @BindView(R.id.friendmessage_time)
        TextView friendmessageTime;
        @BindView(R.id.friendmessage_nickname)
        TextView friendmessageNickname;
        @BindView(R.id.friendmessage_text)
        EditText friendmessageText;
        @BindView(R.id.friendmessage_layout)
        RelativeLayout friendmessageLayout;
        @BindView(R.id.mymessgae_icon)
        ImageView mymessgaeIcon;
        @BindView(R.id.mymessage_time)
        TextView mymessageTime;
        @BindView(R.id.mymessage_nickname)
        TextView mymessage_Nickname;
        @BindView(R.id.mymessage_text)
        EditText mymessageText;
        @BindView(R.id.mymessage_layout)
        RelativeLayout mymessageLayout;
        @BindView(R.id.message_layout)
        RelativeLayout messageLayout;

        ChatViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
