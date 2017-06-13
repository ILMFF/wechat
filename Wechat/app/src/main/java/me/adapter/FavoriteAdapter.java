package me.adapter;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.bean.MyFavorite;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MySex;
import static Xmpp.AppTemp.MySignature;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.favorite_context;
import static Xmpp.AppTemp.favorite_list;
import static teacher.davisstore.com.wechat.BaseActivity.favoriteadapter;
import static teacher.davisstore.com.wechat.Register_main.BitmapToBytes;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class FavoriteAdapter extends BaseAdapter {

    List<MyFavorite> list;
    Context context;

    public FavoriteAdapter(List<MyFavorite> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
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
    public View getView(final int position, View converview, ViewGroup viewGroup) {

        FavoriteViewHolder holder = null;
        if (converview == null) {
            converview = LayoutInflater.from(context).inflate(R.layout.myfavorite_list, null);
            holder = new FavoriteViewHolder(converview);
            converview.setTag(holder);
        } else {

            holder = (FavoriteViewHolder) converview.getTag();
        }

        holder.myfavoriteIcon.setImageBitmap(list.get(position).icon);
        holder.myfavoriteNickname.setText(list.get(position).nickname);
        holder.myfavoriteTime.setText(list.get(position).time);

        EmojiSelect(list.get(position).text, holder.myfavoriteContent);

        holder.myfavoriteLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(favorite_context);
                deletefriend_builder.setPositiveButton("删除收藏内容", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        VCard card = new VCard();
                        try {
                            card.load(con,MyUserName+"@"+con.getServiceName());

                        String now_myfavourite = card.getMiddleName();
                        String replace  = null;
                        Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|([^\\}]*)");
                        Matcher matcher = pat.matcher(now_myfavourite);

                        while (matcher.find()){

                            String str = "{"+matcher.group(0)+"}";
                            String name = matcher.group(1);
                            String time = matcher.group(2);
                            String content = matcher.group(3);

//                            Log.e("收藏比较",name+"   "+list.get(position).usename+"  " +
//                                    time+"    "+list.get(position).time+"     "+
//                                    content+"   "+list.get(position).text);
                            if (name.equals(list.get(position).usename) &&
                                    time.equals(list.get(position).time) &&
                                    content.equals(list.get(position).text)){

                                replace = now_myfavourite.replace(str,"");//把要删除的清除为空
                            }

                        }

//                            Log.e("replace",replace);

                        if (replace != null) {
                            byte[] avatar = card.getAvatar();
                            byte[] usericon_bytes = BitmapToBytes(MyIcon);
                            String encodeImage = StringUtils.encodeBase64(usericon_bytes);
                            String friendCircle_text = card.getField("FriendCircle");
                            card.setEncodedImage(encodeImage);
                            card.setAvatar(avatar);
                            card.setProperty("PHOTO", encodeImage);
                            card.setNickName(MyNickName);
                            card.setFirstName(MySex);
                            card.setLastName(MySignature);
                            card.setField("FriendCircle", friendCircle_text);
                            card.setMiddleName(replace);
                            card.save(con);

                            for (int j = 0; j <favorite_list.size() ; j++) {

                                if (favorite_list.get(j).usename.equals(list.get(position).usename) &&
                                        favorite_list.get(j).time.equals(list.get(position).time) &&
                                        favorite_list.get(j).text.equals(list.get(position).text))
                                {
                                    favorite_list.remove(j);
                                    break;
                                }

                            }

                            notifyDataSetChanged();
                        }

                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }



                    }
                });
                AlertDialog deletefriend_dialog = deletefriend_builder.create();
                deletefriend_dialog.show();
                return true;
            }
        });


        return converview;
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


    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            Log.e("刷新收藏夹", "111111111111");
            VCard card = new VCard();
            String encodeImage = null;
            byte[] usericon_bytes = new byte[0];
            String favor = null;

            String new_favorite_username = intent.getStringExtra("new_favorite_username");
            String new_favorite_time = intent.getStringExtra("new_favorite_time");
            String new_favorite_content = intent.getStringExtra("new_favorite_content");
            Parcelable new_favorite_icon = intent.getParcelableExtra("new_favorite_icon");
            Bitmap bitmap = (Bitmap) new_favorite_icon;
            String new_favorite_nickname = intent.getStringExtra("new_favorite_nickname");
            favor = "{" + new_favorite_username + "|" + new_favorite_time + "|" + new_favorite_content + "}";

            try {
                card.load(con, MyUserName + "@" + con.getServiceName());


                String now_favorite = card.getMiddleName();
                now_favorite += favor;
                Log.e("now_favorite", now_favorite);
                byte[] avatar = card.getAvatar();
                usericon_bytes = BitmapToBytes(MyIcon);
                encodeImage = StringUtils.encodeBase64(usericon_bytes);
                String friendCircle_text = card.getField("FriendCircle");
                card.setEncodedImage(encodeImage);
                card.setAvatar(avatar);
                card.setProperty("PHOTO", encodeImage);
                card.setNickName(MyNickName);
                card.setFirstName(MySex);
                card.setLastName(MySignature);
                card.setField("FriendCircle", friendCircle_text);
                card.setMiddleName(now_favorite);
                card.save(con);

            } catch (XMPPException e) {
                e.printStackTrace();
            }
            favorite_list.add(new MyFavorite(new_favorite_username, new_favorite_nickname,
                    bitmap, new_favorite_time, new_favorite_content));
            favoriteadapter.notifyDataSetChanged();
        }
    }


    class FavoriteViewHolder {
        @BindView(R.id.myfavorite_icon)
        ImageView myfavoriteIcon;
        @BindView(R.id.myfavorite_nickname)
        TextView myfavoriteNickname;
        @BindView(R.id.myfavorite_time)
        TextView myfavoriteTime;
        @BindView(R.id.myfavorite_content)
        EditText myfavoriteContent;
        @BindView(R.id.myfavorite_layout)
        RelativeLayout myfavoriteLayout;

        FavoriteViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
