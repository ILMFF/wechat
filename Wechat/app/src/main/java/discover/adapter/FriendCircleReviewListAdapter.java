package discover.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Xmpp.AppTemp;
import butterknife.BindView;
import butterknife.ButterKnife;
import discover.bean.FriendCircleReviewList;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class FriendCircleReviewListAdapter extends BaseAdapter  {

    List<FriendCircleReviewList> list;
    Context context;

    public FriendCircleReviewListAdapter(List<FriendCircleReviewList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
//        Log.e("显示的评论长度",list.size()+"");
        return list.size();
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
    public View getView(int position, View converview, ViewGroup viewGroup) {

        ReviewViewHolder holder = null;
        if (converview == null) {
            converview = LayoutInflater.from(context).inflate(R.layout.discoverfragment_friendcircle_reviewlist, null);
            holder = new ReviewViewHolder(converview);
            converview.setTag(holder);
        }else {

            holder = (ReviewViewHolder) converview.getTag();
        }
//        Log.e("评论内容",list.get(position).review_nickname+"   "+list.get(position).review_content);
        holder.friendcircleSenderNickname.setText(list.get(position).review_nickname+" : ");
        EmojiSelect(list.get(position).review_content,holder.friendcircleSenderContent);
        return converview;
    }



    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            Log.e("评论以改变","----------------");
            FriendCircleReviewListAdapter adapter = intent.getParcelableExtra("adapter");
            adapter.notifyDataSetChanged();
        }
    }

    //将显示的信息进行筛选显示表情
    public SpannableString EmojiSelect(String Message, EditText textView) {

        String EmojiText = Message;
        textView.setText(EmojiText);
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

    static class ReviewViewHolder {
        @BindView(R.id.friendcircle_sender_nickname)
        TextView friendcircleSenderNickname;
        @BindView(R.id.friendcircle_sender_content)
        EditText friendcircleSenderContent;

        ReviewViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
