package discover.adapter;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import discover.bean.FriendCircleDisplay;
import discover.bean.FriendCircleReviewList;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MySex;
import static Xmpp.AppTemp.MySignature;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.friendlist;
import static Xmpp.AppTemp.target_friendCircle;
import static Xmpp.AppTemp.target_text;
import static discover.DiscoverFragment_FriendCirCle.fc_adapter;


/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class FriendCircleAdapter extends BaseAdapter {

    FriendCircleReviewListAdapter review_adapter;
    List<FriendCircleReviewList> reviewlist = new ArrayList<>();
    RelativeLayout friendcircleReviewLayout;
    Context context;

    public FriendCircleAdapter(Context context, RelativeLayout friendcircleReviewLayout) {
        this.context = context;
        this.friendcircleReviewLayout = friendcircleReviewLayout;
    }

    @Override
    public int getCount() {
//        Log.e("list1111111", friendlist.size() + "  ");
        return friendlist.size();
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
    public View getView(final int position, View converview, ViewGroup viewGroup) {

        FriendSenderViewHolder holder = null;
        if (converview == null) {

            converview = LayoutInflater.from(context).inflate(R.layout.discoverfragment_friendcircle_list, null);
            holder = new FriendSenderViewHolder(converview);
            converview.setTag(holder);
        } else {

            holder = (FriendSenderViewHolder) converview.getTag();
        }

        holder.friendcircleSendericon.setImageBitmap(friendlist.get(position).friendcircle_icon);
        holder.friendcircleSenderNickname.setText(friendlist.get(position).friendcircle_nickname);
        holder.friendcircleSenderTime.setText(friendlist.get(position).friendcircle_time);
        EmojiSelect(friendlist.get(position).friendcircle_text, holder.friendcircleSenderContent);

        reviewlist = getreview(friendlist.get(position));
        review_adapter = new FriendCircleReviewListAdapter(reviewlist, context);
        holder.friendcircleReviewList.setAdapter(review_adapter);
//        holder.friendcircleReviewList.setLayoutParams();
        setListViewHeight(holder.friendcircleReviewList);


       if (reviewlist.size() < 1) {
           if ( holder.friendcircleReviewList.getVisibility() == View.VISIBLE)
            holder.friendcircleReviewList.setVisibility(View.GONE);
        }
        else {
           holder.friendcircleReviewList.setVisibility(View.VISIBLE);
       }


        final FriendSenderViewHolder finalHolder = holder;
        holder.friendcircleSenderReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowPopWindow(view, friendlist.get(position),position, finalHolder.friendcircleReviewList);
            }
        });


        holder.friendcircleLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(context);
                deletefriend_builder.setPositiveButton("添加进收藏夹", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Log.e("收藏的目标信息", friendlist.get(position).friendcircle_username);
                        Intent intent = new Intent("com.broadcast.myfavorite");

                        intent.putExtra("new_favorite_username", friendlist.get(position).friendcircle_username);
                        intent.putExtra("new_favorite_time", friendlist.get(position).friendcircle_time);
                        intent.putExtra("new_favorite_content", friendlist.get(position).friendcircle_text);
                        intent.putExtra("new_favorite_icon", friendlist.get(position).friendcircle_icon);
                        intent.putExtra("new_favorite_nickname", friendlist.get(position).friendcircle_nickname);


                        context.sendBroadcast(intent);
                    }
                });
                AlertDialog deletefriend_dialog = deletefriend_builder.create();
                deletefriend_dialog.show();
                return true;
            }
        });

        return converview;
    }

    private List<FriendCircleReviewList> getreview(FriendCircleDisplay friendCircleDisplay) {

        List<FriendCircleReviewList> reviewlist = new ArrayList<>();
        final VCard card = new VCard();
        try {
            card.load(con, friendCircleDisplay.friendcircle_username + "@" + con.getServiceName());
            final String friendCircle = card.getField("FriendCircle");
            Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
            Matcher matcher = pat.matcher(friendCircle);

            while (matcher.find()) {
                String Message = matcher.group(1);
                if (Message.equals(friendCircleDisplay.friendcircle_text)) { //找到目标内容
                    String review = matcher.group(4);
                    Pattern pat1 = Pattern.compile("([^\\(^\\|]+)\\|([^\\)]+)");
                    Matcher matcher1 = pat1.matcher(review);
                    while (matcher1.find()) {
//                        Log.e("评论显示", matcher1.group(1) + "       " + matcher1.group(2));
                        reviewlist.add(new FriendCircleReviewList(matcher1.group(1), matcher1.group(2)));
                    }
                }
            }
//            Log.e("reviewlist",reviewlist.size()+"");

        } catch (XMPPException e) {
            e.printStackTrace();
        }

        return reviewlist;

    }

    private void ShowPopWindow(View view, final FriendCircleDisplay text, final int position, final ListView friendcircleReviewList) {


        final View inflate = LayoutInflater.from(context).inflate(R.layout.discoverfragment_friendcircle_pop, null);
        final TextView mylike_btn = (TextView) inflate.findViewById(R.id.mylike_btn);
        TextView myreview_btn = (TextView) inflate.findViewById(R.id.myreview_btn);

        final VCard card = new VCard();
        try {
//            Log.e("当前要点赞的消息来自",text.friendcircle_username+"  "+text.friendcircle_nickname);

            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
            card.load(con, text.friendcircle_username + "@" + con.getServiceName());
            final String friendCircle = card.getField("FriendCircle");
            final byte[] myavatar = card.getAvatar();
            final String encodeImage = StringUtils.encodeBase64(myavatar);
            final String sex = card.getFirstName();
            final String signature = card.getLastName();
//            Log.e("当前的朋友圈",friendCircle);
            boolean alreadypoint = alreadypointlike(friendCircle, text);
            if (alreadypoint){
//                Log.e("赞按钮的文字","取消");
                mylike_btn.setText("取消");
            }else {
//                Log.e("赞按钮的文字","赞");
                mylike_btn.setText("赞");
            }
            mylike_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        if (mylike_btn.getText().toString().equals("赞")) {
                            String newfriendCircle = addFriendCircle_Review(friendCircle, text);  //目标内容
//                            Log.e("最新评论", newfriendCircle);

                            if (text.friendcircle_username.equals(MyUserName)){

                                card.setEncodedImage(encodeImage);
                                card.setAvatar(myavatar);
                                card.setProperty("PHOTO", encodeImage);
                                card.setNickName(MyNickName);
                                card.setFirstName(sex);
                                card.setLastName(signature);
                                card.setField("FriendCircle", newfriendCircle);
                                try {
                                    card.save(con);
                                } catch (XMPPException e) {
                                    e.printStackTrace();
                                }

                            }else {

                                Presence subscription = new Presence(Presence.Type.available);
                                subscription.setTo(text.friendcircle_username + "@" + con.getServiceName());
                                con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                                subscription.setProperty("newfriendCircle", newfriendCircle);
                                con.sendPacket(subscription);
                            }

                            mylike_btn.setText("取消");


                        }else {
                            //删除点赞
                            String deleteFriendCircle = deleteFriendCircle_like(friendCircle, text);
                            if (text.friendcircle_username.equals(MyUserName)){
                                byte[] myavatar = card.getAvatar();
                                String encodeImage = StringUtils.encodeBase64(myavatar);
                                String sex = card.getFirstName();
                                String signature = card.getLastName();
                                card.setEncodedImage(encodeImage);
                                card.setAvatar(myavatar);
                                card.setProperty("PHOTO", encodeImage);
                                card.setNickName(MyNickName);
                                card.setFirstName(sex);
                                card.setLastName(signature);
                                card.setField("FriendCircle", deleteFriendCircle);
                                try {
                                    card.save(con);
                                } catch (XMPPException e) {
                                    e.printStackTrace();
                                }

                            } else {

                                Presence subscription = new Presence(Presence.Type.available);
                                subscription.setTo(text.friendcircle_username + "@" + con.getServiceName());
                                con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                                subscription.setProperty("deleteFriendCircle", deleteFriendCircle);
                                con.sendPacket(subscription);
                            }

                            mylike_btn.setText("赞");

                        }

                    notifyDataSetChanged();

                }
            });

        myreview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendcircleReviewLayout.getVisibility() == View.GONE) {
                    friendcircleReviewLayout.setVisibility(View.VISIBLE);
                    target_friendCircle = friendCircle;
                    target_text = text;
                }else {
                    friendcircleReviewLayout.setVisibility(View.GONE);
                }


            }
        });

        } catch (XMPPException e) {
            e.printStackTrace();
        }
        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAsDropDown(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        popupWindow.getContentView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.setFocusable(false);//失去焦点
                popupWindow.dismiss();//消除pw
                return true;
            }
        });


//        popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
    }

    private String deleteFriendCircle_like(String friendCircle, FriendCircleDisplay text) {

        String str = friendCircle;
        String OldFriendCircle = null;
        String pointmessage = "("+MyNickName +"|[emoji24]  点赞"+")";
        String AfterDeleteReview = "";
        Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
        Matcher matcher = pat.matcher(friendCircle);

        while (matcher.find()) {
            String Message = matcher.group(1);
//            Log.e("ssssss",Message+ "   "+text.friendcircle_text);
            if (Message.equals(text.friendcircle_text)) { //找到目标内容
                OldFriendCircle = "{" + text.friendcircle_text + "|" + text.friendcircle_time + "|" + matcher.group(3)+ "}";

                Pattern pat1=Pattern.compile("\\(([^\\(^\\|]+)\\|([^\\)]+)\\)");
                Matcher matcher1 = pat1.matcher(matcher.group(4));
                while (matcher1.find()){
                    if (matcher1.group(0).equals(pointmessage)  == false){
                        AfterDeleteReview += matcher1.group(0);
                    }
                }
            }

        }
        String NewReview = "{"+text.friendcircle_text+"|"+text.friendcircle_time+"|"+"/"+AfterDeleteReview+"/"+"}";
//        Log.e("OldFrien++++NewReview",str+"     "+OldFriendCircle+"    "+NewReview);
        String replace = str.replace(OldFriendCircle,NewReview);
//        Log.e("replace",replace);
        if (replace != null) {
            return replace;
        }else {
            return str;
        }

    }

    private boolean alreadypointlike(String friendCircle, FriendCircleDisplay text) {

        String pointmessage = "(" + MyNickName +"|[emoji24]  点赞" + ")";

        Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
        Matcher matcher = pat.matcher(friendCircle);
//        Log.e("查找是否点赞",friendCircle);
        while (matcher.find()) {
            String Message = matcher.group(1);
//            Log.e("text.friendcircle_text",text.friendcircle_text+"  "+Message+"   "+Message.equals(text.friendcircle_text));
            if (Message.equals(text.friendcircle_text)) { //找到目标内容
                Pattern pat1=Pattern.compile("\\(([^\\(^\\|]+)\\|([^\\)]+)\\)");
                Matcher matcher1 = pat1.matcher(matcher.group(4));

                while (matcher1.find()){
//                    Log.e("点赞设定",matcher1.group(0)+"   "+pointmessage+"    "+matcher1.group(0).equals(pointmessage));
                    if (matcher1.group(0).equals(pointmessage)){
                        return true;  //已经点过赞
                    }
                }


            }

        }
        return false;

    }

    private String addFriendCircle_Review(String FriendCircleField, FriendCircleDisplay text) {


        String str = FriendCircleField; //所有内容
        String replace = null ; //替换后内容
        String updatereview = null;
        Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
        Matcher matcher = pat.matcher(str);

        while (matcher.find()) {

            String Message = matcher.group(1);
            if (Message.equals(text.friendcircle_text)) { //找到目标内容

                String OldFriendCircle = "{" + text.friendcircle_text + "|" + text.friendcircle_time + "|" + matcher.group(3)+ "}";
                String Time = matcher.group(2);
                String NewReview = matcher.group(4) + "(" + MyNickName +"|[emoji24]  点赞" + ")"; //在旧的上面添加
                String NewFriendCircle = "{" + Message + "|" + Time + "|" + "/"+NewReview +"/"+"}";

//                Log.e("OldFriendCircle..",OldFriendCircle+"      "+NewFriendCircle);
                replace = str.replace(OldFriendCircle, NewFriendCircle);
                updatereview =  NewReview ;
//                Log.e("replace",replace);

            }

        }


        //重新更新评论列表
        if (updatereview != null) {
            reviewlist.clear();
            Pattern pat1 = Pattern.compile("([^\\(^\\|]+)\\|([^\\)]+)");
            Matcher matcher1 = pat1.matcher(updatereview);

            while (matcher1.find()) {
//                Log.e("000000",matcher1.group(1)+"                 "+matcher1.group(2));
//                if (friendcircleReviewList.getVisibility() == View.GONE)
//                friendcircleReviewList.setVisibility(View.VISIBLE);
                reviewlist.add(new FriendCircleReviewList(matcher1.group(1), matcher1.group(2)));
            }
            review_adapter.notifyDataSetChanged();

            return replace;
        }else {
            return str;
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


    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(context, "收到了朋友圈刷新广播", Toast.LENGTH_SHORT).show();
            if (fc_adapter != null)
            fc_adapter.notifyDataSetChanged();

        }
    }



    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = 120 * listAdapter.getCount();
        if (listAdapter.getCount() > 1){
            params.height = params.height-((listAdapter.getCount()-1)*78);
        }

//        Log.e("子个数",listAdapter.getCount()+"      "+params.height);
        listView.setLayoutParams(params);

    }



    static class FriendSenderViewHolder {
        @BindView(R.id.friendcircle_sendericon)
        ImageView friendcircleSendericon;
        @BindView(R.id.friendcircle_sender_nickname)
        TextView friendcircleSenderNickname;
        @BindView(R.id.friendcircle_sender_content)
        EditText friendcircleSenderContent;
        @BindView(R.id.friendcircle_sender_time)
        TextView friendcircleSenderTime;
        @BindView(R.id.friendcircle_sender_review)
        ImageView friendcircleSenderReview;
        @BindView(R.id.friendcircle_layout)
        RelativeLayout friendcircleLayout;
        @BindView(R.id.friendcircle_review_list)
        ListView friendcircleReviewList;

        FriendSenderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
