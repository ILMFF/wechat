package weixin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import teacher.davisstore.com.wechat.R;
import weixin.bean.MessageBean;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Chat_CheckRecordListAdapter extends BaseAdapter {

    List<MessageBean> list;
    Context context;

    public Chat_CheckRecordListAdapter(List<MessageBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View ConverView, ViewGroup viewGroup) {

        CheckRecordViewHolder holder = null;
        if (ConverView == null) {

            ConverView = LayoutInflater.from(context).inflate(R.layout.chat_dateselect_list, null);
            holder = new CheckRecordViewHolder(ConverView);
            ConverView.setTag(holder);
        } else {

            holder = (CheckRecordViewHolder) ConverView.getTag();
        }


        MessageBean messageBean = list.get(position);

        switch (messageBean.from) {

            case "OUT":  //发送消息
                if (messageBean.ChatRoomOrNot.equals("YES")) { //群聊就显示昵称
                    holder.recordMymessageNickname.setVisibility(View.VISIBLE);
                    VCard card = new VCard();
                    try {
                        card.load(con, messageBean.MemberSender + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    holder.recordMymessageNickname.setText(card.getNickName());
                    holder.recordMymessageIcon.setImageBitmap(bitmap);
                    EmojiSelect(messageBean.message, holder.recordMymessageText);
                    holder.recordFriendmessageLayout.setVisibility(View.GONE); //对方的布局隐藏
                    holder.recordMymessageLayout.setVisibility(View.VISIBLE); //自己发送的布局显示
                    holder.recordMymessageTime.setText(messageBean.time);

                } else {
                    holder.recordMymessageNickname.setVisibility(View.GONE);
                    EmojiSelect(messageBean.message, holder.recordMymessageText);
                    holder.recordFriendmessageLayout.setVisibility(View.GONE); //对方的布局隐藏
                    holder.recordMymessageLayout.setVisibility(View.VISIBLE); //自己发送的布局显示
                    holder.recordMymessageTime.setText(messageBean.time);
                    holder.recordMymessageIcon.setImageBitmap(messageBean.icon);
                }

                break;


            case "IN":   //接收消息
                if (messageBean.ChatRoomOrNot.equals("YES")) {
                    holder.recordFriendmessageNickname.setVisibility(View.VISIBLE);
                    VCard card = new VCard();
                    try {
                        card.load(con, messageBean.MemberSender + "@" + con.getServiceName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    byte[] avatar = card.getAvatar();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                    holder.recordFriendmessageNickname.setText(card.getNickName());
                    holder.recordFriendmessgaeIcon.setImageBitmap(bitmap);
                    EmojiSelect(messageBean.message, holder.recordFriendmessageText);
                    holder.recordMymessageLayout.setVisibility(View.GONE);
                    holder.recordFriendmessageLayout.setVisibility(View.VISIBLE);
                    holder.recordFriendmessageTime.setText(messageBean.time);

                } else {
                    holder.recordFriendmessageNickname.setVisibility(View.GONE);
                    EmojiSelect(messageBean.message, holder.recordFriendmessageText);
                    holder.recordMymessageLayout.setVisibility(View.GONE);
                    holder.recordFriendmessageLayout.setVisibility(View.VISIBLE);
                    holder.recordFriendmessageTime.setText(messageBean.time);
                    holder.recordFriendmessgaeIcon.setImageBitmap(messageBean.icon);
                }

                break;

            default:
                break;

        }


        return ConverView;
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

    class CheckRecordViewHolder {

        @BindView(R.id.record_friendmessgae_icon)
        ImageView recordFriendmessgaeIcon;
        @BindView(R.id.record_friendmessage_time)
        TextView recordFriendmessageTime;
        @BindView(R.id.record_friendmessage_nickname)
        TextView recordFriendmessageNickname;
        @BindView(R.id.record_friendmessage_text)
        EditText recordFriendmessageText;
        @BindView(R.id.record_friendmessage_layout)
        RelativeLayout recordFriendmessageLayout;
        @BindView(R.id.record_mymessage_icon)
        ImageView recordMymessageIcon;
        @BindView(R.id.record_mymessage_time)
        TextView recordMymessageTime;
        @BindView(R.id.record_mymessage_nickname)
        TextView recordMymessageNickname;
        @BindView(R.id.record_mymessage_text)
        EditText recordMymessageText;
        @BindView(R.id.record_mymessage_layout)
        RelativeLayout recordMymessageLayout;

        CheckRecordViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
