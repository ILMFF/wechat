package discover;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import discover.adapter.FriendCircleAdapter;
import discover.adapter.FriendCricleEmojiAdapter;
import discover.bean.FriendCircleDisplay;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Image_Emoji;
import static Xmpp.AppTemp.Image_Emoji_friendcircle;
import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.friendlist;
import static Xmpp.AppTemp.target_friendCircle;
import static Xmpp.AppTemp.target_text;


/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class DiscoverFragment_FriendCirCle extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    public static FriendCircleAdapter fc_adapter;

    @BindView(R.id.friendcircle_back_btn)
    ImageView friendcircleBackBtn;
    @BindView(R.id.friendcircle_send_btn)
    ImageView friendcircleSendBtn;
    @BindView(R.id.friendcircle_top_title)
    RelativeLayout friendcircleTopTitle;
    @BindView(R.id.friendcircle_list)
    ListView friendcircleList;
    @BindView(R.id.friendcircle_refresh)
    SwipeRefreshLayout friendcircleRefresh;
    @BindView(R.id.review_send)
    Button reviewSend;
    @BindView(R.id.review_edit)
    EditText reviewEdit;
    @BindView(R.id.review_layout)
    RelativeLayout reviewLayout;
    @BindView(R.id.friendcircle_review_layout)
    RelativeLayout friendcircleReviewLayout;
    @BindView(R.id.review_emoji_gridview)
    GridView reviewEmojiGridview;
//    @BindView(R.id.friendcircle_myicon)
//    ImageView friendcircleMyicon;
//    @BindView(R.id.friendcircle_mynickname)
//    TextView friendcircleMynickname;


    private Handler handler = new Handler() {

        public void handleMessage(Message message) {

//            Log.e("重新计算", "-------------");
//            setListViewHeightBasedOnChildren(friendcircleList);
            fc_adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discoverfragment_friendcircle);
        ButterKnife.bind(this);
        init();
    }

    private void init() {



        friendlist = getFriendlist();
        fc_adapter = new FriendCircleAdapter(this, friendcircleReviewLayout);
        View inflate = LayoutInflater.from(this).inflate(R.layout.discoverfragment_top_msg, null);
        friendcircleList.addHeaderView(inflate); //添加头部

        ImageView friendcircleMyicon = (ImageView) inflate.findViewById(R.id.friendcircle_myicon);
        TextView friendcircleMynickname = (TextView) inflate.findViewById(R.id.friendcircle_mynickname);
        friendcircleMyicon.setImageBitmap(MyIcon);
        friendcircleMynickname.setText(MyNickName);
        friendcircleList.setAdapter(fc_adapter);
//        setListViewHeightBasedOnChildren(friendcircleList);

        friendcircleRefresh.setOnRefreshListener(this);
        friendcircleRefresh.setColorSchemeResources(R.color.blue,
                R.color.black,
                R.color.green);

        FriendCricleEmojiAdapter adapter = new FriendCricleEmojiAdapter(Image_Emoji_friendcircle, this, reviewEdit);
        reviewEmojiGridview.setAdapter(adapter);


    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    @OnClick({R.id.friendcircle_back_btn, R.id.friendcircle_send_btn, R.id.review_send, R.id.review_emoji})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friendcircle_back_btn:
                finish();
                break;
            case R.id.friendcircle_send_btn:
                Intent intent = new Intent(DiscoverFragment_FriendCirCle.this, DiscoverFragment_sendmessage.class);
                startActivity(intent);
                break;
            case R.id.review_emoji: //评论表情开关
                if (reviewEmojiGridview.getVisibility() == View.VISIBLE) {
                    reviewEmojiGridview.setVisibility(View.GONE);
                } else {
                    reviewEmojiGridview.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.review_send: //评论发送
                if (reviewEdit.getText().toString() != null && reviewEdit.getText().toString().length() > 0) {
                    if (target_text.friendcircle_username.equals(MyUserName)) { //自己评论自己

                        String newfriendCircle = addFriendCircle_Review(target_friendCircle, target_text);  //目标内容
                        VCard card = new VCard();
                        try {
                            card.load(con, MyUserName + "@" + con.getServiceName());
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
                            card.setField("FriendCircle", newfriendCircle);
                            card.save(con);
                            reviewEmojiGridview.setVisibility(View.GONE);
                            friendcircleReviewLayout.setVisibility(View.GONE);
                            reviewEdit.setText("");
//                            fc_adapter.notifyDataSetChanged();

                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("发送评论了","------------"+target_text.friendcircle_username);
                        String newfriendCircle = addFriendCircle_Review(target_friendCircle, target_text);  //目标内容
                        Presence subscription = new Presence(Presence.Type.unavailable);
                        subscription.setTo(target_text.friendcircle_username + "@" + con.getServiceName());
                        con.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual); //手动处理
                        subscription.setProperty("newfriendCircle", newfriendCircle);
                        con.sendPacket(subscription);
                        reviewEmojiGridview.setVisibility(View.GONE);
                        friendcircleReviewLayout.setVisibility(View.GONE);
//                        fc_adapter.notifyDataSetChanged();
                    }
                    Intent intent_friendcircle = new Intent("com.broadcast.FriendCircleRefresh");
                    sendBroadcast(intent_friendcircle);
                } else {
                    Toast.makeText(DiscoverFragment_FriendCirCle.this, "不能发送空评论", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private String addFriendCircle_Review(String target_friendCircle, FriendCircleDisplay target_text) {

        String str = target_friendCircle; //所有内容
        String replace = null; //替换后内容
        Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
        Matcher matcher = pat.matcher(str);

        while (matcher.find()) {

            String Message = matcher.group(1);
            if (Message.equals(target_text.friendcircle_text)) { //找到目标内容

                String OldFriendCircle = "{" + target_text.friendcircle_text + "|" + target_text.friendcircle_time + "|" + matcher.group(3) + "}";
                String Time = matcher.group(2);
                String NewReview = matcher.group(4) + "(" + MyNickName + "|" + reviewEdit.getText().toString() + ")"; //在旧的上面添加
                String NewFriendCircle = "{" + Message + "|" + Time + "|" + "/" + NewReview + "/" + "}";
//                Log.e("OldFriendCircle..",OldFriendCircle+"      "+NewFriendCircle);
                replace = str.replace(OldFriendCircle, NewFriendCircle);
//                Log.e("replace",replace);

            }

        }

        if (replace != null)
            return replace;
        else
            return str;

    }


    public List<FriendCircleDisplay> getFriendlist() {

        List<FriendCircleDisplay> list = new ArrayList<>();
        try { //加载自己的
            VCard card = new VCard();
            card.load(con, MyUserName + "@" + con.getServiceName());

            List<FriendCircleDisplay> list1 = SpliteContent(card, MyUserName);

            List<FriendCircleDisplay> list2 = new ArrayList<>();


            for (int i = 0; i < ContactPersonList.size(); i++) {

                Log.e("ContactPersonList21214",ContactPersonList.get(i));
                VCard friend_card = new VCard();
                friend_card.load(con, ContactPersonList.get(i) + "@" + con.getServiceName());
                List<FriendCircleDisplay> list_other = SpliteContent(friend_card, (String) ContactPersonList.get(i));
                for (int j = 0; j < list_other.size(); j++) {
                    list2.add(list_other.get(j));
                }
            }

            for (int i = 0; i < list1.size(); i++) {
                list.add(list1.get(i));
            }

            for (int i = 0; i < list2.size(); i++) {
                list.add(list2.get(i));
            }

            Log.e("listlist",list1.size()+"  "+list2.size()+"    "+list.size()+"");
            for (int i = 0; i < list.size(); i++) {
                Log.e("Total_list",list.get(i).friendcircle_username+"   "+ list.get(i).friendcircle_text + "");
            }

            list = listsort(list);

            for (int i = 0; i < list.size(); i++) {
                Log.e("Total_list11",list.get(i).friendcircle_username+"   "+ list.get(i).friendcircle_text + "");
            }

        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<FriendCircleDisplay> listsort(List<FriendCircleDisplay> list) {
        List<FriendCircleDisplay> newlist = list;

        for (int i = 0; i < newlist.size(); i++) {

            for (int j = i; j < newlist.size(); j++) {

                String friendcircle_time1 = newlist.get(i).friendcircle_time;
                String friendcircle_time2 = newlist.get(j).friendcircle_time;


                if (friendcircle_time1.compareTo(friendcircle_time2) < 0) {

                    FriendCircleDisplay Temp;
                    Temp = newlist.get(i);
                    newlist.set(i, newlist.get(j));
                    newlist.set(j, Temp);

                }

            }

        }
        for (int i = 0; i < newlist.size(); i++) {
            Log.e("sort", newlist.get(i).friendcircle_time);
        }

        return newlist;

    }

    private List<FriendCircleDisplay> SpliteContent(VCard card, String username) {

        List<FriendCircleDisplay> list = new ArrayList<>();
        String nickName = card.getNickName();
        String friendCircle_text = card.getField("FriendCircle");
        byte[] avatar = card.getAvatar();
        Bitmap icon = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        String sendtime = null;
        String text = null;

        if (friendCircle_text != null) {
            Log.e("friendCircle_text11111",friendCircle_text);
            Pattern pat = Pattern.compile("([^\\{^\\|]+)\\|([^\\{^\\|]+)\\|(\\/([^\\/]*)\\/)");
            Matcher matcher = pat.matcher(friendCircle_text);

            while (matcher.find()) {

                text = matcher.group(1);
                sendtime = matcher.group(2);
                String review = matcher.group(4);
                Log.e("所有评论显示",text+"       "+sendtime+"     "+review);

                list.add(new FriendCircleDisplay(username, icon, nickName, sendtime, text));

            }
        }

        return list;

    }

    @Override
    public void onRefresh() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                friendlist = getFriendlist();
                Message msg = handler.obtainMessage();
                msg.sendToTarget();
                Intent intent = new Intent("com.broadcast.FriendCircleRefresh");
                sendBroadcast(intent);

                friendcircleRefresh.setRefreshing(false);
            }
        }).start();

    }
}
